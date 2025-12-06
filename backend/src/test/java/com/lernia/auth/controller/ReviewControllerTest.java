package com.lernia.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lernia.auth.dto.ReviewDTO;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.UserRepository;
import com.lernia.auth.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewController reviewController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Principal principal;
    private UserEntity user;
    private ReviewDTO reviewDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();

        principal = mock(Principal.class);
        
        lenient().when(principal.getName()).thenReturn("testuser");

        user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");

        reviewDTO = new ReviewDTO();
        reviewDTO.setRating(5F);
        reviewDTO.setTitle("Great");
        reviewDTO.setDescription("Description");
    }

    // --- GET Reviews Tests ---

    @Test
    void getReviews_ShouldReturnList() throws Exception {
        when(reviewService.getReviewsByUniversity(1L)).thenReturn(List.of(reviewDTO));

        mockMvc.perform(get("/api/reviews/university/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Great"));
    }

    @Test
    void getCourseReviews_ShouldReturnList() throws Exception {
        when(reviewService.getReviewsByCourse(1L)).thenReturn(List.of(reviewDTO));

        mockMvc.perform(get("/api/reviews/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Great"));
    }

    // --- Eligibility Tests ---

    @Test
    void checkEligibility_ShouldReturnTrue_WhenEligible() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.canUserReview(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/api/reviews/eligibility/1").principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkEligibility_ShouldReturnFalse_WhenNotEligible() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.canUserReview(1L, 1L)).thenReturn(false);

        mockMvc.perform(get("/api/reviews/eligibility/1").principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void checkEligibility_ShouldReturnFalse_WhenPrincipalIsNull() throws Exception {
        mockMvc.perform(get("/api/reviews/eligibility/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void checkCourseEligibility_ShouldReturnTrue_WhenEligible() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.canUserReviewCourse(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/api/reviews/course/eligibility/1").principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkCourseEligibility_ShouldReturnFalse_WhenPrincipalIsNull() throws Exception {
        mockMvc.perform(get("/api/reviews/course/eligibility/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void checkCourseEligibility_ShouldReturnFalse_WhenServiceReturnsFalse() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.canUserReviewCourse(1L, 1L)).thenReturn(false);

        mockMvc.perform(get("/api/reviews/course/eligibility/1").principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // --- Add Review Tests ---

    @Test
    void addReview_ShouldReturnOk_WhenAuthenticated() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.addReview(any(ReviewDTO.class))).thenReturn(reviewDTO);

        mockMvc.perform(post("/api/reviews")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Great"));
        
        verify(reviewService).addReview(any(ReviewDTO.class));
    }

    @Test
    void addReview_ShouldReturnUnauthorized_WhenPrincipalIsNull() throws Exception {
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andDo(print()) 
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("You must be logged in to post a review."));
    }

    @Test
    void addReview_ShouldReturnBadRequest_WhenServiceThrows() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.addReview(any(ReviewDTO.class))).thenThrow(new RuntimeException("Validation failed"));

        mockMvc.perform(post("/api/reviews")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void addReview_ShouldReturnBadRequest_WhenUserNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/reviews")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(reviewService, never()).addReview(any());
    }

    @Test
    void addCourseReview_ShouldReturnOk_WhenAuthenticated() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.addCourseReview(any(ReviewDTO.class))).thenReturn(reviewDTO);

        mockMvc.perform(post("/api/reviews/course")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void addCourseReview_ShouldReturnUnauthorized_WhenPrincipalNull() throws Exception {
        mockMvc.perform(post("/api/reviews/course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("You must be logged in to post a review."));
    }

    @Test
    void addCourseReview_ShouldReturnBadRequest_WhenServiceThrows() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.addCourseReview(any(ReviewDTO.class))).thenThrow(new RuntimeException("Course error"));

        mockMvc.perform(post("/api/reviews/course")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Course error"));
    }

    @Test
    void addCourseReview_ShouldReturnBadRequest_WhenUserNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/reviews/course")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(reviewService, never()).addCourseReview(any());
    }

    // --- Update Review Tests ---

    @Test
    void updateReview_ShouldReturnOk_WhenAuthorized() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.updateReview(eq(10L), any(ReviewDTO.class), eq(1L))).thenReturn(reviewDTO);

        mockMvc.perform(put("/api/reviews/10")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Great"));
    }

    @Test
    void updateReview_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Not authorized")).when(reviewService).updateReview(eq(10L), any(ReviewDTO.class), eq(1L));

        mockMvc.perform(put("/api/reviews/10")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Not authorized"));
    }

    @Test
    void updateReview_ShouldReturnBadRequest_WhenUserNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/reviews/15")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(reviewService, never()).updateReview(anyLong(), any(), anyLong());
    }

    @Test
    void updateCourseReview_ShouldReturnOk_WhenAuthorized() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.updateCourseReview(eq(12L), any(ReviewDTO.class), eq(1L))).thenReturn(reviewDTO);

        mockMvc.perform(put("/api/reviews/course/12")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Great"));
    }

    @Test
    void updateCourseReview_ShouldReturnBadRequest_WhenServiceThrows() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewService.updateCourseReview(eq(12L), any(ReviewDTO.class), eq(1L)))
                .thenThrow(new RuntimeException("Not allowed"));

        mockMvc.perform(put("/api/reviews/course/12")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Not allowed"));
    }

    @Test
    void updateCourseReview_ShouldReturnBadRequest_WhenUserNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/reviews/course/18")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(reviewService, never()).updateCourseReview(anyLong(), any(), anyLong());
    }

    @Test
    void updateReview_ShouldReturnUnauthorized_WhenPrincipalIsNull() throws Exception {
        mockMvc.perform(put("/api/reviews/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("You must be logged in."));
    }

    @Test
    void updateCourseReview_ShouldReturnUnauthorized_WhenPrincipalNull() throws Exception {
        mockMvc.perform(put("/api/reviews/course/12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("You must be logged in."));
    }

    // --- Delete Review Tests ---

    @Test
    void deleteReview_ShouldReturnOk_WhenAuthorized() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doNothing().when(reviewService).deleteReview(10L, 1L);

        mockMvc.perform(delete("/api/reviews/10")
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Review deleted successfully"));
    }

    @Test
    void deleteReview_ShouldReturnUnauthorized_WhenPrincipalIsNull() throws Exception {
        mockMvc.perform(delete("/api/reviews/10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("You must be logged in."));
    }

    @Test
    void deleteCourseReview_ShouldReturnOk_WhenAuthorized() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doNothing().when(reviewService).deleteCourseReview(20L, 1L);

        mockMvc.perform(delete("/api/reviews/course/20")
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Review deleted successfully"));
    }

    @Test
    void deleteReview_ShouldReturnBadRequest_WhenServiceThrows() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Cannot delete")).when(reviewService).deleteReview(10L, 1L);

        mockMvc.perform(delete("/api/reviews/10").principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot delete"));
    }

    @Test
    void deleteCourseReview_ShouldReturnUnauthorized_WhenPrincipalNull() throws Exception {
        mockMvc.perform(delete("/api/reviews/course/20"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("You must be logged in."));
    }

    @Test
    void deleteCourseReview_ShouldReturnBadRequest_WhenServiceThrows() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Cannot remove"))
                .when(reviewService).deleteCourseReview(20L, 1L);

        mockMvc.perform(delete("/api/reviews/course/20").principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot remove"));
    }

    @Test
    void deleteReview_ShouldReturnBadRequest_WhenUserNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/reviews/22").principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(reviewService, never()).deleteReview(anyLong(), anyLong());
    }

    @Test
    void deleteCourseReview_ShouldReturnBadRequest_WhenUserNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/reviews/course/25").principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(reviewService, never()).deleteCourseReview(anyLong(), anyLong());
    }

}