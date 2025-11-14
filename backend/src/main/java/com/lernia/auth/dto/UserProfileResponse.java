package com.lernia.auth.dto;

import java.time.LocalDate;

public class UserProfileResponse {

    private Long id;
    private String name;
    private String username;
    private String email;
    private Integer age;
    private String gender;      // Usamos String, pois na entidade é enum
    private String location;
    private String profilePicture;
    private String jobTitle;
    private LocalDate creationDate;  // Adicionado para refletir a entidade
    private String userRole;     // Usamos String, pois na entidade é enum
    private LocalDate premiumStartDate;

    public UserProfileResponse() {}

    // Getters e Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getUserRole() {
        return userRole;
    }
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public LocalDate getPremiumStartDate() {
        return premiumStartDate;
    }
    public void setPremiumStartDate(LocalDate premiumStartDate) {
        this.premiumStartDate = premiumStartDate;
    }
}
