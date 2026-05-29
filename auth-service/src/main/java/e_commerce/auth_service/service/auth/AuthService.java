package e_commerce.auth_service.service.auth;

import e_commerce.auth_service.dto.request.LoginRequest;
import e_commerce.auth_service.dto.request.RegisterRequest;
import e_commerce.auth_service.dto.response.AuthResponseInternal;
import e_commerce.auth_service.entity.RefreshTokenEntity;
import e_commerce.auth_service.entity.UserEntity;
import e_commerce.auth_service.enums.UserStatus;
import e_commerce.auth_service.repository.refreshToken.RefreshTokenRepository;
import e_commerce.auth_service.repository.user.UserRepository;
import e_commerce.auth_service.service.jwt.JwtService;
import e_commerce.common_shared.exception.AuthException;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  @Autowired private final PasswordEncoder passwordEncoder;
  @Autowired private final AuthenticationManager authenticationManager;

  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  public UUID register(RegisterRequest request) {
    // 1. Tạo đối tượng User từ request
    var userId = UUID.randomUUID();

    UserEntity user =
        UserEntity.builder()
            .id(userId)
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword())) // Băm mật khẩu
            .status(UserStatus.ACTIVE)
            .build();

    // 2. Lưu vào DB
    userRepository.save(user);

    return userId;
  }

  public AuthResponseInternal login(LoginRequest request) {
    // 1. Xác thực bằng AuthenticationManager
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    // 2. Nếu không có lỗi, tìm user và tạo token
    var user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // 3. Xử lý token cũ: Tìm token cũ của user trên thiết bị này và thu hồi
    Optional<RefreshTokenEntity> oldTokenOpt =
        refreshTokenRepository.findByUserAndDeviceIdAndIsRevokedFalse(user, request.getDeviceId());
    if (oldTokenOpt.isPresent()) {
      var oldToken = oldTokenOpt.get();
      oldToken.setRevoked(true);
      refreshTokenRepository.save(oldToken);
    }

    // 4. Tạo và lưu refresh token mới
    var refreshToken = UUID.randomUUID();
    RefreshTokenEntity newRefreshToken =
        RefreshTokenEntity.builder()
            .id(UUID.randomUUID())
            .user(user)
            .token(refreshToken)
            .deviceId(request.getDeviceId())
            .isRevoked(false)
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build();
    refreshTokenRepository.save(newRefreshToken);

    // 5. Tạo accessToken
    var accessToken = jwtService.generateToken(user);

    return AuthResponseInternal.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public AuthResponseInternal refreshToken(String refreshTokenValue) {
    if (refreshTokenValue == null || refreshTokenValue.isEmpty()) {
      throw new AuthException("Không tìm thấy Refresh Token trong Cookie.");
    }

    UUID tokenUUID;
    try {
      tokenUUID = UUID.fromString(refreshTokenValue);
    } catch (IllegalArgumentException e) {
      throw new AuthException("Định dạng Refresh Token không hợp lệ.");
    }

    //  1. Tìm refresh token trong DB
    Optional<RefreshTokenEntity> refreshTokenEntityOptional =
        refreshTokenRepository.findByToken(tokenUUID);
    if (refreshTokenEntityOptional.isEmpty()) {
      throw new AuthException("Refresh Token không tồn tại.");
    }
    RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOptional.get();
    var user = refreshTokenEntity.getUser();

    // 2. Case b. Token đã bị thu hồi (nghi ngờ bị lộ) -> Thu hồi tất cả token của user đó
    if (refreshTokenEntity.isRevoked()) {
      refreshTokenRepository.revokeAllTokenByUser(user);
      throw new AuthException(
          "Refresh Token đã bị thu hồi. Tất cả token của bạn đã bị thu hồi vì lý do bảo mật. Vui lòng đăng nhập lại.");
    }

    // 3. Case a. Token hết hạn
    if (refreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
      refreshTokenEntity.setRevoked(true);
      refreshTokenRepository.save(refreshTokenEntity);
      throw new AuthException("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
    }

    // 4. Case c. Hợp lệ -> Thu hồi token hiện tại (xoay vòng token)
    refreshTokenEntity.setRevoked(true);
    refreshTokenRepository.save(refreshTokenEntity);

    // 5. Sinh cặp token mới
    var newRefreshTokenUuid = UUID.randomUUID();
    var newRefreshTokenEntity =
        RefreshTokenEntity.builder()
            .id(UUID.randomUUID())
            .user(user)
            .token(newRefreshTokenUuid)
            .deviceId(
                refreshTokenEntity
                    .getDeviceId()) // Kế thừa deviceId từ token cũ (hoặc có thể truyền deviceId
            // xuống)
            .isRevoked(false)
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build();
    refreshTokenRepository.save(newRefreshTokenEntity);

    var newAccessToken = jwtService.generateToken(user);

    return AuthResponseInternal.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshTokenUuid)
        .build();
  }

  public Cookie createRefreshTokenCookie(String refreshToken) {
    Cookie cookie = new Cookie("refreshToken", refreshToken);
    cookie.setHttpOnly(true); // 🛡️ Chống XSS (JavaScript không đọc được)
    cookie.setSecure(true); // Chỉ gửi qua HTTPS (trên môi trường thật)
    cookie.setPath("/api/v1/refresh"); // 🎯 Chỉ gửi Cookie này khi Frontend gọi đúng API refresh
    cookie.setMaxAge(7 * 24 * 60 * 60); // Sống 7 ngày
    return cookie;
  }

  public Cookie deleteRefreshTokenCookie() {
    Cookie cookie = new Cookie("refreshToken", "");
    cookie.setHttpOnly(true); // 🛡️ Chống XSS (JavaScript không đọc được)
    cookie.setSecure(true); // Chỉ gửi qua HTTPS (trên môi trường thật)
    cookie.setPath("/api/v1/refresh"); // 🎯 Chỉ gửi Cookie này khi Frontend gọi đúng API refresh
    cookie.setMaxAge(7 * 24 * 60 * 60); // Sống 0 giây = Xóa ngay lập tức

    return cookie;
  }

  public void logout(String refreshTokenValue, String deviceId) {
    if (refreshTokenValue == null || refreshTokenValue.isEmpty()) {
      throw new AuthException("Không tìm thấy token để đăng xuất.");
    }

    UUID tokenUuid = UUID.fromString(refreshTokenValue);

    // 1. Tìm đúng token đang hoạt động của thiết bị này
    RefreshTokenEntity refreshTokenEntity =
        refreshTokenRepository
            .findByToken(tokenUuid)
            .orElseThrow(() -> new AuthException("Phiên đăng nhập không tồn tại."));

    // 2. Kiểm tra tính chính chủ của thiết bị để tránh lỗi bảo mật
    if (!refreshTokenEntity.getDeviceId().equals(deviceId)) {
      throw new AuthException("Thiết bị yêu cầu đăng xuất không hợp lệ.");
    }

    // 3. Tiến hành thu hồi
    refreshTokenEntity.setRevoked(true);
    refreshTokenRepository.save(refreshTokenEntity);
  }
}
