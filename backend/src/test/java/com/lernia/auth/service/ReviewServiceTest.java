package com.lernia.auth.service;

import com.lernia.auth.dto.ReviewDTO;
import com.lernia.auth.entity.*;
import com.lernia.auth.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private UniversityReviewRepository reviewRepository;
    @Mock
    private CourseReviewRepository courseReviewRepository;
    @Mock
    private UniversityRepository universityRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCourseRepository userCourseRepository;

    @InjectMocks
    private ReviewService reviewService;

    private UserEntity user;
    private UniversityEntity university;
    private CourseEntity course;
    private ReviewDTO reviewDTO;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setName("Test User");

        university = new UniversityEntity();
        university.setId(1L);
        university.setName("Test University");

        course = new CourseEntity();
        course.setId(1L);
        course.setName("Test Course");

        reviewDTO = new ReviewDTO();
        reviewDTO.setUserId(1L);
        reviewDTO.setRating(5F);
        reviewDTO.setTitle("Great!");
        reviewDTO.setDescription("Loved it.");
    }

    // --- University Review Tests ---

    @Test
    void addReview_ShouldSucceed_WhenUserIsEligible() {
        reviewDTO.setUniversityId(1L);

        when(userCourseRepository.existsByUserIdAndCourse_UniversityId(1L, 1L)).thenReturn(true);
        when(universityRepository.findById(1L)).thenReturn(Optional.of(university));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(UniversityReviewEntity.class))).thenAnswer(invocation -> {
            UniversityReviewEntity saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        ReviewDTO result = reviewService.addReview(reviewDTO);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Great!", result.getTitle());
        verify(reviewRepository).save(any(UniversityReviewEntity.class));
    }

    @Test
    void addReview_ShouldThrowException_WhenUserNotEligible() {
        reviewDTO.setUniversityId(1L);
        when(userCourseRepository.existsByUserIdAndCourse_UniversityId(1L, 1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.addReview(reviewDTO);
        });

        assertEquals("User is not eligible to review this university.", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void deleteReview_ShouldSucceed_WhenUserIsOwner() {
        UniversityReviewEntity review = new UniversityReviewEntity();
        review.setId(10L);
        review.setUser(user);

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(10L, 1L);

        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_ShouldThrowException_WhenUserIsNotOwner() {
        UserEntity otherUser = new UserEntity();
        otherUser.setId(99L);

        UniversityReviewEntity review = new UniversityReviewEntity();
        review.setId(10L);
        review.setUser(otherUser); // Owner is ID 99

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(10L, 1L); // Trying to delete with ID 1
        });

        assertEquals("You are not authorized to delete this review.", exception.getMessage());
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void updateReview_ShouldSucceed_WhenUserIsOwner() {
        UniversityReviewEntity existingReview = new UniversityReviewEntity();
        existingReview.setId(10L);
        existingReview.setUser(user);
        existingReview.setTitle("Old Title");
        existingReview.setUniversity(university); // Needed for DTO conversion

        reviewDTO.setTitle("New Title");

        when(reviewRepository.findById(10L)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(UniversityReviewEntity.class))).thenAnswer(i -> i.getArgument(0));

        ReviewDTO result = reviewService.updateReview(10L, reviewDTO, 1L);

        assertEquals("New Title", result.getTitle());
        verify(reviewRepository).save(existingReview);
    }

    // --- Course Review Tests ---

    @Test
    void addCourseReview_ShouldSucceed_WhenUserIsEligible() {
        reviewDTO.setCourseId(1L);

        when(userCourseRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(true);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseReviewRepository.save(any(CourseReviewEntity.class))).thenAnswer(invocation -> {
            CourseReviewEntity saved = invocation.getArgument(0);
            saved.setId(200L);
            return saved;
        });

        ReviewDTO result = reviewService.addCourseReview(reviewDTO);

        assertNotNull(result);
        assertEquals(200L, result.getId());
        verify(courseReviewRepository).save(any(CourseReviewEntity.class));
    }

    @Test
    void addCourseReview_ShouldThrowException_WhenUserNotEligible() {
        reviewDTO.setCourseId(1L);
        when(userCourseRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.addCourseReview(reviewDTO);
        });

        assertEquals("User is not eligible to review this course.", exception.getMessage());
        verify(courseReviewRepository, never()).save(any());
    }

    @Test
    void deleteCourseReview_ShouldSucceed_WhenUserIsOwner() {
        CourseReviewEntity review = new CourseReviewEntity();
        review.setId(20L);
        review.setUser(user);

        when(courseReviewRepository.findById(20L)).thenReturn(Optional.of(review));

        reviewService.deleteCourseReview(20L, 1L);

        verify(courseReviewRepository).delete(review);
    }

    @Test
    void deleteCourseReview_ShouldThrowException_WhenUserIsNotOwner() {
        UserEntity otherUser = new UserEntity();
        otherUser.setId(99L);

        CourseReviewEntity review = new CourseReviewEntity();
        review.setId(20L);
        review.setUser(otherUser); // Owner is ID 99

        when(courseReviewRepository.findById(20L)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteCourseReview(20L, 1L); // Trying to delete with ID 1
        });

        assertEquals("You are not authorized to delete this review.", exception.getMessage());
        verify(courseReviewRepository, never()).delete(any());
    }

    @Test
    void updateCourseReview_ShouldSucceed_WhenUserIsOwner() {
        CourseReviewEntity existingReview = new CourseReviewEntity();
        existingReview.setId(20L);
        existingReview.setUser(user);
        existingReview.setTitle("Old Course Title");
        existingReview.setCourse(course); // Needed for DTO conversion

        reviewDTO.setTitle("New Course Title");

        when(courseReviewRepository.findById(20L)).thenReturn(Optional.of(existingReview));
        when(courseReviewRepository.save(any(CourseReviewEntity.class))).thenAnswer(i -> i.getArgument(0));

        ReviewDTO result = reviewService.updateCourseReview(20L, reviewDTO, 1L);

        assertEquals("New Course Title", result.getTitle());
        verify(courseReviewRepository).save(existingReview);
    }

    @Test
    void getReviewsByCourse_ShouldReturnList() {
        CourseReviewEntity review1 = new CourseReviewEntity();
        review1.setId(2L);
        review1.setUser(user);
        review1.setCourse(course);

        when(courseReviewRepository.findByCourseIdOrderByReviewDateDesc(1L)).thenReturn(List.of(review1));

        List<ReviewDTO> results = reviewService.getReviewsByCourse(1L);

        assertEquals(1, results.size());
        assertEquals(2L, results.get(0).getId());
    }
}