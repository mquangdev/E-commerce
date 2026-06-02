package e_commerce.order_service.temporal.worker;

import e_commerce.order_service.temporal.activity.OrderActivities;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderWorkerRunner {

  private final WorkerFactory workerFactory;
  private final OrderActivities orderActivitiesImpl;

  @Value("${temporal.task-queue:ORDER_TASK_QUEUE}")
  private String taskQueue;

  @PostConstruct
  public void startWorker() {
    Worker worker = workerFactory.newWorker(taskQueue);
    worker.registerActivitiesImplementations(orderActivitiesImpl);
    workerFactory.start();
    System.out.println("🚀 [ORDER WORKER] Đã kết nối Temporal và sẵn sàng nhận nhiệm vụ!");
  }
}
