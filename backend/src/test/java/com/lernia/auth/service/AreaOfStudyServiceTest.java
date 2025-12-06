package com.lernia.auth.service;

import com.lernia.auth.dto.AreaOfStudyDTO;
import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.repository.AreaOfStudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AreaOfStudyServiceTest {

    @InjectMocks
    private AreaOfStudyService areaOfStudyService;

    @Mock
    private AreaOfStudyRepository areaOfStudyRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAreasOfStudy_ReturnsMappedDTOs() {
        AreaOfStudyEntity e1 = new AreaOfStudyEntity();
        e1.setId(1L);
        e1.setName("Computer Science");

        AreaOfStudyEntity e2 = new AreaOfStudyEntity();
        e2.setId(2L);
        e2.setName("Engineering");

        when(areaOfStudyRepository.findAll()).thenReturn(List.of(e1, e2));

        List<AreaOfStudyDTO> result = areaOfStudyService.getAllAreasOfStudy();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Computer Science", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Engineering", result.get(1).getName());

        verify(areaOfStudyRepository, times(1)).findAll();
    }

    @Test
    void testGetAllAreasOfStudy_EmptyList() {
        when(areaOfStudyRepository.findAll()).thenReturn(List.of());

        List<AreaOfStudyDTO> result = areaOfStudyService.getAllAreasOfStudy();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(areaOfStudyRepository).findAll();
    }

    @Test
    void testGetAllAreasOfStudy_EntityWithNullFields() {
        AreaOfStudyEntity e1 = new AreaOfStudyEntity();
        e1.setId(null);
        e1.setName(null);

        when(areaOfStudyRepository.findAll()).thenReturn(List.of(e1));

        List<AreaOfStudyDTO> result = areaOfStudyService.getAllAreasOfStudy();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getId());
        assertNull(result.get(0).getName());

        verify(areaOfStudyRepository).findAll();
    }
}
