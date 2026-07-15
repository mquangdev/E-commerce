package e_commerce.catalog_service.commands.dtos.request;

import lombok.AllArgsConstructor;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OutboxEventRequest {
    private String eventType;
    private String aggregateType;
    private UUID aggregateId;
    private Object payloadObj;
}
