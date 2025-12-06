package com.lernia.auth.repository;

import com.lernia.auth.dto.ScholarshipFilter;
import com.lernia.auth.entity.ScholarshipEntity;
import com.lernia.auth.entity.UniversityEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ScholarshipSpecification {

  private ScholarshipSpecification() {
  }

  public static Specification<ScholarshipEntity> filter(ScholarshipFilter req) {
    return Specification.where(searchQuery(req.getSearch()))
        .and(hasCourseType(req.getCourseType()))
        .and(hasMinAmount(req.getMinAmount()))
        .and(hasMaxAmount(req.getMaxAmount()));
  }

  private static Specification<ScholarshipEntity> searchQuery(String search) {
    return (root, query, cb) -> {
      if (search == null || search.isEmpty()) {
        return cb.conjunction();
      }

      String searchPattern = "%" + search.toLowerCase() + "%";
      Join<ScholarshipEntity, UniversityEntity> universityJoin = root.join("university");

      List<Predicate> searchPredicates = new ArrayList<>();
      searchPredicates.add(cb.like(cb.lower(root.get("name")), searchPattern));
      searchPredicates.add(cb.like(cb.lower(root.get("description")), searchPattern));
      searchPredicates.add(cb.like(cb.lower(universityJoin.get("name")), searchPattern));

      return cb.or(searchPredicates.toArray(new Predicate[0]));
    };
  }

  private static Specification<ScholarshipEntity> hasCourseType(String courseType) {
    return (root, query, cb) -> {
      if (courseType == null || courseType.isEmpty()) {
        return cb.conjunction();
      }
      return cb.equal(cb.upper(root.get("courseType")), courseType.toUpperCase());
    };
  }

  private static Specification<ScholarshipEntity> hasMinAmount(Integer minAmount) {
    return (root, query, cb) -> {
      if (minAmount == null) {
        return cb.conjunction();
      }
      return cb.greaterThanOrEqualTo(root.get("amount"), minAmount);
    };
  }

  private static Specification<ScholarshipEntity> hasMaxAmount(Integer maxAmount) {
    return (root, query, cb) -> {
      if (maxAmount == null) {
        return cb.conjunction();
      }
      return cb.lessThanOrEqualTo(root.get("amount"), maxAmount);
    };
  }
}
