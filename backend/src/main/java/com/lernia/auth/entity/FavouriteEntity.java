package com.lernia.auth.entity;

import com.lernia.auth.entity.enums.FavouriteType;
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
import java.time.LocalDate;

@Entity
@Table(name = "favourite", schema = "lernia")
@Data
public class FavouriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Enumerated(EnumType.STRING)
    private FavouriteType favouriteType;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity course;
    @ManyToOne
    @JoinColumn(name = "university_id")
    private UniversityEntity university;
    private LocalDate favouriteDate;
}
