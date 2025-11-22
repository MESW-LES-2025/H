package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UniversityDTOLight {
    private Long id;
    private String name;
    private String description;
    private LocationDTO location;
}
