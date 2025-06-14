package com.example.library.dto.response;

import lombok.*;
import java.util.List;

// Данные пользователя для API ответов
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;            // ID пользователя в системе
    private String username;    // Логин пользователя
    private String email;       // Email адрес
    private String role;        // Роль (ADMIN, READER и т.д.)

    // Список книг, которые пользователь сейчас арендовал
    private List<BookResponse> borrowedBooks;
}
