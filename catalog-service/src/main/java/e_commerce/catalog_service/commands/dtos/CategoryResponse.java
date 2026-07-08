package e_commerce.catalog_service.commands.dtos;

import java.time.LocalDate;
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
  private LocalDate createdAt;
  private LocalDate updatedAt;
  private Long version;
}
