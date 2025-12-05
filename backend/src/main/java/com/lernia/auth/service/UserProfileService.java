package com.lernia.auth.service;

import com.lernia.auth.dto.EditProfileRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lernia.auth.dto.UserProfileResponse;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UserRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
@Service
@Transactional
public class UserProfileService {
    private final UserRepository userRepository;

    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse getProfileById(Long id) {
        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return map(user);
    }

    public UserProfileResponse getProfileByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return map(user);
    }

    public UserProfileResponse updateProfile(Long id, EditProfileRequest req) {
        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return updateUserProfile(user, req);
    }

    public UserProfileResponse updateProfileByUsername(String username, EditProfileRequest req) {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return updateUserProfile(user, req);
    }

    private UserProfileResponse updateUserProfile(UserEntity user, EditProfileRequest req) {
        if (req.getName() != null) user.setName(req.getName());
        if (req.getLocation() != null) user.setLocation(req.getLocation());
        if (req.getJobTitle() != null) user.setJobTitle(req.getJobTitle());
        if (req.getGender() != null) user.setGender(req.getGender());
        if (req.getAge() != null) user.setAge(req.getAge());
        userRepository.save(user);
        return map(user);
    }

    private UserProfileResponse map(UserEntity u) {
        UserProfileResponse r = new UserProfileResponse();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setName(u.getName());
        r.setEmail(u.getEmail());
        r.setAge(u.getAge());
        r.setGender(u.getGender() != null ? u.getGender() : null);
        r.setLocation(u.getLocation());
        r.setJobTitle(u.getJobTitle());
        r.setUserRole(u.getUserRole() != null ? u.getUserRole().name() : null);
        return r;
    }
}
