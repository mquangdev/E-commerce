package e_commerce.orchestrator.temporal.workflows;

import e_commerce.orchestrator.dto.OrderCreateDTO;
import e_commerce.orchestrator.dto.OrderUpdateDTO;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderSagaWorkflow {
  @WorkflowMethod
  void createOrderSaga(OrderCreateDTO order);

  @WorkflowMethod
  void cancelOrderSaga(OrderUpdateDTO order);
}
