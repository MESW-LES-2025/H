package com.lernia.auth.dto.request;

import com.lernia.auth.entity.enums.EducationLevel;
import com.lernia.auth.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {

    private Long id;
    private String name;
    private Integer age;
    private Gender gender;
    private String location;
    private String jobTitle;

    // Academic profile
    private Integer academicGrade;
    private EducationLevel educationLevel;
    private String studyArea;

}
