package com.lernia.auth.dto.request;

import com.lernia.auth.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

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
