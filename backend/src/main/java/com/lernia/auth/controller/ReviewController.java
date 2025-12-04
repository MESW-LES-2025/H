package com.lernia.auth.controller;

import com.lernia.auth.dto.ReviewDTO;
import com.lernia.auth.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/university/{universityId}")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Long universityId) {
        return ResponseEntity.ok(reviewService.getReviewsByUniversity(universityId));
    }

    @GetMapping("/eligibility/{universityId}")
    public ResponseEntity<Boolean> checkEligibility(@PathVariable Long universityId, @RequestParam Long userId) {
        return ResponseEntity.ok(reviewService.canUserReview(userId, universityId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ReviewDTO>> getCourseReviews(@PathVariable Long courseId) {
        return ResponseEntity.ok(reviewService.getReviewsByCourse(courseId));
    }

    @GetMapping("/course/eligibility/{courseId}")
    public ResponseEntity<Boolean> checkCourseEligibility(@PathVariable Long courseId, @RequestParam Long userId) {
        return ResponseEntity.ok(reviewService.canUserReviewCourse(userId, courseId));
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO reviewDto) {
        try {
            return ResponseEntity.ok(reviewService.addReview(reviewDto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/course")
    public ResponseEntity<?> addCourseReview(@RequestBody ReviewDTO reviewDto) {
        try {
            return ResponseEntity.ok(reviewService.addCourseReview(reviewDto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, @RequestParam Long userId) {
        try {
            reviewService.deleteReview(reviewId, userId);
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/course/{reviewId}")
    public ResponseEntity<?> deleteCourseReview(@PathVariable Long reviewId, @RequestParam Long userId) {
        try {
            reviewService.deleteCourseReview(reviewId, userId);
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}