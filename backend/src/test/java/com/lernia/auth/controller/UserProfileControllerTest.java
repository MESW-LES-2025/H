package com.lernia.auth.controller;

import com.lernia.auth.dto.UserProfileRequest;
import com.lernia.auth.dto.UserProfileResponse;
import com.lernia.auth.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileControllerTest {

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOwnProfile_ReturnsOk() {
        when(authentication.getName()).thenReturn("john");
        UserProfileResponse profile = new UserProfileResponse();
        profile.setUsername("john");

        when(userProfileService.getProfileByUsername("john")).thenReturn(profile);

        ResponseEntity<UserProfileResponse> response =
                userProfileController.getOwnProfile(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(profile, response.getBody());

        verify(authentication, times(1)).getName();
        verify(userProfileService, times(1)).getProfileByUsername("john");
    }

    @Test
    void testUpdateOwnProfile_ReturnsUpdatedProfile() {
        when(authentication.getName()).thenReturn("maria");

        UserProfileRequest req = new UserProfileRequest();
        req.setName("Updated");

        UserProfileResponse updated = new UserProfileResponse();
        updated.setName("Updated");
        updated.setUsername("maria");

        when(userProfileService.updateProfileByUsername("maria", req))
                .thenReturn(updated);

        ResponseEntity<UserProfileResponse> response =
                userProfileController.updateOwnProfile(authentication, req);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(updated, response.getBody());

        verify(authentication).getName();
        verify(userProfileService).updateProfileByUsername("maria", req);
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
    void testGetByUsername_ReturnsProfile() {
        UserProfileResponse profile = new UserProfileResponse();
        profile.setUsername("alice");

        when(userProfileService.getProfileByUsername("alice")).thenReturn(profile);

        ResponseEntity<UserProfileResponse> response =
                userProfileController.getByUsername("alice");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("alice", response.getBody().getUsername());

        verify(userProfileService).getProfileByUsername("alice");
    }

    @Test
    void testUpdateById_ReturnsUpdatedProfile() {
        UserProfileRequest req = new UserProfileRequest();
        req.setName("Bob Updated");

        UserProfileResponse updated = new UserProfileResponse();
        updated.setId(7L);
        updated.setName("Bob Updated");

        when(userProfileService.updateProfile(7L, req)).thenReturn(updated);

        ResponseEntity<UserProfileResponse> response =
                userProfileController.update(7L, req);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(updated, response.getBody());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UserProfileRequest> reqCaptor = ArgumentCaptor.forClass(UserProfileRequest.class);

        verify(userProfileService).updateProfile(idCaptor.capture(), reqCaptor.capture());
        assertEquals(7L, idCaptor.getValue());
        assertSame(req, reqCaptor.getValue());
    }
}
