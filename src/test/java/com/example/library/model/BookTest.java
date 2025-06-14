package com.example.library.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    // Тест проверяет корректность работы билдера класса Book
    @Test
    void testBookBuilder() {
        // Создаем тестового пользователя через билдер
        User user = User.builder()
                .id(1L)
                .username("testUser")
                .build();

        // Создаем книгу через билдер с заполнением всех полей
        Book book = Book.builder()
                .id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0-13-468599-1")
                .publicationYear(2018)
                .available(false)  // Книга взята в аренду
                .borrowDate(LocalDate.now())  // Дата взятия - сегодня
                .returnDate(LocalDate.now().plusWeeks(2))  // Вернуть через 2 недели
                .borrowedBy(user)  // Книгу взял наш тестовый пользователь
                .build();

        // Проверяем, что все поля заполнены корректно
        assertEquals(1L, book.getId());
        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals("978-0-13-468599-1", book.getIsbn());
        assertEquals(2018, book.getPublicationYear());
        assertFalse(book.isAvailable());  // Проверяем, что книга недоступна
        assertEquals(user, book.getBorrowedBy());  // Проверяем пользователя, взявшего книгу
    }

    // Тест проверяет работу конструктора без аргументов
    @Test
    void testNoArgsConstructor() {
        Book book = new Book();  // Создаем пустую книгу

        // Проверяем, что все поля null, кроме available (должно быть true по умолчанию)
        assertNull(book.getId());
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertNull(book.getIsbn());
        assertTrue(book.isAvailable());  // Проверка значения по умолчанию
        assertNull(book.getBorrowedBy());
    }

    // Тест проверяет работу конструктора со всеми аргументами
    @Test
    void testAllArgsConstructor() {
        // Создаем тестового пользователя
        User user = new User();
        user.setId(1L);

        // Создаем книгу через конструктор со всеми параметрами
        Book book = new Book(
                1L,
                "Clean Code",
                "Robert Martin",
                "978-0-13-235088-4",
                true,  // Книга доступна
                2008,  // Год публикации
                null,  // Дата взятия не указана
                null,  // Дата возврата не указана
                user   // Связь с пользователем
        );

        // Проверяем основные поля
        assertEquals(1L, book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertTrue(book.isAvailable());  // Книга должна быть доступна
        assertEquals(user, book.getBorrowedBy());  // Проверяем связь с пользователем
    }

    // Тест проверяет значение поля available по умолчанию
    @Test
    void testDefaultAvailableValue() {
        // Создаем книгу через билдер без указания available
        Book book = Book.builder().build();

        // Проверяем, что по умолчанию книга доступна (true)
        assertTrue(book.isAvailable());
    }
}