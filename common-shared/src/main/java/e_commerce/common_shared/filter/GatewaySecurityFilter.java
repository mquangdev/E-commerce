package e_commerce.common_shared.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

public class GatewaySecurityFilter extends OncePerRequestFilter {
  @Value("${app.security.internal-secret}")
  private String internalSecret;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 1. Lấy mã bí mật từ Header do Gateway gửi xuống
    String incomingSecret = request.getHeader("X-Gateway-Secret");

    // 2. Kiểm tra xem có khớp với mã cấu hình trong application.yml không
    if (incomingSecret == null || !incomingSecret.equals(internalSecret)) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.getWriter().write("Forbidden: Direct access is not allowed!");
      return;
    }

    // 3. Nếu hợp lệ, cho phép request tiếp tục đi vào các bộ lọc sau và đến Controller
    filterChain.doFilter(request, response);
  }
}
