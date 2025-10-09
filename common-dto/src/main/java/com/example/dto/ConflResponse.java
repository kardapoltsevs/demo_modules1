package main.java.com.example.dto;

public class ConflResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    // Конструкторы
    public ConflResponse() {}

    public ConflResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
    // Геттеры и сеттеры
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
