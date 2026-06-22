package e_commerce.order_service.temporal.activity;

import e_commerce.common_shared.dtos.SendEmailCommand;
import e_commerce.order_service.dto.OrderDTO;
import e_commerce.order_service.entity.OrderEntity;
import e_commerce.order_service.enums.OrderStatus;
import e_commerce.order_service.repository.OrderRepository;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderActivitiesImpl implements OrderActivities {
  private final OrderRepository orderRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;

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

  @Override
  public void sendOrderCreatedEmail(OrderDTO order) {
    System.out.println(
        "Chuẩn bị gửi lệnh bắn email từ OrderActivities cho đơn: " + order.getOrderId());

    // 1. Khởi tạo dữ liệu params
    Map<String, Object> params = new HashMap<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
    DecimalFormat currencyFormat = new DecimalFormat("#,###");

    params.put("customerName", "Khách hàng " + order.getCustomerFullName());
    params.put("orderId", order.getOrderId().toString().toUpperCase());
    params.put("createdAt", LocalDateTime.now().format(dateFormatter));
    params.put("shippingAddress", order.getShippingAddress());
    params.put("totalAmount", currencyFormat.format(order.getAmount()) + " đ");

    List<Map<String, Object>> itemsList = new ArrayList<>();
    for (var item : order.getOrderItems()) {
      Map<String, Object> itemData = new HashMap<>();
      itemData.put("productName", "Sản phẩm " + item.getProductName());
      itemData.put("imageUrl", "https://via.placeholder.com/50");
      itemData.put("quantity", item.getQuantity());
      itemData.put("unitPrice", currencyFormat.format(item.getUnitPrice()) + " đ");
      itemsList.add(itemData);
    }
    params.put("items", itemsList);

    // 2. Đóng gói Command
    SendEmailCommand command =
        SendEmailCommand.builder()
            .to(order.getEmail())
            .subject(
                "Đơn hàng #"
                    + order.getOrderId().toString().substring(0, 8)
                    + " đã đặt thành công!")
            .templateCode("order-created-template")
            .templateParams(params)
            .build();

    // 3. Bắn lên Kafka
    kafkaTemplate.send("send-email-commands", command);
  }
}
