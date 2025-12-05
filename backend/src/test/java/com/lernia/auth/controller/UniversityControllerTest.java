package com.lernia.auth.controller;

import com.lernia.auth.service.UniversityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
}
