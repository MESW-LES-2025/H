package com.lernia.auth.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "review", schema = "lernia")
@Data
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float rating;
    private String description;
    private LocalDate reviewDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
