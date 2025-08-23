package com.example.user.utils;

import org.springframework.stereotype.Component;
@Component
public class JwtUtils {
    public String extractToken(String authHeader) {

        return authHeader.substring(7);
    }
}
