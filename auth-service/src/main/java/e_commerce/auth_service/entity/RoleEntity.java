package e_commerce.auth_service.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {

  @Id private UUID id;

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Column(length = 255)
  private String description;
}
