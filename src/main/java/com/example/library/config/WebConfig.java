package com.example.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Настройка CORS политики для приложения
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Конфигурация CORS (Cross-Origin Resource Sharing)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                // Применяем правила ко всем endpoint'ам
                .allowedOrigins("http://localhost:3000")  // Разрешаем доступ только с localhost:3000
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Разрешенные HTTP методы
                .allowedHeaders("*")              // Разрешаем все заголовки
                .allowCredentials(true);          // Разрешаем передачу учетных данных (куки, auth)

        // Для production среды рекомендуется:
        // 1. Заменить allowedOrigins на конкретные домены
        // 2. Ограничить список allowedMethods только необходимыми
        // 3. Указать конкретные allowedHeaders вместо "*"
    }
}
