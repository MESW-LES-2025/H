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
@Table(name = "scholarships", schema = "lernia")
@Data
public class ScholarshipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "university_id", nullable = false)
    private UniversityEntity university;
    private String name;
    private String description;
    private Integer amount;
    private String courseType;
}
