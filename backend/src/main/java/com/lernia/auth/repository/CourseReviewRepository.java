package com.lernia.auth.repository;

import com.lernia.auth.entity.CourseReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseReviewRepository extends JpaRepository<CourseReviewEntity, Long> {
    List<CourseReviewEntity> findByCourseIdOrderByReviewDateDesc(Long courseId);
}