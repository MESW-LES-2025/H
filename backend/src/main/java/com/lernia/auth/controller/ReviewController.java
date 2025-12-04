package com.lernia.auth.controller;

import com.lernia.auth.dto.ReviewDTO;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UserRepository;
import com.lernia.auth.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/university/{universityId}")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Long universityId) {
        return ResponseEntity.ok(reviewService.getReviewsByUniversity(universityId));
    }

    @GetMapping("/eligibility/{universityId}")
    public ResponseEntity<Boolean> checkEligibility(@PathVariable Long universityId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(false);
        }
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(reviewService.canUserReview(user.getId(), universityId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ReviewDTO>> getCourseReviews(@PathVariable Long courseId) {
        return ResponseEntity.ok(reviewService.getReviewsByCourse(courseId));
    }

    @GetMapping("/course/eligibility/{courseId}")
    public ResponseEntity<Boolean> checkCourseEligibility(@PathVariable Long courseId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(false);
        }
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(reviewService.canUserReviewCourse(user.getId(), courseId));
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO reviewDto, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "You must be logged in to post a review."));
        }
        try {
            UserEntity user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            reviewDto.setUserId(user.getId());
            
            return ResponseEntity.ok(reviewService.addReview(reviewDto));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "An unexpected error occurred";
            return ResponseEntity.badRequest().body(Map.of("message", msg));
        }
    }

    @PostMapping("/course")
    public ResponseEntity<?> addCourseReview(@RequestBody ReviewDTO reviewDto, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "You must be logged in to post a review."));
        }
        try {
            UserEntity user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            reviewDto.setUserId(user.getId());
            
            return ResponseEntity.ok(reviewService.addCourseReview(reviewDto));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "An unexpected error occurred";
            return ResponseEntity.badRequest().body(Map.of("message", msg));
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, Principal principal) {
        try {
            UserEntity user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Pass the authenticated user's ID
            reviewService.deleteReview(reviewId, user.getId());
            
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/course/{reviewId}")
    public ResponseEntity<?> deleteCourseReview(@PathVariable Long reviewId, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body(Map.of("message", "You must be logged in."));
            }
            UserEntity user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            reviewService.deleteCourseReview(reviewId, user.getId());
            
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}