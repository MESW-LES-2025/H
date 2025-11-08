package com.lernia.auth.entity;

import com.lernia.auth.entity.enums.CourseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course", schema = "lernia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_of_study_id")
    private AreaOfStudyEntity areaOfStudy;
    @Enumerated(EnumType.STRING)
    private CourseType courseType;
    private Boolean isRemote;
    private Integer minAdmissionGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private UniversityEntity university;

    @OneToMany(mappedBy = "course")
    private List<CurricularUnitEntity> curricularUnits = new ArrayList<>();

}
