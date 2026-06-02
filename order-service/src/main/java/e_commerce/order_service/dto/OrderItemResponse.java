package e_commerce.order_service.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
  private UUID id;
  private UUID productId;
  private int quantity;
  private BigDecimal unitPrice;
}
