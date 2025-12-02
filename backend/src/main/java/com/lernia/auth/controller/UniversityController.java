package com.lernia.auth.controller;

import com.lernia.auth.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/university")
@RequiredArgsConstructor
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    @GetMapping("/countries")
    public List<String> getAllCountries() {
        return universityService.getAllCountries();
    }
}
