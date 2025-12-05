package com.lernia.auth.dto;

import com.lernia.auth.entity.UserCourseEntity;
import com.lernia.auth.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EditProfileRequest {

    private Long id;
    private String name;
    private Integer age;
    private Gender gender;
    private String location;
    private String jobTitle;

}
