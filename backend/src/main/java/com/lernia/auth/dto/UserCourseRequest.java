package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCourseRequest {

    private Long courseId;
    private String courseName;
    private String schoolName;
    private Integer startYear;
    private Integer endYear;

}
