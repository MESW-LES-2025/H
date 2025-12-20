package com.lernia.auth.controller;

import com.lernia.auth.dto.request.SubscriptionRequest;
import com.lernia.auth.dto.response.SubscriptionResponse;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UserRepository;
import com.lernia.auth.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    public SubscriptionController(SubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<SubscriptionResponse> subscribe(
            @RequestBody SubscriptionRequest request,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(new SubscriptionResponse("Authentication required", "error"));
        }

        Long userId = getUserIdFromPrincipal(principal);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new SubscriptionResponse("User not found", "error"));
        }

        SubscriptionResponse response = subscriptionService.subscribe(userId, request);

        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/me/status")
    public ResponseEntity<SubscriptionResponse> getSubscriptionStatus(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(new SubscriptionResponse("Authentication required", "error"));
        }

        Long userId = getUserIdFromPrincipal(principal);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new SubscriptionResponse("User not found", "error"));
        }

        SubscriptionResponse response = subscriptionService.getSubscriptionStatus(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/subscriptions")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(new SubscriptionResponse("Authentication required", "error"));
        }

        Long userId = getUserIdFromPrincipal(principal);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new SubscriptionResponse("User not found", "error"));
        }

        SubscriptionResponse response = subscriptionService.cancelSubscription(userId);

        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        String username = principal.getName();
        Optional<UserEntity> user = userRepository.findByUsername(username);
        return user.map(UserEntity::getId).orElse(null);
    }
}
