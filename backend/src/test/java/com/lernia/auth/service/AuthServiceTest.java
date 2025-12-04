package com.lernia.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.lernia.auth.dto.LoginRequest;
import com.lernia.auth.dto.LoginResponse;
import com.lernia.auth.dto.RegisterRequest;
import com.lernia.auth.dto.RegisterResponse;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.enums.Gender;
import com.lernia.auth.entity.enums.UserRole;
import com.lernia.auth.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getSession(true)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
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

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("success", res.getStatus());
        assertEquals("Login successful", res.getMessage());
        assertEquals(99L, res.getUserId());
        verify(session).setAttribute("user", "loginuser");
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("noone")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("noone")).thenReturn(Optional.empty());

        LoginRequest req = new LoginRequest();
        req.setText("noone");
        req.setPassword("whatever");

        LoginResponse res = authService.login(req, request, response);

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

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Invalid credentials", res.getMessage());
    }
    @Test
    void testLoginWithEmailSuccess() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "secret-email";
        String hashed = encoder.encode(rawPassword);

        UserEntity user = new UserEntity();
        user.setId(123L);
        user.setUsername("userEmail");
        user.setEmail("email@example.com");
        user.setPassword(hashed);
        user.setCreationDate(LocalDate.now());

        when(userRepository.findByUsername("email@example.com"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("email@example.com"))
                .thenReturn(Optional.of(user));

        LoginRequest req = new LoginRequest();
        req.setText("email@example.com");
        req.setPassword(rawPassword);

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("success", res.getStatus());
        assertEquals("Login successful", res.getMessage());
        assertEquals(123L, res.getUserId());

        verify(userRepository).findByUsername("email@example.com");
        verify(userRepository).findByEmail("email@example.com");
    }

    @Test
    void testRegisterUsernameAndEmailTakenStillReturnsUsernameError() {
        RegisterRequest req = new RegisterRequest(
                "Name",
                "existingUser",
                "password123",
                "taken@example.com"
        );

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        RegisterResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Username already taken", res.getMessage());

        verify(userRepository, never()).save(any());
    }
    @Test
    void testRegisterChecksUsernameAndEmail() {
        RegisterRequest req = new RegisterRequest(
                "Name",
                "someUser",
                "somePassword",
                "some@example.com"
        );

        when(userRepository.existsByUsername("someUser")).thenReturn(false);
        when(userRepository.existsByEmail("some@example.com")).thenReturn(false);

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        RegisterResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("success", res.getStatus());

        verify(userRepository, times(1)).existsByUsername("someUser");
        verify(userRepository, times(1)).existsByEmail("some@example.com");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
    @Test
    void testLoginUserNotFoundChecksUsernameAndEmail() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ghost")).thenReturn(Optional.empty());

        LoginRequest req = new LoginRequest();
        req.setText("ghost");
        req.setPassword("whatever");

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Invalid credentials", res.getMessage());

        verify(userRepository, times(1)).findByUsername("ghost");
        verify(userRepository, times(1)).findByEmail("ghost");
    }
    @Test
    void testLoginWithEmailWrongPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String correctPassword = "correct-pass";
        String hashed = encoder.encode(correctPassword);

        UserEntity user = new UserEntity();
        user.setId(555L);
        user.setUsername("userEmail");
        user.setEmail("email2@example.com");
        user.setPassword(hashed);
        user.setCreationDate(LocalDate.now());

        when(userRepository.findByUsername("email2@example.com"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("email2@example.com"))
                .thenReturn(Optional.of(user));

        LoginRequest req = new LoginRequest();
        req.setText("email2@example.com");
        req.setPassword("wrong-pass");

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Invalid credentials", res.getMessage());

        verify(userRepository, times(1)).findByUsername("email2@example.com");
        verify(userRepository, times(1)).findByEmail("email2@example.com");
    }

    @Test
    void testRegisterWithoutEmail_SuccessAndDoesNotCheckEmail() {
        RegisterRequest req = new RegisterRequest(
                "Name",
                "userNoEmail",
                "password123",
                null
        );

        when(userRepository.existsByUsername("userNoEmail")).thenReturn(false);

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity u = invocation.getArgument(0);
            u.setId(7L);
            return u;
        });

        RegisterResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("success", res.getStatus());
        assertEquals("User registered", res.getMessage());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity saved = captor.getValue();

        assertEquals("userNoEmail", saved.getUsername());
        assertNull(saved.getEmail());

        verify(userRepository, times(1)).existsByUsername("userNoEmail");
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void testRegisterSetsDefaultGenderRoleAndCreationDate() {
        RegisterRequest req = new RegisterRequest(
                "Name",
                "roleUser",
                "pass123",
                "role@example.com"
        );

        when(userRepository.existsByUsername("roleUser")).thenReturn(false);
        when(userRepository.existsByEmail("role@example.com")).thenReturn(false);

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        LocalDate today = LocalDate.now();

        RegisterResponse res = authService.register(req);

        assertNotNull(res);
        assertEquals("success", res.getStatus());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity saved = captor.getValue();

        assertEquals(Gender.OTHER, saved.getGender());
        assertEquals(UserRole.REGULAR, saved.getUserRole());
        assertNotNull(saved.getCreationDate());
        assertEquals(today, saved.getCreationDate());
    }

    @Test
    void testLoginByUsernameDoesNotCallFindByEmail() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "plain-pass";
        String hashed = encoder.encode(rawPassword);

        UserEntity user = new UserEntity();
        user.setId(321L);
        user.setUsername("simpleuser");
        user.setPassword(hashed);

        when(userRepository.findByUsername("simpleuser")).thenReturn(Optional.of(user));

        LoginRequest req = new LoginRequest();
        req.setText("simpleuser");
        req.setPassword(rawPassword);

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("success", res.getStatus());
        assertEquals("Login successful", res.getMessage());
        assertEquals(321L, res.getUserId());

        verify(userRepository, times(1)).findByUsername("simpleuser");
        verify(userRepository, never()).findByEmail(anyString());
    }

}