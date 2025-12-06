package com.lernia.auth.controller;

import com.lernia.auth.dto.ChangePasswordRequest;
import com.lernia.auth.dto.EditProfileRequest;
import com.lernia.auth.dto.UserProfileResponse;
import com.lernia.auth.service.AuthService; 
import com.lernia.auth.service.UserProfileService;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; 

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService service;
    private final AuthService authService; 

    public UserProfileController(UserProfileService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProfileById(id));
    }

    @PutMapping("/{id}/update-profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody EditProfileRequest request,
            Principal principal) {

        UserProfileResponse currentUser = service.getProfileByUsername(principal.getName());
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(service.updateProfile(id, request));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request,
            Principal principal) { 
        
        UserProfileResponse currentUser = service.getProfileByUsername(principal.getName());
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        authService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }
}
