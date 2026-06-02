package e_commerce.payment_service.dto;

import e_commerce.payment_service.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
  private UUID id;
  private UUID orderId;
  private UUID userId;
  private BigDecimal amount;
  private String paymentMethod;
  private String gatewayTransactionId;
  private TransactionStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
