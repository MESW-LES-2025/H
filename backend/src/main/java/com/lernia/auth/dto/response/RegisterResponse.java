package com.lernia.auth.dto.response;

public class RegisterResponse {
    private String message;
    private String status;
    private Long userId;

    public RegisterResponse() {
    }

    public RegisterResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public RegisterResponse(String message, String status, Long userId) {
        this.message = message;
        this.status = status;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
