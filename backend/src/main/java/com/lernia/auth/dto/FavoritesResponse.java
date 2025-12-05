package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FavoritesResponse {

    private List<UniversityDTOLight> universities;
    private List<CourseLightDTO> courses;
}
