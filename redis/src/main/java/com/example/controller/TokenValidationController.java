package com.example.controller;

import com.example.service.RedisTokenService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens")
public class TokenValidationController {

    private final RedisTokenService redisTokenService;

    public TokenValidationController(RedisTokenService redisTokenService) {
        this.redisTokenService = redisTokenService;
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenValidationRequest request) {
        boolean isValid = redisTokenService.isTokenValid(request.getToken());

        TokenValidationResponse response = new TokenValidationResponse();
        response.setValid(isValid);

        if (isValid) {
            response.setMessage("Token is valid");
            return ResponseEntity.ok(response);
        } else {
            response.setMessage("Token is invalid or expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    @PostMapping("/invalidate")
    public ResponseEntity<String> invalidateToken(@RequestBody TokenValidationRequest request) {
        redisTokenService.invalidateToken(request.getToken());
        return ResponseEntity.ok("Token invalidated successfully");
    }

    @PostMapping("/store")
    public ResponseEntity<String> storeToken(@RequestBody TokenValidationRequest request) {
        redisTokenService.saveToken(request.getToken());
        return ResponseEntity.ok("Token stored successfully");
    }
}
@Data
class TokenValidationRequest {
    private String token;

    public String getToken() {
        return "";
    }
}

@Data
class TokenValidationResponse {
    private boolean valid;
    private String message;

    public void setValid(boolean isValid) {
    }
}
