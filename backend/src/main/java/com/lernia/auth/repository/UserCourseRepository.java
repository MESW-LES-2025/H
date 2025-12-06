package com.lernia.auth.repository;

import com.lernia.auth.entity.UserCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface UserCourseRepository extends JpaRepository<UserCourseEntity, Long> {
    boolean existsByUserIdAndCourse_UniversityId(Long userId, Long universityId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    List<UserCourseEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
