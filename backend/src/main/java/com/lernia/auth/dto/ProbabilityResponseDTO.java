package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProbabilityResponseDTO {
    private Long courseId;
    private Integer percentage; // 0-100
    private String confidenceLevel; // LOW, MEDIUM, HIGH
    private String label; // "Low Chance", "Good Match", "Excellent Fit"
    private Map<String, String> factors; // Breakdown of scoring factors
}
