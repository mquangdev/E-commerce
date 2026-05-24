package e_commerce.api_gateway.filters;

import e_commerce.api_gateway.services.JwtService;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthGatewayFilterFactory
    extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {

  private final JwtService jwtService;

  public JwtAuthGatewayFilterFactory(JwtService jwtService) {
    super(Config.class);
    this.jwtService = jwtService;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      var request = exchange.getRequest();

      // 2. Kiểm tra Header Authorization có tồn tại và đúng chuẩn "Bearer " không
      if (!request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
        return onError(exchange, HttpStatus.UNAUTHORIZED);
      }

      String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return onError(exchange, HttpStatus.UNAUTHORIZED);
      }

      String token = authHeader.substring(7);

      try {
        // 3. Giải mã token để lấy userId và roles
        String userId =
            jwtService.extractClaim(token, claims -> claims.get("userId", String.class));

        // Trích xuất roles (Vì roles lưu dạng List<Map>, ta ép về String để nhét vào Header)
        String roles =
            jwtService.extractClaim(
                token,
                claims -> {
                  Object rolesObj = claims.get("roles");
                  return rolesObj != null ? rolesObj.toString() : "";
                });

        // 4. TẠO BẢN SAO (mutate) của Request và nhét Header vào
        var modifiedRequest =
            request.mutate().header("X-User-Id", userId).header("X-User-Roles", roles).build();

        // 5. TẠO BẢN SAO của Exchange chứa chiếc Request mới
        var modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        // 6. Cho phép request đi tiếp xuống các service nội bộ
        return chain.filter(modifiedExchange);

      } catch (Exception e) {
        // Bắt các lỗi từ jjwt như: Token hết hạn (ExpiredJwtException), sai chữ ký...
        return onError(exchange, HttpStatus.UNAUTHORIZED);
      }
    };
  }

  // Hàm phụ trợ để ngắt luồng và trả về lỗi 401 trong môi trường WebFlux (Bất đồng bộ)
  private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
    var response = exchange.getResponse();
    response.setStatusCode(httpStatus);
    return response.setComplete(); // Kết thúc request ngay tại đây
  }

  // Class Config dùng để nhận tham số từ file yml (nếu cần)
  public static class Config {
    // Trống vì chúng ta không cần truyền tham số gì vào filter này
  }
}
