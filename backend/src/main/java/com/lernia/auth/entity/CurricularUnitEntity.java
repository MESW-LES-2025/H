package com.lernia.auth.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
