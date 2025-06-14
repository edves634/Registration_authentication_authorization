package com.example.library.security;

import com.example.library.exception.JwtValidationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Сервис для работы с JWT токенами (генерация, валидация, извлечение данных)
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret-key}") // Секретный ключ из конфига
    private String secretKey;

    @Value("${jwt.expiration-time}") // Время жизни токена (мс)
    private long expirationTime;

    // Извлечь имя пользователя из токена
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Общий метод для извлечения данных из токена
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Сгенерировать токен для пользователя (с ролью в claims)
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities());
        return generateToken(claims, userDetails);
    }

    // Сгенерировать токен с дополнительными claims
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims) // Дополнительные данные
                .setSubject(userDetails.getUsername()) // Имя пользователя
                .setIssuedAt(new Date(System.currentTimeMillis())) // Время создания
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Срок действия
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Подпись
                .compact(); // Генерация строки
    }

    // Проверить валидность токена для пользователя
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // Проверить истек ли срок действия токена
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Извлечь дату истечения токена
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Извлечь все claims из токена
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSignInKey()) // Изменено на setSigningKey
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new JwtValidationException("Invalid token signature");
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new JwtValidationException("Invalid token");
        }
    }

    // Получить ключ для подписи из секрета
    private Key getSignInKey() {
        try {
            // Корректировка длины base64 ключа
            String base64Key = secretKey;
            while (base64Key.length() % 4 != 0) {
                base64Key += "=";
            }
            byte[] keyBytes = Decoders.BASE64.decode(base64Key);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Invalid JWT key configuration", e);
            throw new JwtValidationException("Invalid JWT key configuration");
        }
    }
}