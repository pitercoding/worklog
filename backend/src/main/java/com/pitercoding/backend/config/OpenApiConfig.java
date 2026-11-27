package com.pitercoding.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI worklogOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Worklog API")
                .description("API to register daily activities with automatic time tracking. "
                        + "Demo project: there is no authentication and the sample data is fictional.")
                .version("v1")
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
    }
}
