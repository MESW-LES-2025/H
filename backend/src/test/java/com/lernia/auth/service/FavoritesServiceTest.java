package com.lernia.auth.service;

import com.lernia.auth.dto.CourseLightDTO;
import com.lernia.auth.dto.FavoritesResponse;
import com.lernia.auth.dto.LocationDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.repository.CourseRepository;
import com.lernia.auth.repository.UniversityRepository;
import com.lernia.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoritesServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UniversityRepository universityRepository;

    @InjectMocks
    private FavoritesService favoritesService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserEntity();
        user.setId(1L);
        user.setBookmarkedCourses(new ArrayList<>());
        user.setBookmarkedUniversities(new ArrayList<>());
    }

    @Test
    void addCourseToFavorites_ShouldStoreAndSave() {
        CourseEntity course = buildCourse(10L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

        favoritesService.addCourseToFavorites(1L, 10L);

        assertTrue(user.getBookmarkedCourses().contains(course));
        verify(userRepository).save(user);
    }

    @Test
    void addCourseToFavorites_ShouldNotDuplicate() {
        CourseEntity course = buildCourse(10L);
        user.getBookmarkedCourses().add(course);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

        favoritesService.addCourseToFavorites(1L, 10L);

        assertEquals(1, user.getBookmarkedCourses().size());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addCourseToFavorites_CourseMissing_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> favoritesService.addCourseToFavorites(1L, 10L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void addCourseToFavorites_UserMissing_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> favoritesService.addCourseToFavorites(1L, 10L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void removeCourseFromFavorites_RemovesAndSaves() {
        CourseEntity course = buildCourse(20L);
        user.getBookmarkedCourses().add(course);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        favoritesService.removeCourseFromFavorites(1L, 20L);

        assertTrue(user.getBookmarkedCourses().isEmpty());
        verify(userRepository).save(user);
    }

    @Test
    void removeCourseFromFavorites_UserMissing_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> favoritesService.removeCourseFromFavorites(1L, 20L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void addUniversityToFavorites_ShouldStore() {
        UniversityEntity university = buildUniversity(5L, buildLocation(100L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(universityRepository.findById(5L)).thenReturn(Optional.of(university));

        favoritesService.addUniversityToFavorites(1L, 5L);

        assertTrue(user.getBookmarkedUniversities().contains(university));
        verify(userRepository).save(user);
    }

    @Test
    void addUniversityToFavorites_ShouldNotDuplicate() {
        UniversityEntity university = buildUniversity(5L, null);
        user.getBookmarkedUniversities().add(university);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(universityRepository.findById(5L)).thenReturn(Optional.of(university));

        favoritesService.addUniversityToFavorites(1L, 5L);

        verify(userRepository, never()).save(any());
    }

    @Test
    void addUniversityToFavorites_UniversityMissing_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(universityRepository.findById(5L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> favoritesService.addUniversityToFavorites(1L, 5L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void removeUniversityFromFavorites_RemovesAndSaves() {
        UniversityEntity university = buildUniversity(6L, null);
        user.getBookmarkedUniversities().add(university);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        favoritesService.removeUniversityFromFavorites(1L, 6L);

        assertTrue(user.getBookmarkedUniversities().isEmpty());
        verify(userRepository).save(user);
    }

    @Test
    void getFavoritesForUser_ReturnsSortedDtos() {
        UniversityEntity uni1 = buildUniversity(2L, buildLocation(200L));
        UniversityEntity uni2 = buildUniversity(3L, null);
        CourseEntity course1 = buildCourse(11L);
        CourseEntity course2 = buildCourse(12L);

        user.getBookmarkedUniversities().addAll(List.of(uni1, uni2));
        user.getBookmarkedCourses().addAll(List.of(course1, course2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        FavoritesResponse response = favoritesService.getFavoritesForUser(1L);

        assertNotNull(response);
        assertEquals(List.of(uni2.getId(), uni1.getId()),
                response.getUniversities().stream().map(UniversityDTOLight::getId).toList());
        assertNull(response.getUniversities().getFirst().getLocation());
        LocationDTO mappedLocation = response.getUniversities().get(1).getLocation();
        assertNotNull(mappedLocation);
        assertEquals(200L, mappedLocation.getId());
        assertEquals(List.of(course2.getId(), course1.getId()),
                response.getCourses().stream().map(CourseLightDTO::getId).toList());
    }

    @Test
    void getFavoritesForUser_UserMissing_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> favoritesService.getFavoritesForUser(1L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    private CourseEntity buildCourse(Long id) {
        CourseEntity course = new CourseEntity();
        course.setId(id);
        course.setName("Course " + id);
        course.setCourseType("Type");
        return course;
    }

    private UniversityEntity buildUniversity(Long id, LocationEntity location) {
        UniversityEntity university = new UniversityEntity();
        university.setId(id);
        university.setName("University " + id);
        university.setDescription("Desc");
        university.setLocation(location);
        return university;
    }

    private LocationEntity buildLocation(Long id) {
        LocationEntity location = new LocationEntity();
        location.setId(id);
        location.setCity("City");
        location.setCountry("Country");
        location.setCostOfLiving(1000);
        return location;
    }
}