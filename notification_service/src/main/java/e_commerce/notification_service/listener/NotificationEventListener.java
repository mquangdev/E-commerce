package e_commerce.notification_service.listener;

import e_commerce.common_shared.dtos.SendEmailCommand;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.context.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventListener {

  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine; // Của Thymeleaf

  @KafkaListener(topics = "send-email-commands", groupId = "notification-service-group")
  public void handleNotificationEvents(SendEmailCommand command) {
    try {
      // 1. Đổ dữ liệu từ Map vào Context của Thymeleaf
      Context thymeleafContext = new Context();
      thymeleafContext.setVariables(command.getTemplateParams());

      // 2. Render ra chuỗi HTML dựa trên templateCode
      // Nó sẽ tự tìm file "order-created-template.html" và nhét biến vào
      String htmlBody = templateEngine.process(command.getTemplateCode(), thymeleafContext);

      // 3. Gửi email
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(command.getTo());
      helper.setSubject(command.getSubject());
      helper.setText(htmlBody, true); // true = Bật chế độ HTML

      mailSender.send(message);
      System.out.println("✅ Đã gửi email bằng template: " + command.getTemplateCode());

    } catch (Exception e) {
      System.err.println("❌ Lỗi gửi mail: " + e.getMessage());
    }
  }
}
