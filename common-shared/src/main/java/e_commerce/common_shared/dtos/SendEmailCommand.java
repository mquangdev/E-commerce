package e_commerce.common_shared.dtos;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailCommand {
  private String to; // Email người nhận
  private String subject; // Tiêu đề email
  private String templateCode; // Tên file template HTML (VD: "ORDER_CREATED", "RESET_PASSWORD")

  private Map<String, Object> templateParams;
}
