package com.lernia.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "campus", schema = "lernia")
@Data
public class CampusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private UniversityEntity university;
    private String name;
    private String description;
    private String country;
    private String city;
    private Integer capacity;
}