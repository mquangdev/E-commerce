package e_commerce.catalog_service.commands.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
  private UUID id;
  private UUID categoryId;
  private String sku;
  private String name;
  private String description;
  private BigDecimal price;
  private String imageUrl;
  private int stockQuantity;
  private boolean isDeleted;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long version;
}
