package com.lernia.auth.service;

import com.lernia.auth.dto.LocationDTO;
import com.lernia.auth.dto.UniversityDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.dto.filter.UniversityFilter;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.mapper.UniversityMapper;
import com.lernia.auth.repository.CourseRepository;
import com.lernia.auth.repository.ScholarshipRepository;
import com.lernia.auth.repository.UniversityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UniversityServiceTest {

    public static final String CITY = "City";
    public static final String COUNTRY = "Country";
    public static final int COST_OF_LIVING = 1200;
    public static final String UNIVERSITY = "University ";
    public static final String SAMPLE_DESCRIPTION = "Sample description";
    @InjectMocks
    private UniversityService universityService;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ScholarshipRepository scholarshipRepository;

    @Mock
    private UniversityMapper universityMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCountries_ReturnsList() {
        List<String> countries = List.of("Portugal", "Spain", "Germany");
        when(universityRepository.findDistinctCountries()).thenReturn(countries);

        List<String> result = universityService.getAllCountries();

        assertNotNull(result);
        assertEquals(countries, result);
        verify(universityRepository, times(1)).findDistinctCountries();
    }

    @Test
    void testGetAllCountries_EmptyList() {
        when(universityRepository.findDistinctCountries()).thenReturn(Collections.emptyList());

        List<String> result = universityService.getAllCountries();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(universityRepository, times(1)).findDistinctCountries();
    }

    @Test
    void testGetUniversityById_ReturnsPopulatedDto() {
        Long universityId = 10L;
        String universityName = "University 10";
        LocationEntity location = createLocationEntity(1L);
        UniversityEntity entity = createUniversityEntity(universityId, location);
        when(universityRepository.findById(universityId)).thenReturn(java.util.Optional.of(entity));
        when(universityMapper.toDTOLight(entity)).thenReturn(createUniversityDTOLight(universityId));
        var result = universityService.getUniversityById(universityId);

        assertNotNull(result);
        assertEquals(universityId, result.getId());
        assertEquals(universityName, result.getName());
        assertEquals("Sample description", result.getDescription());
        assertNotNull(result.getLocation());
        assertEquals("City", result.getLocation().getCity());
        assertEquals(Integer.valueOf(5000), result.getStudentCount());
        assertEquals(Integer.valueOf(1990), result.getFoundedYear());
        assertNotNull(result.getCourses());
        assertTrue(result.getCourses().isEmpty());
        verify(universityRepository, times(1)).findById(universityId);
    }

    @Test
    void testGetUniversityById_WithoutLocationSetsNull() {
        Long universityId = 11L;
        UniversityDTOLight universityDTOLight = createUniversityDTOLight(universityId);
        universityDTOLight.setLocation(null);
        UniversityEntity entity = createUniversityEntity(universityId, null);
        when(universityRepository.findById(universityId)).thenReturn(java.util.Optional.of(entity));
        when(universityMapper.toDTOLight(entity)).thenReturn(universityDTOLight);

        var result = universityService.getUniversityById(universityId);

        assertNotNull(result);
        assertNull(result.getLocation());
    }

    @Test
    void testGetUniversityById_NotFoundThrows() {
        when(universityRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> universityService.getUniversityById(99L));
        verify(universityRepository, times(1)).findById(99L);
    }

    @Test
    void testGetUniversitiesByFilter_ReturnsMappedPage() {
        UniversityFilter filter = mock(UniversityFilter.class);
        when(filter.getName()).thenReturn("Tech");
        when(filter.getCountries()).thenReturn(List.of("Portugal"));
        when(filter.getMaxCostOfLiving()).thenReturn(1500);
        when(filter.getHasScholarship()).thenReturn(true);

        Pageable pageable = PageRequest.of(0, 5);
        UniversityEntity entity = createUniversityEntity(20L, createLocationEntity(2L));
        Page<UniversityEntity> page = new PageImpl<>(List.of(entity));

        when(universityRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<com.lernia.auth.dto.UniversityDTOLight> result = universityService.getUniversitiesByFilter(filter, pageable);

        assertEquals(1, result.getTotalElements());
        var dto = result.getContent().getFirst();
        assertEquals(20L, dto.getId());
        assertEquals("University 20", dto.getName());
        assertNotNull(dto.getLocation());
        assertEquals(CITY, dto.getLocation().getCity());
        verify(universityRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testGetUniversitiesByFilter_EntityWithoutLocationMapsToNull() {
        UniversityFilter filter = mock(UniversityFilter.class);
        Pageable pageable = PageRequest.of(0, 3);
        UniversityEntity entity = createUniversityEntity(21L, null);
        Page<UniversityEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(universityRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<com.lernia.auth.dto.UniversityDTOLight> result = universityService.getUniversitiesByFilter(filter, pageable);

        assertEquals(1, result.getTotalElements());
        var dto = result.getContent().getFirst();
        assertEquals(21L, dto.getId());
        assertNull(dto.getLocation(), "Location should be null when entity has no location");
        verify(universityRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testGetUniversityDetailsById_ReturnsDtoWithFilteredCourses() {
        LocationEntity location = createLocationEntity(3L);
        UniversityEntity entity = createUniversityEntity(30L, location);
        when(universityRepository.findById(30L)).thenReturn(java.util.Optional.of(entity));

        CourseEntity matchingCourse = createCourseEntity(1L, "Course A", "Bachelor", entity);
        CourseEntity otherCourse = createCourseEntity(2L, "Course B", "Master", createUniversityEntity(31L, null));
        when(courseRepository.findAll()).thenReturn(List.of(matchingCourse, otherCourse));
        when(scholarshipRepository.findByUniversityId(30L)).thenReturn(Collections.emptyList());

        UniversityDTO result = universityService.getUniversityDetailsById(30L);

        assertNotNull(result);
        assertEquals(30L, result.getId());
        assertEquals("University 30", result.getName());
        assertNotNull(result.getLocation());
        assertEquals(CITY, result.getLocation().getCity());
        assertEquals(1, result.getCourses().size());
        assertEquals("Course A", result.getCourses().getFirst().getName());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testGetUniversityDetailsById_LocationNullHandled() {
        UniversityEntity entity = createUniversityEntity(31L, null);
        when(universityRepository.findById(31L)).thenReturn(java.util.Optional.of(entity));

        CourseEntity course = createCourseEntity(10L, "Course X", "Bachelor", entity);
        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(scholarshipRepository.findByUniversityId(31L)).thenReturn(Collections.emptyList());

        UniversityDTO result = universityService.getUniversityDetailsById(31L);

        assertNotNull(result);
        assertNull(result.getLocation(), "DTO location should be null when entity location is null");
        assertEquals(1, result.getCourses().size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testGetUniversityDetailsById_NoMatchingCoursesReturnsEmptyList() {
        LocationEntity location = createLocationEntity(4L);
        UniversityEntity entity = createUniversityEntity(41L, location);
        when(universityRepository.findById(41L)).thenReturn(java.util.Optional.of(entity));

        CourseEntity otherCourse = createCourseEntity(11L, "Course Y", "Master", createUniversityEntity(42L, location));
        when(courseRepository.findAll()).thenReturn(List.of(otherCourse));
        when(scholarshipRepository.findByUniversityId(41L)).thenReturn(Collections.emptyList());

        UniversityDTO result = universityService.getUniversityDetailsById(41L);

        assertNotNull(result);
        assertTrue(result.getCourses().isEmpty(), "Courses list should be empty when no course matches the university");
        verify(courseRepository, times(1)).findAll();
    }

    private UniversityEntity createUniversityEntity(Long id, LocationEntity location) {
        UniversityEntity entity = new UniversityEntity();
        entity.setId(id);
        entity.setName(UNIVERSITY + id);
        entity.setDescription(SAMPLE_DESCRIPTION);
        entity.setContactInfo("contact@university.com");
        entity.setWebsite("http://university.com");
        entity.setAddress("123 Main St");
        entity.setLogo("logo.png");
        entity.setLocation(location);
        entity.setScholarships(new ArrayList<>());
        return entity;
    }

    private LocationEntity createLocationEntity(Long id) {
        LocationEntity location = new LocationEntity();
        location.setId(id);
        location.setCity(CITY);
        location.setCountry(COUNTRY);
        location.setCostOfLiving(COST_OF_LIVING);
        return location;
    }

    private CourseEntity createCourseEntity(Long id, String name, String type, UniversityEntity university) {
        CourseEntity course = new CourseEntity();
        course.setId(id);
        course.setName(name);
        course.setCourseType(type);
        course.setUniversity(university);
        return course;
    }

    private UniversityDTOLight createUniversityDTOLight(Long id) {
        UniversityDTOLight universityDTOLight = new UniversityDTOLight();
        universityDTOLight.setId(id);
        universityDTOLight.setName(UNIVERSITY + id);
        universityDTOLight.setDescription(SAMPLE_DESCRIPTION);
        universityDTOLight.setLocation(buildLocationDTO());
        universityDTOLight.setStudentCount(5000);
        universityDTOLight.setFoundedYear(1990);
        universityDTOLight.setCourses(new ArrayList<>());
        return universityDTOLight;
    }

    private LocationDTO buildLocationDTO() {
        return new LocationDTO(1L, CITY, COUNTRY, COST_OF_LIVING);
    }
}
