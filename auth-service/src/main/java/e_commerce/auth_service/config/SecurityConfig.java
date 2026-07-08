package e_commerce.auth_service.config;

import e_commerce.auth_service.filters.JwtAuthenticationFilter;
import e_commerce.common_shared.filter.GatewaySecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final GatewaySecurityFilter gatewaySecurityFilter;

  public SecurityConfig(
      JwtAuthenticationFilter jwtAuthFilter, GatewaySecurityFilter gatewaySecurityFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.gatewaySecurityFilter = gatewaySecurityFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/graphiql/**",
                        "/graphql/**",
                        "/api/v1/login",
                        "/api/v1/register",
                        "/api/v1/refresh")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(gatewaySecurityFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(jwtAuthFilter, GatewaySecurityFilter.class);
    return http.build();
  }

  // 2. Định nghĩa chi tiết tờ giấy phép CORS
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // BẮT BUỘC ghi đích danh domain của Frontend, tuyệt đối không dùng "*"
    configuration.setAllowedOrigins(List.of("http://localhost:3000"));

    // Cho phép các phương thức HTTP cơ bản
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // Cho phép Frontend gửi lên bất kỳ Header nào
    configuration.setAllowedHeaders(List.of("*"));

    // 🛡️ CHÌA KHÓA VÀNG: Ra lệnh cho trình duyệt cho phép lưu và gửi Cookie
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // Áp dụng giấy phép này cho toàn bộ các endpoint trong ứng dụng
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
