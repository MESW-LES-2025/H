package com.lernia.auth.repository;

import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.UniversityEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@Transactional
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findById_loadsUniversityLocationAndAreasOfStudy() {
        LocationEntity location = new LocationEntity();
        location.setCity("Porto");
        entityManager.persist(location);

        UniversityEntity university = new UniversityEntity();
        university.setName("FEUP");
        university.setLocation(location);
        entityManager.persist(university);

        AreaOfStudyEntity area = new AreaOfStudyEntity();
        area.setName("Engineering");
        entityManager.persist(area);

        CourseEntity course = new CourseEntity();
        course.setName("BSc Computing");
        course.setUniversity(university);
        course.setAreasOfStudy(List.of(area));
        course.setLanguage("EN");

        entityManager.persist(course);
        entityManager.flush();
        entityManager.clear();

        CourseEntity result = courseRepository.findById(course.getId())
                .orElseThrow();

        assertThat(result.getUniversity()).isNotNull();
        assertThat(result.getUniversity().getLocation()).isNotNull();
        assertThat(result.getAreasOfStudy()).isNotEmpty();

        assertThat(result.getUniversity().getLocation().getCity())
                .isEqualTo("Porto");
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        Optional<CourseEntity> result = courseRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void findDistinctLanguages_returnsDistinctNonNullLanguages() {
        UniversityEntity uni1 = new UniversityEntity();
        uni1.setName("Test University1");
        entityManager.persist(uni1);
        UniversityEntity uni2 = new UniversityEntity();
        uni2.setName("Test University2");
        entityManager.persist(uni2);
        UniversityEntity uni3 = new UniversityEntity();
        uni3.setName("Test University3");
        entityManager.persist(uni3);
        UniversityEntity uni4 = new UniversityEntity();
        uni4.setName("Test University4");
        entityManager.persist(uni4);

        CourseEntity c1 = new CourseEntity();
        c1.setLanguage("EN");
        c1.setName("Master in Aeronautical Engineering");
        c1.setUniversity(uni1);
        entityManager.persist(c1);

        CourseEntity c2 = new CourseEntity();
        c2.setLanguage("PT");
        c2.setName("Doctorate in Biology");
        c2.setUniversity(uni2);
        entityManager.persist(c2);

        CourseEntity c3 = new CourseEntity();
        c3.setLanguage("EN");
        c3.setName("Bachelor in Mathematics");
        c3.setUniversity(uni3);
        entityManager.persist(c3);

        CourseEntity c4 = new CourseEntity();
        c4.setLanguage(null);
        c4.setName("Bachelor in Medicine");
        c4.setUniversity(uni4);
        entityManager.persist(c4);

        entityManager.flush();

        List<String> languages = courseRepository.findDistinctLanguages();

        assertThat(languages).contains("EN", "PT");
    }

    @Test
    void findById_returnsCourseEvenWhenAreasOfStudyIsEmpty() {
        UniversityEntity university = new UniversityEntity();
        university.setName("Test University");
        entityManager.persist(university);
        CourseEntity course = new CourseEntity();
        course.setLanguage("EN");
        course.setName("Doctorate in Chemistry");
        course.setUniversity(university);
        course.setAreasOfStudy(List.of());
        entityManager.persist(course);

        entityManager.flush();
        entityManager.clear();

        CourseEntity result = courseRepository.findById(course.getId()).orElseThrow();

        assertNotNull(result);
        assertThat(result.getAreasOfStudy()).isEmpty();
    }

    @Test
    void findById_returnsCourseWithoutUniversityIfAllowed() {
        var university = new UniversityEntity();
        university.setName("Test University");
        entityManager.persist(university);

        CourseEntity course = new CourseEntity();
        course.setLanguage("EN");
        course.setName("PhD Chemistry");
        course.setUniversity(university);
        entityManager.persist(course);

        entityManager.flush();
        entityManager.clear();

        CourseEntity result = courseRepository.findById(course.getId()).orElseThrow();
        assertThat(result.getUniversity().getId()).isEqualTo(university.getId());
    }
}
