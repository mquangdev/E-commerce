package e_commerce.catalog_service.commands.dtos;

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
public class CategoryResponse {
  private UUID id;
  private String name;
  private String description;
  private boolean isActive;
  private boolean isDeleted;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long version;
}
