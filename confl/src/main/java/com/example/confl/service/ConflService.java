package com.example.confl.service;

import com.example.confl.model.Email;
import com.example.confl.repository.EmailRepository;
import com.example.confl.exception.InvalidTokenException;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class ConflService {

    private final EmailRepository emailRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String TOKEN_KEY_PREFIX = "token:";

    public List<Email> getAllEmails() {
        return emailRepository.findAll();
    }

    public boolean isEmailAvailable(String email, String token) {
        if (!isValidToken(token)) {
            throw new InvalidTokenException("Неверный или просроченный токен");
        }
        boolean isAvailable = !emailRepository.existsByEmail(email);

        if (isAvailable) {
            Email newEmail = Email.builder()
                    .email(email)
                    .createdAt(LocalDateTime.now())
                    .build();

            emailRepository.save(newEmail);
            deleteToken(token);
        }

        return isAvailable;
    }
    private boolean isValidToken(String token) {
        String key = TOKEN_KEY_PREFIX + token;

        Boolean exists = redisTemplate.hasKey(key);

        return exists != null && exists;
    }
    private void deleteToken(String token) {
        String key = TOKEN_KEY_PREFIX + token;
        redisTemplate.delete(key);
    }

    public void saveToken(String token, long expirationTimeInSeconds) {
        String key = TOKEN_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(key, "valid", expirationTimeInSeconds, TimeUnit.SECONDS);
    }
    public boolean validateAndUseToken(String token) {
        String key = TOKEN_KEY_PREFIX + token;

        // Используем транзакцию для атомарной проверки и удаления
        return Boolean.TRUE.equals(redisTemplate.execute(new SessionCallback<Boolean>() {
            @Override
            public Boolean execute(RedisOperations operations) {
                operations.watch(key);

                if (operations.hasKey(key)) {
                    operations.multi();
                    operations.delete(key);
                    return operations.exec() != null;
                }

                operations.unwatch();
                return false;
            }
        }));
    }
}

