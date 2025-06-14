package com.example.library.repository;

import com.example.library.model.Book;
import com.example.library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Репозиторий для работы с книгами в базе данных
public interface BookRepository extends JpaRepository<Book, Long> {

    // Получить все книги с пагинацией
    Page<Book> findAll(Pageable pageable);

    // Найти все доступные книги (available = true)
    List<Book> findByAvailableTrue();

    // Найти книги, взятые конкретным пользователем
    List<Book> findByBorrowedBy(User user);

    // Поиск книг по автору (без учета регистра)
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Поиск книг по названию (без учета регистра)
    List<Book> findByTitleContainingIgnoreCase(String title);
}
