package e_commerce.order_service.controller;

import e_commerce.order_service.dto.*;
import e_commerce.order_service.enums.OrderStatus;
import e_commerce.order_service.service.OrderService;
import jakarta.validation.Valid;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(
      @Valid @RequestBody OrderRequest request,
      @RequestHeader(value = "X-User-FullName") String rawFullName) {
    String customerFullName = URLDecoder.decode(rawFullName, StandardCharsets.UTF_8);
    return ResponseEntity.ok(orderService.createOrder(request, customerFullName));
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {
    return ResponseEntity.ok(orderService.getOrderById(orderId));
  }

  @PutMapping("/{orderId}/status")
  public ResponseEntity<OrderResponse> updateOrderStatus(
      @PathVariable UUID orderId, @RequestParam OrderStatus status) {
    return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
  }
}
