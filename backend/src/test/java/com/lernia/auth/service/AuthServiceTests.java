package com.lernia.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lernia.auth.dto.LoginRequest;
import com.lernia.auth.dto.LoginResponse;
import com.lernia.auth.dto.RegisterRequest;
import com.lernia.auth.dto.RegisterResponse;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

class AuthServiceTests {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest("Name", "newuser", "password123", "new@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity u = invocation.getArgument(0);
            u.setId(42L);
            return u;
        });

        RegisterResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("success", res.getStatus());
        assertEquals("User registered", res.getMessage());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository, times(1)).save(captor.capture());
        UserEntity saved = captor.getValue();

        assertEquals("newuser", saved.getUsername());
        assertEquals("Name", saved.getName());
        assertEquals("new@example.com", saved.getEmail());
        assertNotNull(saved.getPassword());
        // Ensure password was hashed (bcrypt hashes start with $2a$, $2b$ or $2y$)
        assertTrue(saved.getPassword().startsWith("$2a$") ||
                   saved.getPassword().startsWith("$2b$") ||
                   saved.getPassword().startsWith("$2y$"));
    }

    @Test
    void testRegisterUsernameTaken() {
        RegisterRequest req = new RegisterRequest("Name", "existing", "password", "e@example.com");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        RegisterResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Username already taken", res.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterEmailTaken() {
        RegisterRequest req = new RegisterRequest("Name", "user", "password", "taken@example.com");

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        RegisterResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Email already registered", res.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void testLoginSuccess() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "secret";
        String hashed = encoder.encode(rawPassword);

        UserEntity user = new UserEntity();
        user.setId(99L);
        user.setUsername("loginuser");
        user.setEmail("login@example.com");
        user.setPassword(hashed);
        user.setCreationDate(LocalDate.now());

        when(userRepository.findByUsername("loginuser")).thenReturn(Optional.of(user));

        LoginRequest req = new LoginRequest();
        req.setText("loginuser");
        req.setPassword(rawPassword);

        LoginResponse res = authService.login(req);

        assertNotNull(res);
        assertEquals("success", res.getStatus());
        assertEquals("Login successful", res.getMessage());
        assertEquals(99L, res.getUserId());
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("noone")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("noone")).thenReturn(Optional.empty());

        LoginRequest req = new LoginRequest();
        req.setText("noone");
        req.setPassword("whatever");

        LoginResponse res = authService.login(req);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Invalid credentials", res.getMessage());
    }

    @Test
    void testLoginWrongPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode("rightpass");

        UserEntity user = new UserEntity();
        user.setId(11L);
        user.setUsername("user1");
        user.setPassword(hashed);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        LoginRequest req = new LoginRequest();
        req.setText("user1");
        req.setPassword("wrongpass");

        LoginResponse res = authService.login(req);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Invalid credentials", res.getMessage());
    }
}