package com.lernia.auth.controller;

import com.lernia.auth.dto.CourseDTO;
import com.lernia.auth.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoursesControllerTest {

    @InjectMocks
    private CoursesController coursesController;

    @Mock
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------------------
    // getAllLanguages
    // ---------------------------------------------------------------------

    @Test
    void testGetAllLanguages_ReturnsListFromService() {
        List<String> langs = List.of("English", "Portuguese", "Spanish");
        when(courseService.getAllLanguages()).thenReturn(langs);

        List<String> result = coursesController.getAllLanguages();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(langs, result);

        verify(courseService, times(1)).getAllLanguages();
    }

    @Test
    void testGetAllLanguages_ReturnsEmptyList() {
        when(courseService.getAllLanguages()).thenReturn(Collections.emptyList());

        List<String> result = coursesController.getAllLanguages();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(courseService, times(1)).getAllLanguages();
    }

    // ---------------------------------------------------------------------
    // getCourseById
    // ---------------------------------------------------------------------

    @Test
    void testGetCourseById_Found_ReturnsOk() {
        Long id = 5L;
        CourseDTO dto = mock(CourseDTO.class);

        when(courseService.getCourseById(id)).thenReturn(Optional.of(dto));

        ResponseEntity<CourseDTO> response = coursesController.getCourseById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertSame(dto, response.getBody());

        verify(courseService, times(1)).getCourseById(id);
    }

    @Test
    void testGetCourseById_NotFound_ReturnsNotFound() {
        Long id = 999L;
        when(courseService.getCourseById(id)).thenReturn(Optional.empty());

        ResponseEntity<CourseDTO> response = coursesController.getCourseById(id);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(courseService, times(1)).getCourseById(id);
    }
}
