package e_commerce.search_service.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;

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
  @Field(type = FieldType.Boolean)
  private boolean isDeleted;

  @Field(type = FieldType.Date)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = Shape.STRING)
  private LocalDateTime createdAt;

  @Field(type = FieldType.Date)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = Shape.STRING)
  private LocalDateTime updatedAt;
}
