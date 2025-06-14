package com.example.library.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;
import java.io.IOException;

// Фильтр для JWT аутентификации (выполняется на каждый запрос)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // Сервис работы с JWT
    private final UserDetailsService userDetailsService; // Сервис загрузки пользователей

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Получаем заголовок Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Если нет токена или неверный формат - пропускаем запрос дальше
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Извлекаем JWT из заголовка (убираем "Bearer ")
        jwt = authHeader.substring(7);
        // Получаем email пользователя из токена
        userEmail = jwtService.extractUsername(jwt);

        // Если email есть и пользователь еще не аутентифицирован
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Загружаем данные пользователя
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Проверяем валидность токена
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Создаем объект аутентификации
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // Добавляем детали запроса
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Устанавливаем аутентификацию в контекст безопасности
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
}
