package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CourseFilter {
    private String name;
    private List<String> courseTypes;
    private Boolean onlyRemote;
    private Integer maxCost;
    private Integer duration;
    private List<String> languages;
    private List<String> countries;
    private List<String> areasOfStudy;
    private Boolean hasScholarship;

}
