package com.lernia.auth.controller;

import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.dto.UniversityFilter;
import com.lernia.auth.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/university")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    @GetMapping
    public ResponseEntity<Page<UniversityDTOLight>> getUniversitiesByFilter(
            @ModelAttribute UniversityFilter universityFilter,
            Pageable pageable) {
        Page<UniversityDTOLight> page = universityService.getUniversitiesByFilter(universityFilter, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UniversityDTOLight> getUniversityById(@PathVariable Long id) {
        UniversityDTOLight university = universityService.getUniversityById(id);
        if (university != null) {
            return ResponseEntity.ok(university);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/countries")
    public List<String> getAllCountries() {
        return universityService.getAllCountries();
    }
}
