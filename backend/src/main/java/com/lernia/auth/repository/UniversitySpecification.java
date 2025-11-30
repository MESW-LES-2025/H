package com.lernia.auth.repository;

import com.lernia.auth.dto.UniversityFilter;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.ScholarshipEntity;
import com.lernia.auth.entity.UniversityEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UniversitySpecification {

    private UniversitySpecification() {
    }

    public static Specification<UniversityEntity> filter(UniversityFilter req) {
        return Specification.where(hasCountries(req.getCountries()))
                .and(maxCostOfLiving(req.getCostOfLivingMax()))
                .and(hasScholarship(req.getHasScholarship()));
    }

    private static Specification<UniversityEntity> hasCountries(List<String> countries) {
        return (root, query, cb) -> {
            if (countries == null || countries.isEmpty())
                return cb.conjunction();
            Join<UniversityEntity, LocationEntity> locationJoin = root.join("location");
            return locationJoin.get("country").in(countries);
        };
    }

    private static Specification<UniversityEntity> maxCostOfLiving(Integer costOfLiving) {
        return (root, query, cb) -> {
            if (costOfLiving == null)
                return cb.conjunction();
            Join<UniversityEntity, LocationEntity> locationJoin = root.join("location");
            return cb.lessThanOrEqualTo(locationJoin.get("cost_of_living"), costOfLiving);
        };
    }

    private static Specification<UniversityEntity> hasScholarship(Boolean has) {
        return (root, query, cb) -> {
            if (has == null)
                return cb.conjunction();
            Join<UniversityEntity, ScholarshipEntity> scholarshipJoin = root.join("scholarships", JoinType.LEFT);

            if (has) {
                // If hasScholarship is true, there must be at least one scholarship
                query.distinct(true);
                return cb.isNotNull(scholarshipJoin.get("id"));
            } else {
                // If hasScholarship is false, there should be no scholarships
                return cb.isNull(scholarshipJoin.get("id"));
            }
        };
    }
}
