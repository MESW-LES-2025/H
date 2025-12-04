package com.lernia.auth.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

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