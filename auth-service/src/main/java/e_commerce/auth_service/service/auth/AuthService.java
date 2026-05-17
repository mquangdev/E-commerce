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
import java.time.LocalDateTime;
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

    var user =
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
    var oldTokenOpt =
        refreshTokenRepository.findByUserAndDeviceIdAndIsRevokedFalse(user, request.getDeviceId());
    if (oldTokenOpt.isPresent()) {
      var oldToken = oldTokenOpt.get();
      oldToken.setRevoked(true);
      refreshTokenRepository.save(oldToken);
    }

    // 4. Tạo và lưu refresh token mới
    var refreshToken = UUID.randomUUID();
    var newRefreshToken =
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

    return AuthResponseInternal.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }
}
