package e_commerce.catalog_service.commands.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import e_commerce.catalog_service.commands.entities.OutboxEventEntity;
import e_commerce.catalog_service.commands.repositories.OutboxEventRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;

  @Transactional(propagation = Propagation.MANDATORY)
  public void insertOutboxEvent(
      String eventType, String aggregateType, UUID aggregateId, Object payloadObj) {
    String payload = null;
    if (payloadObj != null) {
      try {
        payload = objectMapper.writeValueAsString(payloadObj);
      } catch (Exception e) {
        // Log or handle JSON serialization exception
      }
    }

    OutboxEventEntity outboxEvent =
        OutboxEventEntity.builder()
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(eventType)
            .payload(payload)
            .build();

    outboxEventRepository.save(outboxEvent);
  }
}
