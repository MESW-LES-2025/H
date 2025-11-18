package com.lernia.auth.repository;

import com.lernia.auth.entity.CourseEntity;
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
    List<CourseEntity> findAllByOrderByNameAsc();

    @Query(
            value = """
    SELECT DISTINCT c.*
    FROM courses c
    JOIN universities u ON c.university_id = u.id
    JOIN locations l ON u.location_id = l.id
    LEFT JOIN course_area_of_study cas ON c.id = cas.course_id
    LEFT JOIN areas_of_study aos ON cas.area_of_study_id = aos.id
    WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:courseType IS NULL OR c.course_type = CAST(:courseType AS course_type))
      AND (:isRemote IS NULL OR c.is_remote = :isRemote)
      AND (:costMax IS NULL OR c.cost <= :costMax)
      AND (:duration IS NULL OR c.duration = :duration)
      AND (:language IS NULL OR c.language = :language)
      AND (:country IS NULL OR l.country = :country)
      AND (:costOfLivingMax IS NULL OR l.cost_of_living <= :costOfLivingMax)
      AND (:areaOfStudy IS NULL OR aos.id = ANY(string_to_array(:areaOfStudy, ',')::bigint[]))
    """,
            nativeQuery = true
    )
    List<CourseEntity> findCoursesByFilters(
            @Param("name") String name,
            @Param("courseType") String courseType,
            @Param("isRemote") Boolean isRemote,
            @Param("costMax") Integer costMax,
            @Param("duration") String duration,
            @Param("language") String language,
            @Param("country") String country,
            @Param("costOfLivingMax") Integer costOfLivingMax,
            @Param("areaOfStudy") String areaOfStudy
    );

}
