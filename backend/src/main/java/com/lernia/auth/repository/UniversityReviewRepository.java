package com.lernia.auth.repository;

import com.lernia.auth.entity.UniversityReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UniversityReviewRepository extends JpaRepository<UniversityReviewEntity, Long> {
    List<UniversityReviewEntity> findByUniversityIdOrderByReviewDateDesc(Long universityId);
}