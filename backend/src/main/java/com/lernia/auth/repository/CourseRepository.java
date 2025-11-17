package com.lernia.auth.repository;

import com.lernia.auth.entity.CourseEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    @EntityGraph(attributePaths = {
            "university",
            "university.location",
            "areaOfStudies"
    })
    @NonNull
    Optional<CourseEntity> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {
            "university",
            "university.location",
            "areaOfStudies"
    })
    List<CourseEntity> findAllByOrderByNameAsc();
}
