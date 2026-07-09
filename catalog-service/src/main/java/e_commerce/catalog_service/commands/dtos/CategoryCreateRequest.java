package e_commerce.catalog_service.commands.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateRequest {
  @NotBlank(message = "Tên danh mục không được để trống")
  @Size(max = 100, message = "Tên danh mục không vượt quá 100 ký tự")
  private String name;

  @Size(max = 255, message = "Mô tả không vượt quá 255 ký tự")
  private String description;

  private Boolean isActive;
}
