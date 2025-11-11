package com.lernia.auth.controller;

import com.lernia.auth.dto.RegisterRequest;
import com.lernia.auth.dto.RegisterResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.lernia.auth.dto.LoginRequest;
import com.lernia.auth.dto.LoginResponse;
import com.lernia.auth.service.AuthService;

@RestController
@CrossOrigin(origins="http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
}