package com.example.user.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Service
public class ConflService {

  private final RestTemplate restTemplate;
  @Value("${confl.url}")
  String conflUrl;

  public ConflService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public ResponseEntity<Map> checkEmail(String email, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    HttpEntity<Void> requestHeaders = new HttpEntity<>(headers);

    return restTemplate
        .exchange(
            conflUrl + "/api/confl/email/check?email=" + email,
            org.springframework.http.HttpMethod.GET,
            requestHeaders,
            Map.class
        );
  }
}
