package com.lernia.auth.controller;

import com.lernia.auth.dto.CourseDTO;
import com.lernia.auth.dto.CourseFilter;
import com.lernia.auth.entity.enums.CourseType;
import com.lernia.auth.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CoursesController {

    private final CourseService courseService;

    @GetMapping
    public List<CourseDTO> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<CourseDTO>> getCoursesByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) CourseType courseType,
            @RequestParam(required = false) Boolean isRemote,
            @RequestParam(required = false) Integer costMax,
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<Long> areasOfStudy,
            @RequestParam(required = false) Integer costOfLivingMax,
            Pageable pageable
    ) {
        CourseFilter filter = new CourseFilter();
        filter.setName(name);
        filter.setCourseType(courseType);
        filter.setIsRemote(isRemote);
        filter.setCostMax(costMax);
        filter.setDuration(duration);
        filter.setLanguage(language);
        filter.setCountry(country);
        filter.setCostOfLivingMax(costOfLivingMax);
        filter.setAreaOfStudy(areasOfStudy);

        Page<CourseDTO> resultPage = courseService.getCoursesByFilter(filter, pageable);

        return ResponseEntity.ok(resultPage);
    }


}
