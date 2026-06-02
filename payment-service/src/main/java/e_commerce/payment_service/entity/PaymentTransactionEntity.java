package e_commerce.payment_service.entity;

import e_commerce.payment_service.enums.TransactionStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionEntity {

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "order_id", nullable = false, unique = true)
  private UUID orderId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "payment_method", nullable = false)
  private String paymentMethod;

  @Column(name = "gateway_transaction_id")
  private String gatewayTransactionId;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private TransactionStatus status;

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
