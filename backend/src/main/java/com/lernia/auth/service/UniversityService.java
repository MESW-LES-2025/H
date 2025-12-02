package com.lernia.auth.service;

import com.lernia.auth.dto.LocationDTO; // Import LocationDTO
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversityService {
    private final UniversityRepository universityRepository;

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
                entity.getLocation().getCostOfLiving()
            ));
        } else {
            dto.setLocation(null);
        }

        dto.setStudentCount(5000); 
        dto.setFoundedYear(1990);
        dto.setCourses(new ArrayList<>());

        return dto;
    }
}