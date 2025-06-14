package com.example.library.dto.response;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookResponseTest {

    @Test
    void shouldCreateBookResponseWithAllFields() {
        // Создаем тестового пользователя, который будет указан как взявший книгу
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testUser");

        // Создаем и заполняем объект BookResponse всеми полями
        BookResponse response = new BookResponse();
        response.setId(1L);  // Устанавливаем ID книги
        response.setTitle("Test Book");  // Название книги
        response.setAuthor("Test Author");  // Автор книги
        response.setIsbn("978-3-16-148410-0");  // ISBN в корректном формате
        response.setPublicationYear(2023);  // Год публикации
        response.setAvailable(false);  // Книга не доступна (взята)
        response.setBorrowDate(LocalDate.now().minusDays(5));  // Дата взятия (5 дней назад)
        response.setReturnDate(LocalDate.now());  // Дата возврата (сегодня)
        response.setBorrowedBy(userResponse);  // Кто взял книгу

        // Проверяем что все поля установлены корректно:
        // - Проверка идентификатора
        assertEquals(1L, response.getId());
        // - Проверка метаданных книги
        assertEquals("Test Book", response.getTitle());
        assertEquals("Test Author", response.getAuthor());
        assertEquals("978-3-16-148410-0", response.getIsbn());
        assertEquals(2023, response.getPublicationYear());
        // - Проверка статуса доступности
        assertFalse(response.isAvailable());
        // - Проверка дат операций
        assertNotNull(response.getBorrowDate());
        assertNotNull(response.getReturnDate());
        // - Проверка информации о пользователе
        assertEquals(1L, response.getBorrowedBy().getId());
        assertEquals("testUser", response.getBorrowedBy().getUsername());
    }

    @Test
    void shouldCreateEmptyBookResponse() {
        // Создаем пустой объект без установки каких-либо значений
        BookResponse response = new BookResponse();

        // Проверяем что все поля null (значения по умолчанию):
        assertNull(response.getId());
        assertNull(response.getTitle());
        assertNull(response.getAuthor());
        assertNull(response.getIsbn());
        assertNull(response.getPublicationYear());
        assertNull(response.getBorrowDate());
        assertNull(response.getReturnDate());
        assertNull(response.getBorrowedBy());
    }
}