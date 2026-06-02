package e_commerce.orchestrator.temporal.workflows;

import e_commerce.orchestrator.dto.OrderDTO;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderSagaWorkflow {
  @WorkflowMethod
  void createOrderSaga(OrderDTO order);
}
