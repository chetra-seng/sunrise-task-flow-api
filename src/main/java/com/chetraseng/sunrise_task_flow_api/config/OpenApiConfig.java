package com.chetraseng.sunrise_task_flow_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI taskFlowApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Flow API")
                        .description("REST API for managing tasks, projects, and users")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Sunrise Team")
                                .email("team@sunrise.dev")));
    }
}
