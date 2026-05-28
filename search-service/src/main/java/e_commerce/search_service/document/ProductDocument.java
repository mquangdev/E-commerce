package e_commerce.search_service.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "products")
@Data
public class ProductDocument {
  @Id private String id;

  // Đập phẳng quan hệ @ManyToOne: Chỉ lưu những gì Frontend cần hiển thị
  @Field(type = FieldType.Keyword)
  private String categoryId;

  @Field(type = FieldType.Text)
  private String categoryName;

  // Cần quyết định kiểu dữ liệu cho SKU
  @Field(type = FieldType.Keyword) // Hoặc Text?
  private String sku;

  @Field(type = FieldType.Text)
  private String name;

  @Field(type = FieldType.Text)
  private String description;

  @Field(type = FieldType.Double)
  private Double price; // ES không hỗ trợ gốc cho BigDecimal, thường dùng Double

  @Field(type = FieldType.Text)
  private String imageUrl;

  @Field(type = FieldType.Integer)
  private Integer stockQuantity;

  // --- Các trường phẳng hóa từ BaseEntity ---
  @Field(type = FieldType.Boolean)
  private boolean isDeleted;
}
