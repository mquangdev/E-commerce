package e_commerce.catalog_service.commands.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import e_commerce.catalog_service.commands.dtos.request.OutboxEventRequest;
import e_commerce.catalog_service.commands.entities.OutboxEventEntity;
import e_commerce.catalog_service.commands.repositories.OutboxEventRepository;
import e_commerce.catalog_service.commands.services.OutboxEventService;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxEventServiceImpl implements OutboxEventService {

  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional(propagation = Propagation.MANDATORY)
  public void insertOutboxEvent(OutboxEventRequest request) {
    String payload = null;
    if (request.getPayloadObj() != null) {
      try {
        payload = objectMapper.writeValueAsString(request.getPayloadObj());
      } catch (Exception e) {
        // Log or handle JSON serialization exception
      }
    }

    OutboxEventEntity outboxEvent =
        OutboxEventEntity.builder()
            .aggregateType(request.getAggregateType())
            .aggregateId(request.getAggregateId())
            .eventType(request.getEventType())
            .payload(payload)
            .build();

    outboxEventRepository.save(outboxEvent);
  }

  @Override
  @Transactional(propagation = Propagation.MANDATORY)
  public void batchInsertOutboxEvents(List<OutboxEventRequest> listRequest) {
    if (listRequest == null || listRequest.isEmpty()) {
      return;
    }
    List<OutboxEventEntity> entities = listRequest.stream().map(request -> {
      String payload = null;
      if (request.getPayloadObj() != null) {
        try {
          payload = objectMapper.writeValueAsString(request.getPayloadObj());
        } catch (Exception e) {
          // Log or handle JSON serialization exception
        }
      }
      return OutboxEventEntity.builder()
          .aggregateType(request.getAggregateType())
          .aggregateId(request.getAggregateId())
          .eventType(request.getEventType())
          .payload(payload)
          .build();
    }).toList();

    outboxEventRepository.saveAll(entities);
  }
}
