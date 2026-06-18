package e_commerce.order_service.service.impl;

import e_commerce.common_shared.exception.ResourceNotFoundException;
import e_commerce.order_service.dto.*;
import e_commerce.order_service.entity.*;
import e_commerce.order_service.enums.OrderStatus;
import e_commerce.order_service.repository.*;
import e_commerce.order_service.service.OrderService;
import e_commerce.order_service.temporal.workflow.CreateOrderSagaWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final WorkflowClient workflowClient;

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  @Override
  @Transactional
  public OrderResponse createOrder(OrderRequest request, String customerFullName) {
    UUID orderId = UUID.randomUUID();
    // 1. Tạo và tính toán hóa đơn như cũ
    OrderEntity order =
        OrderEntity.builder()
            .id(orderId)
            .userId(request.getUserId())
            .email(request.getEmail())
            .shippingAddress(request.getShippingAddress())
            .status(OrderStatus.PENDING)
            .build();

    List<OrderItemEntity> orderItems =
        request.getItems().stream()
            .map(
                itemReq ->
                    OrderItemEntity.builder()
                        .order(order)
                        .productId(itemReq.getProductId())
                        .quantity(itemReq.getQuantity())
                        .unitPrice(itemReq.getUnitPrice())
                        .build())
            .collect(Collectors.toList());

    order.setItems(orderItems);

    BigDecimal totalAmount =
        orderItems.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    order.setTotalAmount(totalAmount);

    OrderEntity saved = orderRepository.save(order);

    // 2. Ánh xạ (Map) dữ liệu sang OrderDTO của Orchestrator 🔄
    List<OrderItemDTO> sagaItems =
        orderItems.stream()
            .map(
                item ->
                    new OrderItemDTO(
                        "san pham test",
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice()))
            .collect(Collectors.toList());

    OrderDTO orderDto =
        OrderDTO.builder()
            .orderId(orderId)
            .userId(request.getUserId())
            .amount(totalAmount)
            .email(request.getEmail())
            .customerName(customerFullName)
            .shippingAddress(request.getShippingAddress())
            .orderItems(sagaItems)
            .build();

    // 3. Cấu hình luồng chạy trên Temporal Server ⚙️
    WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setWorkflowId("SAGA-ORDER-" + orderId) // Định danh duy nhất trên Temporal Dashboard
            .setTaskQueue("SAGA_TASK_QUEUE") // Đẩy vào hàng đợi của Nhạc trưởng
            .build();

    // 4. Tạo kết nối ảo (Stub) và kích hoạt luồng bất đồng bộ (Async) 🚀
    CreateOrderSagaWorkflow workflow =
        workflowClient.newWorkflowStub(CreateOrderSagaWorkflow.class, options);

    // Sử dụng WorkflowClient.start giúp kích hoạt luồng chạy ngầm, API lập tức trả về phản hồi
    WorkflowClient.start(workflow::createOrderSaga, orderDto);

    return mapToOrderResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public OrderResponse getOrderById(UUID orderId) {
    OrderEntity order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + orderId));
    return mapToOrderResponse(order);
  }

  @Override
  @Transactional
  public OrderResponse updateOrderStatus(UUID orderId, OrderStatus status) {
    OrderEntity order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + orderId));

    order.setStatus(status);
    OrderEntity saved = orderRepository.save(order);
    return mapToOrderResponse(saved);
  }

  private OrderResponse mapToOrderResponse(OrderEntity entity) {
    List<OrderItemResponse> itemResponses =
        entity.getItems().stream()
            .map(
                item ->
                    OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
            .collect(Collectors.toList());

    return OrderResponse.builder()
        .id(entity.getId())
        .userId(entity.getUserId())
        .totalAmount(entity.getTotalAmount())
        .shippingAddress(entity.getShippingAddress())
        .email(entity.getEmail())
        .status(entity.getStatus())
        .version(entity.getVersion())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .items(itemResponses)
        .build();
  }
}
