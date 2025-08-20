package com.example.user.service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.example.user.utils.JwtUtils;

@Service
public class UserService {

  private final ConflService conflService;
  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;

  public UserService(ConflService conflService, UserRepository userRepository, JwtUtils jwtUtils) {
    this.conflService = conflService;
    this.userRepository = userRepository;
    this.jwtUtils = jwtUtils;
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public ResponseEntity<?> createUser(@Validated @RequestBody User newUser,
                                      @RequestHeader("Authorization") String authHeader) {
    // Извлекаем токен из заголовка Authorization (формат: "Bearer <token>")
    //String token = authHeader.substring(7); // Удаляем "Bearer " префикс
    String token = jwtUtils.extractToken(authHeader);

    ResponseEntity<Map> response = conflService.checkEmail(newUser.getEmail(), token);

    if (Boolean.TRUE.equals(response.getBody().get("available"))) {
      return ResponseEntity.ok(userRepository.save(newUser));
    } else {
      return ResponseEntity.unprocessableEntity()
              .body("Такой email уже зарегистрирован");
    }
  }
}
