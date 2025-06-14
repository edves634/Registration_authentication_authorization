package com.example.library.dto.response;

import lombok.Data;
import java.time.LocalDate;

// Информация о книге для ответов API
@Data
public class BookResponse {
    private Long id;               // Уникальный идентификатор книги
    private String title;          // Название книги
    private String author;         // Автор книги
    private String isbn;           // ISBN номер
    private Integer publicationYear; // Год публикации

    private boolean available;     // Доступна ли книга для аренды
    private LocalDate borrowDate;  // Дата взятия книги (если арендована)
    private LocalDate returnDate;  // Дата возврата книги (если арендована)
    private UserResponse borrowedBy; // Информация о пользователе, взявшем книгу
}
