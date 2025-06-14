package com.example.library.service;

import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Подключаем поддержку Mockito для тестов
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    // Мок репозитория пользователей
    @Mock
    private UserRepository userRepository;

    // Тестируемый сервис с внедренными моками
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    // Тест загрузки пользователя с ролью ADMIN
    @Test
    void loadUserByUsername_ShouldReturnUserDetailsWithCorrectAuthorities_WhenUserExists() {
        // Подготовка тестовых данных
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_ADMIN); // Устанавливаем роль администратора

        // Настройка поведения мока
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Вызов тестируемого метода
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Проверки результатов
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());

        // Проверка прав пользователя (authorities)
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size()); // Должна быть одна роль
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))); // Проверяем наличие роли ADMIN

        // Проверка вызова репозитория
        verify(userRepository).findByUsername(username);
    }

    // Тест загрузки пользователя с ролью READER
    @Test
    void loadUserByUsername_ShouldWorkForReaderRole() {
        // Подготовка тестовых данных
        String username = "reader";
        User user = new User();
        user.setUsername(username);
        user.setPassword("pass");
        user.setRole(Role.ROLE_READER); // Устанавливаем роль читателя

        // Настройка поведения мока
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Вызов тестируемого метода
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Проверка прав пользователя
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_READER"))); // Проверяем наличие роли READER
    }

    // Тест обработки случая, когда пользователь не найден
    @Test
    void loadUserByUsername_ShouldThrowExceptionWithCorrectMessage_WhenUserNotFound() {
        // Подготовка тестовых данных
        String username = "unknown";

        // Настройка поведения мока - возвращаем пустой Optional
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Проверка выбрасывания исключения
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );

        // Проверка сообщения исключения
        assertEquals("Пользователь '" + username + "' не найден", exception.getMessage());

        // Проверка вызова репозитория
        verify(userRepository).findByUsername(username);
    }

    // Тест проверки стандартных методов UserDetails
    @Test
    void loadUserByUsername_ShouldVerifyUserDetailsMethods() {
        // Подготовка тестовых данных
        User user = new User();
        user.setUsername("test");
        user.setPassword("pass");
        user.setRole(Role.ROLE_ADMIN);

        // Настройка поведения мока
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        // Вызов тестируемого метода
        UserDetails userDetails = userDetailsService.loadUserByUsername("test");

        // Проверка стандартных методов UserDetails
        assertTrue(userDetails.isAccountNonExpired()); // Аккаунт не просрочен
        assertTrue(userDetails.isAccountNonLocked()); // Аккаунт не заблокирован
        assertTrue(userDetails.isCredentialsNonExpired()); // Учетные данные не просрочены
        assertTrue(userDetails.isEnabled()); // Аккаунт включен
    }
}