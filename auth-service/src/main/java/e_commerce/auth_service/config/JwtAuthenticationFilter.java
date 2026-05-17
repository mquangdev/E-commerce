package e_commerce.auth_service.config;

import e_commerce.auth_service.service.jwt.JwtService;
import e_commerce.auth_service.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor // Tự động inject JwtService và UserDetailsService qua Constructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserService userService; // Lớp tìm kiếm User trong DB

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    // 1. Kiểm tra cấu trúc Header Authorization
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. Cắt chuỗi để lấy JWT nguyên bản
    jwt = authHeader.substring(7);
    username = jwtService.extractUsername(jwt);

    // 3. Nếu có username và chưa được xác thực trong phiên này
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userService.loadUserByUsername(username);

      // 4. Nếu Token hợp lệ, tiến hành đưa vào SecurityContextHolder
      if (jwtService.isTokenValid(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 🚀 Đăng ký user vào Context hệ thống
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    // Cho phép request đi tiếp đến bộ lọc tiếp theo hoặc vào Controller
    filterChain.doFilter(request, response);
  }
}
