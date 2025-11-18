package com.lernia.auth.dto;

import com.lernia.auth.entity.enums.CourseType;
import java.util.List;

public class CourseDTO {
    private Long id;
    private String name;
    private String description;
    private CourseType courseType;
    private Boolean isRemote;
    private Integer minAdmissionGrade;
    private Integer cost;
    private UniversityDTOLight university;
    private List<String> areasOfStudy;

    public CourseDTO() {}

    public CourseDTO(Long id,
                     String name,
                     String description,
                     CourseType courseType,
                     Boolean isRemote,
                     Integer minAdmissionGrade,
                     Integer cost,
                     UniversityDTOLight university,
                     List<String> areaOfStudies) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.courseType = courseType;
        this.isRemote = isRemote;
        this.minAdmissionGrade = minAdmissionGrade;
        this.cost = cost;
        this.university = university;
        this.areasOfStudy = areaOfStudies;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public Boolean getIsRemote() {
        return isRemote;
    }

    public Integer getMinAdmissionGrade() {
        return minAdmissionGrade;
    }

    public Integer getCost() {
        return cost;
    }

    public UniversityDTOLight getUniversity() {
        return university;
    }

    public List<String> getAreasOfStudy() {
        return areasOfStudy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public void setIsRemote(Boolean isRemote) {
        this.isRemote = isRemote;
    }

    public void setMinAdmissionGrade(Integer minAdmissionGrade) {
        this.minAdmissionGrade = minAdmissionGrade;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public void setUniversity(UniversityDTOLight university) {
        this.university = university;
    }

    public void setAreasOfStudy(List<String> areasOfStudy) {
        this.areasOfStudy = areasOfStudy;
    }
}
