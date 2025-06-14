package com.example.library.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.*;

class BookRequestTest {

    // Валидатор для проверки аннотаций Bean Validation
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        // Инициализация валидатора перед всеми тестами
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrect_ThenNoViolations() {
        // Подготовка корректного объекта запроса
        BookRequest request = new BookRequest();
        request.setTitle("Valid Title");
        request.setAuthor("Valid Author");
        request.setIsbn("978-3-16-148410-0"); // Валидный ISBN формат
        request.setPublicationYear(2023);

        // Валидация объекта
        Set<ConstraintViolation<BookRequest>> violations = validator.validate(request);

        // Проверка что нет нарушений валидации
        assertTrue(violations.isEmpty(), "Должно быть 0 нарушений при корректных данных");
    }

    @Test
    void whenTitleBlank_ThenViolationOccurs() {
        // Подготовка объекта с невалидным названием (пустая строка)
        BookRequest request = new BookRequest();
        request.setTitle("");  // Нарушение @NotBlank
        request.setAuthor("Author");
        request.setIsbn("978-3-16-148410-0");
        request.setPublicationYear(2023);

        // Валидация объекта
        Set<ConstraintViolation<BookRequest>> violations = validator.validate(request);

        // Проверки:
        // 1. Должно быть ровно одно нарушение
        // 2. Сообщение об ошибке должно соответствовать аннотации
        assertEquals(1, violations.size(), "Должно быть 1 нарушение");
        assertEquals("Название обязательно", violations.iterator().next().getMessage());
    }
}

