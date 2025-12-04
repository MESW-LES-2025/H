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

    // --- University Review Tests ---

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

    // --- Course Review Tests ---

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
    void deleteCourseReview_ShouldReturnOk_WhenAuthorized() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doNothing().when(reviewService).deleteCourseReview(20L, 1L);

        mockMvc.perform(delete("/api/reviews/course/20")
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Review deleted successfully"));
    }
}