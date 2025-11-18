package com.lernia.auth.entity;

import com.lernia.auth.entity.enums.CourseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses", schema = "lernia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    private Boolean isRemote;
    private Integer minAdmissionGrade;
    private Integer cost;

    private String duration;
    private Integer credits;
    private String language;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Column(name = "contact_email")
    private String contactEmail;

    private String website;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private UniversityEntity university;

    @OneToMany(mappedBy = "course")
    private List<CurricularUnitEntity> curricularUnits = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "course_area_of_study",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "area_of_study_id"))
    private List<AreaOfStudyEntity> areaOfStudies = new ArrayList<>();
}

