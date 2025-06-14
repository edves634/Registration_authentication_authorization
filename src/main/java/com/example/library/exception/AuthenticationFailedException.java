package com.example.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Исключение при неудачной аутентификации (ошибка 401 Unauthorized)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationFailedException extends RuntimeException {

    // Создание исключения с сообщением об ошибке
    public AuthenticationFailedException(String message) {
        super(message);
    }

    // Создание исключения с сообщением и причиной (вложенным исключением)
    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
