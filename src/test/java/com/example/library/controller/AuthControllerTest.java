package com.example.library.controller;

import com.example.library.dto.request.LoginRequest;
import com.example.library.dto.request.RegisterRequest;
import com.example.library.dto.response.AuthResponse;
import com.example.library.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Интеграция Mockito с JUnit 5
class AuthControllerTest {

    @Mock // Создаем mock-объект сервиса аутентификации
    private AuthService authService;

    @InjectMocks // Внедряем mock-зависимости в тестируемый контроллер
    private AuthController authController;

    @Test
    void register_ShouldReturnAuthResponse() {
        // Подготовка тестовых данных
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");

        AuthResponse expectedResponse = new AuthResponse();
        expectedResponse.setToken("test-token");

        // Настройка поведения mock-сервиса
        when(authService.register(request)).thenReturn(expectedResponse);

        // Вызов тестируемого метода
        ResponseEntity<AuthResponse> response = authController.register(request);

        // Проверки:
        // 1. Статус ответа должен быть OK (200)
        // 2. Тело ответа должно соответствовать ожидаемому
        // 3. Должен быть вызван метод сервиса с правильными параметрами
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(authService).register(request);
    }

    @Test
    void login_ShouldReturnAuthResponse() {
        // Подготовка тестовых данных
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        AuthResponse expectedResponse = new AuthResponse();
        expectedResponse.setToken("test-token");

        // Настройка поведения mock-сервиса
        when(authService.authenticate(request)).thenReturn(expectedResponse);

        // Вызов тестируемого метода
        ResponseEntity<AuthResponse> response = authController.login(request);

        // Проверки:
        // 1. Статус ответа должен быть OK (200)
        // 2. Тело ответа должно содержать ожидаемый токен
        // 3. Должен быть вызван метод сервиса с правильными параметрами
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(authService).authenticate(request);
    }
}