package com.lernia.auth.dto.response;

import com.lernia.auth.entity.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
    private Integer age;
    private Gender gender;
    private String location;
    private String jobTitle;
}
