package com.lernia.auth.dto;

public class LoginResponse {
    private Long userId;
    private String message;
    private String status;

    public LoginResponse() {}
    public LoginResponse(String message, String status) {
        this.message = message; this.status = status;
    }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}