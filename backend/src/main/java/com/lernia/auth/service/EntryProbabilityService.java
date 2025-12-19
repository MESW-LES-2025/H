package com.lernia.auth.service;

import com.lernia.auth.dto.ProbabilityResponseDTO;
import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.enums.EducationLevel;
import com.lernia.auth.repository.CourseRepository;
import com.lernia.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EntryProbabilityService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EntryProbabilityService(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public ProbabilityResponseDTO calculateProbability(Long userId, Long courseId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Map<String, String> factors = new LinkedHashMap<>();
        int totalScore = 0;

        // 1. Grade comparison (50% weight, max 45 points)
        int gradeScore = calculateGradeScore(user, course, factors);
        totalScore += gradeScore;

        // 2. Education level match (30% weight, max 30 points)
        int educationScore = calculateEducationScore(user, course, factors);
        totalScore += educationScore;

        // 3. Study area alignment (20% weight, max 20 points)
        int areaScore = calculateAreaScore(user, course, factors);
        totalScore += areaScore;

        // Clamp to 15-95 range (never 0% or 100%)
        int percentage = Math.max(15, Math.min(95, totalScore));

        // Determine confidence level and label
        String confidenceLevel;
        String label;

        if (percentage >= 70) {
            confidenceLevel = "HIGH";
            label = "Excellent Fit";
        } else if (percentage >= 45) {
            confidenceLevel = "MEDIUM";
            label = "Good Match";
        } else {
            confidenceLevel = "LOW";
            label = "Low Chance";
        }

        return new ProbabilityResponseDTO(courseId, percentage, confidenceLevel, label, factors);
    }

    private int calculateGradeScore(UserEntity user, CourseEntity course, Map<String, String> factors) {
        Integer userGrade = user.getAcademicGrade();
        Integer minGrade = course.getMinAdmissionGrade();

        if (userGrade == null) {
            factors.put("Academic Grade", "Not provided - update your profile");
            return 20; // Default score if no grade
        }

        if (minGrade == null) {
            factors.put("Academic Grade", "No minimum required");
            return 40; // Course has no minimum requirement
        }

        int difference = userGrade - minGrade;

        if (difference >= 20) {
            factors.put("Academic Grade", "Exceeds requirements (" + userGrade + "/" + minGrade + ")");
            return 45;
        } else if (difference >= 0) {
            factors.put("Academic Grade", "Meets requirements (" + userGrade + "/" + minGrade + ")");
            return 35;
        } else if (difference >= -10) {
            factors.put("Academic Grade", "Slightly below (" + userGrade + "/" + minGrade + ")");
            return 20;
        } else {
            factors.put("Academic Grade", "Below requirements (" + userGrade + "/" + minGrade + ")");
            return 5;
        }
    }

    private int calculateEducationScore(UserEntity user, CourseEntity course, Map<String, String> factors) {
        EducationLevel userLevel = user.getEducationLevel();
        String courseType = course.getCourseType();

        if (userLevel == null) {
            factors.put("Education Level", "Not provided - update your profile");
            return 15; // Default score
        }

        // Determine required education level based on course type
        boolean meetsRequirement = false;
        String requirement = "Unknown";

        if (courseType != null) {
            String lowerCourseType = courseType.toLowerCase();

            if (lowerCourseType.contains("bachelor") || lowerCourseType.contains("undergraduate")) {
                requirement = "High School";
                meetsRequirement = true; // Any level can apply
            } else if (lowerCourseType.contains("master")) {
                requirement = "Bachelor's Degree";
                meetsRequirement = userLevel == EducationLevel.BACHELORS
                        || userLevel == EducationLevel.MASTERS
                        || userLevel == EducationLevel.PHD;
            } else if (lowerCourseType.contains("phd") || lowerCourseType.contains("doctor")) {
                requirement = "Master's Degree";
                meetsRequirement = userLevel == EducationLevel.MASTERS
                        || userLevel == EducationLevel.PHD;
            } else {
                // Default: any level acceptable
                meetsRequirement = true;
            }
        } else {
            meetsRequirement = true;
        }

        if (meetsRequirement) {
            factors.put("Education Level", "Meets requirement (" + formatEducationLevel(userLevel) + ")");
            return 30;
        } else {
            factors.put("Education Level",
                    "Requires " + requirement + " (You have: " + formatEducationLevel(userLevel) + ")");
            return 5;
        }
    }

    private int calculateAreaScore(UserEntity user, CourseEntity course, Map<String, String> factors) {
        String userArea = user.getStudyArea();

        if (userArea == null || userArea.isEmpty()) {
            factors.put("Study Area", "Not provided - update your profile");
            return 10; // Default score
        }

        Set<String> courseAreas = course.getAreasOfStudy().stream()
                .map(AreaOfStudyEntity::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        String userAreaLower = userArea.toLowerCase();

        // Check if user's area matches any course area
        boolean matches = courseAreas.stream()
                .anyMatch(a -> a.contains(userAreaLower) || userAreaLower.contains(a));

        if (matches) {
            factors.put("Study Area", "Related field (" + userArea + ")");
            return 20;
        } else {
            factors.put("Study Area", "Different field (" + userArea + ")");
            return 8;
        }
    }

    private String formatEducationLevel(EducationLevel level) {
        switch (level) {
            case HIGH_SCHOOL:
                return "High School";
            case BACHELORS:
                return "Bachelor's";
            case MASTERS:
                return "Master's";
            case PHD:
                return "PhD";
            default:
                return level.name();
        }
    }
}
