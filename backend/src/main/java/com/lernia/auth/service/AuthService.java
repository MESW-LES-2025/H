package com.lernia.auth.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import com.lernia.auth.dto.LoginRequest;
import com.lernia.auth.dto.LoginResponse;
import com.lernia.auth.dto.RegisterRequest;
import com.lernia.auth.dto.RegisterResponse;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.enums.Gender;
import com.lernia.auth.entity.enums.UserRole;
import com.lernia.auth.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, SecurityContextRepository securityContextRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
    }

    public RegisterResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return new RegisterResponse("Username already taken", "error");
        }
        if (req.getEmail() != null && userRepository.existsByEmail(req.getEmail())) {
            return new RegisterResponse("Email already registered", "error");
        }

        String hash = passwordEncoder.encode(req.getPassword());
        UserEntity user = new UserEntity();
        user.setUsername(req.getUsername());
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(hash);
        user.setGender(Gender.OTHER);
        user.setUserRole(UserRole.REGULAR);
        user.setCreationDate(LocalDate.now());

        userRepository.save(user);
        return new RegisterResponse("User registered", "success");
    }
/*
    public LoginResponse login(LoginRequest req) {
        String text = req.getText();
        Optional<UserEntity> userOpt = userRepository.findByUsername(text);
        if (userOpt.isEmpty()) userOpt = userRepository.findByEmail(text);
        if (userOpt.isEmpty()) {
            return new LoginResponse("Invalid credentials", "error");
        }

        UserEntity user = userOpt.get();

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return new LoginResponse("Invalid credentials", "error");
        }

        LoginResponse res = new LoginResponse("Login successful", "success");
        res.setUserId(user.getId());
        return res;
    }*/

    public LoginResponse login(LoginRequest req, HttpServletRequest request, HttpServletResponse response) {
        String text = req.getText();
        Optional<UserEntity> userOpt = userRepository.findByUsername(text);
        if (userOpt.isEmpty()) userOpt = userRepository.findByEmail(text);
        if (userOpt.isEmpty()) {
            return new LoginResponse("Invalid credentials", "error");
        }

        UserEntity user = userOpt.get();

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return new LoginResponse("Invalid credentials", "error");
        }

        // --- Create Session ---
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            user.getUsername(), 
            null, 
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()))
        );
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
        
        securityContextRepository.saveContext(context, request, response);
        // ----------------------

        LoginResponse res = new LoginResponse("Login successful", "success");
        res.setUserId(user.getId());
        res.setUsername(user.getUsername());
        res.setRole(user.getUserRole().name());
        return res;
    }


    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    public void deleteAccount(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }

}