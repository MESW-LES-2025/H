package com.lernia.auth.service;

import com.lernia.auth.dto.ReviewDTO;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.entity.UniversityReviewEntity;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.CourseReviewEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.repository.UniversityRepository; 
import com.lernia.auth.repository.UniversityReviewRepository;
import com.lernia.auth.repository.UserRepository; 
import com.lernia.auth.repository.UserCourseRepository;
import com.lernia.auth.repository.CourseReviewRepository;
import com.lernia.auth.repository.CourseRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final UniversityReviewRepository reviewRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final UniversityRepository universityRepository;
    private final CourseRepository courseRepository; 
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;

    public ReviewService(UniversityReviewRepository reviewRepository,
                         CourseReviewRepository courseReviewRepository,
                         UniversityRepository universityRepository,
                         CourseRepository courseRepository,
                         UserRepository userRepository,
                         UserCourseRepository userCourseRepository) {
        this.reviewRepository = reviewRepository;
        this.courseReviewRepository = courseReviewRepository;
        this.universityRepository = universityRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.userCourseRepository = userCourseRepository;
    }

    public boolean canUserReview(Long userId, Long universityId) {
        return userCourseRepository.existsByUserIdAndCourse_UniversityId(userId, universityId);
    }

    public boolean canUserReviewCourse(Long userId, Long courseId) {
        return userCourseRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    public List<ReviewDTO> getReviewsByUniversity(Long universityId) {
        return reviewRepository.findByUniversityIdOrderByReviewDateDesc(universityId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByCourse(Long courseId) {
        return courseReviewRepository.findByCourseIdOrderByReviewDateDesc(courseId).stream()
                .map(this::convertCourseReviewToDto)
                .collect(Collectors.toList());
    }

    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        if (!canUserReview(reviewDTO.getUserId(), reviewDTO.getUniversityId())) {
            throw new RuntimeException("User is not eligible to review this university.");
        }

        UniversityReviewEntity review = new UniversityReviewEntity();
        review.setRating(reviewDTO.getRating());
        review.setTitle(reviewDTO.getTitle());
        review.setDescription(reviewDTO.getDescription());
        review.setReviewDate(LocalDate.now());
        
        UniversityEntity university = universityRepository.findById(reviewDTO.getUniversityId())
                .orElseThrow(() -> new RuntimeException("University not found"));
        UserEntity user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        review.setUniversity(university);
        review.setUser(user);

        UniversityReviewEntity savedReview = reviewRepository.save(review);
        return convertToDto(savedReview);
    }

    public ReviewDTO addCourseReview(ReviewDTO reviewDTO) {
        if (!canUserReviewCourse(reviewDTO.getUserId(), reviewDTO.getCourseId())) {
            throw new RuntimeException("User is not eligible to review this course.");
        }

        CourseReviewEntity review = new CourseReviewEntity();
        review.setRating(reviewDTO.getRating());
        review.setTitle(reviewDTO.getTitle());
        review.setDescription(reviewDTO.getDescription());
        review.setReviewDate(LocalDate.now());

        CourseEntity course = courseRepository.findById(reviewDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        UserEntity user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        review.setCourse(course);
        review.setUser(user);

        CourseReviewEntity savedReview = courseReviewRepository.save(review);
        return convertCourseReviewToDto(savedReview);
    }

    public void deleteReview(Long reviewId, Long userId) {
        UniversityReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this review.");
        }

        reviewRepository.delete(review);
    }

    public void deleteCourseReview(Long reviewId, Long userId) {
        CourseReviewEntity review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this review.");
        }

        courseReviewRepository.delete(review);
    }

    public ReviewDTO updateReview(Long reviewId, ReviewDTO reviewDTO, Long userId) {
        UniversityReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to edit this review.");
        }

        review.setRating(reviewDTO.getRating());
        review.setTitle(reviewDTO.getTitle());
        review.setDescription(reviewDTO.getDescription());
        review.setReviewDate(LocalDate.now());

        UniversityReviewEntity updatedReview = reviewRepository.save(review);
        return convertToDto(updatedReview);
    }

    public ReviewDTO updateCourseReview(Long reviewId, ReviewDTO reviewDTO, Long userId) {
        CourseReviewEntity review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to edit this review.");
        }

        review.setRating(reviewDTO.getRating());
        review.setTitle(reviewDTO.getTitle());
        review.setDescription(reviewDTO.getDescription());
        review.setReviewDate(LocalDate.now());

        CourseReviewEntity updatedReview = courseReviewRepository.save(review);
        return convertCourseReviewToDto(updatedReview);
    }

    private ReviewDTO convertToDto(UniversityReviewEntity review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setDescription(review.getDescription());
        dto.setReviewDate(review.getReviewDate());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getName());
        dto.setUniversityId(review.getUniversity().getId());
        return dto;
    }

    private ReviewDTO convertCourseReviewToDto(CourseReviewEntity review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setDescription(review.getDescription());
        dto.setReviewDate(review.getReviewDate());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getName());
        dto.setCourseId(review.getCourse().getId());
        return dto;
    }
}