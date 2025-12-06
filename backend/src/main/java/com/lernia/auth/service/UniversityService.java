package com.lernia.auth.service;

import com.lernia.auth.dto.LocationDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.dto.*;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.ScholarshipEntity;
import com.lernia.auth.repository.CourseRepository;
import com.lernia.auth.repository.ScholarshipRepository;
import com.lernia.auth.repository.UniversityRepository;
import com.lernia.auth.repository.UniversitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversityService {
    private final UniversityRepository universityRepository;
    private final CourseRepository courseRepository;
    private final ScholarshipRepository scholarshipRepository;

    public List<String> getAllCountries() {
        return universityRepository.findDistinctCountries();
    }

    public UniversityDTOLight getUniversityById(Long id) {
        UniversityEntity entity = universityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("University not found"));

        UniversityDTOLight dto = new UniversityDTOLight();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setLogo(entity.getLogo());
        dto.setContactInfo(entity.getContactInfo());
        dto.setWebsite(entity.getWebsite());

        if (entity.getLocation() != null) {
            dto.setLocation(new LocationDTO(
                    entity.getLocation().getId(),
                    entity.getLocation().getCity(),
                    entity.getLocation().getCountry(),
                    entity.getLocation().getCostOfLiving()));
        } else {
            dto.setLocation(null);
        }

        dto.setStudentCount(5000);
        dto.setFoundedYear(1990);
        dto.setCourses(new ArrayList<>());

        return dto;
    }

    public Page<UniversityDTOLight> getUniversitiesByFilter(UniversityFilter filter, Pageable pageable) {
        Specification<com.lernia.auth.entity.UniversityEntity> spec = UniversitySpecification.filter(filter);

        return universityRepository.findAll(spec, pageable)
                .map(university -> new UniversityDTOLight(
                        university.getId(),
                        university.getName(),
                        university.getDescription(),
                        university.getLocation() != null ? new LocationDTO(
                                university.getLocation().getId(),
                                university.getLocation().getCity(),
                                university.getLocation().getCountry(),
                                university.getLocation().getCostOfLiving()) : null));
    }

    // public UniversityDTOLight getUniversityById(Long id) {
    // return universityRepository.findById(id)
    // .map(university -> new UniversityDTOLight(
    // university.getId(),
    // university.getName(),
    // university.getDescription(),
    // university.getLocation() != null ? new LocationDTO(
    // university.getLocation().getId(),
    // university.getLocation().getCity(),
    // university.getLocation().getCountry(),
    // university.getLocation().getCost_of_living()) : null))
    // .orElse(null);
    // }

    public UniversityDTO getUniversityDetailsById(Long id) {
        return universityRepository.findById(id)
                .map(university -> {
                    List<CourseEntity> courseEntities = courseRepository.findAll()
                            .stream()
                            .filter(course -> course.getUniversity().getId().equals(id))
                            .collect(Collectors.toList());

                    List<CourseLightDTO> courses = courseEntities.stream()
                            .map(course -> new CourseLightDTO(
                                    course.getId(),
                                    course.getName(),
                                    course.getCourseType()))
                            .collect(Collectors.toList());

                    List<ScholarshipEntity> scholarshipEntities = scholarshipRepository.findByUniversityId(id);

                    List<ScholarshipDTO> scholarships = scholarshipEntities.stream()
                            .map(scholarship -> new ScholarshipDTO(
                                    scholarship.getId(),
                                    scholarship.getName(),
                                    scholarship.getDescription(),
                                    scholarship.getAmount(),
                                    scholarship.getCourseType()))
                            .collect(Collectors.toList());

                    return new UniversityDTO(
                            university.getId(),
                            university.getName(),
                            university.getDescription(),
                            university.getContactInfo(),
                            university.getWebsite(),
                            university.getAddress(),
                            university.getLogo(),
                            university.getLocation() != null ? new LocationDTO(
                                    university.getLocation().getId(),
                                    university.getLocation().getCity(),
                                    university.getLocation().getCountry(),
                                    university.getLocation().getCostOfLiving()) : null,
                            courses,
                            scholarships);
                })
                .orElse(null);
    }

}