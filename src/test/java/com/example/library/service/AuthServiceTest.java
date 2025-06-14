package com.example.library.service;

import com.example.library.dto.request.LoginRequest;
import com.example.library.dto.request.RegisterRequest;
import com.example.library.dto.response.AuthResponse;
import com.example.library.exception.AuthenticationFailedException;
import com.example.library.exception.UserAlreadyExistsException;
import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import com.example.library.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Подключаем поддержку Mockito для тестов
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // Мок репозитория пользователей
    @Mock
    private UserRepository userRepository;

    // Мок кодировщика паролей
    @Mock
    private PasswordEncoder passwordEncoder;

    // Мок сервиса JWT
    @Mock
    private JwtService jwtService;

    // Мок менеджера аутентификации
    @Mock
    private AuthenticationManager authenticationManager;

    // Тестируемый сервис с внедренными моками
    @InjectMocks
    private AuthService authService;

    // Тест успешной регистрации нового пользователя
    @Test
    void register_ShouldReturnAuthResponse_WhenUserDoesNotExist() {
        // Подготовка тестовых данных
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        // Модель сохраненного пользователя
        User savedUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .role(Role.ROLE_READER)
                .build();

        // Настройка поведения моков
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Вызов тестируемого метода
        AuthResponse response = authService.register(request);

        // Проверки результатов
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("ROLE_READER", response.getRole());

        // Проверка вызовов зависимостей
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    // Тест попытки регистрации с существующим именем пользователя
    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        // Подготовка тестовых данных
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        // Настройка поведения мока
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Проверка выбрасывания исключения
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));

        // Проверка вызовов зависимостей
        verify(userRepository).existsByUsername("existinguser");
        verify(userRepository, never()).existsByEmail(anyString());
    }

    // Тест попытки регистрации с существующим email
    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Подготовка тестовых данных
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("password");

        // Настройка поведения моков
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Проверка выбрасывания исключения
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));

        // Проверка вызовов зависимостей
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("existing@example.com");
    }

    // Тест успешной аутентификации
    @Test
    void authenticate_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // Подготовка тестовых данных
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        // Модель пользователя
        User user = User.builder()
                .username("testuser")
                .role(Role.ROLE_READER)
                .build();

        // Настройка поведения моков
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        // Вызов тестируемого метода
        AuthResponse response = authService.authenticate(request);

        // Проверки результатов
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("ROLE_READER", response.getRole());

        // Проверка вызовов зависимостей
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("testuser", "password"));
        verify(userRepository).findByUsername("testuser");
        verify(jwtService).generateToken(user);
    }

    // Тест неудачной аутентификации
    @Test
    void authenticate_ShouldThrowException_WhenCredentialsAreInvalid() {
        // Подготовка тестовых данных
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        // Настройка поведения мока - выбрасываем исключение
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Проверка выбрасывания исключения
        assertThrows(AuthenticationFailedException.class, () -> authService.authenticate(request));

        // Проверка вызовов зависимостей
        verify(authenticationManager).authenticate(any());
        verify(userRepository, never()).findByUsername(anyString());
    }
}