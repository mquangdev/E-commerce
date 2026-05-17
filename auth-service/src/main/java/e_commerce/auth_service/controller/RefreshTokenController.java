package e_commerce.auth_service.controller;

import e_commerce.auth_service.service.refreshToken.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class RefreshTokenController {

  private final RefreshTokenService refreshTokenService;
}
