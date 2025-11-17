package com.lernia.auth.service;

import com.lernia.auth.dto.CourseDTO;
import com.lernia.auth.dto.LocationDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAllByOrderByNameAsc().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<CourseDTO> getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(this::convertToDTO);
    }

    private CourseDTO convertToDTO(CourseEntity course) {
        UniversityEntity university = course.getUniversity();
        LocationEntity location = university.getLocation();

        LocationDTO locationDTO = new LocationDTO(
                location.getId(),
                location.getCity(),
                location.getCountry()
        );

        UniversityDTOLight universityDTOLight = new UniversityDTOLight(
                university.getId(),
                university.getName(),
                university.getDescription(),
                locationDTO
        );

        List<String> areasOfStudy = course.getAreaOfStudies().stream()
                .map(AreaOfStudyEntity::getName)
                .toList();

        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCourseType(),
                course.getIsRemote(),
                course.getMinAdmissionGrade(),
                course.getCost(),
                universityDTOLight,
                areasOfStudy
        );
    }
}
