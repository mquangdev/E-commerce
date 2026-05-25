package e_commerce.catalog_service.commands.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "products")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity extends BaseEntity {
  @Id
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  // Thiết lập khóa ngoại liên kết với bảng categories
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  @Column(name = "sku", nullable = false, unique = true, length = 50)
  private String sku;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(name = "image_url", length = 500)
  private String imageUrl;

  @Column(name = "stock_quantity", nullable = false)
  @Builder.Default
  private int stockQuantity = 0;
}
