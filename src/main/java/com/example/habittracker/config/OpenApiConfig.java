package com.example.habittracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI habitTrackerOpenAPI() {
        return new OpenAPI()
        .info(new Info()
        .title("Habit Tracker API")
        .description("REST API for tracking habits, daily check-ins, and habit statistics")
        .version("v1.0.0"));
    }
}