package e_commerce.order_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import e_commerce.order_service.dto.*;
import e_commerce.order_service.entity.*;
import e_commerce.order_service.enums.OrderStatus;
import e_commerce.order_service.repository.OrderRepository;
import e_commerce.order_service.service.impl.OrderServiceImpl;
import e_commerce.order_service.temporal.workflow.CreateOrderSagaWorkflow;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private e_commerce.order_service.repository.OrderItemRepository orderItemRepository;

  @Mock private io.temporal.client.WorkflowClient workflowClient;

  @Mock private CreateOrderSagaWorkflow createOrderSagaWorkflow;

  @InjectMocks private OrderServiceImpl orderService;

  private UUID userId;
  private UUID orderId;
  private UUID productId1;
  private UUID productId2;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    orderId = UUID.randomUUID();
    productId1 = UUID.randomUUID();
    productId2 = UUID.randomUUID();
  }

  @Test
  void createOrder_CalculatesTotalAmountAndPersistsOrder() {
    OrderItemRequest item1 =
        OrderItemRequest.builder()
            .productId(productId1)
            .quantity(2)
            .unitPrice(BigDecimal.valueOf(10.00))
            .build();

    OrderItemRequest item2 =
        OrderItemRequest.builder()
            .productId(productId2)
            .quantity(3)
            .unitPrice(BigDecimal.valueOf(25.00))
            .build();

    OrderRequest request =
        OrderRequest.builder()
            .userId(userId)
            .shippingAddress("123 Main St")
            .email("test@example.com")
            .items(List.of(item1, item2))
            .build();

    when(orderRepository.save(any(OrderEntity.class)))
        .thenAnswer(
            invocation -> {
              OrderEntity entity = invocation.getArgument(0);
              entity.setId(orderId);
              // Mock setting IDs on items
              for (int i = 0; i < entity.getItems().size(); i++) {
                entity.getItems().get(i).setId(UUID.randomUUID());
              }
              return entity;
            });

    when(workflowClient.newWorkflowStub(
            eq(CreateOrderSagaWorkflow.class), any(io.temporal.client.WorkflowOptions.class)))
        .thenReturn(createOrderSagaWorkflow);

    OrderResponse response = orderService.createOrder(request);

    assertNotNull(response);
    assertEquals(orderId, response.getId());
    assertEquals(userId, response.getUserId());
    assertEquals(OrderStatus.PENDING, response.getStatus());
    assertEquals("123 Main St", response.getShippingAddress());
    assertEquals("test@example.com", response.getEmail());

    // Total should be: (10.00 * 2) + (25.00 * 3) = 20.00 + 75.00 = 95.00
    assertEquals(BigDecimal.valueOf(95.00), response.getTotalAmount());
    assertEquals(2, response.getItems().size());

    verify(orderRepository, times(1)).save(any(OrderEntity.class));
  }

  @Test
  void getOrderById_WhenOrderExists_ReturnsOrderResponse() {
    OrderEntity order =
        OrderEntity.builder()
            .id(orderId)
            .userId(userId)
            .shippingAddress("123 Main St")
            .email("test@example.com")
            .totalAmount(BigDecimal.valueOf(95.00))
            .status(OrderStatus.PENDING)
            .build();

    order.setItems(
        List.of(
            OrderItemEntity.builder()
                .id(UUID.randomUUID())
                .order(order)
                .productId(productId1)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(10.00))
                .build()));

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    OrderResponse response = orderService.getOrderById(orderId);

    assertNotNull(response);
    assertEquals(orderId, response.getId());
    assertEquals(1, response.getItems().size());
    assertEquals("test@example.com", response.getEmail());
    verify(orderRepository, times(1)).findById(orderId);
  }

  @Test
  void updateOrderStatus_WhenOrderExists_UpdatesAndReturnsResponse() {
    OrderEntity order =
        OrderEntity.builder()
            .id(orderId)
            .userId(userId)
            .shippingAddress("123 Main St")
            .email("test@example.com")
            .totalAmount(BigDecimal.valueOf(95.00))
            .status(OrderStatus.PENDING)
            .build();

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderRepository.save(any(OrderEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    OrderResponse response = orderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);

    assertNotNull(response);
    assertEquals(OrderStatus.COMPLETED, response.getStatus());
    verify(orderRepository, times(1)).findById(orderId);
    verify(orderRepository, times(1)).save(order);
  }
}
