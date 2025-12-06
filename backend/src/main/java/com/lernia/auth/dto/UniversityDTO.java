package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UniversityDTO {
  private Long id;
  private String name;
  private String description;
  private String contactInfo;
  private String website;
  private String address;
  private String logo;
  private LocationDTO location;
  private List<CourseLightDTO> courses;
  private List<ScholarshipDTO> scholarships;
}
