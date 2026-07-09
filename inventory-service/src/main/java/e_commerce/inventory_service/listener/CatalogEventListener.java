package e_commerce.inventory_service.listener;

import e_commerce.common_shared.exception.BusinessException;
import e_commerce.inventory_service.entity.InventoryEntity;
import e_commerce.inventory_service.repository.InventoryRepository;

import java.util.Optional;
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
        String payloadString = afterNode.path("payload").asText();
        int stockQuantity = 0;
        try {
          JsonNode payloadNode = objectMapper.readTree(payloadString);
          stockQuantity = payloadNode.path("stockQuantity").asInt();
        } catch (Exception e) {
          System.err.println("Lỗi khi parse payload JSON: " + e.getMessage());
        }

        if ("products".equals(aggregateType)) {
          UUID productId = UUID.fromString(aggregateId);

          switch (eventType) {
            case "CREATE":
              if (!inventoryRepository.existsByProductId(productId)) {
                InventoryEntity inventory = InventoryEntity.builder()
                    .productId(productId)
                    .availableQuantity(stockQuantity)
                    .reservedQuantity(0)
                    .build();
                inventoryRepository.save(inventory);
                System.out.println("Đã khởi tạo tồn kho thành công cho sản phẩm ID: " + productId);
              } else {
                System.out.println("Tồn kho cho sản phẩm ID: " + productId + " đã tồn tại, bỏ qua khởi tạo.");
              }
              break;
//            case "UPDATE":
//              InventoryEntity inventory = inventoryRepository.findByProductId(productId).orElseThrow(() -> new BusinessException("Không có thông tin kho cho sản phẩm ID: " + productId));
//              inventory.setAvailableQuantity(stockQuantity);
//              inventoryRepository.save(inventory);
//              System.out.println("Đã cập nhật tồn kho thành công cho sản phẩm ID: " + productId);
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
