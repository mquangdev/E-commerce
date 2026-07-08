package e_commerce.catalog_service.commands.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = false;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDate createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDate updatedAt;

  @Column(name = "version", nullable = false)
  @Version
  private Long version;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDate.now();
    this.updatedAt = LocalDate.now();
    this.isDeleted = false;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDate.now();
  }
}
