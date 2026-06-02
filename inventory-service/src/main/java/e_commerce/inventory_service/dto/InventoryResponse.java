package e_commerce.inventory_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
  private UUID id;
  private UUID productId;
  private int availableQuantity;
  private int reservedQuantity;
  private long version;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
