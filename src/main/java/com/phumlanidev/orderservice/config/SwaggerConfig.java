package com.phumlanidev.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Comment: this is the placeholder for documentation.
 */
@Configuration
public class SwaggerConfig {

  /**
   * Comment: this is the placeholder for documentation.
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Infinity Tech Product Service API")
            .version("1.0.0")
            .description("API documentation for Infinity Tech Product Service"));
  }

  /**
   * Comment: this is the placeholder for documentation.
   */
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("products")
        .pathsToMatch("/api/products/**")
        .build();
  }
}
