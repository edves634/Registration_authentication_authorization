package com.example.library.dto.response;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class UserResponseTest {

    /**
     * Тест проверяет корректность работы билдера и геттеров класса UserResponse
     */
    @Test
    void testUserResponseBuilderAndGetters() {
        // Подготовка тестовых данных
        BookResponse book = new BookResponse();
        book.setId(1L);
        book.setTitle("Test Book");

        List<BookResponse> borrowedBooks = Collections.singletonList(book);

        // Создание объекта через builder
        UserResponse user = UserResponse.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .role("READER")
                .borrowedBooks(borrowedBooks)
                .build();

        // Проверки значений
        assertEquals(1L, user.getId(), "ID пользователя должно быть 1");
        assertEquals("testUser", user.getUsername(), "Имя пользователя не совпадает");
        assertEquals("test@example.com", user.getEmail(), "Email не совпадает");
        assertEquals("READER", user.getRole(), "Роль должна быть READER");

        // Проверка связанных книг
        assertNotNull(user.getBorrowedBooks(), "Список книг не должен быть null");
        assertEquals(1, user.getBorrowedBooks().size(), "Должна быть 1 книга");
        assertEquals("Test Book", user.getBorrowedBooks().get(0).getTitle(), "Название книги не совпадает");
    }

    /**
     * Тест проверяет работу конструктора без аргументов
     */
    @Test
    void testNoArgsConstructor() {
        UserResponse user = new UserResponse();

        assertNull(user.getId(), "ID должно быть null");
        assertNull(user.getUsername(), "Имя пользователя должно быть null");
        assertNull(user.getEmail(), "Email должно быть null");
        assertNull(user.getRole(), "Роль должна быть null");
        assertNull(user.getBorrowedBooks(), "Список книг должен быть null");
    }

    /**
     * Тест проверяет работу конструктора со всеми аргументами
     */
    @Test
    void testAllArgsConstructor() {
        // Подготовка тестовых данных
        BookResponse book = new BookResponse();
        book.setId(1L);
        book.setTitle("Test Book");

        List<BookResponse> borrowedBooks = Collections.singletonList(book);

        // Создание объекта через полный конструктор
        UserResponse user = new UserResponse(
                1L,
                "testUser",
                "test@example.com",
                "READER",
                borrowedBooks
        );

        // Проверки значений
        assertEquals(1L, user.getId(), "ID пользователя должно быть 1");
        assertEquals("testUser", user.getUsername(), "Имя пользователя не совпадает");
        assertEquals("test@example.com", user.getEmail(), "Email не совпадает");
        assertEquals("READER", user.getRole(), "Роль должна быть READER");

        // Проверка связанных книг
        assertNotNull(user.getBorrowedBooks(), "Список книг не должен быть null");
        assertEquals(1, user.getBorrowedBooks().size(), "Должна быть 1 книга");
        assertEquals("Test Book", user.getBorrowedBooks().get(0).getTitle(), "Название книги не совпадает");
    }
}