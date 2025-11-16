package com.lernia.auth.dto;

import com.lernia.auth.entity.enums.Gender;

public class UserProfileRequest {
    private String name;
    private String location;
    private String jobTitle;
    private String profilePicture;
    private Gender gender;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
}