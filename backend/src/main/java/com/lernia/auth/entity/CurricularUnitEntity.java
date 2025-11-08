package com.lernia.auth.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "curricular_unit", schema = "lernia")
@Data
public class CurricularUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity course;
    private String name;
    private Integer credits;
    private Integer semester;
    private Integer year;
    private Integer hours;
    @ManyToOne
    @JoinColumn(name = "professor_id")
    private ProfessorEntity professor;
}
