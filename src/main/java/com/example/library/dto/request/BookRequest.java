package com.example.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// DTO для создания/обновления книги
@Data
public class BookRequest {

    // Название книги (обязательное поле, макс. 100 символов)
    @NotBlank(message = "Название обязательно")
    @Size(max = 100, message = "Название должно быть не более 100 символов")
    private String title;

    // Автор книги (обязательное поле, макс. 100 символов)
    @NotBlank(message = "Автор обязателен")
    @Size(max = 100, message = "Имя автора должно быть не более 100 символов")
    private String author;

    // ISBN книги (обязательное поле, должен соответствовать формату ISBN)
    @NotBlank(message = "ISBN обязателен")
    @Pattern(regexp = "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]$",
            message = "Неверный формат ISBN")
    private String isbn;

    // Год публикации (обязательное поле)
    @NotNull(message = "Год публикации обязателен")
    private Integer publicationYear;
}
