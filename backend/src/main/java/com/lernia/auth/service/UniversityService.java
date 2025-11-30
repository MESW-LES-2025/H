package com.lernia.auth.service;

import com.lernia.auth.dto.LocationDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.dto.UniversityFilter;
import com.lernia.auth.repository.UniversityRepository;
import com.lernia.auth.repository.UniversitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversityService {
    private final UniversityRepository universityRepository;

    public List<String> getAllCountries() {
        return universityRepository.findDistinctCountries();
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
                                university.getLocation().getCost_of_living()) : null));
    }

    public UniversityDTOLight getUniversityById(Long id) {
        return universityRepository.findById(id)
                .map(university -> new UniversityDTOLight(
                        university.getId(),
                        university.getName(),
                        university.getDescription(),
                        university.getLocation() != null ? new LocationDTO(
                                university.getLocation().getId(),
                                university.getLocation().getCity(),
                                university.getLocation().getCountry(),
                                university.getLocation().getCost_of_living()) : null))
                .orElse(null);
    }
}