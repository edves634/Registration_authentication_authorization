package com.example.library.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;

import static org.mockito.Mockito.*;

class WebConfigTest {

    @Test
    void addCorsMappings_ShouldConfigureCorsCorrectly() {
        // Подготовка мок-объектов:
        // - CorsRegistry - основной объект для регистрации CORS настроек в Spring
        // - CorsRegistration - объект для настройки отдельных параметров CORS
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);

        // Настройка поведения мок-объектов:
        // - При вызове addMapping("/**") возвращаем registration
        when(registry.addMapping("/**")).thenReturn(registration);

        // - Настраиваем цепочку fluent-вызовов для CorsRegistration:
        //   Каждый метод конфигурации возвращает тот же объект registration
        when(registration.allowedOrigins("http://localhost:3000")).thenReturn(registration);
        when(registration.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")).thenReturn(registration);
        when(registration.allowedHeaders("*")).thenReturn(registration);
        when(registration.allowCredentials(true)).thenReturn(registration);

        // Выполнение:
        // Создаем экземпляр WebConfig и вызываем тестируемый метод
        new WebConfig().addCorsMappings(registry);

        // Проверки (верификации):
        // - Проверяем, что был вызван addMapping для всех путей
        verify(registry).addMapping("/**");

        // - Проверяем параметры CORS-конфигурации:
        //   Разрешенный origin (источник)
        verify(registration).allowedOrigins("http://localhost:3000");

        //   Разрешенные HTTP-методы
        verify(registration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");

        //   Разрешенные заголовки
        verify(registration).allowedHeaders("*");

        //   Разрешение передачи учетных данных (куки, аутентификация)
        verify(registration).allowCredentials(true);
    }
}