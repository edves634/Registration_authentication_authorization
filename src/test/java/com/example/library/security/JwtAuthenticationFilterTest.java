package com.example.library.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

// Используем Mockito для тестирования
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    // Мок сервиса для работы с JWT токенами
    @Mock
    private JwtService jwtService;

    // Мок сервиса для загрузки данных пользователя
    @Mock
    private UserDetailsService userDetailsService;

    // Мок HTTP запроса
    @Mock
    private HttpServletRequest request;

    // Мок HTTP ответа
    @Mock
    private HttpServletResponse response;

    // Мок цепочки фильтров
    @Mock
    private FilterChain filterChain;

    // Тестируемый фильтр с внедренными моками
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Тест: когда нет заголовка Authorization
    @Test
    void whenNoAuthorizationHeader_thenContinueFilterChain() throws Exception {
        // Задаем поведение мока - возвращаем null для заголовка Authorization
        when(request.getHeader("Authorization")).thenReturn(null);

        // Выполняем фильтрацию
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Проверяем, что цепочка фильтров продолжена
        verify(filterChain).doFilter(request, response);
        // Проверяем, что сервисы не вызывались
        verifyNoInteractions(jwtService, userDetailsService);
    }

    // Тест: когда неверный формат заголовка Authorization
    @Test
    void whenInvalidAuthorizationHeader_thenContinueFilterChain() throws Exception {
        // Задаем неверный формат токена (без Bearer)
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // Выполняем фильтрацию
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Проверяем, что цепочка фильтров продолжена
        verify(filterChain).doFilter(request, response);
        // Проверяем, что сервисы не вызывались
        verifyNoInteractions(jwtService, userDetailsService);
    }

    // Тест: когда валидный JWT токен
    @Test
    void whenValidJwtToken_thenAuthenticateUser() throws Exception {
        // Подготовка тестовых данных
        String validJwt = "valid.jwt.token";
        String userEmail = "user@example.com";
        // Создаем mock пользователя
        UserDetails userDetails = new User(userEmail, "", Collections.emptyList());

        // Настраиваем поведение моков:
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validJwt);
        when(jwtService.extractUsername(validJwt)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(validJwt, userDetails)).thenReturn(true);

        // Выполняем фильтрацию
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Проверяем, что:
        // 1. Был вызван сервис загрузки пользователя
        verify(userDetailsService).loadUserByUsername(userEmail);
        // 2. Был проверен токен
        verify(jwtService).isTokenValid(validJwt, userDetails);
        // 3. Цепочка фильтров продолжена
        verify(filterChain).doFilter(request, response);
        // 4. Аутентификация установлена в SecurityContext
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // Тест: когда невалидный JWT токен
    @Test
    void whenInvalidJwtToken_thenContinueWithoutAuthentication() throws Exception {
        // Подготовка тестовых данных
        String invalidJwt = "invalid.jwt.token";
        String userEmail = "user@example.com";
        // Создаем mock пользователя
        UserDetails userDetails = new User(userEmail, "", Collections.emptyList());

        // Настраиваем поведение моков:
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidJwt);
        when(jwtService.extractUsername(invalidJwt)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(invalidJwt, userDetails)).thenReturn(false);

        // Выполняем фильтрацию
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Проверяем, что:
        // 1. Был вызван сервис загрузки пользователя
        verify(userDetailsService).loadUserByUsername(userEmail);
        // 2. Был проверен токен
        verify(jwtService).isTokenValid(invalidJwt, userDetails);
        // 3. Цепочка фильтров продолжена
        verify(filterChain).doFilter(request, response);
        // 4. Аутентификация НЕ установлена в SecurityContext
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}