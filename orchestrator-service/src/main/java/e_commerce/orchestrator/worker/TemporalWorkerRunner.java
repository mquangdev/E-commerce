package e_commerce.orchestrator.worker;

import e_commerce.orchestrator.temporal.workflows.order.CancelOrderSagaWorkflowImpl;
import e_commerce.orchestrator.temporal.workflows.order.CreateOrderSagaWorkflowImpl;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TemporalWorkerRunner {
  private final WorkerFactory workerFactory;
  private final String taskQueue;

  // Tiêm (Inject) WorkerFactory từ file Config ở trên vào
  public TemporalWorkerRunner(
      WorkerFactory workerFactory, @Value("${temporal.task-queue}") String taskQueue) {
    this.workerFactory = workerFactory;
    this.taskQueue = taskQueue;
  }

  @PostConstruct
  public void startWorker() {
    // 1. Tạo một Worker gắn với Task Queue chỉ định
    Worker worker = workerFactory.newWorker(taskQueue);

    // 2. Đăng ký các Workflow thực thi (Nhạc trưởng chỉ cần đăng ký Workflow, KHÔNG đăng ký
    // Activity)
    worker.registerWorkflowImplementationTypes(CreateOrderSagaWorkflowImpl.class);
    worker.registerWorkflowImplementationTypes(CancelOrderSagaWorkflowImpl.class);

    // 3. Khởi động Worker chạy ngầm
    workerFactory.start();
    System.out.println(
        "🚀 Orchestrator Worker đã kết nối tới Temporal và bắt đầu lắng nghe Task Queue: "
            + taskQueue);
  }
}
