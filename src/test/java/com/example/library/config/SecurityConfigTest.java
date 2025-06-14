package com.example.library.config;

import com.example.library.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    // Моки зависимостей для тестирования
    private final JwtAuthenticationFilter jwtAuthFilter = mock(JwtAuthenticationFilter.class);
    private final UserDetailsService userDetailsService = mock(UserDetailsService.class);
    private final AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);

    // Тестируемый класс с внедренными зависимостями
    private final SecurityConfig securityConfig = new SecurityConfig(jwtAuthFilter, userDetailsService);

    @Test
    void securityFilterChain() throws Exception {
        // Подготовка мока HttpSecurity для имитации Spring Security конфигурации
        HttpSecurity httpSecurity = mock(HttpSecurity.class);

        // Настройка fluent API вызовов (цепочка методов возвращает тот же объект)
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.headers(any())).thenReturn(httpSecurity);

        // Создание мока SecurityFilterChain, который должен вернуться
        DefaultSecurityFilterChain mockFilterChain = mock(DefaultSecurityFilterChain.class);
        when(httpSecurity.build()).thenReturn(mockFilterChain);

        // Вызов тестируемого метода
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);

        // Проверки:
        // - Фильтр не должен быть null
        // - Должен вернуться ожидаемый объект фильтра
        assertNotNull(filterChain, "SecurityFilterChain should not be null");
        assertEquals(mockFilterChain, filterChain);

        // 6. Проверка вызовов критически важных методов конфигурации:
        // - Отключение CSRF
        // - Настройка авторизации запросов
        // - Управление сессиями
        // - Добавление провайдера аутентификации
        // - Добавление JWT фильтра перед стандартным фильтром аутентификации
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authenticationProvider(any(AuthenticationProvider.class));
        verify(httpSecurity).addFilterBefore(any(JwtAuthenticationFilter.class), eq(UsernamePasswordAuthenticationFilter.class));
    }

    @Test
    void authenticationProvider() {
        // Подготовка тестовых данных
        String username = "testUser";
        String password = "testPass";
        String encodedPassword = securityConfig.passwordEncoder().encode(password);

        // Создание тестового пользователя с закодированным паролем
        UserDetails testUser = User.withUsername(username)
                .password(encodedPassword)
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();

        // Настройка мока UserDetailsService
        when(userDetailsService.loadUserByUsername(username)).thenReturn(testUser);

        // Вызов тестируемого метода
        AuthenticationProvider provider = securityConfig.authenticationProvider();

        // Проверки:
        // - Провайдер не должен быть null
        // - Должен быть экземпляром DaoAuthenticationProvider
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);

        // Тест успешной аутентификации (правильные учетные данные)
        assertDoesNotThrow(() -> provider.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        ));

        // Тест неудачной аутентификации (неправильный пароль)
        assertThrows(Exception.class, () -> provider.authenticate(
                new UsernamePasswordAuthenticationToken(username, "wrongPass")
        ));
    }

    @Test
    void authenticationManager() throws Exception {
        // Подготовка мока AuthenticationManager
        AuthenticationManager mockManager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(mockManager);

        // Вызов тестируемого метода
        AuthenticationManager manager = securityConfig.authenticationManager(authConfig);

        // Проверки:
        // - Менеджер не должен быть null
        // - Должен вернуться ожидаемый мок-объект
        // - Должен быть вызван метод getAuthenticationManager()
        assertNotNull(manager);
        assertEquals(mockManager, manager);
        verify(authConfig).getAuthenticationManager();
    }

    @Test
    void passwordEncoder() {
        // Вызов тестируемого метода
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // Проверки:
        // - Кодировщик не должен быть null
        // - Должен быть экземпляром BCryptPasswordEncoder
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);

        // Тест кодирования пароля
        String rawPassword = "testPassword";
        String encodedPassword = encoder.encode(rawPassword);

        // Проверки:
        // - Закодированный пароль не должен совпадать с исходным
        // - Должен корректно проверять соответствие пароля
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }
}