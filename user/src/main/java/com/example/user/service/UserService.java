package com.example.user.service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import com.example.user.request.UserRequest;
import com.example.user.response.UserResponse;
import com.example.user.utils.JwtUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ConflService conflService;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserService(UserRepository userRepository, JwtService jwtService,
                       ConflService conflServiceClient, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.conflService = conflServiceClient;
        this.redisTemplate = redisTemplate;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User authenticate(String token, String username, String email) {
        // Проверяем токен через JWT сервис (который использует Redis)
        if (!jwtService.validateToken(token, email)) {
            throw new SecurityException("Invalid token");
        }

        // Проверяем в модуле confl
        ConflResponse conflResponse = conflServiceClient.checkEmail(token, email);

        if ("available".equals(conflResponse.getStatus())) {
            // Создаем нового пользователя
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setToken(token);

            User savedUser = userRepository.save(newUser);

            // Кешируем информацию о пользователе
            cacheUserInfo(savedUser);

            return savedUser;
        }

        throw new RuntimeException("Email not available or user already exists");
    }

    @Cacheable(value = "users", key = "#email")
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    private void cacheUserInfo(User user) {
        String userKey = "user:" + user.getEmail();
        redisTemplate.opsForValue().set(userKey, user, Duration.ofHours(1));
    }

    @CacheEvict(value = "users", key = "#email")
    public void logout(String email) {
        jwtService.invalidateToken(email);
    }
}
