package com.lernia.auth.controller;

import com.lernia.auth.dto.request.RegisterRequest;
import com.lernia.auth.dto.request.ResetPasswordRequest;
import com.lernia.auth.dto.response.RegisterResponse;
import com.lernia.auth.dto.request.ForgotPasswordRequest;
import com.lernia.auth.dto.request.LoginRequest;
import com.lernia.auth.dto.response.LoginResponse;
import com.lernia.auth.dto.response.PasswordResetTokenResponse;
import com.lernia.auth.service.AuthService;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@CrossOrigin
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        return authService.login(loginRequest, request, response);
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @DeleteMapping("/api/profile/delete/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        authService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(null);
        }

        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("provider", user.getProvider() != null ? user.getProvider().name() : "LOCAL");

        if (user.getName() != null) {
            result.put("name", user.getName());
        }
        if (user.getEmail() != null) {
            result.put("email", user.getEmail());
        }
        if (user.getUserRole() != null) {
            result.put("userRole", user.getUserRole().name());
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/auth/password/forgot")
    public ResponseEntity<PasswordResetTokenResponse> requestPasswordReset(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.requestPasswordReset(request));
    }

    @PostMapping("/api/auth/password/reset")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}
