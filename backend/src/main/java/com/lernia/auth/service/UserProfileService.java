package com.lernia.auth.service;

import com.lernia.auth.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.UserCourseEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.repository.UserRepository;
import com.lernia.auth.repository.UserCourseRepository;
import com.lernia.auth.repository.CourseRepository;
import com.lernia.auth.repository.UniversityRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserProfileService {
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;

    public UserProfileService(UserRepository userRepository,
                              UserCourseRepository userCourseRepository,
                              CourseRepository courseRepository,
                              UniversityRepository universityRepository) {
        this.userRepository = userRepository;
        this.userCourseRepository = userCourseRepository;
    }

    public UserProfileResponse getProfileById(Long id) {
        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return map(user);
    }

    public UpdateUserProfileResponse updateProfile(Long id, EditProfileRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found: " + id));
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setGender(request.getGender());
        user.setLocation(request.getLocation());
        user.setJobTitle(request.getJobTitle());

        UserEntity savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }


    private List<UserCourseEntity> updateAcademicHistory(Long userId, List<UserCourseEntity> requests) {
        userCourseRepository.deleteByUserId(userId);

        if (requests == null) {
            return null;
        }

        List<UserCourseEntity> entities = requests.stream()
                .map(req -> {
                    UserCourseEntity e = new UserCourseEntity();
                    e.setUserId(userId);
                    e.setCourseId(req.getCourseId());
                    e.setStartDate(req.getStartDate());
                    e.setEndDate(req.getEndDate());
                    e.setFinished(req.getFinished());
                    return e;
                })
                .toList();

        userCourseRepository.saveAll(entities);
        return entities;
    }

    private UpdateUserProfileResponse mapToResponse(UserEntity profile) {
        UpdateUserProfileResponse response = new UpdateUserProfileResponse();
        response.setId(profile.getId());
        response.setName(profile.getName());
        response.setAge(profile.getAge());
        response.setGender(profile.getGender());
        response.setLocation(profile.getLocation());
        response.setJobTitle(profile.getJobTitle());

        return response;
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
        r.setProfilePicture(u.getProfilePicture());
        r.setJobTitle(u.getJobTitle());
        r.setCreationDate(u.getCreationDate());
        r.setUserRole(u.getUserRole() != null ? u.getUserRole().name() : null);
        r.setPremiumStartDate(u.getPremiumStartDate());
        return r;
    }

}
