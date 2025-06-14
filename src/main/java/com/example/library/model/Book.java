package com.example.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

// Сущность книги в системе
@Entity
@Table(name = "books") // Соответствует таблице books в БД
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоинкрементный ID
    private Long id;

    // Название книги (обязательное, макс. 100 символов)
    @Column(nullable = false, length = 100)
    private String title;

    // Автор книги (обязательное, макс. 100 символов)
    @Column(nullable = false, length = 100)
    private String author;

    // Уникальный ISBN номер (обязательное, макс. 20 символов)
    @Column(nullable = false, length = 20, unique = true)
    private String isbn;

    // Доступность книги (по умолчанию true)
    @Column(nullable = false)
    @Builder.Default
    private boolean available = true;

    // Год публикации (хранится в БД как publication_year)
    @Column(name = "publication_year")
    private Integer publicationYear;

    // Дата взятия книги (хранится в БД как borrow_date)
    @Column(name = "borrow_date")
    private LocalDate borrowDate;

    // Дата возврата книги (хранится в БД как return_date)
    @Column(name = "return_date")
    private LocalDate returnDate;

    // Пользователь, взявший книгу (ленивая загрузка)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User borrowedBy;
}
