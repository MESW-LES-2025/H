package com.lernia.auth.service;

import com.lernia.auth.repository.UniversityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UniversityServiceTest {

    @InjectMocks
    private UniversityService universityService;

    @Mock
    private UniversityRepository universityRepository;

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
}
