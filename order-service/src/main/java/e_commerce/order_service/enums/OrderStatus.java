package e_commerce.order_service.enums;

public enum OrderStatus {
  PENDING, // Phục vụ saga
  PROCESSING,
  COMPLETED,
  CANCELLING, // Phục vụ saga
  CANCELLED,
  FAILED // Phục vụ saga
}
