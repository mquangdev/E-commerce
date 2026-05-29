package e_commerce.search_service.listener;

import e_commerce.search_service.document.CategoryDocument;
import e_commerce.search_service.document.ProductDocument;
import e_commerce.search_service.repository.CategorySearchRepository;
import e_commerce.search_service.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CatalogEventListener {
  private final ObjectMapper objectMapper;
  private final CategorySearchRepository categorySearchRepository;
  private final ProductSearchRepository productSearchRepository;

  @KafkaListener(topics = "ecommerce.public.outbox_events", groupId = "search-service-group")
  public void handleCatalogEvents(String message) {
    try {
      JsonNode rootNode = objectMapper.readTree(message);
      JsonNode afterNode = rootNode.path("after");

      if (!afterNode.isMissingNode() && !afterNode.isNull()) {
        String aggregateType = afterNode.path("aggregate_type").asText();
        String eventType = afterNode.path("event_type").asText();
        String payloadString = afterNode.path("payload").asText();
        String aggregateId = afterNode.path("aggregate_id").asText();

        // Lọc: Chỉ xử lý nếu sự kiện thuộc về bảng Categories
        if ("categories".equals(aggregateType)) {
          switch (eventType) {
            case "CREATE":
            case "UPDATE":
              // Bước 2: Chỉ ép kiểu payload khi có dữ liệu (CREATE/UPDATE)
              CategoryDocument document =
                  objectMapper.readValue(payloadString, CategoryDocument.class);
              categorySearchRepository.save(document);

              String action = eventType.equals("CREATE") ? "tạo" : "cập nhật";
              System.out.println(
                  "Đã " + action + " thành công Category vào ES: " + document.getName());
              break;
            case "DELETE":
              // Bước 3: Xóa trực tiếp bằng aggregateId không cần đọc payload
              categorySearchRepository.deleteById(aggregateId);
              System.out.println("Đã xóa thành công Category khỏi ES ID: " + aggregateId);
              break;
            default:
              System.out.println("Không hỗ trợ loại sự kiện: " + eventType);
          }
        } else if ("products".equals(aggregateType)) {
          switch (eventType) {
            case "CREATE":
            case "UPDATE":
              ProductDocument document =
                  objectMapper.readValue(payloadString, ProductDocument.class);
              productSearchRepository.save(document);

              String action = eventType.equals("CREATE") ? "tạo" : "cập nhật";
              System.out.println(
                  "Đã " + action + " thành công Product vào ES: " + document.getName());
              break;
            case "DELETE":
              productSearchRepository.deleteById(aggregateId);
              System.out.println("Đã xóa thành công Product khỏi ES ID: " + aggregateId);
              break;
            default:
              System.out.println("Không hỗ trợ loại sự kiện: " + eventType);
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Lỗi phân tích cú pháp tin nhắn: " + e.getMessage());
    }
  }
}
