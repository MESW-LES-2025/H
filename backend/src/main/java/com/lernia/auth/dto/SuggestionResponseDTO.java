package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String type; // "course" or "university"
    private Double matchScore; // 0-100
    private String imageUrl;
    private String location;
    private String courseType; // For courses only
    private String universityName; // For courses only
}
