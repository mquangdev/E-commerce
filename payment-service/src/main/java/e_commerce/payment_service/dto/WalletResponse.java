package e_commerce.payment_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
  private UUID id;
  private UUID userId;
  private BigDecimal balance;
  private long version;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
