package com.lernia.auth.controller;

import com.lernia.auth.entity.enums.CourseType;
import com.lernia.auth.exception.GlobalExceptionHandler;
import com.lernia.auth.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CoursesControllerValidationTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CoursesController coursesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(coursesController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetCoursesByFilter_WithInvalidCourseType_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("courseTypes", "INVALID_TYPE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid parameter value"))
                .andExpect(jsonPath("$.parameter").value("courseTypes"))
                .andExpect(jsonPath("$.message").value(containsString("Invalid value 'INVALID_TYPE' for parameter 'courseTypes'")))
                .andExpect(jsonPath("$.message").value(containsString("BACHELOR")))
                .andExpect(jsonPath("$.message").value(containsString("MASTER")))
                .andExpect(jsonPath("$.message").value(containsString("DOCTORATE")));
    }

    @Test
    void testGetCoursesByFilter_WithValidCourseType_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("courseTypes", "BACHELOR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCoursesByFilter_WithMultipleValidCourseTypes_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("courseTypes", "BACHELOR", "MASTER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCoursesByFilter_WithMixedValidAndInvalidCourseTypes_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("courseTypes", "BACHELOR", "INVALID_TYPE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid parameter value"));
    }
}
