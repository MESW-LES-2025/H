package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class CourseFilter {
    private String name;
    private List<String> courseTypes;
    private Boolean onlyRemote;
    private Integer costMax;
    private Integer duration;
    private List<String> languages;
    private List<String> countries;
    private List<String> areasOfStudy;

    public CourseFilter() {
        this.onlyRemote = false;
        this.courseTypes = new ArrayList<>();
        this.languages = new ArrayList<>();
        this.countries = new ArrayList<>();
        this.areasOfStudy = new ArrayList<>();
    }
}
