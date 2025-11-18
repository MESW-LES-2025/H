package com.lernia.auth.service;

import com.lernia.auth.dto.*;
import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        UniversityDTOLight universityDTOLight = getUniversityDTOLight(course);

        List<AreaOfStudyDTO> areasOfStudy = course.getAreaOfStudies().stream()
                .map(this::getAreaOfStudyDTO).toList();

        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCourseType(),
                course.getIsRemote(),
                course.getMinAdmissionGrade(),
                course.getCost(),
                course.getDuration(),
                course.getCredits(),
                course.getLanguage(),
                course.getStartDate(),
                course.getApplicationDeadline(),
                course.getWebsite(),
                course.getContactEmail(),
                universityDTOLight,
                areasOfStudy
        );

    }

    public List<CourseDTO> getCoursesByFilter(CourseFilter filter) {
        String areaOfStudyParam = null;
        if (filter.getAreaOfStudy() != null && !filter.getAreaOfStudy().isEmpty()) {
            areaOfStudyParam = filter.getAreaOfStudy().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
        List<CourseEntity> entities = courseRepository.findCoursesByFilters(
                filter.getName(),
                filter.getCourseType() != null ? filter.getCourseType().name() : null,
                filter.getIsRemote(),
                filter.getCostMax(),
                filter.getDuration(),
                filter.getLanguage(),
                filter.getCountry(),
                filter.getCostOfLivingMax(),
                areaOfStudyParam
        );

        return entities.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private static UniversityDTOLight getUniversityDTOLight(CourseEntity course) {
        UniversityEntity university = course.getUniversity();
        LocationEntity location = university.getLocation();

        LocationDTO locationDTO = new LocationDTO(
                location.getId(),
                location.getCity(),
                location.getCountry(),
                location.getCost_of_living()
        );

        return new UniversityDTOLight(
                university.getId(),
                university.getName(),
                university.getDescription(),
                locationDTO
        );
    }

    private AreaOfStudyDTO getAreaOfStudyDTO(AreaOfStudyEntity areaOfStudy) {

        return new AreaOfStudyDTO(
                areaOfStudy.getId(),
                areaOfStudy.getName()
        );
    }
}
