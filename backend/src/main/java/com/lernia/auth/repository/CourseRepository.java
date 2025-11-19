package com.lernia.auth.repository;

import com.lernia.auth.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    @EntityGraph(attributePaths = {
            "university",
            "university.location",
            "areaOfStudies"
    })
    @Override
    @NonNull
    Optional<CourseEntity> findById(@NonNull Long id);

    @Query(
            value = """
        SELECT DISTINCT c.*
        FROM courses c
        JOIN universities u ON c.university_id = u.id
        JOIN locations l ON u.location_id = l.id
        LEFT JOIN course_area_of_study cas ON c.id = cas.course_id
        LEFT JOIN areas_of_study aos ON cas.area_of_study_id = aos.id
        WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:courseTypes IS NULL OR c.course_type IN (:courseTypes))
            AND (:onlyRemote IS NULL OR c.is_remote = :onlyRemote)
            AND (:costMax IS NULL OR c.cost <= :costMax)
            AND (:duration IS NULL OR c.duration = :duration)
            AND (:languages IS NULL OR c.language IN (:languages))
            AND (:countries IS NULL OR l.country IN (:countries))
            AND (:areasOfStudy IS NULL OR aos.name IN (:areasOfStudy))
    """,
            countQuery = """
        SELECT COUNT(DISTINCT c.id)
        FROM courses c
        JOIN universities u ON c.university_id = u.id
        JOIN locations l ON u.location_id = l.id
        LEFT JOIN course_area_of_study cas ON c.id = cas.course_id
        LEFT JOIN areas_of_study aos ON cas.area_of_study_id = aos.id
        WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:courseTypes IS NULL OR c.course_type IN (:courseTypes))
            AND (:onlyRemote IS NULL OR c.is_remote = :onlyRemote)
            AND (:costMax IS NULL OR c.cost <= :costMax)
            AND (:duration IS NULL OR c.duration = :duration)
            AND (:languages IS NULL OR c.language IN (:languages))
            AND (:countries IS NULL OR l.country IN (:countries))
            AND (:areasOfStudy IS NULL OR aos.name IN (:areasOfStudy))
    """,
            nativeQuery = true
    )
    Page<CourseEntity> findCourses(
            @Param("name") String name,
            @Param("courseTypes") List<String> courseTypes,
            @Param("onlyRemote") Boolean onlyRemote,
            @Param("costMax") Integer costMax,
            @Param("duration") Integer duration,
            @Param("languages") List<String> languages,
            @Param("countries") List<String> countries,
            @Param("areasOfStudy") List<String> areasOfStudy,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c.language FROM CourseEntity c WHERE c.language IS NOT NULL")
    List<String> findDistinctLanguages();


}
