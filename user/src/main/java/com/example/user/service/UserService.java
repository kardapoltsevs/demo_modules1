package com.example.user.service;

import com.example.user.model.User;
import com.example.user.model.UserBody;
import com.example.user.model.UserRequest;
import com.example.user.model.UserResponse;
import com.example.user.repository.UserRepository;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.user.utils.JwtUtils;

@Service
@Builder
public class UserService {

  private final ConflService conflService;
  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;
  private final UserBody newUser;
  private final UserRequest userRequest;

  public UserService(ConflService conflService, UserRepository userRepository, JwtUtils jwtUtils, UserBody newUser, UserRequest userRequest) {
    this.conflService = conflService;
    this.userRepository = userRepository;
    this.jwtUtils = jwtUtils;
    this.newUser = newUser;
    this.userRequest = userRequest;
  }
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public ResponseEntity<?> createUser( String authHeader, UserRequest userRequest) {
    String token = jwtUtils.extractToken(authHeader);
    ResponseEntity<Map> response = conflService.checkEmail(userRequest.getEmail(), token);

    if (Boolean.TRUE.equals(response.getBody().get("available"))) {
      User savedUser = userRepository.save(
              User.builder()
                      .email(userRequest.getEmail())
                      .name(userRequest.getName())
                      .build()
      );
      return ResponseEntity.ok(
              UserResponse.builder()
                      .email(savedUser.getEmail())
                      .name(savedUser.getName())
                      .build()
      );
    } else {
      return ResponseEntity.unprocessableEntity()
              .body("Такой email уже зарегистрирован");
    }
  }
}
