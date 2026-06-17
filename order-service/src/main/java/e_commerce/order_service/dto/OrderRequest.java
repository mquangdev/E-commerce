package e_commerce.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

  @NotNull(message = "ID người dùng không được để trống")
  private UUID userId;

  @NotBlank(message = "Địa chỉ giao hàng không được để trống")
  private String shippingAddress;

  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không đúng định dạng")
  private String email;

  @NotEmpty(message = "Đơn hàng phải chứa ít nhất một mặt hàng")
  @Valid
  private List<OrderItemRequest> items;
}
