package e_commerce.orchestrator.temporal.activities;

import e_commerce.orchestrator.dto.OrderCreateDTO;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.UUID;

@ActivityInterface
public interface OrderActivities {
  @ActivityMethod
  void updateStatus(UUID orderId, String status);

  @ActivityMethod
  void cancelAndCleanupOrder(UUID orderId);

  @ActivityMethod
  void sendOrderCreatedEmail(OrderCreateDTO order);
}
