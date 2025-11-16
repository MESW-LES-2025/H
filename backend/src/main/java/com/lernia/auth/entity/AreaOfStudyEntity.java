package com.lernia.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "area_of_study", schema = "lernia")
@Data
public class AreaOfStudyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "areaOfStudies")
    private List<CourseEntity> courses = new ArrayList<>();
}
