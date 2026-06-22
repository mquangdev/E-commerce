package e_commerce.order_service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
  private UUID orderId;
  private UUID userId;
  private String customerFullName;
  private BigDecimal amount;
  private String email;
  private String shippingAddress;
  private List<OrderItemDTO> orderItems;
}
