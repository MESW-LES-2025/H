package com.lernia.auth.controller;

import com.lernia.auth.dto.SuggestionResponseDTO;
import com.lernia.auth.entity.enums.UserRole;
import com.lernia.auth.repository.UserRepository;
import com.lernia.auth.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    public RecommendationController(RecommendationService recommendationService,
            UserRepository userRepository) {
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getRecommendations(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
        }

        try {
            String username = principal.getName();

            // Check if user exists and is premium
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }

            var user = userOpt.get();
            if (user.getUserRole() != UserRole.PREMIUM) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Premium subscription required", "requiresPremium", true));
            }

            List<SuggestionResponseDTO> recommendations = recommendationService.getRecommendations(user.getId());
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching recommendations: " + e.getMessage()));
        }
    }
}
