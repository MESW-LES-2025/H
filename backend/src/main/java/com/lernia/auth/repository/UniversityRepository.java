package com.lernia.auth.repository;

import com.lernia.auth.entity.UniversityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UniversityRepository extends JpaRepository<UniversityEntity, Long> {

    @Query("SELECT DISTINCT c.location.country FROM UniversityEntity c WHERE c.location.country IS NOT NULL")
    List<String> findDistinctCountries();
}