package com.example.library.controller;

import com.example.library.dto.response.BookResponse;
import com.example.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// Контроллер для работы с книгами (просмотр, аренда, возврат)
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    // Получение списка книг с пагинацией
    // GET /api/books/public?page=0&size=10
    @GetMapping("/public")
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size));
    }

    // Получение информации о конкретной книге
    // GET /api/books/public/1
    @GetMapping("/public/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // Аренда книги (только для пользователей с ролью READER)
    // POST /api/books/1/borrow
    @PostMapping("/{id}/borrow")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<BookResponse> borrowBook(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookService.borrowBook(id, userDetails.getUsername()));
    }

    // Возврат книги (только для пользователей с ролью READER)
    // POST /api/books/1/return
    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<BookResponse> returnBook(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookService.returnBook(id, userDetails.getUsername()));
    }
}
