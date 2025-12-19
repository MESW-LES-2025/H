package com.lernia.auth.entity;

import com.lernia.auth.entity.enums.AuthProvider;
import com.lernia.auth.entity.enums.EducationLevel;
import com.lernia.auth.entity.enums.Gender;
import com.lernia.auth.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", schema = "lernia")
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

        @Column(nullable = true)
        private String password;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private AuthProvider provider = AuthProvider.LOCAL;

        private String providerId;

        private Integer age;
        @Enumerated(EnumType.STRING)
        @JdbcTypeCode(SqlTypes.NAMED_ENUM)
        @Column(nullable = false, columnDefinition = "gender")
        private Gender gender;
        private String location;
        private String profilePicture;
        private String jobTitle;
        private LocalDate creationDate;
        @Enumerated(EnumType.STRING)
        @JdbcTypeCode(SqlTypes.NAMED_ENUM)
        @Column(nullable = false, columnDefinition = "user_role")
        private UserRole userRole;
        private LocalDate premiumStartDate;

        private Integer academicGrade; // 0-200 scale (e.g., Portuguese grading)

        @Enumerated(EnumType.STRING)
        @Column(columnDefinition = "varchar(50)")
        private EducationLevel educationLevel;

        private String studyArea;

        @ManyToMany
        @JoinTable(name = "user_bookmarked_courses", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
        private List<CourseEntity> bookmarkedCourses = new ArrayList<>();

        @ManyToMany
        @JoinTable(name = "user_bookmarked_universities", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "university_id"))
        private List<UniversityEntity> bookmarkedUniversities = new ArrayList<>();
}
