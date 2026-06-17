package e_commerce.order_service.temporal.workflow;

import e_commerce.order_service.dto.OrderDTO;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CreateOrderSagaWorkflow {
  @WorkflowMethod
  void createOrderSaga(OrderDTO order);
}
