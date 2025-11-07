package com.lernia.auth.entity;

import com.lernia.auth.entity.enums.Gender;
import com.lernia.auth.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

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
    @Column(nullable = false)
    private Gender gender;
    private String location;
    private String profilePicture;
    private String jobTitle;
    private LocalDate creationDate;
    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.REGULAR;
    private LocalDate premiumStartDate;
}