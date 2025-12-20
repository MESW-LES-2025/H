package com.lernia.auth.repository;

import com.lernia.auth.entity.CourseEntity;
import com.lernia.auth.entity.CourseReviewEntity;
import com.lernia.auth.entity.UniversityEntity;
import com.lernia.auth.entity.UserEntity;
import com.lernia.auth.entity.enums.Gender;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CourseReviewRepositoryTest {

    @Autowired
    private CourseReviewRepository courseReviewRepository;

    @Autowired
    private EntityManager em;

    @Test
    void findByCourseIdOrderByReviewDateDesc_returnsReviewsInDescendingOrder() {

        UniversityEntity university = new UniversityEntity();
        university.setName("Test University");
        em.persist(university);

        UserEntity user = new UserEntity();
        user.setGender(Gender.FEMALE);
        em.persist(user);

        CourseEntity course = new CourseEntity();
        course.setName("Computer Science");
        course.setUniversity(university);
        em.persist(course);

        CourseReviewEntity older = new CourseReviewEntity();
        older.setCourse(course);
        older.setRating(3F);
        older.setReviewDate(LocalDate.now().minusMonths(3));
        older.setUser(user);
        em.persist(older);

        CourseReviewEntity newer = new CourseReviewEntity();
        newer.setCourse(course);
        newer.setRating(5F);
        newer.setReviewDate(LocalDate.now().plusDays(3));
        newer.setUser(user);
        em.persist(newer);

        em.flush();
        em.clear();

        List<CourseReviewEntity> reviews = courseReviewRepository.findByCourseIdOrderByReviewDateDesc(course.getId());

        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getReviewDate()).isAfter(reviews.get(1).getReviewDate());
    }

    @Test
    void findByCourseIdOrderByReviewDateDesc_returnsOnlyReviewsForThatCourse() {

        UniversityEntity university = new UniversityEntity();
        university.setName("Test University");
        em.persist(university);

        UserEntity user = new UserEntity();
        user.setGender(Gender.FEMALE);
        em.persist(user);

        CourseEntity course1 = new CourseEntity();
        course1.setName("Course 1");
        course1.setUniversity(university);
        em.persist(course1);

        CourseEntity course2 = new CourseEntity();
        course2.setName("Course 2");
        course2.setUniversity(university);
        em.persist(course2);

        CourseReviewEntity review1 = new CourseReviewEntity();
        review1.setCourse(course1);
        review1.setReviewDate(LocalDate.now().plusDays(2));
        review1.setRating(2F);
        review1.setUser(user);
        em.persist(review1);

        CourseReviewEntity review2 = new CourseReviewEntity();
        review2.setCourse(course2);
        review2.setReviewDate(LocalDate.now().plusDays(7));
        review2.setRating(3F);
        review2.setUser(user);
        em.persist(review2);

        em.flush();
        em.clear();

        List<CourseReviewEntity> reviews = courseReviewRepository.findByCourseIdOrderByReviewDateDesc(course1.getId());

        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getCourse().getId()).isEqualTo(course1.getId());
    }

    @Test
    void findByCourseIdOrderByReviewDateDesc_returnsEmptyWhenNoReviews() {

        UniversityEntity university = new UniversityEntity();
        university.setName("Test University");
        em.persist(university);

        CourseEntity course = new CourseEntity();
        course.setName("Empty Course");
        course.setUniversity(university);
        em.persist(course);

        em.flush();
        em.clear();

        List<CourseReviewEntity> reviews = courseReviewRepository.findByCourseIdOrderByReviewDateDesc(course.getId());

        assertThat(reviews).isEmpty();
    }
}
