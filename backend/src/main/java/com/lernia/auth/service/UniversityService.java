package com.lernia.auth.service;

import com.lernia.auth.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversityService {
    private final UniversityRepository universityRepository;

    public List<String> getAllCountries() {
        return universityRepository.findDistinctCountries();
    }
}