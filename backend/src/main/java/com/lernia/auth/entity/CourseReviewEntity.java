package com.lernia.auth.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "course_reviews", schema = "lernia")
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseReviewEntity extends ReviewEntity {
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;
}

