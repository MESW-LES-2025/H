package com.lernia.auth.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.lernia.auth.dto.request.ChangePasswordRequest;
import com.lernia.auth.dto.request.ForgotPasswordRequest;
import com.lernia.auth.dto.request.ResetPasswordRequest;
import com.lernia.auth.dto.response.PasswordResetTokenResponse;
import com.lernia.auth.dto.request.LoginRequest;
import com.lernia.auth.dto.request.RegisterRequest;
import com.lernia.auth.dto.response.LoginResponse;
import com.lernia.auth.dto.response.RegisterResponse;
import com.lernia.auth.dto.response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.enums.AuthProvider;
import com.lernia.auth.entity.enums.Gender;
import com.lernia.auth.entity.enums.UserRole;
import com.lernia.auth.repository.UserRepository;
import com.lernia.auth.entity.PasswordResetTokenEntity;
import com.lernia.auth.service.PasswordResetTokenService.GeneratedToken;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            SecurityContextRepository securityContextRepository,
            PasswordResetTokenService passwordResetTokenService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailService = emailService;
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
        user.setProvider(AuthProvider.LOCAL);
        user.setProviderId(null);
        user.setGender(Gender.OTHER);
        user.setUserRole(UserRole.REGULAR);
        user.setCreationDate(LocalDate.now());

        UserEntity savedUser = userRepository.save(user);
        return new RegisterResponse("User registered", "success", savedUser.getId());
    }

    public LoginResponse login(LoginRequest req, HttpServletRequest request, HttpServletResponse response) {
        String text = req.getText();
        Optional<UserEntity> userOpt = userRepository.findByUsername(text);
        if (userOpt.isEmpty())
            userOpt = userRepository.findByEmail(text);
        if (userOpt.isEmpty()) {
            return new LoginResponse("Invalid credentials", "error");
        }

        UserEntity user = userOpt.get();

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return new LoginResponse("Invalid credentials", "error");
        }

        // --- Create Session ---
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        String roleName = (user.getUserRole() != null) ? user.getUserRole().name() : UserRole.REGULAR.name();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + roleName)));
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        UserProfileResponse profile = map(user);

        LoginResponse res = new LoginResponse("Login successful", "success");
        res.setUser(profile);
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

    public void changePassword(Long userId, ChangePasswordRequest req) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getCurrentPassword() == null || req.getCurrentPassword().trim().isEmpty() ||
                req.getNewPassword() == null || req.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Passwords cannot be empty");
        }

        if (req.getCurrentPassword().equals(req.getNewPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the current password");
        }

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect current password");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    public PasswordResetTokenResponse requestPasswordReset(ForgotPasswordRequest req) {
        String message = "If an account exists for that email, we have sent reset instructions.";

        userRepository.findByEmail(req.getEmail()).ifPresent(user -> {
            GeneratedToken generated = passwordResetTokenService.createToken(user);
            String resetLink = frontendUrl + "/reset-password?token=" + generated.token();
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        });

        return new PasswordResetTokenResponse(message);
    }

    public void resetPassword(ResetPasswordRequest req) {
        PasswordResetTokenEntity token = passwordResetTokenService.validate(req.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));
        UserEntity user = token.getUser();
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        passwordResetTokenService.consume(token);
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
        r.setJobTitle(u.getJobTitle());
        r.setUserRole(u.getUserRole() != null ? u.getUserRole().name() : null);
        r.setProvider(u.getProvider() != null ? u.getProvider().name() : null);
        r.setPremium(u.getUserRole() != null && "PREMIUM".equals(u.getUserRole().name()));
        return r;
    }

    public void adminResetPassword(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalStateException("User does not have an email address");
        }

        GeneratedToken generated = passwordResetTokenService.createToken(user);
        String resetLink = frontendUrl + "/reset-password?token=" + generated.token();
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }
}