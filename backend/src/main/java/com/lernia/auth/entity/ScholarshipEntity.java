package com.lernia.auth.entity;

import com.lernia.auth.entity.enums.CourseType;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "scholarship", schema = "lernia")
@Data
public class ScholarshipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "university_id")
    private UniversityEntity university;
    private String name;
    @Enumerated(EnumType.STRING)
    private CourseType courseType;
}
