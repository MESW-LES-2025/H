package com.lernia.login.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lernia.login.dto.LoginRequest;
import com.lernia.login.dto.LoginResponse;

@RestController
public class AuthController {

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        // This method is not functional at this stage
        return new LoginResponse("Login functionality not implemented yet");
    }
}