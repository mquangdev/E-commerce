package e_commerce.catalog_service.commands.services;

import java.util.UUID;

public interface OutboxEventService {
  void insertOutboxEvent(
      String eventType, String aggregateType, UUID aggregateId, Object payloadObj);
}
