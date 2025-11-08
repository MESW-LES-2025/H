package com.lernia.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "area_of_study", schema = "lernia")
@Data
public class AreaOfStudyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

}
