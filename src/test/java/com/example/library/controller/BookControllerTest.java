package com.example.library.controller;

import com.example.library.dto.response.BookResponse;
import com.example.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Интеграция Mockito с JUnit 5
class BookControllerTest {

    @Mock // Создаем mock-объект сервиса для работы с книгами
    private BookService bookService;

    @InjectMocks // Внедряем mock-зависимости в тестируемый контроллер
    private BookController bookController;

    @Test
    void getAllBooks_ShouldReturnPageOfBooks() {
        // Подготовка тестовых данных:
        // - Параметры пагинации (страница 0, размер 10)
        // - Пустая страница с книгами в качестве mock-ответа
        int page = 0;
        int size = 10;
        Page<BookResponse> mockPage = new PageImpl<>(Collections.emptyList());

        // Настройка поведения mock-сервиса
        when(bookService.getAllBooks(page, size)).thenReturn(mockPage);

        // Вызов тестируемого метода
        ResponseEntity<Page<BookResponse>> response = bookController.getAllBooks(page, size);

        // Проверки:
        // 1. Статус ответа должен быть OK (200)
        // 2. Тело ответа должно содержать ожидаемую страницу с книгами
        // 3. Должен быть вызван метод сервиса с правильными параметрами пагинации
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPage, response.getBody());
        verify(bookService).getAllBooks(page, size);
    }

    @Test
    void getBookById_ShouldReturnBook() {
        // Подготовка тестовых данных:
        // - ID книги для поиска
        // - Mock-объект книги в качестве ожидаемого ответа
        Long bookId = 1L;
        BookResponse mockResponse = new BookResponse();

        // Настройка поведения mock-сервиса
        when(bookService.getBookById(bookId)).thenReturn(mockResponse);

        // Вызов тестируемого метода
        ResponseEntity<BookResponse> response = bookController.getBookById(bookId);

        // Проверки:
        // 1. Статус ответа должен быть OK (200)
        // 2. Тело ответа должно содержать ожидаемую книгу
        // 3. Должен быть вызван метод сервиса с правильным ID книги
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(bookService).getBookById(bookId);
    }

    @Test
    void borrowBook_ShouldReturnBorrowedBook() {
        // Подготовка тестовых данных:
        // - ID книги для аренды
        // - Данные аутентифицированного пользователя
        // - Mock-объект книги в качестве ответа
        Long bookId = 1L;
        String username = "testuser";
        UserDetails userDetails = User.withUsername(username)
                .password("password")
                .authorities("ROLE_READER")
                .build();

        BookResponse mockResponse = new BookResponse();

        // Настройка поведения mock-сервиса
        when(bookService.borrowBook(bookId, username)).thenReturn(mockResponse);

        // Вызов тестируемого метода с передачей данных пользователя
        ResponseEntity<BookResponse> response = bookController.borrowBook(bookId, userDetails);

        // Проверки:
        // 1. Статус ответа должен быть OK (200)
        // 2. Тело ответа должно содержать данные о взятой книге
        // 3. Должен быть вызван метод сервиса с правильными параметрами
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(bookService).borrowBook(bookId, username);
    }

    @Test
    void returnBook_ShouldReturnReturnedBook() {
        // Подготовка тестовых данных:
        // - ID книги для возврата
        // - Данные аутентифицированного пользователя
        // - Mock-объект книги в качестве ответа
        Long bookId = 1L;
        String username = "testuser";
        UserDetails userDetails = User.withUsername(username)
                .password("password")
                .authorities("ROLE_READER")
                .build();

        BookResponse mockResponse = new BookResponse();

        // Настройка поведения mock-сервиса
        when(bookService.returnBook(bookId, username)).thenReturn(mockResponse);

        // Вызов тестируемого метода с передачей данных пользователя
        ResponseEntity<BookResponse> response = bookController.returnBook(bookId, userDetails);

        // Проверки:
        // 1. Статус ответа должен быть OK (200)
        // 2. Тело ответа должно содержать данные о возвращенной книге
        // 3. Должен быть вызван метод сервиса с правильными параметрами
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(bookService).returnBook(bookId, username);
    }
}