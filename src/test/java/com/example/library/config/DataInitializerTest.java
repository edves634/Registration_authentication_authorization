package com.example.library.config;

import com.example.library.model.Book;
import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Аннотация для интеграции Mockito с JUnit 5
class DataInitializerTest {

    @Mock // Создаем mock-объект для PasswordEncoder
    private PasswordEncoder passwordEncoder;

    @Mock // Создаем mock-объект для UserRepository
    private UserRepository userRepository;

    @Mock // Создаем mock-объект для BookRepository
    private BookRepository bookRepository;

    @InjectMocks // Внедряем mock-объекты в тестируемый класс DataInitializer
    private DataInitializer dataInitializer;

    @Test
    void initData_ShouldCreateAdminAndReaderUsers() throws Exception {
        // Arrange (Подготовка)
        // Настраиваем mock PasswordEncoder возвращать "encodedPassword" для любого входного пароля
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        // Act (Действие)
        // Вызываем метод инициализации данных
        dataInitializer.initData(userRepository, bookRepository).run(null);

        // Assert (Проверка)
        // Проверяем, что метод save был вызван 2 раза (для admin и reader)
        verify(userRepository, times(2)).save(any(User.class));

        // Проверяем, что был сохранен пользователь admin с правильными атрибутами
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("admin") &&
                        user.getEmail().equals("admin@library.com") &&
                        user.getRole() == Role.ROLE_ADMIN
        ));

        // Проверяем, что был сохранен пользователь reader с правильными атрибутами
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("reader") &&
                        user.getEmail().equals("reader@library.com") &&
                        user.getRole() == Role.ROLE_READER
        ));
    }

    @Test
    void initData_ShouldCreateTestBooks() throws Exception {
        // Arrange
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        // Act
        dataInitializer.initData(userRepository, bookRepository).run(null);

        // Assert
        // Проверяем, что метод save был вызван 2 раза (для двух книг)
        verify(bookRepository, times(2)).save(any(Book.class));

        // Проверяем атрибуты первой тестовой книги ("Война и мир")
        verify(bookRepository).save(argThat(book ->
                book.getTitle().equals("Война и мир") &&
                        book.getAuthor().equals("Лев Толстой") &&
                        book.getIsbn().equals("978-5-389-06256-6") &&
                        book.getPublicationYear() == 1869 &&
                        book.isAvailable()
        ));

        // Проверяем атрибуты второй тестовой книги ("Преступление и наказание")
        verify(bookRepository).save(argThat(book ->
                book.getTitle().equals("Преступление и наказание") &&
                        book.getAuthor().equals("Федор Достоевский") &&
                        book.getIsbn().equals("978-5-17-090539-2") &&
                        book.getPublicationYear() == 1866 &&
                        book.isAvailable()
        ));
    }

    @Test
    void initData_ShouldEncodePasswords() throws Exception {
        // Arrange
        // Настраиваем mock PasswordEncoder возвращать разные закодированные пароли
        when(passwordEncoder.encode("admin123")).thenReturn("encodedAdminPass");
        when(passwordEncoder.encode("reader123")).thenReturn("encodedReaderPass");

        // Act
        dataInitializer.initData(userRepository, bookRepository).run(null);

        // Assert
        // Проверяем, что encode был вызван с правильными паролями
        verify(passwordEncoder).encode("admin123");
        verify(passwordEncoder).encode("reader123");

        // Проверяем, что пароли были правильно закодированы и сохранены
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("admin") &&
                        user.getPassword().equals("encodedAdminPass")
        ));

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("reader") &&
                        user.getPassword().equals("encodedReaderPass")
        ));
    }
}