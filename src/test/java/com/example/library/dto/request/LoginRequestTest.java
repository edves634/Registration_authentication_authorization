package com.example.library.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    // Статический валидатор для проверки аннотаций Bean Validation
    private static Validator validator;

    // Инициализация валидатора перед всеми тестами класса
    @BeforeAll
    static void setUp() {
        // Создание фабрики валидаторов
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        // Получение валидатора
        validator = factory.getValidator();
    }

    @Test
    void whenValidRequest_ThenNoViolations() {
        // Подготовка корректного объекта запроса
        LoginRequest request = new LoginRequest();
        request.setUsername("validUser"); // Валидное имя пользователя
        request.setPassword("validPass123"); // Валидный пароль

        // Проведение валидации
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Проверка что нет нарушений валидации
        assertTrue(violations.isEmpty(), "При корректных данных не должно быть нарушений");
    }

    @Test
    void whenUsernameBlank_ThenViolationOccurs() {
        // Подготовка объекта с пустым именем пользователя
        LoginRequest request = new LoginRequest();
        request.setUsername("");  // Нарушение @NotBlank
        request.setPassword("validPass123"); // Корректный пароль

        // Проведение валидации
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Проверки:
        // 1. Должно быть ровно одно нарушение
        // 2. Сообщение об ошибке должно соответствовать аннотации
        assertEquals(1, violations.size(), "Должно быть одно нарушение для пустого username");
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void whenPasswordBlank_ThenViolationOccurs() {
        // Подготовка объекта с пустым паролем
        LoginRequest request = new LoginRequest();
        request.setUsername("validUser"); // Корректное имя пользователя
        request.setPassword("");  // Нарушение @NotBlank

        // Проведение валидации
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Проверки:
        // 1. Должно быть ровно одно нарушение
        // 2. Сообщение об ошибке должно соответствовать аннотации
        assertEquals(1, violations.size(), "Должно быть одно нарушение для пустого password");
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }
}