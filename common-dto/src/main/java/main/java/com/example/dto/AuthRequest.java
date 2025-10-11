package main.java.com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthRequest {
    @JsonProperty("token")
    private String token;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    // Конструкторы
    public AuthRequest() {}

    public AuthRequest(String token, String username, String email) {
        this.token = token;
        this.username = username;
        this.email = email;
    }
    // Геттеры и сеттеры
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
