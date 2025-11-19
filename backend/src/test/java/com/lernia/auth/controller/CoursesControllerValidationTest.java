package com.lernia.auth.controller;

import com.lernia.auth.entity.enums.CourseType;
import com.lernia.auth.exception.GlobalExceptionHandler;
import com.lernia.auth.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
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

    @Test
    void testGetCoursesByFilter_WithNegativeMaxCost_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("maxCost", "-100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input"))
                .andExpect(jsonPath("$.message").value("maxCost must be a positive value"));
    }

    @Test
    void testGetCoursesByFilter_WithNegativeDuration_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("duration", "-5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input"))
                .andExpect(jsonPath("$.message").value("duration must be a positive value"));
    }

    @Test
    void testGetCoursesByFilter_WithValidMaxCost_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("maxCost", "1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCoursesByFilter_WithValidDuration_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("duration", "12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCoursesByFilter_WithZeroMaxCost_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("maxCost", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCoursesByFilter_WithZeroDuration_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("duration", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCoursesByFilter_WithTooLongName_ReturnsBadRequest() throws Exception {
        String longName = "a".repeat(256);
        mockMvc.perform(get("/api/courses")
                        .param("name", longName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input"))
                .andExpect(jsonPath("$.message").value("name exceeds maximum length of 255"));
    }

    @Test
    void testGetCoursesByFilter_WithTooLongLanguage_ReturnsBadRequest() throws Exception {
        String longLanguage = "a".repeat(256);
        mockMvc.perform(get("/api/courses")
                        .param("languages", longLanguage)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input"))
                .andExpect(jsonPath("$.message").value("language value exceeds maximum length of 255"));
    }

    @Test
    void testGetCoursesByFilter_WithTooLongCountry_ReturnsBadRequest() throws Exception {
        String longCountry = "a".repeat(256);
        mockMvc.perform(get("/api/courses")
                        .param("countries", longCountry)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input"))
                .andExpect(jsonPath("$.message").value("country value exceeds maximum length of 255"));
    }

    @Test
    void testGetCoursesByFilter_WithTooLongAreaOfStudy_ReturnsBadRequest() throws Exception {
        String longArea = "a".repeat(256);
        mockMvc.perform(get("/api/courses")
                        .param("areasOfStudy", longArea)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input"))
                .andExpect(jsonPath("$.message").value("areasOfStudy value exceeds maximum length of 255"));
    }

    @Test
    void testGetCoursesByFilter_WithValidName_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("name", "Computer Science")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCoursesByFilter_WithMaxLengthName_ReturnsOk() throws Exception {
        String maxLengthName = "a".repeat(255);
        mockMvc.perform(get("/api/courses")
                        .param("name", maxLengthName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
