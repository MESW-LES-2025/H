package com.lernia.auth.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
        if (userOpt.isEmpty()) return new LoginResponse("Invalid credentials", "error");

        UserEntity user = userOpt.get();
        String stored = user.getPassword();
        boolean isBcrypt = stored != null && stored.matches("^\\$2[aby]\\$\\d{2}\\$.*");

        if (!isBcrypt) {
            if (stored != null && stored.equals(req.getPassword())) {
                user.setPassword(passwordEncoder.encode(req.getPassword())); 
                userRepository.save(user);
            } else {
                return new LoginResponse("Invalid credentials", "error");
            }
        } else if (!passwordEncoder.matches(req.getPassword(), stored)) {
            return new LoginResponse("Invalid credentials", "error");
        }
        LoginResponse res = new LoginResponse("Login successful", "success");
        res.setUserId(user.getId());          
        return res;    
    }
}
