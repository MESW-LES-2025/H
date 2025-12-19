package com.lernia.auth.controller;

import com.lernia.auth.dto.ProbabilityResponseDTO;
import com.lernia.auth.entity.enums.UserRole;
import com.lernia.auth.repository.UserRepository;
import com.lernia.auth.service.EntryProbabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class EntryProbabilityController {

    private final EntryProbabilityService entryProbabilityService;
    private final UserRepository userRepository;

    public EntryProbabilityController(EntryProbabilityService entryProbabilityService,
            UserRepository userRepository) {
        this.entryProbabilityService = entryProbabilityService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{courseId}/entry-probability")
    public ResponseEntity<?> getEntryProbability(@PathVariable Long courseId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
        }

        try {
            String username = principal.getName();

            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }

            var user = userOpt.get();

            // Check if user is premium
            if (user.getUserRole() != UserRole.PREMIUM) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Premium subscription required", "requiresPremium", true));
            }

            ProbabilityResponseDTO probability = entryProbabilityService.calculateProbability(user.getId(), courseId);
            return ResponseEntity.ok(probability);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error calculating probability: " + e.getMessage()));
        }
    }
}
