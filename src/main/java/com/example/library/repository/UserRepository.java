package com.example.library.repository;

import com.example.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Репозиторий для работы с пользователями в базе данных
public interface UserRepository extends JpaRepository<User, Long> {

    // Найти пользователя по логину (возвращает Optional)
    Optional<User> findByUsername(String username);

    // Проверить существование пользователя с указанным логином
    Boolean existsByUsername(String username);

    // Проверить существование пользователя с указанным email
    Boolean existsByEmail(String email);
}
