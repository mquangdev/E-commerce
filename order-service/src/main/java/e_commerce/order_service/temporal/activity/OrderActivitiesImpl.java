package e_commerce.order_service.temporal.activity;

import e_commerce.order_service.entity.OrderEntity;
import e_commerce.order_service.enums.OrderStatus;
import e_commerce.order_service.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderActivitiesImpl implements OrderActivities {
  private final OrderRepository orderRepository;

  @Override
  @Transactional
  public void updateStatus(UUID orderId, String status) {
    log.info("[ORDER SERVICE] Đang cập nhật trạng thái đơn hàng {} sang: {}", orderId, status);

    OrderEntity order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

    order.setStatus(OrderStatus.valueOf(status));

    orderRepository.save(order);

    log.info("[ORDER SERVICE] Cập nhật thành công đơn hàng {}", orderId);
  }

  @Override
  @Transactional
  public void cancelAndCleanupOrder(UUID orderId) {
    log.info("[ORDER SERVICE] Đang tiến hành hủy và dọn dẹp đơn hàng: {}", orderId);

    // 1. Tìm đơn hàng trong Database
    OrderEntity order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));

    // 2. Cập nhật trạng thái sang FAILED
    order.setStatus(OrderStatus.FAILED);

    // 3. Xóa danh sách sản phẩm khỏi Entity
    // Lệnh clear() kết hợp với orphanRemoval = true sẽ kích hoạt việc xóa ngầm dưới DB
    if (order.getItems() != null) {
      order.getItems().clear();
    }

    orderRepository.save(order);
    log.info(
        "✅ [ORDER SERVICE] Đã chuyển trạng thái đơn hàng {} sang FAILED và dọn dẹp các mục hàng thành công",
        orderId);
  }
}
