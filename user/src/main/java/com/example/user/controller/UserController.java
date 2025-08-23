package com.example.user.controller;

import com.example.user.model.User;
import com.example.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @PostMapping("/create")
  public ResponseEntity<?> addUser(@RequestBody User user, @RequestHeader(value = "Authorization", required = false) String authHeader) {
    return userService.createUser(user, authHeader);
  }
}
