package com.example.library.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void testAuthResponseCreation() {
        // Подготовка и выполнение: создаем объект через Builder
        AuthResponse response = AuthResponse.builder()
                .token("testToken123")       // Устанавливаем тестовый токен
                .username("testUser")       // Устанавливаем тестовое имя пользователя
                .role("ROLE_READER")        // Устанавливаем тестовую роль
                .build();

        // Проверки:
        assertEquals("testToken123", response.getToken(), "Токен должен соответствовать установленному значению");
        assertEquals("testUser", response.getUsername(), "Имя пользователя должно соответствовать установленному значению");
        assertEquals("ROLE_READER", response.getRole(), "Роль должна соответствовать установленному значению");
    }


    @Test
    void testAuthResponseSetters() {
        // Подготовка: создаем пустой объект
        AuthResponse response = new AuthResponse();

        // Выполнение: устанавливаем значения через сеттеры
        response.setToken("newToken");       // Устанавливаем новый токен
        response.setUsername("newUser");     // Устанавливаем новое имя пользователя
        response.setRole("ROLE_ADMIN");      // Устанавливаем новую роль

        // Проверки:
        assertEquals("newToken", response.getToken(), "Токен должен обновиться через сеттер");
        assertEquals("newUser", response.getUsername(), "Имя пользователя должно обновиться через сеттер");
        assertEquals("ROLE_ADMIN", response.getRole(), "Роль должна обновиться через сеттер");
    }

    @Test
    void testAuthResponseNoArgsConstructor() {
        // Подготовка и выполнение: создаем объект через конструктор без параметров
        AuthResponse response = new AuthResponse();

        // Проверки:
        assertNull(response.getToken(), "Токен должен быть null при создании через no-args конструктор");
        assertNull(response.getUsername(), "Имя пользователя должно быть null при создании через no-args конструктор");
        assertNull(response.getRole(), "Роль должна быть null при создании через no-args конструктор");
    }
}