package e_commerce.order_service.service;

import e_commerce.common_shared.dtos.PageResponse;
import e_commerce.order_service.dto.*;
import e_commerce.order_service.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
  OrderResponse createOrder(OrderRequest request, String customerFullName);

  OrderResponse getOrderById(UUID orderId);

  OrderResponse updateOrderStatus(UUID orderId, OrderStatus status);

  PageResponse<OrderResponse> getOrders(String keyword, int page, int size);
}
