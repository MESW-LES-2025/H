package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseLightDTO {
  private Long id;
  private String name;
  private String courseType;
  private String universityName;
}
