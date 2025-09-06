package com.example.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JwtService {

    private final RestTemplate restTemplate;
    @Value("${jwt.url}")
    public
    String jwtUrl;

    public JwtService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getToken() {
        return restTemplate.getForObject(jwtUrl + "/token", String.class);
    }
}
