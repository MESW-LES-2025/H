package com.lernia.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lernia.auth.dto.LoginRequest;
import com.lernia.auth.dto.LoginResponse;
import com.lernia.auth.dto.RegisterRequest;
import com.lernia.auth.dto.RegisterResponse;
import com.lernia.auth.model.User;
import com.lernia.auth.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return new RegisterResponse("Username already taken", "error");
        }
        if (req.getEmail() != null && userRepository.existsByEmail(req.getEmail())) {
            return new RegisterResponse("Email already registered", "error");
        }
        String hash = passwordEncoder.encode(req.getPassword());
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(hash);
        user.setEmail(req.getEmail());
        userRepository.save(user);
        return new RegisterResponse("User registered", "success");
    }

    public LoginResponse login(LoginRequest req) {
        Optional<User> userOpt = userRepository.findByUsername(req.getUsername());
        if (userOpt.isEmpty()) {
            return new LoginResponse("Invalid credentials", "error");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return new LoginResponse("Invalid credentials", "error");
        }
        // TODO: generate token or session when integrating with frontend
        return new LoginResponse("Login successful (no token yet)", "success");
    }
}
