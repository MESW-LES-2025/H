package com.lernia.auth.dto;

import com.lernia.auth.entity.enums.CourseType;

import java.util.List;

public record CourseDTO(
        Long id,
        String name,
        String description,
        CourseType courseType,
        Boolean isRemote,
        Integer minAdmissionGrade,
        Integer cost,
        UniversityDTOLight university,
        List<String> areasOfStudy
) {}

