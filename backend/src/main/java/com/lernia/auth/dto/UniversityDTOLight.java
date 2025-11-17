package com.lernia.auth.dto;

public record UniversityDTOLight(
        Long id,
        String name,
        String description,
        LocationDTO location
) {}
