package com.lernia.auth.repository;

import com.lernia.auth.dto.filter.CourseFilter;
import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.ScholarshipEntity;
import com.lernia.auth.entity.UniversityEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CourseSpecification {

    private CourseSpecification() {}

    public static Specification<CourseEntity> filter(CourseFilter req) {
        return Specification.where(hasName(req.getName()))
                .and(hasTypes(req.getCourseTypes()))
                .and(isRemote(req.getOnlyRemote()))
                .and(maxCourseCost(req.getMaxCost()))
                .and(duration(req.getDuration()))
                .and(hasLanguages(req.getLanguages()))
                .and(hasCountries(req.getCountries()))
                .and(hasAreasOfStudy(req.getAreasOfStudy()))
                .and(hasScholarship(req.getHasScholarship()));
    }

    private static Specification<CourseEntity> hasName(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<CourseEntity> hasTypes(List<String> types) {
        return (root, query, cb) ->
                types == null || types.isEmpty()
                        ? cb.conjunction()
                        : root.get("courseType").in(types);
    }

    private static Specification<CourseEntity> isRemote(Boolean remote) {
        return (root, query, cb) ->
                remote == null ? cb.conjunction() : cb.equal(root.get("isRemote"), remote);
    }

    private static Specification<CourseEntity> maxCourseCost(Integer cost) {
        return (root, query, cb) ->
                cost == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("cost"), cost);
    }

    private static Specification<CourseEntity> duration(Integer duration) {
        return (root, query, cb) ->
                duration == null ? cb.conjunction() : cb.equal(root.get("duration"), duration);
    }

    private static Specification<CourseEntity> hasLanguages(List<String> languages) {
        return (root, query, cb) ->
                languages == null || languages.isEmpty() ? cb.conjunction() : root.get("language").in(languages);
    }

    private static Specification<CourseEntity> hasCountries(List<String> countries) {
        return (root, query, cb) -> {
            if (countries == null || countries.isEmpty()) return cb.conjunction();

            // Join only when filter is active
            Join<CourseEntity, UniversityEntity> uniJoin = root.join("university", JoinType.INNER);
            Join<UniversityEntity, LocationEntity> locJoin = uniJoin.join("location", JoinType.INNER);

            return locJoin.get("country").in(countries);
        };
    }

    private static Specification<CourseEntity> hasAreasOfStudy(List<String> areas) {
        return (root, query, cb) -> {
            if (areas == null || areas.isEmpty()) return cb.conjunction();

            // Many-to-many → join only when needed
            Join<CourseEntity, AreaOfStudyEntity> join =
                    root.join("areasOfStudy", JoinType.INNER);

            return join.get("name").in(areas);
        };
    }

    private static Specification<CourseEntity> hasScholarship(Boolean has) {
        return (root, query, cb) -> {
            if (has == null) return cb.conjunction();

            // Only join when filtering for scholarships
            Join<CourseEntity, UniversityEntity> uniJoin = root.join("university", JoinType.INNER);
            Join<UniversityEntity, ScholarshipEntity> schJoin =
                    uniJoin.join("scholarships", JoinType.LEFT);

            return has
                    ? cb.isNotNull(schJoin.get("id"))
                    : cb.isNull(schJoin.get("id"));
        };
    }
}
