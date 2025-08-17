package com.example.confl.controller;

import com.example.confl.model.Email;
import com.example.confl.service.ConflService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<?> checkEmail(@RequestParam String email) {
    boolean available = conflService.isEmailAvailable(email);
    return ResponseEntity.ok().body(Map.of("available", available));
  }
}
