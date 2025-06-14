package com.example.library.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    // Тестирование работы билдера пользователя
    @Test
    void testUserBuilder() {
        // Создаем пользователя с помощью билдера
        User user = User.builder()
                .id(1L)  // Устанавливаем ID
                .username("testUser")  // Устанавливаем имя пользователя
                .password("encodedPassword")  // Устанавливаем закодированный пароль
                .email("test@example.com")  // Устанавливаем email
                .role(Role.ROLE_ADMIN)  // Устанавливаем роль администратора
                .build();  // Собираем объект

        // Проверяем корректность установленных значений
        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(Role.ROLE_ADMIN, user.getRole());

        // Проверяем, что у пользователя есть соответствующая роль
        assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    // Тестирование методов UserDetails (интерфейса Spring Security)
    @Test
    void testUserDetailsMethods() {
        User user = new User();  // Создаем пустого пользователя

        // Проверяем стандартные значения для учетной записи:
        assertTrue(user.isAccountNonExpired());  // Аккаунт не просрочен
        assertTrue(user.isAccountNonLocked());  // Аккаунт не заблокирован
        assertTrue(user.isCredentialsNonExpired());  // Учетные данные не просрочены
        assertTrue(user.isEnabled());  // Аккаунт включен
    }

    // Тестирование конструктора без аргументов
    @Test
    void testNoArgsConstructor() {
        User user = new User();  // Создаем пользователя через пустой конструктор

        // Проверяем, что все поля null
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getRole());
        assertNull(user.getBorrowedBooks());  // Список взятых книг должен быть null
    }

    // Тестирование конструктора со всеми аргументами
    @Test
    void testAllArgsConstructor() {
        // Создаем пользователя через полный конструктор
        User user = new User(
                1L,  // ID
                "testUser",  // Имя пользователя
                "encodedPassword",  // Пароль
                "test@example.com",  // Email
                Role.ROLE_READER,  // Роль читателя
                List.of()  // Пустой список книг
        );

        // Проверяем основные поля
        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals(Role.ROLE_READER, user.getRole());
    }

    // Тестирование получения прав для роли READER
    @Test
    void testGetAuthoritiesForReader() {
        // Создаем пользователя с ролью читателя
        User user = User.builder()
                .role(Role.ROLE_READER)
                .build();

        // Проверяем, что у пользователя есть только одна роль - ROLE_READER
        assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_READER")));
        assertEquals(1, user.getAuthorities().size());
    }

    // Тестирование получения прав для роли ADMIN
    @Test
    void testGetAuthoritiesForAdmin() {
        // Создаем пользователя с ролью администратора
        User user = User.builder()
                .role(Role.ROLE_ADMIN)
                .build();

        // Проверяем, что у пользователя есть только одна роль - ROLE_ADMIN
        assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertEquals(1, user.getAuthorities().size());
    }
}