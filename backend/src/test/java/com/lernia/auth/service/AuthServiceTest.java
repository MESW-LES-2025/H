package com.lernia.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private SecurityContextRepository securityContextRepository; 

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
        
        when(request.getSession(true)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password_placeholder");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        doNothing().when(securityContextRepository)
            .saveContext(any(SecurityContext.class), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest("Name", "newuser", "password123", "new@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$mockedhashvalue");

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
        assertEquals("$2a$10$mockedhashvalue", saved.getPassword());
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
        String rawPassword = "secret";
        String hashed = "$2a$10$hashedsecret";

        UserEntity user = new UserEntity();
        user.setId(99L);
        user.setUsername("loginuser");
        user.setEmail("login@example.com");
        user.setPassword(hashed);
        user.setCreationDate(LocalDate.now());

        when(userRepository.findByUsername("loginuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, hashed)).thenReturn(true);

        LoginRequest req = new LoginRequest();
        req.setText("loginuser");
        req.setPassword(rawPassword);

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("success", res.getStatus());
        assertEquals("Login successful", res.getMessage());
        assertEquals(99L, res.getUserId());

        ArgumentCaptor<SecurityContext> contextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
        verify(securityContextRepository).saveContext(contextCaptor.capture(), eq(request), eq(response));
        SecurityContext context = contextCaptor.getValue();
        assertNotNull(context.getAuthentication());
        assertEquals("loginuser", context.getAuthentication().getPrincipal());
        assertTrue(context.getAuthentication().isAuthenticated());
    }

    @Test
    void testLoginUserNotFound() {
        
        LoginRequest req = new LoginRequest();
        req.setText("noone");
        req.setPassword("whatever");

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Invalid credentials", res.getMessage());

        verifyNoInteractions(securityContextRepository);
    }

    @Test
    void testLoginWrongPassword() {
        String hashed = "$2a$10$hashedpass";

        UserEntity user = new UserEntity();
        user.setId(11L);
        user.setUsername("user1");
        user.setPassword(hashed);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", hashed)).thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setText("user1");
        req.setPassword("wrongpass");

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Invalid credentials", res.getMessage());

        verifyNoInteractions(securityContextRepository);
    }

    @Test
    void testLoginWithEmailSuccess() {
        String rawPassword = "secret-email";
        String hashed = "$2a$10$hashedemailpass";

        UserEntity user = new UserEntity();
        user.setId(123L);
        user.setUsername("userEmail");
        user.setEmail("email@example.com");
        user.setPassword(hashed);
        user.setCreationDate(LocalDate.now());

        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(user));
        
        when(passwordEncoder.matches(rawPassword, hashed)).thenReturn(true);

        LoginRequest req = new LoginRequest();
        req.setText("email@example.com");
        req.setPassword(rawPassword);

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("success", res.getStatus());
        assertEquals("Login successful", res.getMessage());
        assertEquals(123L, res.getUser().getId());

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
        String hashed = "$2a$10$hashedpass2";

        UserEntity user = new UserEntity();
        user.setId(555L);
        user.setUsername("userEmail");
        user.setEmail("email2@example.com");
        user.setPassword(hashed);
        user.setCreationDate(LocalDate.now());

        when(userRepository.findByEmail("email2@example.com")).thenReturn(Optional.of(user));
        
        when(passwordEncoder.matches("wrong-pass", hashed)).thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setText("email2@example.com");
        req.setPassword("wrong-pass");

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("error", res.getStatus());
        assertEquals("Invalid credentials", res.getMessage());

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
        String rawPassword = "plain-pass";
        String hashed = "$2a$10$hashedplain";

        UserEntity user = new UserEntity();
        user.setId(321L);
        user.setUsername("simpleuser");
        user.setPassword(hashed);

        when(userRepository.findByUsername("simpleuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, hashed)).thenReturn(true);

        LoginRequest req = new LoginRequest();
        req.setText("simpleuser");
        req.setPassword(rawPassword);

        LoginResponse res = authService.login(req, request, response);

        assertNotNull(res);
        assertEquals("success", res.getStatus());
        assertEquals("Login successful", res.getMessage());
        assertEquals(321L, res.getUser().getId());

        verify(userRepository, times(1)).findByUsername("simpleuser");
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void logout_ShouldClearSecurityContext() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        authService.logout(request, response);

        ArgumentCaptor<SecurityContext> contextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
        verify(securityContextRepository).saveContext(contextCaptor.capture(), eq(request), eq(response));
        
        SecurityContext capturedContext = contextCaptor.getValue();
        assertNull(capturedContext.getAuthentication(), "Authentication should be null after logout");
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Holder should also be cleared");
    }

    @Test
    void testDeleteAccount_UserExists() {
        when(userRepository.existsById(55L)).thenReturn(true);

        authService.deleteAccount(55L);

        verify(userRepository).existsById(55L);
        verify(userRepository).deleteById(55L);
    }

    @Test
    void testDeleteAccount_UserMissingThrows() {
        when(userRepository.existsById(77L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.deleteAccount(77L));
        assertEquals("User not found", ex.getMessage());

        verify(userRepository).existsById(77L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}