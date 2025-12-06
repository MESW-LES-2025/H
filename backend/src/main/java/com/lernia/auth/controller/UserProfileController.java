package com.lernia.auth.controller;

import com.lernia.auth.dto.ChangePasswordRequest;
import com.lernia.auth.dto.EditProfileRequest;
import com.lernia.auth.dto.UserProfileResponse;
import com.lernia.auth.service.AuthService; 
import com.lernia.auth.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody EditProfileRequest request) {

        return ResponseEntity.ok(service.updateProfile(id, request));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        authService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }
}
