package com.lernia.auth.service;

import com.lernia.auth.dto.request.SubscriptionRequest;
import com.lernia.auth.dto.response.SubscriptionResponse;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.enums.UserRole;
import com.lernia.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final UserRepository userRepository;

    public SubscriptionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public SubscriptionResponse subscribe(Long userId, SubscriptionRequest request) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return new SubscriptionResponse("User not found", "error");
        }

        UserEntity user = userOpt.get();

        // Check if already premium
        if (user.getUserRole() == UserRole.PREMIUM) {
            SubscriptionResponse response = new SubscriptionResponse("User is already a premium member", "error");
            response.setUserRole(user.getUserRole().name());
            response.setPremiumStartDate(user.getPremiumStartDate());
            return response;
        }

        // Mock payment validation - accept any payment method
        if (request.getPaymentMethod() == null || request.getPaymentMethod().isEmpty()) {
            return new SubscriptionResponse("Payment method is required", "error");
        }

        // Update user to premium
        user.setUserRole(UserRole.PREMIUM);
        user.setPremiumStartDate(LocalDate.now());
        userRepository.save(user);

        SubscriptionResponse response = new SubscriptionResponse(
                "Subscription successful! Welcome to Premium.",
                "success");
        response.setUserRole(UserRole.PREMIUM.name());
        response.setPremiumStartDate(user.getPremiumStartDate());

        return response;
    }

    public SubscriptionResponse getSubscriptionStatus(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return new SubscriptionResponse("User not found", "error");
        }

        UserEntity user = userOpt.get();
        SubscriptionResponse response = new SubscriptionResponse();
        response.setStatus("success");
        response.setUserRole(user.getUserRole().name());
        response.setPremiumStartDate(user.getPremiumStartDate());

        if (user.getUserRole() == UserRole.PREMIUM) {
            response.setMessage("User is a premium member");
        } else {
            response.setMessage("User is not a premium member");
        }

        return response;
    }

    @Transactional
    public SubscriptionResponse cancelSubscription(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return new SubscriptionResponse("User not found", "error");
        }

        UserEntity user = userOpt.get();

        // Check if user is actually premium
        if (user.getUserRole() != UserRole.PREMIUM) {
            SubscriptionResponse response = new SubscriptionResponse("User is not a premium member", "error");
            response.setUserRole(user.getUserRole().name());
            return response;
        }

        // Cancel subscription - downgrade to REGULAR
        user.setUserRole(UserRole.REGULAR);
        user.setPremiumStartDate(null);
        userRepository.save(user);

        SubscriptionResponse response = new SubscriptionResponse(
                "Subscription cancelled successfully. You have been downgraded to a free account.",
                "success");
        response.setUserRole(UserRole.REGULAR.name());
        response.setPremiumStartDate(null);

        return response;
    }
}
