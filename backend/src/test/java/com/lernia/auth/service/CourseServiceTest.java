package com.lernia.auth.service;

import com.lernia.auth.dto.AreaOfStudyDTO;
import com.lernia.auth.dto.CourseDTO;
import com.lernia.auth.dto.CourseFilter;
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
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

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
    // getCourses (filter + pageable)
    // -------------------------------------------------------

    @Test
    void testGetCourses_MapsPageOfEntitiesToPageOfDTOs() {
        CourseEntity course = buildFullCourseEntity(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CourseEntity> entityPage =
                new PageImpl<>(List.of(course), pageable, 1);

        CourseFilter filter = new CourseFilter(); // usa defaults

        // usamos nullable(...) e anyList() para casar com a assinatura real do repositório
        when(courseRepository.findCourses(
                nullable(String.class),        // name
                anyList(),                     // courseTypes
                nullable(Boolean.class),       // onlyRemote
                nullable(Integer.class),       // costMax
                nullable(Integer.class),       // duration
                anyList(),                     // languages
                anyList(),                     // countries
                anyList(),                     // areasOfStudy
                any(Pageable.class)            // pageable
        )).thenReturn(entityPage);

        Page<CourseDTO> dtoPage = courseService.getCourses(filter, pageable);

        assertNotNull(dtoPage);
        assertEquals(1, dtoPage.getTotalElements());
        assertEquals(1, dtoPage.getContent().size());

        CourseDTO dto = dtoPage.getContent().get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Software Engineering", dto.getName());
        assertNotNull(dto.getUniversity());
        assertEquals("FEUP", dto.getUniversity().getName());

        verify(courseRepository, times(1)).findCourses(
                nullable(String.class),
                anyList(),
                nullable(Boolean.class),
                nullable(Integer.class),
                nullable(Integer.class),
                anyList(),
                anyList(),
                anyList(),
                any(Pageable.class)
        );
    }

    @Test
    void testGetCourses_EmptyResultPage() {
        Pageable pageable = PageRequest.of(0, 10);
        CourseFilter filter = new CourseFilter();

        when(courseRepository.findCourses(
                nullable(String.class),
                anyList(),
                nullable(Boolean.class),
                nullable(Integer.class),
                nullable(Integer.class),
                anyList(),
                anyList(),
                anyList(),
                any(Pageable.class)
        )).thenReturn(Page.empty(pageable));

        Page<CourseDTO> dtoPage = courseService.getCourses(filter, pageable);

        assertNotNull(dtoPage);
        assertEquals(0, dtoPage.getTotalElements());
        assertTrue(dtoPage.getContent().isEmpty());

        verify(courseRepository, times(1)).findCourses(
                nullable(String.class),
                anyList(),
                nullable(Boolean.class),
                nullable(Integer.class),
                nullable(Integer.class),
                anyList(),
                anyList(),
                anyList(),
                any(Pageable.class)
        );
    }

    @Test
    void testGetCourses_NullFilter_ThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(NullPointerException.class,
                () -> courseService.getCourses(null, pageable));
    }

    @Test
    void testGetCourses_NullPageable_ThrowsException() {
        CourseFilter filter = new CourseFilter();

        assertThrows(NullPointerException.class,
                () -> courseService.getCourses(filter, null));
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
        location.setCost_of_living(850);

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
