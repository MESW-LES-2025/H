package com.lernia.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScholarshipFilter {
  private String search; // searches on scholarship name, description, and university name
  private String courseType; // filter by course type (BACHELOR, MASTER, DOCTORATE)
  private Integer minAmount; // filter by minimum scholarship amount
  private Integer maxAmount; // filter by maximum scholarship amount
}
