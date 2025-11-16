package com.lernia.auth.controller;

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

    @GetMapping
    public ResponseEntity<UserProfileResponse> getOwnProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(service.getProfileByUsername(username));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateOwnProfile(Authentication authentication,
                                                                @RequestBody UserProfileRequest req) {
        String username = authentication.getName();
        return ResponseEntity.ok(service.updateProfileByUsername(username, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProfileById(id));
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserProfileResponse> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(service.getProfileByUsername(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> update(@PathVariable Long id,
                                                      @RequestBody UserProfileRequest req) {
        return ResponseEntity.ok(service.updateProfile(id, req));
    }
}
