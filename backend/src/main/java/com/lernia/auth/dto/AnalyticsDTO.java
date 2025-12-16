package com.lernia.auth.dto;

import java.util.List;

public record AnalyticsDTO(
        long totalUsers,
        long totalCourses,
        long totalUniversities,
        long totalCourseReviews,
        long totalUniversityReviews,
        long totalScholarships,
        List<PopularItemDTO> popularCourses,
        List<PopularItemDTO> popularUniversities) {
}
