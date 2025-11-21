package com.lernia.auth.entity;

import com.lernia.auth.entity.enums.CourseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "scholarships", schema = "lernia")
@Data
public class ScholarshipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private UniversityEntity university;
    private String name;
    private String description;
    private Integer amount;
    @Column
    private String courseType;

    @PostLoad
    @PrePersist
    @PreUpdate
    private void validateCourseType() {
        if (!CourseType.contains(courseType)) {
            throw new IllegalArgumentException("Invalid courseType: " + courseType);
        }
    }
}
