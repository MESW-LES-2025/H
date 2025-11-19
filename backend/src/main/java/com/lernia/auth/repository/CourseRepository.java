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

    @EntityGraph(attributePaths = {
            "university",
            "university.location",
            "areaOfStudies"
    })
    @Query(
            value = """
        SELECT DISTINCT c.*
        FROM courses c
        JOIN universities u ON c.university_id = u.id
        JOIN locations l ON u.location_id = l.id
        LEFT JOIN course_area_of_study cas ON c.id = cas.course_id
        LEFT JOIN areas_of_study aos ON cas.area_of_study_id = aos.id
        WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:courseTypes IS NULL OR c.course_type = ANY(string_to_array(:courseTypes, ',')::course_type[]))
          AND (:onlyRemote IS NULL OR c.is_remote = :onlyRemote)
          AND (:costMax IS NULL OR c.cost <= :costMax)
          AND (:duration IS NULL OR c.duration = :duration)
          AND (:languages IS NULL OR c.language = ANY(string_to_array(:languages, ',')::text[]))
          AND (:countries IS NULL OR l.country = ANY(string_to_array(:countries, ',')::text[]))
          AND (:areasOfStudy IS NULL OR aos.name = ANY(string_to_array(:areasOfStudy, ',')::text[]))
    """,
            countQuery = """
        SELECT COUNT(DISTINCT c.id)
        FROM courses c
        JOIN universities u ON c.university_id = u.id
        JOIN locations l ON u.location_id = l.id
        LEFT JOIN course_area_of_study cas ON c.id = cas.course_id
        LEFT JOIN areas_of_study aos ON cas.area_of_study_id = aos.id
        WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:courseTypes IS NULL OR c.course_type = ANY(string_to_array(:courseTypes, ',')::course_type[]))
          AND (:onlyRemote IS NULL OR c.is_remote = :onlyRemote)
          AND (:costMax IS NULL OR c.cost <= :costMax)
          AND (:duration IS NULL OR c.duration = :duration)
          AND (:languages IS NULL OR c.language = ANY(string_to_array(:languages, ',')::text[]))
          AND (:countries IS NULL OR l.country = ANY(string_to_array(:countries, ',')::text[]))
          AND (:areasOfStudy IS NULL OR aos.name = ANY(string_to_array(:areasOfStudy, ',')::text[]))
    """,
            nativeQuery = true
    )
    Page<CourseEntity> findCourses(
            @Param("name") String name,
            @Param("courseTypes") String courseTypes,
            @Param("onlyRemote") Boolean onlyRemote,
            @Param("costMax") Integer costMax,
            @Param("duration") Integer duration,
            @Param("languages") String languages,
            @Param("countries") String countries,
            @Param("areasOfStudy") String areasOfStudy,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c.language FROM CourseEntity c WHERE c.language IS NOT NULL")
    List<String> findDistinctLanguages();


}
