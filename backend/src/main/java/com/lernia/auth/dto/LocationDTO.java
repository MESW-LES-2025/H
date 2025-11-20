package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationDTO {
    private Long id;
    private String city;
    private String country;
    private Integer costOfLiving;
}
