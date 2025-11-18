package com.lernia.auth.service;

import com.lernia.auth.dto.*;
import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<String> getAllLanguages() {
            return courseRepository.findDistinctLanguages();
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

    public Page<CourseDTO> getCourses(CourseFilter filter, Pageable pageable) {

        String courseTypesParam = null;
        if (filter.getCourseTypes() != null && !filter.getCourseTypes().isEmpty()) {
            courseTypesParam = filter.getCourseTypes().stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(","));
        }

        String languagesParam = null;
        if (filter.getLanguages() != null && !filter.getLanguages().isEmpty()) {
            languagesParam = String.join(",", filter.getLanguages());
        }

        String countriesParam = null;
        if (filter.getCountries() != null && !filter.getCountries().isEmpty()) {
            countriesParam = String.join(",", filter.getCountries());
        }

        String areasOfStudyParam = null;
        if (filter.getAreasOfStudy() != null && !filter.getAreasOfStudy().isEmpty()) {
            areasOfStudyParam = filter.getAreasOfStudy().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }

        Page<CourseEntity> page = courseRepository.findCourses(
                filter.getName(),
                courseTypesParam,
                filter.getOnlyRemote(),
                filter.getCostMax(),
                filter.getDuration(),
                languagesParam,
                countriesParam,
                areasOfStudyParam,
                pageable
        );

        return page.map(this::convertToDTO);
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
