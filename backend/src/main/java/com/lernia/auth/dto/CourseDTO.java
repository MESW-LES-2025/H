package com.lernia.auth.dto;

import com.lernia.auth.entity.enums.CourseType;

import java.time.LocalDate;
import java.util.List;

public record CourseDTO(
        Long id,
        String name,
        String description,
        CourseType courseType,
        Boolean isRemote,
        Integer minAdmissionGrade,
        Integer cost,
        String duration,
        Integer credits,
        String language,
        LocalDate startDate,
        LocalDate applicationDeadline,
        String website,
        String contactEmail,
        UniversityDTOLight university,
        List<String> areasOfStudy
) {}


