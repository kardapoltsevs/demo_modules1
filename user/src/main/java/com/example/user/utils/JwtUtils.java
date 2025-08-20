package com.example.user.utils;

public class JwtUtils {
    private String extractToken(String authHeader) {
        if (authHeader == null) {
            throw new IllegalArgumentException("Authorization header is required");
        }
        if (!authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");
        }
        if (authHeader.length() <= 7) {
            throw new IllegalArgumentException("Token is missing in Authorization header");
        }
        return authHeader.substring(7);
    }
}
