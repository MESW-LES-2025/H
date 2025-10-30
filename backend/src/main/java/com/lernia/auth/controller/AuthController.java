package com.lernia.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lernia.auth.dto.LoginRequest;
import com.lernia.auth.dto.LoginResponse;

@RestController
public class AuthController {

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
    // Retorna mensagem padrão já que funcionalidade não está implementada
        return new LoginResponse("Login funcionalidade não implementada ainda", "info");
    }

}