package com.example.service;

import com.example.model.TokenInfo;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RedisTokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtService jwtService;

    private static final String TOKEN_PREFIX = "token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    public RedisTokenService(RedisTemplate<String, Object> redisTemplate, JwtService jwtService) {
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
    }
    public void saveToken(String token) {
        if (jwtService.validateToken(token)) {
            String username = jwtService.extractUsername(token);
            Date expiration = jwtService.extractExpiration(token);

            TokenInfo tokenInfo = new TokenInfo(username, token, expiration, true);
            String key = TOKEN_PREFIX + username;

            redisTemplate.opsForValue().set(key, tokenInfo);

            // Устанавливаем время жизни ключа
            long ttl = expiration.getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS);
            }
        }
    }
    public boolean isTokenValid(String token) {
        if (!jwtService.validateToken(token)) {
            return false;
        }

        // Проверяем черный список
        if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token))) {
            return false;
        }

        String username = jwtService.extractUsername(token);
        String key = TOKEN_PREFIX + username;

        TokenInfo tokenInfo = (TokenInfo) redisTemplate.opsForValue().get(key);
        return tokenInfo != null && tokenInfo.isValid() &&
                token.equals(tokenInfo.getToken());
    }
    public void invalidateToken(String token) {
        // Добавляем токен в черный список
        Date expiration = jwtService.extractExpiration(token);
        long ttl = expiration.getTime() - System.currentTimeMillis();

        if (ttl > 0) {
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "invalid");
            redisTemplate.expire(BLACKLIST_PREFIX + token, ttl, TimeUnit.MILLISECONDS);
        }

        // Также удаляем из активных токенов
        String username = jwtService.extractUsername(token);
        redisTemplate.delete(TOKEN_PREFIX + username);
    }
    public TokenInfo getTokenInfo(String username) {
        return (TokenInfo) redisTemplate.opsForValue().get(TOKEN_PREFIX + username);
    }
}
