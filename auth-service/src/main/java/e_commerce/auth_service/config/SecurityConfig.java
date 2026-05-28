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
                        "/api/v1/register")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(gatewaySecurityFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(jwtAuthFilter, GatewaySecurityFilter.class);
    return http.build();
  }
}
