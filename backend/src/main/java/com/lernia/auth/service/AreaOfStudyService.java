package com.lernia.auth.service;

import com.lernia.auth.dto.AreaOfStudyDTO;
import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.repository.AreaOfStudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaOfStudyService {
    private final AreaOfStudyRepository areaOfStudyRepository;

    public List<AreaOfStudyDTO> getAllAreasOfStudy() {
        return areaOfStudyRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    private AreaOfStudyDTO convertToDTO(AreaOfStudyEntity areaOfStudy) {
        return new AreaOfStudyDTO(
                areaOfStudy.getId(),
                areaOfStudy.getName()
        );

    }
}


