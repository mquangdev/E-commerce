package e_commerce.auth_service.controller;

import e_commerce.auth_service.dto.request.LoginRequest;
import e_commerce.auth_service.dto.request.RegisterRequest;
import e_commerce.auth_service.dto.response.AuthResponse;
import e_commerce.auth_service.dto.response.AuthResponseInternal;
import e_commerce.auth_service.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<UUID> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
      @RequestBody LoginRequest request, HttpServletResponse response) {
    AuthResponseInternal loginResult = authService.login(request);

    // 1. Tạo Cookie chứa Refresh Token
    Cookie cookie = new Cookie("refreshToken", loginResult.getRefreshToken().toString());
    cookie.setHttpOnly(true); // 🛡️ Chống XSS (JavaScript không đọc được)
    cookie.setSecure(true); // Chỉ gửi qua HTTPS (trên môi trường thật)
    cookie.setPath(
        "/api/v1/auth/refresh"); // 🎯 Chỉ gửi Cookie này khi Frontend gọi đúng API refresh
    cookie.setMaxAge(7 * 24 * 60 * 60); // Sống 7 ngày

    // 2. Gắn Cookie vào Response gửi về Client
    response.addCookie(cookie);

    // 3. Chỉ trả về Access Token trong Body JSON
    return ResponseEntity.ok(
        AuthResponse.builder().accessToken(loginResult.getAccessToken()).build());
  }
}
