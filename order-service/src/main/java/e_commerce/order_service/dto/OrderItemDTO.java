package e_commerce.order_service.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
  private String productName;
  private UUID productId;
  private int quantity;
  private BigDecimal unitPrice;
}
