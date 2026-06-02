package e_commerce.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

  @NotNull(message = "ID sản phẩm không được để trống")
  private UUID productId;

  @Min(value = 1, message = "Số lượng mua phải ít nhất là 1")
  private int quantity;

  @NotNull(message = "Đơn giá không được để trống")
  private BigDecimal unitPrice;
}
