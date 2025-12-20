package com.lernia.auth.service;

import com.lernia.auth.dto.SuggestionResponseDTO;
import com.lernia.auth.entity.*;
import com.lernia.auth.repository.CourseRepository;
import com.lernia.auth.repository.UniversityRepository;
import com.lernia.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final UniversityRepository universityRepository;

    public RecommendationService(UserRepository userRepository,
            CourseRepository courseRepository,
            UniversityRepository universityRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.universityRepository = universityRepository;
    }

    @Transactional(readOnly = true)
    public List<SuggestionResponseDTO> getRecommendations(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<CourseEntity> bookmarkedCourses = user.getBookmarkedCourses();
        List<UniversityEntity> bookmarkedUniversities = user.getBookmarkedUniversities();
        String userLocation = user.getLocation();

        // Collect user preferences
        Set<Long> bookmarkedCourseIds = bookmarkedCourses.stream()
                .map(CourseEntity::getId)
                .collect(Collectors.toSet());

        Set<Long> bookmarkedUniversityIds = bookmarkedUniversities.stream()
                .map(UniversityEntity::getId)
                .collect(Collectors.toSet());

        Set<Long> preferredAreaIds = bookmarkedCourses.stream()
                .flatMap(c -> c.getAreasOfStudy().stream())
                .map(AreaOfStudyEntity::getId)
                .collect(Collectors.toSet());

        Set<String> preferredCourseTypes = bookmarkedCourses.stream()
                .map(CourseEntity::getCourseType)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> preferredUniversityIds = bookmarkedCourses.stream()
                .filter(c -> c.getUniversity() != null)
                .map(c -> c.getUniversity().getId())
                .collect(Collectors.toSet());

        // Score all courses
        List<ScoredItem> scoredItems = new ArrayList<>();

        List<CourseEntity> allCourses = courseRepository.findAll();
        for (CourseEntity course : allCourses) {
            // Skip already bookmarked courses
            if (bookmarkedCourseIds.contains(course.getId())) {
                continue;
            }

            double score = calculateCourseScore(course, preferredAreaIds, preferredCourseTypes,
                    preferredUniversityIds, userLocation);

            if (score > 0) {
                scoredItems.add(new ScoredItem(course.getId(), "course", score, course));
            }
        }

        // Score all universities
        List<UniversityEntity> allUniversities = universityRepository.findAll();
        for (UniversityEntity university : allUniversities) {
            // Skip already bookmarked universities
            if (bookmarkedUniversityIds.contains(university.getId())) {
                continue;
            }

            double score = calculateUniversityScore(university, preferredUniversityIds,
                    bookmarkedCourses, userLocation);

            if (score > 0) {
                scoredItems.add(new ScoredItem(university.getId(), "university", score, university));
            }
        }

        // If no preferences exist, return popular items
        if (scoredItems.isEmpty() || preferredAreaIds.isEmpty()) {
            return getPopularItems();
        }

        // Sort by score descending and take top 10
        return scoredItems.stream()
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(10)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private double calculateCourseScore(CourseEntity course,
            Set<Long> preferredAreaIds,
            Set<String> preferredCourseTypes,
            Set<Long> preferredUniversityIds,
            String userLocation) {
        double score = 0;

        // Area of Study match (+30)
        Set<Long> courseAreaIds = course.getAreasOfStudy().stream()
                .map(AreaOfStudyEntity::getId)
                .collect(Collectors.toSet());

        if (!Collections.disjoint(courseAreaIds, preferredAreaIds)) {
            score += 30;
        }

        // Location preference (+25)
        if (userLocation != null && course.getUniversity() != null
                && course.getUniversity().getLocation() != null) {
            String courseLocation = course.getUniversity().getLocation().getCountry();
            if (courseLocation != null &&
                    (userLocation.toLowerCase().contains(courseLocation.toLowerCase()) ||
                            courseLocation.toLowerCase().contains(userLocation.toLowerCase()))) {
                score += 25;
            }
        }

        // Course type match (+20)
        if (preferredCourseTypes.contains(course.getCourseType())) {
            score += 20;
        }

        // University connection (+15)
        if (course.getUniversity() != null && preferredUniversityIds.contains(course.getUniversity().getId())) {
            score += 15;
        }

        // Popularity bonus based on being remote/accessible (+5)
        if (Boolean.TRUE.equals(course.getIsRemote())) {
            score += 5;
        }

        return score;
    }

    private double calculateUniversityScore(UniversityEntity university,
            Set<Long> preferredUniversityIds,
            List<CourseEntity> bookmarkedCourses,
            String userLocation) {
        double score = 0;

        // Location match (+25)
        if (userLocation != null && university.getLocation() != null) {
            String uniLocation = university.getLocation().getCountry();
            if (uniLocation != null && (userLocation.toLowerCase().contains(uniLocation.toLowerCase()) ||
                    uniLocation.toLowerCase().contains(userLocation.toLowerCase()))) {
                score += 25;
            }
        }

        // Similar universities in same location as bookmarked universities
        if (preferredUniversityIds.stream()
                .anyMatch(id -> {
                    UniversityEntity bookmarked = universityRepository.findById(id).orElse(null);
                    return bookmarked != null && bookmarked.getLocation() != null
                            && university.getLocation() != null
                            && Objects.equals(bookmarked.getLocation().getCountry(),
                                    university.getLocation().getCountry());
                })) {
            score += 20;
        }

        return score;
    }

    private List<SuggestionResponseDTO> getPopularItems() {
        // Return top courses and universities as fallback
        List<SuggestionResponseDTO> popular = new ArrayList<>();

        // Get first 5 courses
        courseRepository.findAll().stream()
                .limit(5)
                .map(course -> {
                    SuggestionResponseDTO dto = new SuggestionResponseDTO();
                    dto.setId(course.getId());
                    dto.setTitle(course.getName());
                    dto.setDescription(course.getDescription());
                    dto.setType("course");
                    dto.setMatchScore(50.0); // Default score for popular items
                    dto.setCourseType(course.getCourseType());
                    if (course.getUniversity() != null) {
                        dto.setUniversityName(course.getUniversity().getName());
                        if (course.getUniversity().getLocation() != null) {
                            dto.setLocation(course.getUniversity().getLocation().getCountry());
                        }
                    }
                    return dto;
                })
                .forEach(popular::add);

        // Get first 5 universities
        universityRepository.findAll().stream()
                .limit(5)
                .map(uni -> {
                    SuggestionResponseDTO dto = new SuggestionResponseDTO();
                    dto.setId(uni.getId());
                    dto.setTitle(uni.getName());
                    dto.setDescription(uni.getDescription());
                    dto.setType("university");
                    dto.setMatchScore(50.0);
                    if (uni.getLocation() != null) {
                        dto.setLocation(uni.getLocation().getCountry());
                    }
                    return dto;
                })
                .forEach(popular::add);

        return popular;
    }

    private SuggestionResponseDTO toDTO(ScoredItem item) {
        SuggestionResponseDTO dto = new SuggestionResponseDTO();
        dto.setId(item.id);
        dto.setType(item.type);
        dto.setMatchScore(Math.min(item.score, 100)); // Cap at 100

        if ("course".equals(item.type)) {
            CourseEntity course = (CourseEntity) item.entity;
            dto.setTitle(course.getName());
            dto.setDescription(course.getDescription());
            dto.setCourseType(course.getCourseType());
            if (course.getUniversity() != null) {
                dto.setUniversityName(course.getUniversity().getName());
                if (course.getUniversity().getLocation() != null) {
                    dto.setLocation(course.getUniversity().getLocation().getCountry());
                }
            }
        } else {
            UniversityEntity university = (UniversityEntity) item.entity;
            dto.setTitle(university.getName());
            dto.setDescription(university.getDescription());
            if (university.getLocation() != null) {
                dto.setLocation(university.getLocation().getCountry());
            }
        }

        return dto;
    }

    private static class ScoredItem {
        Long id;
        String type;
        double score;
        Object entity;

        ScoredItem(Long id, String type, double score, Object entity) {
            this.id = id;
            this.type = type;
            this.score = score;
            this.entity = entity;
        }
    }
}
