package e_commerce.auth_service.entity;

import e_commerce.auth_service.enums.UserStatus;
import jakarta.persistence.*;
import java.util.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
public class UserEntity extends BaseEntity implements UserDetails {

  @Id private UUID id;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roles = new HashSet<>();

  // ------------------------------------------------------------
  // CÁC HÀM BẮT BUỘC TỪ INTERFACE USERDETAILS
  // ------------------------------------------------------------

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return this.passwordHash; // Trả về trường mật khẩu băm của bạn
  }

  @Override
  public String getUsername() {
    return this.username; // Trả về tên đăng nhập
  }
}
