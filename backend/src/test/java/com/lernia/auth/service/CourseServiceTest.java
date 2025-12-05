package com.lernia.auth.service;

import com.lernia.auth.dto.AreaOfStudyDTO;
import com.lernia.auth.dto.CourseDTO;
import com.lernia.auth.dto.LocationDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------
    // getAllLanguages
    // -------------------------------------------------------

    @Test
    void testGetAllLanguages_ReturnsLanguagesFromRepository() {
        List<String> langs = List.of("English", "Portuguese", "Spanish");

        when(courseRepository.findDistinctLanguages()).thenReturn(langs);

        List<String> result = courseService.getAllLanguages();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(langs, result);

        verify(courseRepository, times(1)).findDistinctLanguages();
    }

    @Test
    void testGetAllLanguages_EmptyList() {
        when(courseRepository.findDistinctLanguages()).thenReturn(List.of());

        List<String> result = courseService.getAllLanguages();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(courseRepository, times(1)).findDistinctLanguages();
    }

    // -------------------------------------------------------
    // getCourseById
    // -------------------------------------------------------

    @Test
    void testGetCourseById_Found_MapsToDTO() {
        CourseEntity course = buildFullCourseEntity(5L);

        when(courseRepository.findById(5L)).thenReturn(Optional.of(course));

        Optional<CourseDTO> opt = courseService.getCourseById(5L);

        assertTrue(opt.isPresent(), "Expected Optional to be present");

        CourseDTO dto = opt.get();

        assertEquals(5L, dto.getId());
        assertEquals("Software Engineering", dto.getName());
        assertEquals("SE description", dto.getDescription());

        UniversityDTOLight uDto = dto.getUniversity();
        assertNotNull(uDto);
        assertEquals(20L, uDto.getId());
        assertEquals("FEUP", uDto.getName());
        assertEquals("Engineering Faculty", uDto.getDescription());

        LocationDTO lDto = uDto.getLocation();
        assertNotNull(lDto);
        assertEquals(10L, lDto.getId());
        assertEquals("Porto", lDto.getCity());
        assertEquals("Portugal", lDto.getCountry());
        assertEquals(850, lDto.getCostOfLiving());

        List<AreaOfStudyDTO> areas = dto.getAreasOfStudy();
        assertNotNull(areas);
        assertEquals(2, areas.size());
    }

    @Test
    void testGetCourseById_NotFound_ReturnsEmpty() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<CourseDTO> opt = courseService.getCourseById(999L);

        assertTrue(opt.isEmpty(), "Expected Optional to be empty when course is not found");

        verify(courseRepository, times(1)).findById(999L);
    }

    @Test
    void testGetCourseById_MapsEmptyAreasOfStudyList() {
        CourseEntity course = buildFullCourseEntity(7L);
        course.setAreaOfStudies(new ArrayList<>());

        when(courseRepository.findById(7L)).thenReturn(Optional.of(course));

        Optional<CourseDTO> opt = courseService.getCourseById(7L);

        assertTrue(opt.isPresent());
        CourseDTO dto = opt.get();

        assertNotNull(dto.getAreasOfStudy(), "Areas list should not be null");
        assertTrue(dto.getAreasOfStudy().isEmpty(), "Areas list should be empty");

        verify(courseRepository, times(1)).findById(7L);
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private CourseEntity buildFullCourseEntity(Long id) {
        // location
        LocationEntity location = new LocationEntity();
        location.setId(10L);
        location.setCity("Porto");
        location.setCountry("Portugal");
        location.setCostOfLiving(850);

        // university
        UniversityEntity university = new UniversityEntity();
        university.setId(20L);
        university.setName("FEUP");
        university.setDescription("Engineering Faculty");
        university.setLocation(location);

        // areas of study
        AreaOfStudyEntity a1 = new AreaOfStudyEntity();
        a1.setId(100L);
        a1.setName("Computer Science");

        AreaOfStudyEntity a2 = new AreaOfStudyEntity();
        a2.setId(101L);
        a2.setName("Software Engineering");

        List<AreaOfStudyEntity> areas = new ArrayList<>();
        areas.add(a1);
        areas.add(a2);

        // course
        CourseEntity course = new CourseEntity();
        course.setId(id);
        course.setName("Software Engineering");
        course.setDescription("SE description");
        course.setUniversity(university);
        course.setAreaOfStudies(areas);

        return course;
    }
}
