package e_commerce.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

  @NotNull(message = "Mã sản phẩm không được để trống")
  private UUID productId;

  @NotNull(message = "Số lượng không được để trống")
  @Min(value = 1, message = "Số lượng nhập kho phải ít nhất là 1")
  private Integer quantity;
}
