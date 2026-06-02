package e_commerce.inventory_service.temporal.worker;

import e_commerce.inventory_service.temporal.InventoryActivities;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryWorkerRunner {

  private final WorkerFactory workerFactory;
  private final InventoryActivities inventoryActivitiesImpl;

  @Value("${temporal.task-queue:INVENTORY_TASK_QUEUE}")
  private String taskQueue;

  @PostConstruct
  public void startWorker() {
    Worker worker = workerFactory.newWorker(taskQueue);
    worker.registerActivitiesImplementations(inventoryActivitiesImpl);
    workerFactory.start();
    System.out.println("🚀 [INVENTORY WORKER] Đã kết nối Temporal và sẵn sàng nhận nhiệm vụ!");
  }
}
