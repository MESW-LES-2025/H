package com.lernia.auth.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "university_reviews", schema = "lernia")
@Data
@EqualsAndHashCode(callSuper = true)
public class UniversityReviewEntity extends ReviewEntity {
    @ManyToOne
    @JoinColumn(name = "university_id", nullable = false)
    private UniversityEntity university;
}
