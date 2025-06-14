package com.example.library.service;

import com.example.library.dto.response.*;
import com.example.library.exception.*;
import com.example.library.model.*;
import com.example.library.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import java.time.*;


// Сервис для работы с книгами (поиск, аренда, возврат)
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // Получить все книги с пагинацией
    public Page<BookResponse> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable)
                .map(this::mapToBookResponse);
    }

    // Найти книгу по ID
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Книга не найдена с ID: " + id));
        return mapToBookResponse(book);
    }

    // Взять книгу в аренду
    @Transactional
    public BookResponse borrowBook(Long bookId, String username) {
        log.info("Пользователь {} хочет взять книгу {}", username, bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Книга не найдена с ID: " + bookId));

        if (!book.isAvailable()) {
            throw new IllegalStateException("Книга " + bookId + " уже арендована");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + username));

        // Обновляем статус книги
        book.setAvailable(false);
        book.setBorrowedBy(user);
        book.setBorrowDate(LocalDate.now());

        Book savedBook = bookRepository.save(book);
        log.info("Книга {} взята пользователем {}", bookId, username);
        return mapToBookResponse(savedBook);
    }

    // Вернуть книгу
    @Transactional
    public BookResponse returnBook(Long bookId, String username) {
        log.info("Пользователь {} хочет вернуть книгу {}", username, bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Книга не найдена с ID: " + bookId));

        if (book.isAvailable()) {
            throw new IllegalStateException("Книга " + bookId + " не была арендована");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + username));

        // Проверяем, что книгу возвращает тот же пользователь
        if (!book.getBorrowedBy().getId().equals(user.getId())) {
            throw new IllegalStateException("Нельзя вернуть чужую книгу");
        }

        // Обновляем статус книги
        book.setAvailable(true);
        book.setBorrowedBy(null);
        book.setBorrowDate(null);
        book.setReturnDate(LocalDate.now());

        Book savedBook = bookRepository.save(book);
        log.info("Книга {} возвращена пользователем {}", bookId, username);
        return mapToBookResponse(savedBook);
    }

    // Преобразование Book в BookResponse
    private BookResponse mapToBookResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setPublicationYear(book.getPublicationYear());
        response.setAvailable(book.isAvailable());
        response.setBorrowDate(book.getBorrowDate());
        response.setReturnDate(book.getReturnDate());

        // Если книга арендована - добавляем информацию о пользователе
        if (book.getBorrowedBy() != null) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(book.getBorrowedBy().getId());
            userResponse.setUsername(book.getBorrowedBy().getUsername());
            userResponse.setEmail(book.getBorrowedBy().getEmail());
            userResponse.setRole(book.getBorrowedBy().getRole().name());
            response.setBorrowedBy(userResponse);
        }

        return response;
    }
}
