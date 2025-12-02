package com.lernia.auth.controller;

import com.lernia.auth.dto.CourseDTO;
import com.lernia.auth.dto.CourseFilter;
import com.lernia.auth.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class CoursesControllerTest {

    @InjectMocks
    private CoursesController coursesController;

    @Mock
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCoursesByFilter_AllParamsProvided_DelegatesWithCorrectFilter() {
        String name = "Software";
        List<String> courseTypes = List.of("BACHELOR", "MASTER");
        Boolean onlyRemote = true;
        Integer maxCost = 1000;
        Integer duration = 24;
        List<String> languages = List.of("English", "Portuguese");
        List<String> countries = List.of("Portugal", "Spain");
        List<String> areasOfStudy = List.of("Computer Science", "Engineering");

        Pageable pageable = PageRequest.of(1, 20);

        CourseDTO dtoMock = mock(CourseDTO.class);
        Page<CourseDTO> pageMock = new PageImpl<>(List.of(dtoMock), pageable, 1);

        when(courseService.getCourses(any(CourseFilter.class), eq(pageable)))
                .thenReturn(pageMock);

        ResponseEntity<Page<CourseDTO>> response = coursesController.getCoursesByFilter(
                name,
                courseTypes,
                onlyRemote,
                maxCost,
                duration,
                languages,
                countries,
                areasOfStudy,
                pageable
        );

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertSame(pageMock, response.getBody());

        ArgumentCaptor<CourseFilter> filterCaptor = ArgumentCaptor.forClass(CourseFilter.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(courseService, times(1)).getCourses(filterCaptor.capture(), pageableCaptor.capture());

        CourseFilter passedFilter = filterCaptor.getValue();
        Pageable passedPageable = pageableCaptor.getValue();

        assertEquals(pageable, passedPageable);

        assertEquals("Software", passedFilter.getName());
        assertEquals(courseTypes, passedFilter.getCourseTypes());
        assertEquals(Boolean.TRUE, passedFilter.getOnlyRemote());
        assertEquals(maxCost, passedFilter.getCostMax());
        assertEquals(duration, passedFilter.getDuration());
        assertEquals(languages, passedFilter.getLanguages());
        assertEquals(countries, passedFilter.getCountries());
        assertEquals(areasOfStudy, passedFilter.getAreasOfStudy());
    }

    @Test
    void testGetCoursesByFilter_NullOptionalParams_UsesDefaultsAndEmptyLists() {
        String name = null;
        List<String> courseTypes = null;
        Boolean onlyRemote = null;
        Integer maxCost = null;
        Integer duration = null;
        List<String> languages = null;
        List<String> countries = null;
        List<String> areasOfStudy = null;

        Pageable pageable = PageRequest.of(0, 10);

        Page<CourseDTO> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(courseService.getCourses(any(CourseFilter.class), eq(pageable)))
                .thenReturn(emptyPage);

        ResponseEntity<Page<CourseDTO>> response = coursesController.getCoursesByFilter(
                name,
                courseTypes,
                onlyRemote,
                maxCost,
                duration,
                languages,
                countries,
                areasOfStudy,
                pageable
        );

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertSame(emptyPage, response.getBody());

        ArgumentCaptor<CourseFilter> filterCaptor = ArgumentCaptor.forClass(CourseFilter.class);
        verify(courseService, times(1)).getCourses(filterCaptor.capture(), eq(pageable));

        CourseFilter passedFilter = filterCaptor.getValue();

        assertNull(passedFilter.getName());

        assertNotNull(passedFilter.getCourseTypes());
        assertTrue(passedFilter.getCourseTypes().isEmpty());

        assertNotNull(passedFilter.getLanguages());
        assertTrue(passedFilter.getLanguages().isEmpty());

        assertNotNull(passedFilter.getCountries());
        assertTrue(passedFilter.getCountries().isEmpty());

        assertNotNull(passedFilter.getAreasOfStudy());
        assertTrue(passedFilter.getAreasOfStudy().isEmpty());

        assertEquals(Boolean.FALSE, passedFilter.getOnlyRemote());

        assertNull(passedFilter.getCostMax());
        assertNull(passedFilter.getDuration());
    }

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
