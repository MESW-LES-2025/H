package com.lernia.auth.service;

import com.lernia.auth.dto.AreaOfStudyDTO;
import com.lernia.auth.dto.CourseDTO;
import com.lernia.auth.dto.CourseFilter;
import com.lernia.auth.dto.LocationDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.mapper.CourseMapper;
import com.lernia.auth.repository.CourseRepository;
import com.lernia.auth.repository.CourseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

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
        Specification<CourseEntity> spec = CourseSpecification.filter(filter);

        return courseRepository.findAll(spec, pageable)
                .map(courseMapper::toDTO);
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
