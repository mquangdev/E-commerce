package e_commerce.order_service.dto;

import e_commerce.order_service.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
  private UUID id;
  private UUID userId;
  private BigDecimal totalAmount;
  private String shippingAddress;
  private OrderStatus status;
  private long version;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<OrderItemResponse> items;
}
