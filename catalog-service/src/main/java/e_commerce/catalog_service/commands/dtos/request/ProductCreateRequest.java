package e_commerce.catalog_service.commands.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
  @NotNull(message = "ID danh mục không được để trống")
  private UUID categoryId;

  @NotBlank(message = "SKU sản phẩm không được để trống")
  @Size(max = 50, message = "SKU không vượt quá 50 ký tự")
  private String sku;

  @NotBlank(message = "Tên sản phẩm không được để trống")
  @Size(max = 255, message = "Tên sản phẩm không vượt quá 255 ký tự")
  private String name;

  private String description;

  @NotNull(message = "Giá sản phẩm không được để trống")
  @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0")
  private BigDecimal price;

  @Size(max = 500, message = "Đường dẫn hình ảnh không vượt quá 500 ký tự")
  private String imageUrl;

  @Min(value = 0, message = "Số lượng tồn kho không được nhỏ hơn 0")
  private int stockQuantity;
}
