package com.lernia.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lernia.auth.dto.*;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*@Test
    void testGetAllCoursesMapsNestedEntities() {
        // build location
        LocationEntity location = new LocationEntity();
        location.setId(10L);
        location.setCity("TestCity");
        location.setCountry("TestCountry");

        // build university
        UniversityEntity university = new UniversityEntity();
        university.setId(20L);
        university.setName("Test University");
        university.setDescription("Uni Desc");
        university.setLocation(location);

        // build areas of study
        AreaOfStudyEntity a1 = new AreaOfStudyEntity();
        a1.setName("Math");
        AreaOfStudyEntity a2 = new AreaOfStudyEntity();
        a2.setName("Physics");
        List<AreaOfStudyEntity> areas = new ArrayList<>();
        areas.add(a1);
        areas.add(a2);

        // build course
        CourseEntity course = new CourseEntity();
        course.setId(1L);
        course.setName("Intro to Testing");
        course.setDescription("Course Desc");
        course.setUniversity(university);
        course.setAreaOfStudies(areas);

        when(courseRepository.findAllByOrderByNameAsc()).thenReturn(List.of(course));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<CourseDTO> page = courseService.getCourses(new CourseFilter(), pageable);


        assertNotNull(page);
        List<CourseDTO> dtos = page.getContent();

        assertNotNull(dtos);
        assertEquals(1, dtos.size());

        CourseDTO dto = dtos.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Intro to Testing", dto.getName());
        assertEquals("Course Desc", dto.getDescription());

        UniversityDTOLight udto = dto.getUniversity();
        assertNotNull(udto);
        assertEquals(20L, udto.getId());
        assertEquals("Test University", udto.getName());
        assertEquals("Uni Desc", udto.getDescription());

        LocationDTO ldto = udto.getLocation();
        assertNotNull(ldto);
        assertEquals(10L, ldto.getId());
        assertEquals("TestCity", ldto.getCity());
        assertEquals("TestCountry", ldto.getCountry());

        List<AreaOfStudyDTO> areaNames = dto.getAreasOfStudy();
        assertNotNull(areaNames);
        List<String> names = areaNames.stream()
                .map(AreaOfStudyDTO::getName)
                .toList();

        assertTrue(names.contains("Math"));
        assertTrue(names.contains("Physics"));
    }*/

    @Test
    void testGetCourseByIdFound() {
        UniversityEntity university = new UniversityEntity();
        university.setId(2L);
        university.setName("U2");
        LocationEntity location = new LocationEntity();
        location.setId(3L);
        location.setCity("C");
        location.setCountry("Country");
        university.setLocation(location);

        CourseEntity course = new CourseEntity();
        course.setId(5L);
        course.setName("Specific Course");
        course.setDescription("Desc");
        course.setUniversity(university);
        course.setAreaOfStudies(new ArrayList<>());

        when(courseRepository.findById(5L)).thenReturn(Optional.of(course));

        Optional<CourseDTO> opt = courseService.getCourseById(5L);
        assertTrue(opt.isPresent());

        CourseDTO dto = opt.get();
        assertEquals(5L, dto.getId());
        assertEquals("Specific Course", dto.getName());
        assertEquals("Desc", dto.getDescription());
        assertNotNull(dto.getUniversity());
        assertEquals("U2", dto.getUniversity().getName());
    }

    @Test
    void testGetCourseByIdNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<CourseDTO> opt = courseService.getCourseById(99L);
        assertTrue(opt.isEmpty());
    }
}