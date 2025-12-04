package com.lernia.auth.repository;

import com.lernia.auth.entity.UserCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCourseRepository extends JpaRepository<UserCourseEntity, Long> {
    List<UserCourseEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
