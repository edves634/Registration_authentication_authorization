package com.example.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Аннотация для интеграционного теста с поднятием Spring контекста
class SwaggerConfigTest {

    @Autowired // Внедряем тестируемый бин SwaggerConfig
    private SwaggerConfig swaggerConfig;

    @Test
    void customOpenAPI_ShouldReturnOpenAPIInstance() {
        // When - вызываем тестируемый метод
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then - проверяем что возвращается не-null объект OpenAPI
        assertNotNull(openAPI);
    }

    @Test
    void customOpenAPI_ShouldContainCorrectInfo() {
        // When - получаем OpenAPI и информацию из него
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        Info info = openAPI.getInfo();

        // Then - проверяем что информация соответствует ожиданиям:
        // - Заголовок API
        // - Версия API
        // - Описание API
        assertNotNull(info);
        assertEquals("Library API", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertEquals("API for Library Management System", info.getDescription());
    }

    @Test
    void customOpenAPI_ShouldHaveJWTSecurityRequirement() {
        // When - получаем конфигурацию OpenAPI
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then - проверяем требования безопасности:
        // - Список требований не пуст
        // - Первое требование содержит JWT
        assertFalse(openAPI.getSecurity().isEmpty());
        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertTrue(securityRequirement.containsKey("JWT"));
    }

    @Test
    void customOpenAPI_ShouldHaveJWTSecurityScheme() {
        // When - получаем схему безопасности из OpenAPI
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("JWT");

        // Then - проверяем параметры схемы:
        // - Название схемы
        // - Тип (HTTP)
        // - Схема (bearer)
        // - Формат (JWT)
        assertNotNull(securityScheme);
        assertEquals("JWT", securityScheme.getName());
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
    }
}