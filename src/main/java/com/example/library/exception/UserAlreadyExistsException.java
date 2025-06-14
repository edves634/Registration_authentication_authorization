package com.example.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Исключение при попытке регистрации существующего пользователя (ошибка 409 Conflict)
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {

    // Создание исключения с сообщением об ошибке
    public UserAlreadyExistsException(String message) {
        super(message); // Например: "Пользователь с email уже существует"
    }

    // Создание исключения с сообщением и причиной ошибки
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause); // С вложенным исключением-причиной
    }
}