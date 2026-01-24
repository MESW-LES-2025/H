package com.lernia.auth.service;

import com.lernia.auth.dto.CourseLightDTO;
import com.lernia.auth.dto.LocationDTO;
import com.lernia.auth.dto.ScholarshipDTO;
import com.lernia.auth.dto.UniversityDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.dto.filter.UniversityFilter;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.ScholarshipEntity;
import com.lernia.auth.mapper.UniversityMapper;
import com.lernia.auth.repository.CourseRepository;
import com.lernia.auth.repository.ScholarshipRepository;
import com.lernia.auth.repository.UniversityRepository;
import com.lernia.auth.repository.UniversitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversityService {
        private final UniversityRepository universityRepository;
        private final CourseRepository courseRepository;
        private final ScholarshipRepository scholarshipRepository;
        private final UniversityMapper universityMapper;

        public List<String> getAllCountries() {
                return universityRepository.findDistinctCountries();
        }

        public UniversityDTOLight getUniversityById(Long id) {
                return universityRepository.findById(id)
                                .map(universityMapper::toDTOLight).orElseThrow();
        }

        public Page<UniversityDTOLight> getUniversitiesByFilter(UniversityFilter filter, Pageable pageable) {
                Specification<UniversityEntity> spec = UniversitySpecification.filter(filter);

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

        public UniversityDTO getUniversityDetailsById(Long id) {
                return universityRepository.findById(id)
                                .map(university -> {
                                        List<CourseEntity> courseEntities = courseRepository.findAll()
                                                        .stream()
                                                        .filter(course -> course.getUniversity().getId().equals(id))
                                                        .toList();

                                        List<CourseLightDTO> courses = courseEntities.stream()
                                                        .map(course -> new CourseLightDTO(
                                                                        course.getId(),
                                                                        course.getName(),
                                                                        course.getCourseType(),
                                                                        course.getUniversity() != null
                                                                                        ? course.getUniversity()
                                                                                                        .getName()
                                                                                        : null,
                                                                        course.getCost(),
                                                                        course.getCredits(),
                                                                        course.getDescription()))
                                                        .toList();

                                        List<ScholarshipEntity> scholarshipEntities = scholarshipRepository
                                                        .findByUniversityId(id);

                                        List<ScholarshipDTO> scholarships = scholarshipEntities.stream()
                                                        .map(scholarship -> new ScholarshipDTO(
                                                                        scholarship.getId(),
                                                                        scholarship.getName(),
                                                                        scholarship.getDescription(),
                                                                        scholarship.getAmount(),
                                                                        scholarship.getCourseType()))
                                                        .toList();

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
                                                                        university.getLocation().getCostOfLiving())
                                                                        : null,
                                                        university.getStudentCount(),
                                                        university.getFoundedYear(),
                                                        courses,
                                                        scholarships);
                                })
                                .orElse(null);
        }

}