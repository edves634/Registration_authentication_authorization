package com.example.library.service;


import com.example.library.dto.response.BookResponse;
import com.example.library.exception.BookNotFoundException;
import com.example.library.model.Book;
import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repository.BookRepository;
import com.example.library.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Подключаем поддержку Mockito для тестов
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    // Мок репозитория книг
    @Mock
    private BookRepository bookRepository;

    // Мок репозитория пользователей
    @Mock
    private UserRepository userRepository;

    // Тестируемый сервис с внедренными моками
    @InjectMocks
    private BookService bookService;

    // Тест получения всех книг с пагинацией
    @Test
    void getAllBooks_ShouldReturnPageOfBooks() {
        // Подготовка тестовых данных
        Pageable pageable = PageRequest.of(0, 10);
        Book book = createTestBook(1L, "Test Book", true);
        Page<Book> page = new PageImpl<>(Collections.singletonList(book), pageable, 1);

        // Настройка поведения мока
        when(bookRepository.findAll(pageable)).thenReturn(page);

        // Вызов тестируемого метода
        Page<BookResponse> result = bookService.getAllBooks(0, 10);

        // Проверки результатов
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
        verify(bookRepository).findAll(pageable);
    }

    // Тест получения книги по ID (успешный случай)
    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() {
        // Подготовка тестовых данных
        Long bookId = 1L;
        Book book = createTestBook(bookId, "Test Book", true);

        // Настройка поведения мока
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Вызов тестируемого метода
        BookResponse result = bookService.getBookById(bookId);

        // Проверки результатов
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).findById(bookId);
    }

    // Тест получения книги по ID (книга не найдена)
    @Test
    void getBookById_ShouldThrowException_WhenBookNotFound() {
        // Подготовка тестовых данных
        Long bookId = 1L;

        // Настройка поведения мока
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Проверка выбрасывания исключения
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(bookId));
        verify(bookRepository).findById(bookId);
    }

    // Тест взятия книги в аренду (успешный случай)
    @Test
    @Transactional
    void borrowBook_ShouldBorrowBook_WhenBookIsAvailable() {
        // Подготовка тестовых данных
        Long bookId = 1L;
        String username = "testuser";
        Book book = createTestBook(bookId, "Test Book", true);
        User user = createTestUser(1L, username);

        // Настройка поведения моков
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Вызов тестируемого метода
        BookResponse result = bookService.borrowBook(bookId, username);

        // Проверки результатов
        assertNotNull(result);
        assertFalse(result.isAvailable());
        verify(bookRepository).findById(bookId);
        verify(userRepository).findByUsername(username);
        verify(bookRepository).save(book);
    }

    // Тест взятия книги в аренду (книга недоступна)
    @Test
    @Transactional
    void borrowBook_ShouldThrowException_WhenBookIsNotAvailable() {
        // Подготовка тестовых данных
        Long bookId = 1L;
        String username = "testuser";
        Book book = createTestBook(bookId, "Test Book", false);

        // Настройка поведения мока
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Проверка выбрасывания исключения
        assertThrows(IllegalStateException.class, () -> bookService.borrowBook(bookId, username));
        verify(bookRepository).findById(bookId);
        verify(userRepository, never()).findByUsername(anyString());
    }

    // Тест возврата книги (успешный случай)
    @Test
    @Transactional
    void returnBook_ShouldReturnBook_WhenBookIsBorrowedByUser() {
        // Подготовка тестовых данных
        Long bookId = 1L;
        String username = "testuser";
        User user = createTestUser(1L, username);
        Book book = createTestBook(bookId, "Test Book", false);
        book.setBorrowedBy(user);

        // Настройка поведения моков
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Вызов тестируемого метода
        BookResponse result = bookService.returnBook(bookId, username);

        // Проверки результатов
        assertNotNull(result);
        assertTrue(result.isAvailable());
        verify(bookRepository).findById(bookId);
        verify(userRepository).findByUsername(username);
        verify(bookRepository).save(book);
    }

    // Тест возврата книги (книга не была взята в аренду)
    @Test
    @Transactional
    void returnBook_ShouldThrowException_WhenBookIsNotBorrowed() {
        // Подготовка тестовых данных
        Long bookId = 1L;
        String username = "testuser";
        Book book = createTestBook(bookId, "Test Book", true);

        // Настройка поведения мока
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Проверка выбрасывания исключения
        assertThrows(IllegalStateException.class, () -> bookService.returnBook(bookId, username));
        verify(bookRepository).findById(bookId);
        verify(userRepository, never()).findByUsername(anyString());
    }

    // Тест возврата книги (книга взята другим пользователем)
    @Test
    @Transactional
    void returnBook_ShouldThrowException_WhenBookIsBorrowedByAnotherUser() {
        // Подготовка тестовых данных
        Long bookId = 1L;
        String username = "testuser";
        User user = createTestUser(1L, "anotheruser");
        Book book = createTestBook(bookId, "Test Book", false);
        book.setBorrowedBy(user);

        // Настройка поведения моков
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(createTestUser(2L, username)));

        // Проверка выбрасывания исключения
        assertThrows(IllegalStateException.class, () -> bookService.returnBook(bookId, username));
        verify(bookRepository).findById(bookId);
        verify(userRepository).findByUsername(username);
    }

    // Вспомогательный метод для создания тестовой книги
    private Book createTestBook(Long id, String title, boolean available) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor("Test Author");
        book.setIsbn("1234567890");
        book.setPublicationYear(2023);
        book.setAvailable(available);
        return book;
    }

    // Вспомогательный метод для создания тестового пользователя
    private User createTestUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setRole(Role.ROLE_READER);
        return user;
    }
}