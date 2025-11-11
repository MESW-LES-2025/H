package com.lernia.auth.entity;

import com.lernia.auth.entity.enums.Gender;
import com.lernia.auth.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user", schema = "lernia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String email;
    private String password;
    private Integer age;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    private String location;
    private String profilePicture;
    private String jobTitle;
    private LocalDate creationDate;
    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.REGULAR;
    private LocalDate premiumStartDate;

    @ManyToMany
    @JoinTable(name = "user_bookmarked_courses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<CourseEntity> bookmarkedCourses = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_bookmarked_universities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "university_id"))
    private List<UniversityEntity> bookmarkedUniversities = new ArrayList<>();
}
