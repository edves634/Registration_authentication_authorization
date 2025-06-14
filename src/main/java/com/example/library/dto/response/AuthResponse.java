package com.example.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Ответ с данными аутентификации пользователя
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    // JWT токен для авторизации
    private String token;

    // Имя пользователя
    private String username;

    // Роль пользователя (например: ADMIN, READER)
    private String role;
}
