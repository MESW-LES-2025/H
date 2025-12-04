package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long universityId;
    private Long courseId;
    private Float rating;
    private String title;
    private String description;
    private LocalDate reviewDate;
}