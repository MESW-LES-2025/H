package com.lernia.auth.repository;

import com.lernia.auth.entity.UserCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCourseRepository extends JpaRepository<UserCourseEntity, Long> {
    // Checks if a record exists linking the user to a course at the specific university
    // This assumes CourseEntity has a 'university' field.
    boolean existsByUserIdAndCourse_UniversityId(Long userId, Long universityId);
}