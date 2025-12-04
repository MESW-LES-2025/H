package com.lernia.auth.controller;

import com.lernia.auth.dto.RegisterRequest;
import com.lernia.auth.dto.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping; 
import com.lernia.auth.dto.LoginRequest;
import com.lernia.auth.dto.LoginResponse;
import com.lernia.auth.service.AuthService;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth") 
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        return authService.login(loginRequest, request, response);
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }

        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "username", user.getUsername()
        ));
    }
}