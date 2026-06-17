package e_commerce.orchestrator.temporal.workflows.order;

import e_commerce.orchestrator.dto.OrderUpdateDTO;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CancelOrderSagaWorkflow {
  @WorkflowMethod
  void cancelOrderSaga(OrderUpdateDTO order);
}
