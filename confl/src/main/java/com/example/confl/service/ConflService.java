package com.example.confl.service;

import com.example.confl.repository.ConflRepository;
import com.example.dto.ConflResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ConflService {

    private final ConflRepository conflRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ConflResponse checkEmail(String token, String email) {
        String cacheKey = "confl:email:" + email;

        // Проверяем в кеше
        ConflResponse cachedResponse = (ConflResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        // Проверяем в базе данных
        boolean emailExists = conflRepository.existsByEmail(email);
        ConflResponse response = new ConflResponse();

        if (!emailExists) {
            response.setStatus("available");
        } else {
            response.setStatus("exists");
        }
        // Кешируем результат на 30 минут
        redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(30));

        return response;
    }
    public List<String> getAllEmails() {
        // Возвращаем список всех email из базы данных
        // Если используете JPA с сущностью User:
        // return conflRepository.findAll().stream()
        //     .map(User::getEmail)
        //     .collect(Collectors.toList());

        // Временная заглушка:
        return new ArrayList<>();
    }
    public boolean isEmailAvailable(String email, String token) {
        // Проверяем доступность email
        ConflResponse response = checkEmail(token, email);
        return "available".equals(response.getStatus());
    }

    private boolean checkEmailInDatabase(String email) {
        // Временная реализация
        return false;
    }
}

