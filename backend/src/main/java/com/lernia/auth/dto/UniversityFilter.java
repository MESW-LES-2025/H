package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniversityFilter {
    private String name;
    private List<String> countries;
    private Integer costOfLivingMax;
    private Boolean hasScholarship;
}
