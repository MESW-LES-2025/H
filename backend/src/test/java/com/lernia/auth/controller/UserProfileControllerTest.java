package com.lernia.auth.controller;

import com.lernia.auth.dto.ChangePasswordRequest;
import com.lernia.auth.dto.EditProfileRequest;
import com.lernia.auth.dto.UserProfileResponse;
import com.lernia.auth.service.AuthService;
import com.lernia.auth.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.security.Principal; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileControllerTest {

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AuthService authService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetById_ReturnsProfile() {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(10L);

        when(userProfileService.getProfileById(10L)).thenReturn(profile);

        ResponseEntity<UserProfileResponse> response =
                userProfileController.getById(10L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(10L, response.getBody().getId());

        verify(userProfileService).getProfileById(10L);
    }

    @Test
    void testUpdateById_ReturnsUpdatedProfile() {
        EditProfileRequest req = new EditProfileRequest(null, null, null, null, null, null);
        req.setName("Bob Updated");

        UserProfileResponse updated = new UserProfileResponse();
        updated.setId(7L);
        updated.setName("Bob Updated");

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("userBob");
        
        UserProfileResponse currentUser = new UserProfileResponse();
        currentUser.setId(7L);
        when(userProfileService.getProfileByUsername("userBob")).thenReturn(currentUser);

        when(userProfileService.updateProfile(7L, req)).thenReturn(updated);

        ResponseEntity<UserProfileResponse> response =
                userProfileController.updateProfile(7L, req, principal);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(updated, response.getBody());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EditProfileRequest> reqCaptor = ArgumentCaptor.forClass(EditProfileRequest.class);

        verify(userProfileService).updateProfile(idCaptor.capture(), reqCaptor.capture());
        assertEquals(7L, idCaptor.getValue());
        assertSame(req, reqCaptor.getValue());
    }

    @Test
    void testUpdateById_Forbidden_WhenIdsMismatch() {
        Long targetId = 7L;
        EditProfileRequest req = new EditProfileRequest(null, null, null, null, null, null);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("otherUser");
        
        UserProfileResponse currentUser = new UserProfileResponse();
        currentUser.setId(999L); 
        when(userProfileService.getProfileByUsername("otherUser")).thenReturn(currentUser);

        ResponseEntity<UserProfileResponse> response =
                userProfileController.updateProfile(targetId, req, principal);

        assertEquals(403, response.getStatusCodeValue());
        verify(userProfileService, never()).updateProfile(anyLong(), any());
    }

    @Test
    void testChangePassword_ReturnsOk() {
        Long userId = 1L;
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("oldPass");
        req.setNewPassword("newPass");

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");
        
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(userId);
        when(userProfileService.getProfileByUsername("user1")).thenReturn(profile);

        doNothing().when(authService).changePassword(userId, req);

        ResponseEntity<Void> response = userProfileController.changePassword(userId, req, principal);

        assertEquals(200, response.getStatusCodeValue());
        verify(authService, times(1)).changePassword(userId, req);
    }

    @Test
    void testChangePassword_Forbidden_WhenUserIdsDoNotMatch() {
        Long targetUserId = 1L;
        Long loggedInUserId = 2L;
        ChangePasswordRequest req = new ChangePasswordRequest();
        
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user2");
        
        UserProfileResponse profile = new UserProfileResponse();
        profile.setId(loggedInUserId); 
        when(userProfileService.getProfileByUsername("user2")).thenReturn(profile);

        ResponseEntity<Void> response = userProfileController.changePassword(targetUserId, req, principal);

        assertEquals(403, response.getStatusCodeValue());
        verify(authService, never()).changePassword(anyLong(), any());
    }
}
