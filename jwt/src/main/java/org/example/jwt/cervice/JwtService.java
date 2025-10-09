package org.example.jwt.cervice;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class JwtService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TOKEN_PREFIX = "jwt:";
    private static final Duration TOKEN_TTL = Duration.ofHours(1);

    public JwtService(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }
    public String generateToken(String email, String username) {
        String token = jwtUtil.generateToken(email, username);

        // Кешируем токен в Redis
        String redisKey = TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(redisKey, token, TOKEN_TTL);

        return token;
    }
    public boolean validateToken(String token, String email) {
        // Проверяем в Redis сначала
        String redisKey = TOKEN_PREFIX + email;
        String cachedToken = (String) redisTemplate.opsForValue().get(redisKey);

        if (cachedToken != null && cachedToken.equals(token)) {
            return jwtUtil.validateToken(token);
        }

        // Если нет в кеше, проверяем стандартным способом
        boolean isValid = jwtUtil.validateToken(token);
        if (isValid) {
            // Сохраняем в кеш
            redisTemplate.opsForValue().set(redisKey, token, TOKEN_TTL);
        }

        return isValid;
    }
    public void invalidateToken(String email) {
        String redisKey = TOKEN_PREFIX + email;
        redisTemplate.delete(redisKey);
    }

    public String getEmailFromToken(String token) {
        return jwtUtil.extractEmail(token);
    }
}
