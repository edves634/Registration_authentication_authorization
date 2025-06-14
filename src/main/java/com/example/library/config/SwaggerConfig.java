package com.example.library.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Настройка документации API с использованием Swagger/OpenAPI
@Configuration
public class SwaggerConfig {

    // Конфигурация OpenAPI для Swagger документации
    @Bean
    public OpenAPI customOpenAPI() {
        // Базовые метаданные API
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Library API")
                        .version("1.0")
                        .description("API for Library Management System"));

        // Настройка JWT аутентификации в Swagger
        openAPI
                // Добавляем требование безопасности для всех endpoints
                .addSecurityItem(new SecurityRequirement().addList("JWT"))

                // Конфигурация схемы аутентификации
                .components(new Components()
                        .addSecuritySchemes("JWT",
                                new SecurityScheme()
                                        .name("JWT")
                                        .type(SecurityScheme.Type.HTTP)  // Тип HTTP
                                        .scheme("bearer")               // Схема bearer token
                                        .bearerFormat("JWT")            // Формат JWT
                        ));

        return openAPI;
    }
}
