package com.lernia.auth.dto;

public class LoginRequest {
    private String text;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String text, String password) {
        this.text = text;
        this.password = password;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}