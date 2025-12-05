package com.lernia.auth.controller;

import com.lernia.auth.dto.EditProfileRequest;
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

        when(userProfileService.updateProfile(7L, req)).thenReturn(updated);

        ResponseEntity<UserProfileResponse> response =
                userProfileController.updateProfile(7L, req);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(updated, response.getBody());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EditProfileRequest> reqCaptor = ArgumentCaptor.forClass(EditProfileRequest.class);

        verify(userProfileService).updateProfile(idCaptor.capture(), reqCaptor.capture());
        assertEquals(7L, idCaptor.getValue());
        assertSame(req, reqCaptor.getValue());
    }
}
