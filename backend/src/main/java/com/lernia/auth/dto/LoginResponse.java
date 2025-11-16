package com.lernia.auth.dto;

public class LoginResponse {
    private Long userId;
    private String message;
    private String status;
    private String username;
    private String role;

    public LoginResponse() {}

    public LoginResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
