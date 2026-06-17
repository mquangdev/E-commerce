package e_commerce.order_service.temporal.activity;

import e_commerce.order_service.dto.OrderDTO;
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
  void sendOrderCreatedEmail(OrderDTO order);
}
