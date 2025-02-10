package com.datingapp.datingapp.services;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final Argon2PasswordEncoder passwordEncoder;

    public PasswordService() {
        this.passwordEncoder = new Argon2PasswordEncoder(
                32,
                64,
                4,
                32,
                1
        );
    }

    public String hashPassword(String password) {
        return passwordEncoder.encode(password); // Хешируем пароль
    }

    public String extractSalt(String hashedPassword) {
        // Разбираем строку хеша, чтобы получить соль
        String[] parts = hashedPassword.split("\\$");
        return parts[4]; // Соль находится в 5-й части
    }

    public String extractHash(String hashedPassword) {
        String[] parts = hashedPassword.split("\\$");
        return parts[5];
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword); // Проверяем пароль
    }
}
