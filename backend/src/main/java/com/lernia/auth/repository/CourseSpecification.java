package com.lernia.auth.repository;

import com.lernia.auth.dto.CourseFilter;
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
                .and(maxCost(req.getCostMax()))
                .and(duration(req.getDuration()))
                .and(hasLanguages(req.getLanguages()))
                .and(hasCountries(req.getCountries()))
                .and(hasAreasOfStudy(req.getAreasOfStudy()))
                .and(hasScholarship(req.getHasScholarship()));
    }

    private static Specification<CourseEntity> hasName(String name) {
        return (root, query, cb) ->
                name == null ? cb.conjunction() : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<CourseEntity> hasTypes(List<String> types) {
        return (root, query, cb) ->
                types == null || types.isEmpty() ? cb.conjunction() : root.get("courseType").in(types);
    }

    private static Specification<CourseEntity> isRemote(Boolean remote) {
        return (root, query, cb) ->
                remote == null ? cb.conjunction() : cb.equal(root.get("isRemote"), remote);
    }

    private static Specification<CourseEntity> maxCost(Integer cost) {
        return (root, query, cb) ->
                cost == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("cost"), cost);
    }

    private static Specification<CourseEntity> duration(Integer duration) {
        return (root, query, cb) ->
                duration == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("duration"), duration);
    }

    private static Specification<CourseEntity> hasLanguages(List<String> languages) {
        return (root, query, cb) ->
                languages == null || languages.isEmpty() ? cb.conjunction() : root.get("language").in(languages);
    }

    private static Specification<CourseEntity> hasCountries(List<String> countries) {
        return (root, query, cb) -> {
            if (countries == null || countries.isEmpty()) return cb.conjunction();
            // Join university -> location -> country
            Join<CourseEntity, UniversityEntity> universityJoin = root.join("university");
            Join<UniversityEntity, LocationEntity> locationJoin = universityJoin.join("location");
            return locationJoin.get("country").in(countries);
        };
    }

    private static Specification<CourseEntity> hasAreasOfStudy(List<String> areas) {
        return (root, query, cb) -> {
            if (areas == null || areas.isEmpty()) return cb.conjunction();
            return root.join("areaOfStudies").get("name").in(areas);
        };
    }

    private static Specification<CourseEntity> hasScholarship(Boolean has) {
        return (root, query, cb) -> {
            if (has == null) return cb.conjunction();
            // Join course -> university -> scholarships
            Join<CourseEntity, UniversityEntity> universityJoin = root.join("university");
            Join<UniversityEntity, ScholarshipEntity> scholarshipJoin = universityJoin.join("scholarships", JoinType.LEFT);
            if (has) {
                return cb.isNotNull(scholarshipJoin.get("id"));
            } else {
                return cb.isNull(scholarshipJoin.get("id"));
            }
        };
    }
}
