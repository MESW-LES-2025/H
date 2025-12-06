package com.lernia.auth.controller;

import com.lernia.auth.dto.ReviewDTO;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UserRepository;
import com.lernia.auth.service.ReviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    private final UserRepository userRepository;

    @GetMapping("/university/{universityId}")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Long universityId) {
        return ResponseEntity.ok(reviewService.getReviewsByUniversity(universityId));
    }

    @GetMapping("/eligibility/{universityId}")
    public ResponseEntity<Boolean> checkEligibility(@PathVariable Long universityId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(false);
        }
        UserEntity user = getUserFromPrincipal(principal);
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
        UserEntity user = getUserFromPrincipal(principal);
        return ResponseEntity.ok(reviewService.canUserReviewCourse(user.getId(), courseId));
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO reviewDto, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "You must be logged in to post a review."));
        }
        try {
            UserEntity user = getUserFromPrincipal(principal);
            
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
            UserEntity user = getUserFromPrincipal(principal);
            
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
            if (principal == null) {
                return ResponseEntity.status(401).body(Map.of("message", "You must be logged in."));
            }
            UserEntity user = getUserFromPrincipal(principal);
            
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
            UserEntity user = getUserFromPrincipal(principal);
            
            reviewService.deleteCourseReview(reviewId, user.getId());
            
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody ReviewDTO reviewDto, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "You must be logged in."));
        }
        try {
            UserEntity user = getUserFromPrincipal(principal);

            return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewDto, user.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/course/{reviewId}")
    public ResponseEntity<?> updateCourseReview(@PathVariable Long reviewId, @RequestBody ReviewDTO reviewDto, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "You must be logged in."));
        }
        try {
            UserEntity user = getUserFromPrincipal(principal);

            return ResponseEntity.ok(reviewService.updateCourseReview(reviewId, reviewDto, user.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private UserEntity getUserFromPrincipal(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}