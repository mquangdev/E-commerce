package e_commerce.auth_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE refresh_tokens SET is_deleted = true WHERE id = ?")
public class RefreshTokenEntity extends BaseEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(nullable = false, unique = true)
  private UUID token;

  @Column(name = "device_id", nullable = false, length = 100)
  private String deviceId;

  @Column(name = "is_revoked", nullable = false)
  private boolean isRevoked = false;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;
}
