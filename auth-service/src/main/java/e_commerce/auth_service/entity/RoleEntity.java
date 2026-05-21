package e_commerce.auth_service.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE roles SET is_deleted = true WHERE id = ?")
public class RoleEntity extends BaseEntity {

  @Id private UUID id;

  @Column(nullable = false, length = 50)
  private String name;

  @Column(length = 255)
  private String description;
}
