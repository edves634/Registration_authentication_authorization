package com.example.library.security;

import com.example.library.exception.JwtValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    // Сервис для работы с JWT токенами
    private JwtService jwtService;

    // Тестовые данные пользователя
    private UserDetails userDetails;

    // Секретный ключ для подписи токенов (в Base64)
    private static final String SECRET_KEY = "VGhpcyBpcyBhIHNlY3JldCBrZXkgZm9yIEpXVCB0b2tlbiBnZW5lcmF0aW9u";

    // Время жизни токена (24 часа в миллисекундах)
    private static final long EXPIRATION_TIME = 86400000;

    // Метод выполняется перед каждым тестом
    @BeforeEach
    void setUp() {
        // Создаем экземпляр сервиса
        jwtService = new JwtService();

        // Устанавливаем приватные поля через ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "expirationTime", EXPIRATION_TIME);

        // Создаем тестового пользователя
        userDetails = User.withUsername("test@example.com")
                .password("password")
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();
    }

    // Тест генерации токена - должен создавать валидный непустой токен
    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    // Тест извлечения имени пользователя из токена
    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        assertEquals("test@example.com", jwtService.extractUsername(token));
    }

    // Тест проверки валидности токена для правильного пользователя
    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    // Тест проверки токена для другого пользователя (должен вернуть false)
    @Test
    void isTokenValid_ShouldReturnFalseForInvalidUser() {
        // Создаем другого пользователя
        UserDetails otherUser = User.withUsername("other@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        String token = jwtService.generateToken(userDetails);
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    // Тест проверки просроченного токена
    @Test
    void isTokenValid_ShouldReturnFalseForExpiredToken() throws InterruptedException {
        // Устанавливаем очень короткое время жизни токена (1 мс)
        ReflectionTestUtils.setField(jwtService, "expirationTime", 1L);
        String expiredToken = jwtService.generateToken(userDetails);

        // Ждем 10 мс, чтобы токен точно истек
        Thread.sleep(10);

        // Проверяем, что токен невалиден
        assertFalse(jwtService.isTokenValid(expiredToken, userDetails));
    }

    // Тест обработки невалидного токена (должен бросить исключение)
    @Test
    void extractUsername_ShouldThrowForInvalidToken() {
        assertThrows(JwtValidationException.class,
                () -> jwtService.extractUsername("invalid.token"));
    }

    // Тест обработки невалидного секретного ключа (должен бросить исключение)
    @Test
    void getSignInKey_ShouldThrowForInvalidKey() {
        // Устанавливаем неверный ключ
        ReflectionTestUtils.setField(jwtService, "secretKey", "invalid-key");
        assertThrows(JwtValidationException.class,
                () -> jwtService.generateToken(userDetails));
    }
}