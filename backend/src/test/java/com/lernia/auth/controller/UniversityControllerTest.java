package com.lernia.auth.controller;

import com.lernia.auth.dto.UniversityDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import com.lernia.auth.dto.UniversityFilter;
import com.lernia.auth.service.UniversityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UniversityControllerTest {

    @InjectMocks
    private UniversityController universityController;

    @Mock
    private UniversityService universityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCountries_ReturnsListFromService() {
        List<String> countries = List.of("Portugal", "Spain", "Germany");
        when(universityService.getAllCountries()).thenReturn(countries);

        List<String> result = universityController.getAllCountries();

        assertNotNull(result);
        assertEquals(countries, result);
        verify(universityService, times(1)).getAllCountries();
    }

    @Test
    void testGetAllCountries_ReturnsEmptyList() {
        when(universityService.getAllCountries()).thenReturn(Collections.emptyList());

        List<String> result = universityController.getAllCountries();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(universityService, times(1)).getAllCountries();
    }

    @Test
    void testGetUniversitiesByFilter_ReturnsOkWithPage() {
        UniversityFilter filter = mock(UniversityFilter.class);
        Pageable pageable = PageRequest.of(0, 10);
        UniversityDTOLight dto = new UniversityDTOLight(1L, "Uni", "Desc", null);
        Page<UniversityDTOLight> page = new PageImpl<>(List.of(dto), pageable, 1);

        when(universityService.getUniversitiesByFilter(filter, pageable)).thenReturn(page);

        ResponseEntity<Page<UniversityDTOLight>> response = universityController.getUniversitiesByFilter(filter,
                pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertSame(page, response.getBody());
        verify(universityService, times(1)).getUniversitiesByFilter(filter, pageable);
    }

    @Test
    void testGetUniversityById_FoundReturnsOk() {
        Long id = 5L;
        UniversityDTO dto = new UniversityDTO(id, "Uni 5", "Desc", "Contact", "site", "addr", "logo", null, List.of(),
                List.of());
        when(universityService.getUniversityDetailsById(id)).thenReturn(dto);

        ResponseEntity<UniversityDTO> response = universityController.getUniversityById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(dto, response.getBody());
        verify(universityService, times(1)).getUniversityDetailsById(id);
    }

    @Test
    void testGetUniversityById_NotFoundReturns404() {
        Long id = 7L;
        when(universityService.getUniversityDetailsById(id)).thenReturn(null);

        ResponseEntity<UniversityDTO> response = universityController.getUniversityById(id);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(universityService, times(1)).getUniversityDetailsById(id);
    }
}
