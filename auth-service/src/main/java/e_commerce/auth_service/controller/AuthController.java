package e_commerce.auth_service.controller;

import e_commerce.auth_service.dto.request.LoginRequest;
import e_commerce.auth_service.dto.request.RegisterRequest;
import e_commerce.auth_service.dto.response.AuthResponseInternal;
import e_commerce.auth_service.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<UUID> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(
      @RequestBody LoginRequest request, HttpServletResponse response) {
    AuthResponseInternal loginResult = authService.login(request);

    // 1. Tạo Cookie chứa Refresh Token
    Cookie cookie = authService.createRefreshTokenCookie(loginResult.getRefreshToken().toString());

    // 2. Gắn Cookie vào Response gửi về Client
    response.addCookie(cookie);

    // 3. Chỉ trả về Access Token trong Body JSON
    return ResponseEntity.ok(loginResult.getAccessToken());
  }

  @PostMapping("/refresh")
  public ResponseEntity<String> refreshToken(
      @CookieValue(name = "refreshToken", required = false) String refreshToken,
      HttpServletResponse response) {

    AuthResponseInternal refreshResult = authService.refreshToken(refreshToken);

    // 1. Tạo Cookie chứa Refresh Token
    Cookie cookie =
        authService.createRefreshTokenCookie(refreshResult.getRefreshToken().toString());

    // 2. Gắn Cookie vào Response gửi về Client
    response.addCookie(cookie);

    // 3. Chỉ trả về Access Token trong Body JSON
    return ResponseEntity.ok(refreshResult.getAccessToken());
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(
      @CookieValue(name = "refreshToken", required = false) String refreshToken,
      @RequestParam String deviceId, // Hoặc truyền qua Body DTO tùy bạn thiết kế
      HttpServletResponse response) {
    // 1. Gọi Service để thu hồi token dưới DB
    authService.logout(refreshToken, deviceId);

    // 2. Tạo "Cookie xóa" với maxAge = 0
    Cookie cookie = authService.deleteRefreshTokenCookie();

    // 3. Gắn Cookie vào Response gửi về Client
    response.addCookie(cookie);

    return ResponseEntity.ok("Đăng xuất thành công!");
  }
}
