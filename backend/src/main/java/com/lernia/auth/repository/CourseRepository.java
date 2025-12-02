package com.lernia.auth.repository;

import com.lernia.auth.entity.CourseEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<CourseEntity, Long>, JpaSpecificationExecutor<CourseEntity> {

    @EntityGraph(attributePaths = {
            "university",
            "university.location",
            "areaOfStudies"
    })
    @Override
    @NonNull
    Optional<CourseEntity> findById(@NonNull Long id);

    @Query("SELECT DISTINCT c.language FROM CourseEntity c WHERE c.language IS NOT NULL")
    List<String> findDistinctLanguages();


}
