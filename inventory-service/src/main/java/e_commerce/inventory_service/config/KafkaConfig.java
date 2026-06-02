package e_commerce.inventory_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

  @Bean
  public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {
    // Nhặt tin nhắn lỗi ném sang đuôi .DLT
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

    // Thử lại tối đa 3 lần, mỗi lần cách nhau 1 giây
    FixedBackOff backOff = new FixedBackOff(1000L, 3);

    return new DefaultErrorHandler(recoverer, backOff);
  }
}
