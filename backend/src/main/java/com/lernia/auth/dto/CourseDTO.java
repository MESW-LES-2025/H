package com.lernia.auth.dto;

import com.lernia.auth.entity.enums.CourseType;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String name;
    private String description;
    private CourseType courseType;
    private Boolean isRemote;
    private Integer minAdmissionGrade;
    private Integer cost;
    private Integer duration;
    private Integer credits;
    private String language;
    private LocalDate startDate;
    private LocalDate applicationDeadline;
    private String website;
    private String contactEmail;
    private UniversityDTOLight university;
    private List<AreaOfStudyDTO> areasOfStudy;
}

