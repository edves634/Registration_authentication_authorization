package com.example.library.exception;

// Кастомное исключение для ошибок валидации JWT токенов
public class JwtValidationException extends RuntimeException {

    // Создание исключения с сообщением об ошибке
    public JwtValidationException(String message) {
        super(message); // Например: "Недействительный токен"
    }

    // Создание исключения с сообщением и причиной (вложенным исключением)
    public JwtValidationException(String message, Throwable cause) {
        super(message, cause); // Например: "Ошибка подписи токена", с исходным исключением
    }
}
