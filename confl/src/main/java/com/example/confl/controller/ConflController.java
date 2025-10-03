package com.example.confl.controller;

import com.example.confl.model.Email;
import com.example.confl.service.ConflService;
import com.example.confl.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/confl")
public class ConflController {

    private final ConflService conflService;

    public ConflController(ConflService conflService) {
        this.conflService = conflService;
    }

    @GetMapping
    public List<Email> getAllEmails() {
        return conflService.getAllEmails();
    }

    @GetMapping("/email/check")
    public ResponseEntity<?> checkEmail(@RequestParam String email,
                                        @RequestHeader("X-Auth-Token") String token) {
        try {
            boolean available = conflService.isEmailAvailable(email, token);
            return ResponseEntity.ok().body(Map.of("available", available));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
