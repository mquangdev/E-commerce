package e_commerce.order_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI orderServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Order Service API")
                .description("E-commerce Order Management Service")
                .version("v0.0.1")
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }
}
