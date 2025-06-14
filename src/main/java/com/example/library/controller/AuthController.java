package com.example.library.controller;

import com.example.library.dto.request.LoginRequest;
import com.example.library.dto.request.RegisterRequest;
import com.example.library.dto.response.AuthResponse;
import com.example.library.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Контроллер для аутентификации и регистрации пользователей
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Регистрация нового пользователя
    // Пример запроса:
    // POST /api/auth/register
    // {
    //   "firstName": "John",
    //   "lastName": "Doe",
    //   "email": "john@example.com",
    //   "password": "password123"
    // }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // Аутентификация пользователя
    // Пример запроса:
    // POST /api/auth/login
    // {
    //   "email": "john@example.com",
    //   "password": "password123"
    // }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
