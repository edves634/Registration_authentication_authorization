package com.example.library.service;

import com.example.library.dto.request.*;
import com.example.library.dto.response.*;
import com.example.library.exception.*;
import com.example.library.model.*;
import com.example.library.model.User;
import com.example.library.repository.*;
import com.example.library.security.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

// Сервис для аутентификации и регистрации пользователей
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Шифровальщик паролей
    private final JwtService jwtService; // Генератор JWT токенов
    private final AuthenticationManager authenticationManager; // Менеджер аутентификации

    // Регистрация нового пользователя
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Регистрация пользователя: {}", request.getUsername());

        // Проверка существования пользователя
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Логин занят: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email уже зарегистрирован: " + request.getEmail());
        }

        // Создание нового пользователя
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.ROLE_READER) // По умолчанию роль READER
                .build();

        User savedUser = userRepository.save(user);

        // Генерация токена
        String jwtToken = jwtService.generateToken(user);

        // Формирование ответа
        return AuthResponse.builder()
                .token(jwtToken)
                .username(savedUser.getUsername())
                .role(savedUser.getRole().name())
                .build();
    }

    // Аутентификация пользователя
    public AuthResponse authenticate(LoginRequest request) {
        log.info("Попытка входа пользователя: {}", request.getUsername());

        try {
            // Проверка учетных данных
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Поиск пользователя
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

            // Генерация токена
            String jwtToken = jwtService.generateToken(user);

            // Формирование ответа
            return AuthResponse.builder()
                    .token(jwtToken)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Неудачная попытка входа: {}", request.getUsername());
            throw new AuthenticationFailedException("Неверный логин или пароль");
        }
    }
}
