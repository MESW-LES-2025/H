package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipDTOLight {
  private Long id;
  private String name;
  private String description;
  private Integer amount;
  private String courseType;
  private Long universityId;
  private String universityName;
}
