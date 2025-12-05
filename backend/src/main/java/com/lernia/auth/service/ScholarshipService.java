package com.lernia.auth.service;

import com.lernia.auth.dto.ScholarshipDTOLight;
import com.lernia.auth.dto.ScholarshipFilter;
import com.lernia.auth.repository.ScholarshipRepository;
import com.lernia.auth.repository.ScholarshipSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScholarshipService {

  private final ScholarshipRepository scholarshipRepository;

  public Page<ScholarshipDTOLight> getScholarshipsByFilter(ScholarshipFilter filter, Pageable pageable) {
    Specification<com.lernia.auth.entity.ScholarshipEntity> spec = ScholarshipSpecification.filter(filter);

    return scholarshipRepository.findAll(spec, pageable)
        .map(scholarship -> new ScholarshipDTOLight(
            scholarship.getId(),
            scholarship.getName(),
            scholarship.getDescription(),
            scholarship.getAmount(),
            scholarship.getCourseType(),
            scholarship.getUniversity().getId(),
            scholarship.getUniversity().getName()));
  }
}
