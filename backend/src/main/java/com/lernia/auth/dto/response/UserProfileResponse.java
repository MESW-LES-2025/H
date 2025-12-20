package com.lernia.auth.dto.response;

import com.lernia.auth.entity.enums.Gender;
import lombok.Getter;
import lombok.Setter;

public class UserProfileResponse {

    @Setter
    @Getter
    private Long id;
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String username;
    @Setter
    @Getter
    private String email;
    @Setter
    @Getter
    private Integer age;
    @Setter
    @Getter
    private Gender gender;
    @Setter
    @Getter
    private String location;
    @Setter
    @Getter
    private String jobTitle;
    @Setter
    @Getter
    private String userRole;
    @Setter
    @Getter
    private String provider;
    @Setter
    @Getter
    private String profilePicture;
    @Setter
    @Getter
    private java.time.LocalDate premiumStartDate;
    @Setter
    @Getter
    private boolean isPremium;

    // Academic profile
    @Setter
    @Getter
    private Integer academicGrade;
    @Setter
    @Getter
    private String educationLevel;
    @Setter
    @Getter
    private String studyArea;

    public UserProfileResponse() {
    }

}
