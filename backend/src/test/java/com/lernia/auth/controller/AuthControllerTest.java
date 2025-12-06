package com.lernia.auth.controller;

import com.lernia.auth.dto.*;
import com.lernia.auth.service.AuthService;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------
    // /login
    // -------------------------------------------------------

    @Test
    void testLogin_DelegatesToServiceAndReturnsResponse() {
        LoginRequest req = new LoginRequest();
        req.setText("userOrEmail");
        req.setPassword("secret");

        LoginResponse serviceResponse =
                new LoginResponse("Login successful", "success");
        serviceResponse.setUser(new UserProfileResponse());
        serviceResponse.getUser().setId(123L);

        when(authService.login(any(LoginRequest.class), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(serviceResponse);

        LoginResponse controllerResponse = authController.login(req, request, response);

        assertNotNull(controllerResponse);
        assertEquals("Login successful", controllerResponse.getMessage());
        assertEquals("success", controllerResponse.getStatus());
        assertEquals(123L, controllerResponse.getUser().getId());

        ArgumentCaptor<LoginRequest> captor = ArgumentCaptor.forClass(LoginRequest.class);
        verify(authService, times(1)).login(captor.capture(), eq(request), eq(response));
        LoginRequest passedReq = captor.getValue();

        assertSame(req, passedReq);
    }

    @Test
    void testLogin_PropagatesErrorResponseFromService() {
        LoginRequest req = new LoginRequest();
        req.setText("wrong");
        req.setPassword("bad");

        LoginResponse errorResponse =
                new LoginResponse("Invalid credentials", "error");

        when(authService.login(eq(req), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(errorResponse);

        LoginResponse controllerResponse = authController.login(req, request, response);

        assertNotNull(controllerResponse);
        assertEquals("error", controllerResponse.getStatus());
        assertEquals("Invalid credentials", controllerResponse.getMessage());

        verify(authService, times(1)).login(req, request, response);
    }

    // -------------------------------------------------------
    // /register
    // -------------------------------------------------------

    @Test
    void testRegister_DelegatesToServiceAndReturnsResponse() {
        RegisterRequest req = new RegisterRequest(
                "Name",
                "newuser",
                "pass123",
                "new@example.com"
        );

        RegisterResponse serviceResponse =
                new RegisterResponse("User registered", "success");

        when(authService.register(any(RegisterRequest.class))).thenReturn(serviceResponse);

        RegisterResponse controllerResponse = authController.register(req);

        assertNotNull(controllerResponse);
        assertEquals("success", controllerResponse.getStatus());
        assertEquals("User registered", controllerResponse.getMessage());

        ArgumentCaptor<RegisterRequest> captor = ArgumentCaptor.forClass(RegisterRequest.class);
        verify(authService, times(1)).register(captor.capture());
        RegisterRequest passedReq = captor.getValue();

        assertSame(req, passedReq);
    }

    @Test
    void testRegister_PropagatesErrorResponseFromService() {
        RegisterRequest req = new RegisterRequest(
                "Name",
                "existingUser",
                "pass123",
                "taken@example.com"
        );

        RegisterResponse errorResponse =
                new RegisterResponse("Username already taken", "error");

        when(authService.register(req)).thenReturn(errorResponse);

        RegisterResponse controllerResponse = authController.register(req);

        assertNotNull(controllerResponse);
        assertEquals("error", controllerResponse.getStatus());
        assertEquals("Username already taken", controllerResponse.getMessage());

        verify(authService, times(1)).register(req);
    }

    @Test
    void testLogout_ReturnsOkAndDelegates() {
        ResponseEntity<?> responseEntity = authController.logout(request, response);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(Map.of("message", "Logged out successfully"), responseEntity.getBody());
        verify(authService).logout(request, response);
    }

    @Test
    void testDeleteAccount_ReturnsNoContent() {
        ResponseEntity<Void> responseEntity = authController.deleteAccount(88L);

        assertEquals(204, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
        verify(authService).deleteAccount(88L);
    }

    @Test
    void testGetCurrentUser_NotAuthenticated() {
        ResponseEntity<?> responseEntity = authController.getCurrentUser(null);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testGetCurrentUser_ReturnsUserInfo() {
        Principal principal = () -> "john";
        UserEntity user = new UserEntity();
        user.setId(5L);
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(java.util.Optional.of(user));

        ResponseEntity<?> responseEntity = authController.getCurrentUser(principal);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(Map.of("id", 5L, "username", "john"), responseEntity.getBody());
        verify(userRepository).findByUsername("john");
    }
}
