package com.example.library.config;

import com.example.library.model.Book;
import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Конфигурационный класс для начальной загрузки тестовых данных
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    // Инициализация тестовых данных при запуске приложения
    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      BookRepository bookRepository) {
        return args -> {
            // Создание тестового администратора
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@library.com")
                    .role(Role.ROLE_ADMIN)
                    .build();

            // Создание тестового читателя
            User reader = User.builder()
                    .username("reader")
                    .password(passwordEncoder.encode("reader123"))
                    .email("reader@library.com")
                    .role(Role.ROLE_READER)
                    .build();

            userRepository.save(admin);
            userRepository.save(reader);

            // Создание тестовых книг
            Book book1 = Book.builder()
                    .title("Война и мир")
                    .author("Лев Толстой")
                    .isbn("978-5-389-06256-6")
                    .publicationYear(1869)
                    .available(true)
                    .build();

            Book book2 = Book.builder()
                    .title("Преступление и наказание")
                    .author("Федор Достоевский")
                    .isbn("978-5-17-090539-2")
                    .publicationYear(1866)
                    .available(true)
                    .build();

            bookRepository.save(book1);
            bookRepository.save(book2);
        };
    }
}