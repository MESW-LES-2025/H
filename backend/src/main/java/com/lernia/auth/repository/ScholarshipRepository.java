package com.lernia.auth.repository;

import com.lernia.auth.entity.ScholarshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScholarshipRepository extends JpaRepository<ScholarshipEntity, Long> {
  List<ScholarshipEntity> findByUniversityId(Long universityId);
}
