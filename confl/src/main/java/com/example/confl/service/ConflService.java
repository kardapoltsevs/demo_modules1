package com.example.confl.service;

import com.example.confl.model.Email;
import com.example.confl.repository.EmailRepository;
import com.example.confl.exception.InvalidTokenException;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class ConflService {

    private final ConflRepository conflRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ConflService(ConflRepository conflRepository, RedisTemplate<String, Object> redisTemplate) {
        this.conflRepository = conflRepository;
        this.redisTemplate = redisTemplate;
    }

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
}

