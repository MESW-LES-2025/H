package com.lernia.auth.controller;

import com.lernia.auth.dto.ReviewDTO;
import com.lernia.auth.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<ReviewDTO> addReview(@RequestBody ReviewDTO reviewDto) {
        return ResponseEntity.ok(reviewService.addReview(reviewDto));
    }
}