package com.lernia.auth.controller;

import com.lernia.auth.dto.CourseLightDTO;
import com.lernia.auth.dto.LocationDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.dto.UserProfileResponse;
import com.lernia.auth.repository.CourseRepository;
import com.lernia.auth.repository.UniversityRepository;
import com.lernia.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final CourseRepository courseRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userRepository.findAll().stream().map(u -> {
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
        }).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/universities")
    public ResponseEntity<List<UniversityDTOLight>> getAllUniversities() {
        var list = universityRepository.findAll().stream()
                .map(university -> new UniversityDTOLight(
                        university.getId(),
                        university.getName(),
                        university.getDescription(),
                        university.getLocation() != null ? new LocationDTO(
                                university.getLocation().getId(),
                                university.getLocation().getCity(),
                                university.getLocation().getCountry(),
                                university.getLocation().getCostOfLiving()
                        ) : null
                ))
                .toList();

        return ResponseEntity.ok(list);
    }


    @GetMapping("/courses")
    public ResponseEntity<List<CourseLightDTO>> getAllCourses() {
        List<CourseLightDTO> list = courseRepository.findAll().stream()
                .map(course -> new CourseLightDTO(course.getId(), course.getName(), course.getCourseType(), course.getUniversity().getName()))
                .toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
