package e_commerce.auth_service.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
  private LocalDateTime timestamp;
  private int status;       // Ví dụ: 401, 403, 404
  private String error;     // Ví dụ: "Unauthorized"
  private String message;   // Ví dụ: "Refresh token đã hết hạn..."
}
