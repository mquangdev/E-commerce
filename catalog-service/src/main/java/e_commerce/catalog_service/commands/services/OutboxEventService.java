package e_commerce.catalog_service.commands.services;

import e_commerce.catalog_service.commands.dtos.request.OutboxEventRequest;

import java.util.List;
import java.util.UUID;

public interface OutboxEventService {
  void insertOutboxEvent(OutboxEventRequest request);

  void batchInsertOutboxEvents(List<OutboxEventRequest> listRequest);
}
