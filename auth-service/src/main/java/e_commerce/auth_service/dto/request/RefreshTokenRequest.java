package e_commerce.auth_service.dto.request;

import lombok.Data;

@Data
public class RefreshTokenRequest {
  private String refreshToken;
}
