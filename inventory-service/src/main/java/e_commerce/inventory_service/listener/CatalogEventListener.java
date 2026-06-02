package e_commerce.inventory_service.listener;

import e_commerce.inventory_service.entity.InventoryEntity;
import e_commerce.inventory_service.repository.InventoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CatalogEventListener {

  private final ObjectMapper objectMapper;
  private final InventoryRepository inventoryRepository;

  @KafkaListener(topics = "ecommerce.public.outbox_events", groupId = "inventory-service-group")
  public void handleCatalogEvents(String message) {
    try {
      JsonNode rootNode = objectMapper.readTree(message);
      JsonNode afterNode = rootNode.path("after");

      if (!afterNode.isMissingNode() && !afterNode.isNull()) {
        String aggregateType = afterNode.path("aggregate_type").asText();
        String eventType = afterNode.path("event_type").asText();
        String aggregateId = afterNode.path("aggregate_id").asText();

        if ("products".equals(aggregateType)) {
          switch (eventType) {
            case "CREATE":
              UUID productId = UUID.fromString(aggregateId);
              if (!inventoryRepository.existsByProductId(productId)) {
                InventoryEntity inventory = InventoryEntity.builder()
                    .productId(productId)
                    .availableQuantity(0)
                    .reservedQuantity(0)
                    .build();
                inventoryRepository.save(inventory);
                System.out.println("Đã khởi tạo tồn kho bằng 0 thành công cho sản phẩm ID: " + productId);
              } else {
                System.out.println("Tồn kho cho sản phẩm ID: " + productId + " đã tồn tại, bỏ qua khởi tạo.");
              }
              break;
            default:
              break;
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Lỗi đồng bộ Kafka đến Inventory Service: " + e.getMessage());
      throw new RuntimeException("Lỗi đồng bộ Kafka Inventory", e);
    }
  }
}
