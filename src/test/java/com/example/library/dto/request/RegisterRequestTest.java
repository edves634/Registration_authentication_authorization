package com.example.library.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    // Валидатор для проверки аннотаций Jakarta Bean Validation
    private static Validator validator;

    // Инициализация валидатора перед всеми тестами (выполняется один раз)
    @BeforeAll
    static void setUp() {
        // Создание фабрики валидаторов
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        // Получение валидатора
        validator = factory.getValidator();
    }

    // Позитивный тест - все поля валидны
    @Test
    void whenAllFieldsValid_ThenNoViolations() {
        // Подготовка полностью валидного запроса
        RegisterRequest request = new RegisterRequest();
        request.setUsername("validUser"); // 8 символов (4-50 допустимо)
        request.setPassword("validPass123"); // 11 символов (6-100 допустимо)
        request.setEmail("valid@example.com"); // Валидный email

        // Выполнение валидации
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Проверка что нет нарушений
        assertTrue(violations.isEmpty(), "При корректных данных не должно быть нарушений валидации");
    }

    // Тест на слишком короткое имя пользователя (<4 символов)
    @Test
    void whenUsernameTooShort_ThenViolationOccurs() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("abc"); // 3 символа (меньше минимальных 4)
        request.setPassword("validPass123");
        request.setEmail("valid@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Проверки:
        assertEquals(1, violations.size(), "Должно быть одно нарушение");
        assertEquals("Username must be between 4 and 50 characters",
                violations.iterator().next().getMessage());
    }

    // Тест на слишком короткий пароль (<6 символов)
    @Test
    void whenPasswordTooShort_ThenViolationOccurs() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("validUser");
        request.setPassword("short"); // 5 символов (меньше минимальных 6)
        request.setEmail("valid@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Password must be between 6 and 100 characters",
                violations.iterator().next().getMessage());
    }

    // Тест на невалидный email
    @Test
    void whenEmailInvalid_ThenViolationOccurs() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("validUser");
        request.setPassword("validPass123");
        request.setEmail("invalid-email"); // Нарушает @Email валидацию

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Email should be valid",
                violations.iterator().next().getMessage());
    }

    // Тест на отсутствие обязательных полей (все поля null/пустые)
    @Test
    void whenFieldsBlank_ThenMultipleViolationsOccur() {
        RegisterRequest request = new RegisterRequest(); // Все поля не установлены

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Должно быть 3 нарушения (по одному для каждого обязательного поля)
        assertEquals(3, violations.size(), "Должно быть 3 нарушения для пустого запроса");

        // Проверяем сообщения всех нарушений
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Username is required")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password is required")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email is required")));
    }
}