package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EditProfileRequest {

    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private String location;
    private String profileImage;
    private String jobTitle;
    private List<UserCourseRequest> academicHistory;

}
