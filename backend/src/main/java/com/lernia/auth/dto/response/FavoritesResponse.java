package com.lernia.auth.dto.response;

import com.lernia.auth.dto.CourseLightDTO;
import com.lernia.auth.dto.UniversityDTOLight;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FavoritesResponse {

    private List<UniversityDTOLight> universities;
    private List<CourseLightDTO> courses;
}
