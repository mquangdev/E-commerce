package e_commerce.search_service.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CategoryEventListener {
  @KafkaListener(topics = "ecommerce.public.outbox_events", groupId = "search-service-group")
  public void handleCategoryEvent(String message) {
    System.out.println("Đã nhận tin nhắn: " + message);
    // Code bóc tách JSON sẽ nằm ở đây
  }
}
