package com.lernia.auth.controller;

import com.lernia.auth.dto.AreaOfStudyDTO;
import com.lernia.auth.service.AreaOfStudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/area-of-study")
@RequiredArgsConstructor
public class AreaOfStudyController {

    private final AreaOfStudyService areaOfStudyService;

    @GetMapping
    public List<AreaOfStudyDTO> getAllAreasOfStudy() {
        return areaOfStudyService.getAllAreasOfStudy();
    }
}
