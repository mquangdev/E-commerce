package e_commerce.order_service.enums;

public enum OrderStatus {
  PENDING, // Phục vụ saga
  PROCESSING,
  COMPLETED,
  CANCELLED,
  FAILED // Phục vụ saga
}
