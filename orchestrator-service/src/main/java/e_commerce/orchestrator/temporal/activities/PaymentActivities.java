package e_commerce.orchestrator.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.math.BigDecimal;
import java.util.UUID;

@ActivityInterface
public interface PaymentActivities {
  @ActivityMethod
  void processPayment(UUID orderId, UUID userId, BigDecimal amount);

  @ActivityMethod
  void refundPayment(UUID orderId, UUID userId, BigDecimal amount);
}
