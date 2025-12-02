package com.lernia.auth.controller;

import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/university")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    @GetMapping("/countries")
    public List<String> getAllCountries() {
        return universityService.getAllCountries();
    }

    @GetMapping("/{id}")
    public UniversityDTOLight getUniversityById(@PathVariable Long id) {
        return universityService.getUniversityById(id);
    }
}
