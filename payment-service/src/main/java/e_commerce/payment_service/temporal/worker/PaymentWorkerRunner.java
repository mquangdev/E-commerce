package e_commerce.payment_service.temporal.worker;

import e_commerce.payment_service.temporal.PaymentActivities;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentWorkerRunner {

  private final WorkerFactory workerFactory;
  private final PaymentActivities paymentActivitiesImpl;

  @Value("${temporal.task-queue:PAYMENT_TASK_QUEUE}")
  private String taskQueue;

  @PostConstruct
  public void startWorker() {
    Worker worker = workerFactory.newWorker(taskQueue);
    worker.registerActivitiesImplementations(paymentActivitiesImpl);
    workerFactory.start();
    System.out.println("🚀 [PAYMENT WORKER] Đã kết nối Temporal và sẵn sàng nhận nhiệm vụ!");
  }
}
