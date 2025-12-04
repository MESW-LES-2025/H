package com.lernia.auth.controller;

import com.lernia.auth.dto.AreaOfStudyDTO;
import com.lernia.auth.service.AreaOfStudyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AreaOfStudyControllerTest {

    @InjectMocks
    private AreaOfStudyController controller;

    @Mock
    private AreaOfStudyService areaOfStudyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetAllAreasOfStudy_ReturnsListFromService() {
        // given
        List<AreaOfStudyDTO> mockList = List.of(
                new AreaOfStudyDTO(1L, "Engineering"),
                new AreaOfStudyDTO(2L, "Computer Science")
        );

        when(areaOfStudyService.getAllAreasOfStudy()).thenReturn(mockList);

        // when
        List<AreaOfStudyDTO> response = controller.getAllAreasOfStudy();

        // then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Engineering", response.get(0).getName());
        assertEquals("Computer Science", response.get(1).getName());

        verify(areaOfStudyService, times(1)).getAllAreasOfStudy();
    }

    @Test
    void testGetAllAreasOfStudy_ReturnsEmptyList() {
        // given
        when(areaOfStudyService.getAllAreasOfStudy()).thenReturn(List.of());

        // when
        List<AreaOfStudyDTO> response = controller.getAllAreasOfStudy();

        // then
        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(areaOfStudyService, times(1)).getAllAreasOfStudy();
    }
}
