package e_commerce.orchestrator.temporal.workflows.order;

import e_commerce.orchestrator.dto.OrderCreateDTO;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CreateOrderSagaWorkflow {
  @WorkflowMethod
  void createOrderSaga(OrderCreateDTO order);
}
