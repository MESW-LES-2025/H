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

    @Test
    void canUserReview_ShouldDelegateToRepository() {
        when(userCourseRepository.existsByUserIdAndCourse_UniversityId(1L, 9L)).thenReturn(true);

        assertTrue(reviewService.canUserReview(1L, 9L));
        verify(userCourseRepository).existsByUserIdAndCourse_UniversityId(1L, 9L);
    }

    @Test
    void canUserReviewCourse_ShouldDelegateToRepository() {
        when(userCourseRepository.existsByUserIdAndCourseId(1L, 7L)).thenReturn(false);

        assertFalse(reviewService.canUserReviewCourse(1L, 7L));
        verify(userCourseRepository).existsByUserIdAndCourseId(1L, 7L);
    }

    @Test
    void getReviewsByUniversity_ShouldReturnMappedDtos() {
        UniversityReviewEntity entity = new UniversityReviewEntity();
        entity.setId(3L);
        entity.setRating(4.5F);
        entity.setTitle("Nice");
        entity.setDescription("Detailed");
        entity.setReviewDate(LocalDate.now());
        entity.setUser(user);
        entity.setUniversity(university);

        when(reviewRepository.findByUniversityIdOrderByReviewDateDesc(1L)).thenReturn(List.of(entity));

        List<ReviewDTO> result = reviewService.getReviewsByUniversity(1L);

        assertEquals(1, result.size());
        ReviewDTO dto = result.get(0);
        assertEquals(3L, dto.getId());
        assertEquals("Test User", dto.getUserName());
        assertEquals(university.getId(), dto.getUniversityId());
    }

    @Test
    void addReview_ShouldThrow_WhenUniversityMissing() {
        reviewDTO.setUniversityId(2L);
        when(userCourseRepository.existsByUserIdAndCourse_UniversityId(1L, 2L)).thenReturn(true);
        when(universityRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.addReview(reviewDTO));
        assertEquals("University not found", ex.getMessage());
    }

    @Test
    void addReview_ShouldThrow_WhenUserMissing() {
        reviewDTO.setUniversityId(2L);
        when(userCourseRepository.existsByUserIdAndCourse_UniversityId(1L, 2L)).thenReturn(true);
        when(universityRepository.findById(2L)).thenReturn(Optional.of(university));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.addReview(reviewDTO));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void deleteReview_ShouldThrow_WhenReviewMissing() {
        when(reviewRepository.findById(50L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.deleteReview(50L, 1L));
        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void updateReview_ShouldThrow_WhenReviewMissing() {
        when(reviewRepository.findById(80L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.updateReview(80L, reviewDTO, 1L));
        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void updateReview_ShouldThrow_WhenUserNotOwner() {
        UniversityReviewEntity entity = new UniversityReviewEntity();
        UserEntity other = new UserEntity();
        other.setId(9L);
        entity.setUser(other);

        when(reviewRepository.findById(11L)).thenReturn(Optional.of(entity));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.updateReview(11L, reviewDTO, 1L));
        assertEquals("You are not authorized to edit this review.", ex.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void getReviewsByCourse_ShouldReturnMappedDtos() {
        CourseReviewEntity entity = new CourseReviewEntity();
        entity.setId(15L);
        entity.setRating(3.5F);
        entity.setTitle("Solid");
        entity.setDescription("Informative");
        entity.setReviewDate(LocalDate.now());
        entity.setUser(user);
        entity.setCourse(course);

        when(courseReviewRepository.findByCourseIdOrderByReviewDateDesc(1L)).thenReturn(List.of(entity));

        List<ReviewDTO> result = reviewService.getReviewsByCourse(1L);

        assertEquals(1, result.size());
        ReviewDTO dto = result.getFirst();
        assertEquals(15L, dto.getId());
        assertEquals("Test User", dto.getUserName());
        assertEquals(course.getId(), dto.getCourseId());
    }

    @Test
    void addCourseReview_ShouldSucceed_WhenUserIsEligible() {
        reviewDTO.setCourseId(3L);

        when(userCourseRepository.existsByUserIdAndCourseId(1L, 3L)).thenReturn(true);
        when(courseRepository.findById(3L)).thenReturn(Optional.of(course));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseReviewRepository.save(any(CourseReviewEntity.class))).thenAnswer(invocation -> {
            CourseReviewEntity saved = invocation.getArgument(0);
            saved.setId(200L);
            return saved;
        });

        ReviewDTO result = reviewService.addCourseReview(reviewDTO);

        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertEquals("Great!", result.getTitle());
        verify(courseReviewRepository).save(any(CourseReviewEntity.class));
    }

    @Test
    void addCourseReview_ShouldThrow_WhenUserNotEligible() {
        reviewDTO.setCourseId(4L);
        when(userCourseRepository.existsByUserIdAndCourseId(1L, 4L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.addCourseReview(reviewDTO));
        assertEquals("User is not eligible to review this course.", ex.getMessage());
        verify(courseReviewRepository, never()).save(any());
    }

    @Test
    void deleteCourseReview_ShouldSucceed_WhenUserIsOwner() {
        CourseReviewEntity review = new CourseReviewEntity();
        review.setId(25L);
        review.setUser(user);

        when(courseReviewRepository.findById(25L)).thenReturn(Optional.of(review));

        reviewService.deleteCourseReview(25L, 1L);

        verify(courseReviewRepository).delete(review);
    }

    @Test
    void addCourseReview_ShouldThrow_WhenCourseMissing() {
        reviewDTO.setCourseId(5L);
        when(userCourseRepository.existsByUserIdAndCourseId(1L, 5L)).thenReturn(true);
        when(courseRepository.findById(5L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.addCourseReview(reviewDTO));
        assertEquals("Course not found", ex.getMessage());
        verify(courseReviewRepository, never()).save(any());
    }

    @Test
    void addCourseReview_ShouldThrow_WhenUserMissing() {
        reviewDTO.setCourseId(6L);
        when(userCourseRepository.existsByUserIdAndCourseId(1L, 6L)).thenReturn(true);
        when(courseRepository.findById(6L)).thenReturn(Optional.of(course));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.addCourseReview(reviewDTO));
        assertEquals("User not found", ex.getMessage());
        verify(courseReviewRepository, never()).save(any());
    }

    @Test
    void deleteCourseReview_ShouldThrow_WhenReviewMissing() {
        when(courseReviewRepository.findById(70L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.deleteCourseReview(70L, 1L));
        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void deleteCourseReview_ShouldThrow_WhenUserNotOwner() {
        UserEntity other = new UserEntity();
        other.setId(9L);
        CourseReviewEntity review = new CourseReviewEntity();
        review.setId(71L);
        review.setUser(other);

        when(courseReviewRepository.findById(71L)).thenReturn(Optional.of(review));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.deleteCourseReview(71L, 1L));
        assertEquals("You are not authorized to delete this review.", ex.getMessage());
        verify(courseReviewRepository, never()).delete(any());
    }

    @Test
    void updateCourseReview_ShouldThrow_WhenReviewMissing() {
        when(courseReviewRepository.findById(80L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.updateCourseReview(80L, reviewDTO, 1L));
        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void updateCourseReview_ShouldThrow_WhenUserNotOwner() {
        UserEntity other = new UserEntity();
        other.setId(10L);
        CourseReviewEntity review = new CourseReviewEntity();
        review.setId(81L);
        review.setUser(other);

        when(courseReviewRepository.findById(81L)).thenReturn(Optional.of(review));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.updateCourseReview(81L, reviewDTO, 1L));
        assertEquals("You are not authorized to edit this review.", ex.getMessage());
        verify(courseReviewRepository, never()).save(any());
    }
}