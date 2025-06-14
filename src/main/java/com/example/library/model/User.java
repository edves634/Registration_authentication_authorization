package com.example.library.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

// Сущность пользователя системы (реализует UserDetails для Spring Security)
@Entity
@Table(name = "users", // Таблица users в БД
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"), // Уникальный логин
                @UniqueConstraint(columnNames = "email")     // Уникальный email
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоинкрементный ID
    private Long id;

    // Логин пользователя (обязательный, макс. 50 символов)
    @Column(nullable = false, length = 50)
    private String username;

    // Зашифрованный пароль (обязательный)
    @Column(nullable = false)
    private String password;

    // Email пользователя (обязательный, макс. 100 символов)
    @Column(nullable = false, length = 100)
    private String email;

    // Роль пользователя (ADMIN/READER) хранится как строка
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // Список взятых книг (ленивая загрузка, исключен из toString/equals)
    @OneToMany(mappedBy = "borrowedBy", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Book> borrowedBooks;

    // Возвращает права пользователя (на основе роли)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    // Аккаунт не просрочен (всегда true)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Аккаунт не заблокирован (всегда true)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Пароль не просрочен (всегда true)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Аккаунт включен (всегда true)
    @Override
    public boolean isEnabled() {
        return true;
    }
}