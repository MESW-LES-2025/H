package com.lernia.auth.service;

import java.time.LocalDate;
import java.util.Optional;

import com.lernia.auth.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.enums.Gender;
import com.lernia.auth.entity.enums.UserRole;
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

        UserProfileResponse profile = map(user);

        LoginResponse res = new LoginResponse("Login successful", "success");
        res.setUser(profile);
        return res;
    }

    private UserProfileResponse map(UserEntity u) {
        UserProfileResponse r = new UserProfileResponse();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setName(u.getName());
        r.setEmail(u.getEmail());
        r.setAge(u.getAge());
        r.setGender(u.getGender() != null ? u.getGender() : null);
        r.setLocation(u.getLocation());
        r.setProfilePicture(u.getProfilePicture());
        r.setJobTitle(u.getJobTitle());
        r.setCreationDate(u.getCreationDate());
        r.setUserRole(u.getUserRole() != null ? u.getUserRole().name() : null);
        r.setPremiumStartDate(u.getPremiumStartDate());
        return r;
    }
}