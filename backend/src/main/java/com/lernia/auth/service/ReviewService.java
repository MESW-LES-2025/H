package com.lernia.auth.service;

import com.lernia.auth.dto.ReviewDTO;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.entity.UniversityReviewEntity;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UniversityRepository; 
import com.lernia.auth.repository.UniversityReviewRepository;
import com.lernia.auth.repository.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private UniversityReviewRepository reviewRepository;
    @Autowired
    private UniversityRepository universityRepository;
    @Autowired
    private UserRepository userRepository;

    public List<ReviewDTO> getReviewsByUniversity(Long universityId) {
        return reviewRepository.findByUniversityIdOrderByReviewDateDesc(universityId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ReviewDTO addReview(ReviewDTO ReviewDTO) {
        UniversityReviewEntity review = new UniversityReviewEntity();
        review.setRating(ReviewDTO.getRating());
        review.setTitle(ReviewDTO.getTitle());
        review.setDescription(ReviewDTO.getDescription());
        review.setReviewDate(LocalDate.now());
        
        UniversityEntity university = universityRepository.findById(ReviewDTO.getUniversityId())
                .orElseThrow(() -> new RuntimeException("University not found"));
        UserEntity user = userRepository.findById(ReviewDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        review.setUniversity(university);
        review.setUser(user);

        UniversityReviewEntity savedReview = reviewRepository.save(review);
        return convertToDto(savedReview);
    }

    private ReviewDTO convertToDto(UniversityReviewEntity review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setDescription(review.getDescription());
        dto.setReviewDate(review.getReviewDate());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getName());
        dto.setUniversityId(review.getUniversity().getId());
        return dto;
    }
}