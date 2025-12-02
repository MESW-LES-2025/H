package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
public class UniversityDTOLight {
    private Long id;
    private String name;
    private String description;
    private String location; 
    
    private String logo;
    private String bannerImage;
    private Integer studentCount;
    private Integer foundedYear;
    private String contactInfo;
    private String website;
    
    private List<Object> courses; 
}
