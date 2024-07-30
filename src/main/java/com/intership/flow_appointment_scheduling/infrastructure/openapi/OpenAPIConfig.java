package com.intership.flow_appointment_scheduling.infrastructure.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public-api")
        .pathsToMatch("/api/**")
        .build();
  }

  @Bean
  OpenAPI springDocOpenApi() {
    return new OpenAPI()
        .info(new Info().title("Flow Appointment Scheduling API")
            .description("API for Flow Appointment Scheduling")
            .version("v0.0.1-SNAPSHOT"));
  }
}
