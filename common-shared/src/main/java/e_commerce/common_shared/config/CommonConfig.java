package e_commerce.common_shared.config;

import e_commerce.common_shared.exception.GlobalExceptionHandler;
import e_commerce.common_shared.filter.GatewaySecurityFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
  @Bean
  @ConditionalOnProperty(
      name = "app.security.gateway-filter.enabled",
      havingValue = "true",
      matchIfMissing = false)
  public GatewaySecurityFilter gatewaySecurityFilter() {
    return new GatewaySecurityFilter();
  }

  @Bean
  public GlobalExceptionHandler globalExceptionHandler() {
    return new GlobalExceptionHandler();
  }
}
