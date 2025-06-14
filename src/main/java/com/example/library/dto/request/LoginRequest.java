package com.example.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO для запроса входа в систему (логина)
@Data
public class LoginRequest {

    // Логин пользователя (обязательное поле)
    @NotBlank(message = "Username is required")
    private String username;

    // Пароль пользователя (обязательное поле)
    @NotBlank(message = "Password is required")
    private String password;
}
