package com.lernia.auth.repository;

import com.lernia.auth.dto.filter.CourseFilter;
import com.lernia.auth.entity.AreaOfStudyEntity;
import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.LocationEntity;
import com.lernia.auth.entity.ScholarshipEntity;
import com.lernia.auth.entity.UniversityEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@Transactional
class CourseSpecificationTest {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EntityManager em;

    @Test
    void filter_byName_returnsMatchingCourses() {
        UniversityEntity uni1 = new UniversityEntity();
        uni1.setName("University 1");
        em.persist(uni1);
        UniversityEntity uni2 = new UniversityEntity();
        uni2.setName("University 2");
        em.persist(uni2);

        CourseEntity c1 = new CourseEntity();
        c1.setName("Computer Science");
        c1.setUniversity(uni1);
        em.persist(c1);

        CourseEntity c2 = new CourseEntity();
        c2.setName("Mechanical Engineering");
        c2.setUniversity(uni2);
        em.persist(c2);

        em.flush();

        CourseFilter filter = buildCourseFilter("computer");

        List<CourseEntity> result = courseRepository.findAll(CourseSpecification.filter(filter));

        assertThat(result)
                .extracting(CourseEntity::getName)
                .contains("Computer Science");
    }

    @Test
    void filter_byCountry_returnsCoursesFromThatCountry() {
        LocationEntity pt = new LocationEntity();
        pt.setCountry("Portugal");
        em.persist(pt);

        LocationEntity de = new LocationEntity();
        de.setCountry("Germany");
        em.persist(de);

        UniversityEntity u1 = new UniversityEntity();
        u1.setLocation(pt);
        u1.setName("University 1");
        em.persist(u1);

        UniversityEntity u2 = new UniversityEntity();
        u2.setLocation(de);
        u2.setName("University 2");
        em.persist(u2);

        CourseEntity c1 = new CourseEntity();
        c1.setUniversity(u1);
        c1.setName("Course 1");
        em.persist(c1);

        CourseEntity c2 = new CourseEntity();
        c2.setUniversity(u2);
        c2.setName("Course 2");
        em.persist(c2);

        em.flush();

        CourseFilter filter = buildCourseFilter(null);
        filter.setCountries(List.of("Portugal"));

        List<CourseEntity> result =
                courseRepository.findAll(CourseSpecification.filter(filter));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertThat(result)
                .extracting(CourseEntity::getUniversity)
                .extracting(UniversityEntity::getLocation)
                .extracting(LocationEntity::getCountry)
                .contains("Portugal");

    }

    @Test
    void filter_byAreaOfStudy_returnsMatchingCourses() {
        UniversityEntity uni = new UniversityEntity();
        uni.setName("University");
        em.persist(uni);

        AreaOfStudyEntity ai = new AreaOfStudyEntity();
        ai.setName("AI");
        em.persist(ai);

        AreaOfStudyEntity math = new AreaOfStudyEntity();
        math.setName("Math");
        em.persist(math);

        CourseEntity c1 = new CourseEntity();
        c1.setUniversity(uni);
        c1.setAreasOfStudy(List.of(ai));
        c1.setName("MSc Robotics");
        em.persist(c1);

        CourseEntity c2 = new CourseEntity();
        c2.setAreasOfStudy(List.of(math));
        c2.setUniversity(uni);
        c2.setName("BA Economics");
        em.persist(c2);

        em.flush();

        CourseFilter filter = buildCourseFilter(null);
        filter.setAreasOfStudy(List.of("AI"));

        List<CourseEntity> result =
                courseRepository.findAll(CourseSpecification.filter(filter));

        assertThat(result)
                .extracting(CourseEntity::getAreasOfStudy)
                .anyMatch(list ->
                        list.stream().anyMatch(a -> a.getName().equals("AI")));
    }

    @Test
    void filter_byHasScholarship_true_returnsOnlyCoursesWithScholarships() {

        UniversityEntity uWith = new UniversityEntity();
        uWith.setName("Universitat de Barcelona");
        em.persist(uWith);

        ScholarshipEntity scholarship = new ScholarshipEntity();
        scholarship.setCourseType("BACHELOR");
        scholarship.setName("Engineering Excellence");
        scholarship.setAmount(2500);
        scholarship.setUniversity(uWith);

        em.persist(scholarship);

        uWith.getScholarships().add(scholarship);

        UniversityEntity uWithout = new UniversityEntity();
        uWithout.setName("University of Amsterdam");
        em.persist(uWithout);

        CourseEntity c1 = new CourseEntity();
        c1.setUniversity(uWith);
        c1.setName("BA Education");
        em.persist(c1);

        CourseEntity c2 = new CourseEntity();
        c2.setUniversity(uWithout);
        c2.setName("BA Journalism");
        em.persist(c2);

        em.flush();
        em.clear();

        CourseFilter filter = buildCourseFilter(null);
        filter.setHasScholarship(true);

        List<CourseEntity> result = courseRepository.findAll(CourseSpecification.filter(filter));

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    private CourseFilter buildCourseFilter(String name) {
        return new CourseFilter(name, null, null, null, null, null, null, null, null);
    }
}
