package com.lernia.auth.controller;

import com.lernia.auth.dto.ScholarshipDTOLight;
import com.lernia.auth.dto.ScholarshipFilter;
import com.lernia.auth.service.ScholarshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scholarship")
@RequiredArgsConstructor
public class ScholarshipController {

  private final ScholarshipService scholarshipService;

  @GetMapping
  public ResponseEntity<Page<ScholarshipDTOLight>> getScholarshipsByFilter(
      @ModelAttribute ScholarshipFilter scholarshipFilter,
      Pageable pageable) {
    Page<ScholarshipDTOLight> page = scholarshipService.getScholarshipsByFilter(scholarshipFilter, pageable);
    return ResponseEntity.ok(page);
  }
}
