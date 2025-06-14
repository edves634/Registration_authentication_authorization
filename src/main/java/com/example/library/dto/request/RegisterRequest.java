package com.example.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Данные для регистрации нового пользователя
@Data
public class RegisterRequest {

    // Логин пользователя (4-50 символов)
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    private String username;

    // Пароль (6-100 символов)
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    // Email (должен быть валидным)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
}
