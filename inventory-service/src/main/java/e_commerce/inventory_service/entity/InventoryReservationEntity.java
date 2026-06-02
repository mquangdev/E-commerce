package e_commerce.inventory_service.entity;

import e_commerce.inventory_service.enums.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(
    name = "inventory_reservations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"inventory_id", "order_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservationEntity {

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_id", nullable = false)
  private InventoryEntity inventory;

  @Column(name = "order_id", nullable = false)
  private UUID orderId;

  @Column(name = "reserved_quantity", nullable = false)
  private int reservedQuantity;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    if (this.id == null) {
      this.id = UUID.randomUUID();
    }
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
