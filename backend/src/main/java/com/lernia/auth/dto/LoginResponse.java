package com.lernia.auth.dto;

public class LoginResponse {
    private String message;
    private String status;
    private UserProfileResponse user;

    public LoginResponse() {}

    public LoginResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UserProfileResponse getUser() { return user; }
    public void setUser(UserProfileResponse user) { this.user = user; }

}
