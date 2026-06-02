package e_commerce.payment_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletRequest {

  @NotNull(message = "ID người dùng không được để trống")
  private UUID userId;

  @NotNull(message = "Số tiền không được để trống")
  @DecimalMin(value = "0.01", message = "Số tiền nạp phải lớn hơn 0")
  private BigDecimal amount;
}
