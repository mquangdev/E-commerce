package e_commerce.payment_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

  @NotNull(message = "ID đơn hàng không được để trống")
  private UUID orderId;

  @NotNull(message = "ID người dùng không được để trống")
  private UUID userId;

  @NotNull(message = "Số tiền thanh toán không được để trống")
  @DecimalMin(value = "0.01", message = "Số tiền thanh toán phải lớn hơn 0")
  private BigDecimal amount;

  @NotBlank(message = "Phương thức thanh toán không được để trống")
  private String paymentMethod;
}
