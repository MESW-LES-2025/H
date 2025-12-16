package com.lernia.auth.service;

import com.lernia.auth.dto.AnalyticsDTO;
import com.lernia.auth.dto.PopularItemDTO;
import com.lernia.auth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final UniversityRepository universityRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final UniversityReviewRepository universityReviewRepository;
    private final ScholarshipRepository scholarshipRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public AnalyticsDTO getAnalytics() {
        long totalUsers = userRepository.count();
        long totalCourses = courseRepository.count();
        long totalUniversities = universityRepository.count();
        long totalCourseReviews = courseReviewRepository.count();
        long totalUniversityReviews = universityReviewRepository.count();
        long totalScholarships = scholarshipRepository.count();

        List<PopularItemDTO> popularCourses = getPopularCourses();
        List<PopularItemDTO> popularUniversities = getPopularUniversities();

        return new AnalyticsDTO(
                totalUsers,
                totalCourses,
                totalUniversities,
                totalCourseReviews,
                totalUniversityReviews,
                totalScholarships,
                popularCourses,
                popularUniversities);
    }

    private List<PopularItemDTO> getPopularCourses() {
        String jpql = """
                SELECT new com.lernia.auth.dto.PopularItemDTO(
                    c.id,
                    c.name,
                    COUNT(u)
                )
                FROM UserEntity u
                JOIN u.bookmarkedCourses c
                GROUP BY c.id, c.name
                ORDER BY COUNT(u) DESC
                """;

        return entityManager.createQuery(jpql, PopularItemDTO.class)
                .setMaxResults(5)
                .getResultList();
    }

    private List<PopularItemDTO> getPopularUniversities() {
        String jpql = """
                SELECT new com.lernia.auth.dto.PopularItemDTO(
                    u2.id,
                    u2.name,
                    COUNT(u)
                )
                FROM UserEntity u
                JOIN u.bookmarkedUniversities u2
                GROUP BY u2.id, u2.name
                ORDER BY COUNT(u) DESC
                """;

        return entityManager.createQuery(jpql, PopularItemDTO.class)
                .setMaxResults(5)
                .getResultList();
    }
}
