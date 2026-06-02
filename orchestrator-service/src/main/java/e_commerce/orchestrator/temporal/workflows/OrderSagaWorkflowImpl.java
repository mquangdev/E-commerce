package e_commerce.orchestrator.temporal.workflows;

import e_commerce.orchestrator.dto.OrderDTO;
import e_commerce.orchestrator.temporal.activities.InventoryActivities;
import e_commerce.orchestrator.temporal.activities.OrderActivities;
import e_commerce.orchestrator.temporal.activities.PaymentActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class OrderSagaWorkflowImpl implements OrderSagaWorkflow {
  // 1. Cấu hình Luật thử lại (Retry Rules) và Thời gian chờ (Timeout) cho các Activity
  private final ActivityOptions orderOptions =
      ActivityOptions.newBuilder()
          // Thời gian tối đa cho một lần thực thi Activity (nếu quá sẽ coi như timeout)
          .setStartToCloseTimeout(Duration.ofMinutes(2))
          .setTaskQueue("ORDER_TASK_QUEUE")
          // Cấu hình Exponential Backoff tự động của Temporal
          .setRetryOptions(
              RetryOptions.newBuilder()
                  .setInitialInterval(Duration.ofSeconds(2)) // Thử lại sau 2s ở lần đầu tiên
                  .setBackoffCoefficient(2.0) // Nhân đôi thời gian chờ ở lần sau (2s -> 4s -> 8s)
                  .setMaximumAttempts(3) // Thử lại tối đa 3 lần cho lỗi hạ tầng
                  .build())
          .build();

  private final ActivityOptions inventoryOptions =
      ActivityOptions.newBuilder()
          // Thời gian tối đa cho một lần thực thi Activity (nếu quá sẽ coi như timeout)
          .setStartToCloseTimeout(Duration.ofMinutes(2))
          .setTaskQueue("INVENTORY_TASK_QUEUE")
          // Cấu hình Exponential Backoff tự động của Temporal
          .setRetryOptions(
              RetryOptions.newBuilder()
                  .setInitialInterval(Duration.ofSeconds(2)) // Thử lại sau 2s ở lần đầu tiên
                  .setBackoffCoefficient(2.0) // Nhân đôi thời gian chờ ở lần sau (2s -> 4s -> 8s)
                  .setMaximumAttempts(3) // Thử lại tối đa 3 lần cho lỗi hạ tầng
                  .build())
          .build();

  private final ActivityOptions paymentOptions =
      ActivityOptions.newBuilder()
          // Thời gian tối đa cho một lần thực thi Activity (nếu quá sẽ coi như timeout)
          .setStartToCloseTimeout(Duration.ofMinutes(2))
          .setTaskQueue("PAYMENT_TASK_QUEUE")
          // Cấu hình Exponential Backoff tự động của Temporal
          .setRetryOptions(
              RetryOptions.newBuilder()
                  .setInitialInterval(Duration.ofSeconds(2)) // Thử lại sau 2s ở lần đầu tiên
                  .setBackoffCoefficient(2.0) // Nhân đôi thời gian chờ ở lần sau (2s -> 4s -> 8s)
                  .setMaximumAttempts(3) // Thử lại tối đa 3 lần cho lỗi hạ tầng
                  .build())
          .build();

  // 2. Khởi tạo các kết nối ảo (Stubs) tới các service nghiệp vụ thông qua Temporal Server
  // Khi gọi các hàm qua stub này, Temporal sẽ tự đẩy nhiệm vụ vào Task Queue
  private final InventoryActivities inventoryActivities =
      Workflow.newActivityStub(InventoryActivities.class, inventoryOptions);

  private final PaymentActivities paymentActivities =
      Workflow.newActivityStub(PaymentActivities.class, paymentOptions);

  private final OrderActivities orderActivities =
      Workflow.newActivityStub(OrderActivities.class, orderOptions);

  @Override
  public void createOrderSaga(OrderDTO order) {
    // Khởi tạo bộ điều phối Saga của Temporal
    // setParallelCompensation(false): Các bước rollback sẽ chạy tuần tự theo thứ tự ngược lại
    // (LIFO)
    Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(false).build();
    Saga saga = new Saga(sagaOptions);

    try {
      // --- BƯỚC 1: Đổi trạng thái đơn hàng sang PENDING ---
      orderActivities.updateStatus(order.getOrderId(), "PENDING");
      saga.addCompensation(() -> orderActivities.cancelAndCleanupOrder(order.getOrderId()));

      // --- BƯỚC 2: Gọi Kho giữ hàng loạt sản phẩm  ---
      inventoryActivities.reserveStock(order.getOrderId(), order.getOrderItems());
      saga.addCompensation(
          () -> inventoryActivities.compensateStock(order.getOrderId(), order.getOrderItems()));

      // --- BƯỚC 3: Gọi Thanh toán trừ tiền ---
      paymentActivities.processPayment(order.getOrderId(), order.getUserId(), order.getAmount());
      saga.addCompensation(
          () ->
              paymentActivities.refundPayment(
                  order.getOrderId(), order.getUserId(), order.getAmount()));

      // --- BƯỚC 4: Chốt đơn thành công 🎉 ---
      orderActivities.updateStatus(order.getOrderId(), "COMPLETED");
    } catch (ActivityFailure e) {
      System.out.println(
          "🚨 Luồng Saga thất bại cho đơn: " + order.getOrderId() + ". Kích hoạt hoàn tác...");
      saga.compensate();
      throw e;
    }
  }
}
