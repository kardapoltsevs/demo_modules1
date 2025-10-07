package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String EMAIL_KEY_PREFIX = "email:";
    private static final long EMAIL_CACHE_TTL = 3600;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isEmailCached(String email) {
        String key = EMAIL_KEY_PREFIX + email.toLowerCase();
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    // перенести из confl/RedisService
    public void cacheEmail(String email) {
        String key = EMAIL_KEY_PREFIX + email.toLowerCase();
        redisTemplate.opsForValue().set(key, "exist", EMAIL_CACHE_TTL, TimeUnit.SECONDS);
    }
    public void removeEmailFromCache(String email) {
        String key = EMAIL_KEY_PREFIX + email.toLowerCase();
        redisTemplate.delete(key);
    }

}
