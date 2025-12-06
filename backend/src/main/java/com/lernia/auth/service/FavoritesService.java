package com.lernia.auth.service;

import com.lernia.auth.dto.CourseLightDTO;
import com.lernia.auth.dto.FavoritesResponse;
import com.lernia.auth.dto.LocationDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.CourseRepository;
import com.lernia.auth.repository.UniversityRepository;
import com.lernia.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoritesService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final UniversityRepository universityRepository;

    private UserEntity findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + userId
                ));
    }

    // ------------- COURSES -------------

    public void addCourseToFavorites(Long userId, Long courseId) {
        UserEntity user = findUserById(userId);
        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!user.getBookmarkedCourses().contains(course)) {
            user.getBookmarkedCourses().add(course);
            userRepository.save(user);
        }
    }

    public void removeCourseFromFavorites(Long userId, Long courseId) {
        UserEntity user = findUserById(userId);
        user.getBookmarkedCourses().removeIf(c -> c.getId().equals(courseId));
        userRepository.save(user);
    }

    // ------------- UNIVERSITIES -------------

    public void addUniversityToFavorites(Long userId, Long universityId) {
        UserEntity user = findUserById(userId);
        UniversityEntity uni = universityRepository.findById(universityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "University not found"));

        if (!user.getBookmarkedUniversities().contains(uni)) {
            user.getBookmarkedUniversities().add(uni);
            userRepository.save(user);
        }
    }

    public void removeUniversityFromFavorites(Long userId, Long universityId) {
        UserEntity user = findUserById(userId);
        user.getBookmarkedUniversities().removeIf(u -> u.getId().equals(universityId));
        userRepository.save(user);
    }

    // ------------- LISTAR FAVORITOS DO USER -------------

    public FavoritesResponse getFavoritesForUser(Long userId) {
        UserEntity user = findUserById(userId);

        List<UniversityDTOLight> uniDtos = user.getBookmarkedUniversities().stream()
                .sorted(Comparator.comparing(UniversityEntity::getId).reversed())
                .map(this::toUniversityLight)
                .collect(Collectors.toList());

        List<CourseLightDTO> courseDtos = user.getBookmarkedCourses().stream()
                .sorted(Comparator.comparing(CourseEntity::getId).reversed())
                .map(this::toCourseLight)
                .collect(Collectors.toList());

        return new FavoritesResponse(uniDtos, courseDtos);
    }

    // ------------- HELPERS DE MAPEAMENTO -------------

    private UniversityDTOLight toUniversityLight(UniversityEntity university) {
        LocationDTO locationDTO = null;
        LocationEntity loc = university.getLocation();
        if (loc != null) {
            locationDTO = new LocationDTO(
                    loc.getId(),
                    loc.getCity(),
                    loc.getCountry(),
                    loc.getCostOfLiving()
            );
        }

        return new UniversityDTOLight(
                university.getId(),
                university.getName(),
                university.getDescription(),
                locationDTO
        );
    }

    private CourseLightDTO toCourseLight(CourseEntity course) {
        return new CourseLightDTO(
                course.getId(),
                course.getName(),
                course.getCourseType(),
                course.getUniversity().getName()
        );
    }
}
