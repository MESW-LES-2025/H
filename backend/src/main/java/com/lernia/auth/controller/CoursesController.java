package com.lernia.auth.controller;

import com.lernia.auth.dto.CourseDTO;
import com.lernia.auth.dto.CourseFilter;
import com.lernia.auth.entity.enums.CourseType;
import com.lernia.auth.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CoursesController {

    private final CourseService courseService;

    @GetMapping()
    public ResponseEntity<Page<CourseDTO>> getCoursesByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<CourseType> courseTypes,
            @RequestParam(required = false) Boolean onlyRemote,
            @RequestParam(required = false) Integer maxCost,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) List<String> languages,
            @RequestParam(required = false) List<String> countries,
            @RequestParam(required = false) List<String> areasOfStudy,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        CourseFilter filter = new CourseFilter();
        filter.setName(name);
        filter.setCourseTypes(courseTypes != null ? courseTypes : Collections.emptyList());
        filter.setOnlyRemote(onlyRemote != null ? onlyRemote : false);
        filter.setCostMax(maxCost);
        filter.setDuration(duration);
        filter.setLanguages(languages != null ? languages : Collections.emptyList());
        filter.setCountries(countries != null ? countries : Collections.emptyList());
        filter.setAreasOfStudy(areasOfStudy != null ? areasOfStudy : Collections.emptyList());

        Page<CourseDTO> page = courseService.getCourses(filter, pageable);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/languages")
    public List<String> getAllLanguages() {
        return courseService.getAllLanguages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
