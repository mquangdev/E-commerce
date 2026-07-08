package e_commerce.search_service.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(indexName = "categories")
@Data
public class CategoryDocument {
  @Id private String id; // UUID từ Postgres luôn được chuyển thành String trong ES

  @Field(type = FieldType.Text)
  private String name;

  @Field(type = FieldType.Text)
  private String description;

  @Field(type = FieldType.Boolean)
  private boolean isActive;

  // --- Các trường phẳng hóa từ BaseEntity ---
  private boolean isDeleted;

  @Field(type = FieldType.Date)
  private LocalDate createdAt;

  @Field(type = FieldType.Date)
  private LocalDate updatedAt;

  @Field(type = FieldType.Long)
  private Long version;
}
