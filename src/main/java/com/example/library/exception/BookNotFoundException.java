package com.example.library.exception;

// Исключение при попытке доступа к несуществующей книге
public class BookNotFoundException extends RuntimeException {

    // Создает исключение с указанным сообщением об ошибке
    public BookNotFoundException(String message) {
        super(message);
    }
}