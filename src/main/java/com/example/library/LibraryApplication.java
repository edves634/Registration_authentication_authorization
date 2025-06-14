package com.example.library;

import org.slf4j.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.*;

// Главный класс приложения библиотеки
@SpringBootApplication
public class LibraryApplication {
    private static final Logger log = LoggerFactory.getLogger(LibraryApplication.class);

    public static void main(String[] args) {
        // Настройка и запуск Spring Boot приложения
        SpringApplication app = new SpringApplication(LibraryApplication.class);
        app.setBannerMode(Banner.Mode.CONSOLE); // Показывать баннер в консоли

        // Запуск контекста приложения
        ConfigurableApplicationContext context = app.run(args);
        log.info("Приложение успешно запущено!");

        // Временное решение для тестирования -
        // предотвращает немедленное завершение работы приложения
        try {
            Thread.sleep(Long.MAX_VALUE); // Бесконечное ожидание
        } catch (InterruptedException e) {
            log.error("Приложение было прервано", e);
        }
    }
}
