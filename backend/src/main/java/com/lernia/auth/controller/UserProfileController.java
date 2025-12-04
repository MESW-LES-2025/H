package com.lernia.auth.controller;

import com.lernia.auth.dto.EditProfileRequest;
import com.lernia.auth.dto.UpdateUserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.lernia.auth.dto.UserProfileRequest;
import com.lernia.auth.dto.UserProfileResponse;
import com.lernia.auth.service.UserProfileService;


@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    /*@GetMapping
    public ResponseEntity<UserProfileResponse> getOwnProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(service.getProfileByUsername(username));
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProfileById(id));
    }

    @PutMapping("/{id}/update-profile")
    public ResponseEntity<UpdateUserProfileResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody EditProfileRequest request) {

        return ResponseEntity.ok(service.updateProfile(id, request));
    }
}
