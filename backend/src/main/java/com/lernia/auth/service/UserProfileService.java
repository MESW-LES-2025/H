package com.lernia.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lernia.auth.dto.UserProfileRequest;
import com.lernia.auth.dto.UserProfileResponse;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UserRepository;

import java.util.Optional;

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

    public UserProfileResponse updateProfile(Long id, UserProfileRequest req) {
        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (req.getName() != null) user.setName(req.getName());
        if (req.getLocation() != null) user.setLocation(req.getLocation());
        if (req.getJobTitle() != null) user.setJobTitle(req.getJobTitle());
        if (req.getProfilePicture() != null) user.setProfilePicture(req.getProfilePicture());
        if (req.getGender() != null) user.setGender(req.getGender());
        userRepository.save(user);
        return map(user);
    }

    public UserProfileResponse updateProfileByUsername(String username, UserProfileRequest req) {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (req.getName() != null) user.setName(req.getName());
        if (req.getLocation() != null) user.setLocation(req.getLocation());
        if (req.getJobTitle() != null) user.setJobTitle(req.getJobTitle());
        if (req.getProfilePicture() != null) user.setProfilePicture(req.getProfilePicture());
        if (req.getGender() != null) user.setGender(req.getGender());
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
        r.setGender(u.getGender() != null ? u.getGender().name() : null);
        r.setLocation(u.getLocation());
        r.setProfilePicture(u.getProfilePicture());
        r.setJobTitle(u.getJobTitle());
        r.setCreationDate(u.getCreationDate());
        r.setUserRole(u.getUserRole() != null ? u.getUserRole().name() : null);
        r.setPremiumStartDate(u.getPremiumStartDate());
        return r;
    }
}
