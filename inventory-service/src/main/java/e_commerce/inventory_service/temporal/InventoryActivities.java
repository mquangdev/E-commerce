package e_commerce.inventory_service.temporal;

import e_commerce.inventory_service.dto.OrderItemDTO;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;
import java.util.UUID;

@ActivityInterface
public interface InventoryActivities {
  @ActivityMethod
  void reserveStock(UUID orderId, List<OrderItemDTO> items);

  @ActivityMethod
  void compensateStock(UUID orderId, List<OrderItemDTO> items);
}
