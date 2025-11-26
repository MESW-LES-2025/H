package com.lernia.auth.controller;

import com.lernia.auth.dto.CourseDTO;
import com.lernia.auth.dto.CourseFilter;
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

    @GetMapping("/search")
    public ResponseEntity<Page<CourseDTO>> getCoursesByFilter(@ModelAttribute CourseFilter courseFilter, Pageable pageable) {
        Page<CourseDTO> page = courseService.getCourses(courseFilter, pageable);
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
