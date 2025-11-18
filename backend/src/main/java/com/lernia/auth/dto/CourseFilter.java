package com.lernia.auth.dto;

import com.lernia.auth.entity.enums.CourseType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CourseFilter {
    private String name;
    private CourseType courseType;
    private Boolean isRemote;
    private Integer costMax;
    private String duration;
    private String language;
    private String country;
    private List<Long> areaOfStudy;
    private Integer costOfLivingMax;

    public CourseFilter(){}
}
