package e_commerce.notification_service.config;

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
    // 1. Recoverer: Nhặt tin nhắn lỗi ném sang đuôi .DLT
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

    // 2. BackOff: Cấu hình thử lại (Retry)
    // Thử lại tối đa 3 lần, mỗi lần cách nhau 1000 mili-giây (1 giây)
    FixedBackOff backOff = new FixedBackOff(1000L, 3);

    // 3. Kết hợp lại thành một Error Handler tổng
    return new DefaultErrorHandler(recoverer, backOff);
  }
}
