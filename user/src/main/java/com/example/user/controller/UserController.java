package com.example.user.controller;

import com.example.user.model.User;
import com.example.user.request.UserRequest;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> addUser(@Validated @RequestBody UserRequest userRequest, @RequestHeader(value = "Authorization") String authHeader) {
        return userService.createUser(authHeader, userRequest);

    }
}
