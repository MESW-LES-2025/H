package com.lernia.auth.controller;

import com.lernia.auth.dto.CourseLightDTO;
import com.lernia.auth.dto.FavoritesResponse;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.service.FavoritesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoritesControllerTest {

    @InjectMocks
    private FavoritesController favoritesController;

    @Mock
    private FavoritesService favoritesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getOwnFavorites_ReturnsOkResponse() {
        FavoritesResponse response = new FavoritesResponse(
                List.of(new UniversityDTOLight(1L, "Uni", "Desc", null)),
                List.of(new CourseLightDTO(5L, "Course", "Type", "UniName"))
        );
        when(favoritesService.getFavoritesForUser(10L)).thenReturn(response);

        ResponseEntity<FavoritesResponse> entity = favoritesController.getOwnFavorites(10L);

        assertNotNull(entity);
        assertEquals(200, entity.getStatusCodeValue());
        assertSame(response, entity.getBody());
        verify(favoritesService).getFavoritesForUser(10L);
    }

    @Test
    void addUniversityToFavorites_ReturnsNoContent() {
        ResponseEntity<Void> entity = favoritesController.addUniversityToFavorites(1L, 2L);

        assertEquals(204, entity.getStatusCodeValue());
        assertNull(entity.getBody());
        verify(favoritesService).addUniversityToFavorites(1L, 2L);
    }

    @Test
    void removeUniversityFromFavorites_ReturnsNoContent() {
        ResponseEntity<Void> entity = favoritesController.removeUniversityFromFavorites(1L, 2L);

        assertEquals(204, entity.getStatusCodeValue());
        verify(favoritesService).removeUniversityFromFavorites(1L, 2L);
    }

    @Test
    void addCourseToFavorites_ReturnsNoContent() {
        ResponseEntity<Void> entity = favoritesController.addCourseToFavorites(1L, 3L);

        assertEquals(204, entity.getStatusCodeValue());
        verify(favoritesService).addCourseToFavorites(1L, 3L);
    }

    @Test
    void removeCourseFromFavorites_ReturnsNoContent() {
        ResponseEntity<Void> entity = favoritesController.removeCourseFromFavorites(4L, 6L);

        assertEquals(204, entity.getStatusCodeValue());
        verify(favoritesService).removeCourseFromFavorites(4L, 6L);
    }
}