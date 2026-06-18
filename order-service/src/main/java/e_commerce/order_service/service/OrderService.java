package e_commerce.order_service.service;

import e_commerce.order_service.dto.*;
import e_commerce.order_service.enums.OrderStatus;
import java.util.UUID;

public interface OrderService {
  OrderResponse createOrder(OrderRequest request, String customerFullName);

  OrderResponse getOrderById(UUID orderId);

  OrderResponse updateOrderStatus(UUID orderId, OrderStatus status);
}
