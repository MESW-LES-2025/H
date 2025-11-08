-- 5 Users
INSERT INTO lernia.user (name, username, email, password, age, gender, location, profile_picture, job_title, creation_date, user_role, premium_start_date) VALUES
    ('Alice Smith', 'asmith', 'alice@example.com', 'pass1', 21, 'FEMALE', 'Lisbon', 'asmith.png', 'Student', CURRENT_DATE, 'REGULAR', NULL),
    ('Bob Johnson', 'bjohnson', 'bob@example.com', 'pass2', 25, 'MALE', 'Madrid', 'bjohnson.png', 'Analyst', CURRENT_DATE, 'PREMIUM', '2024-01-01'),
    ('Claire Lee', 'clee', 'claire@example.com', 'pass3', 30, 'FEMALE', 'Berlin', 'clee.png', 'Researcher', CURRENT_DATE, 'STUDENT', NULL),
    ('David Wilson', 'dwilson', 'david@example.com', 'pass4', 22, 'MALE', 'Lisbon', 'dwilson.png', 'Student', CURRENT_DATE, 'REGULAR', NULL),
    ('Eva Brown', 'ebrown', 'eva@example.com', 'pass5', 27, 'FEMALE', 'Madrid', 'ebrown.png', 'Manager', CURRENT_DATE, 'ADMIN', NULL);


-- 3 Universities
INSERT INTO lernia.university (name, city, country, description, contact_info, website, address, logo) VALUES
    ('Tech University', 'Lisbon', 'Portugal', 'Top tech university', '123456789', 'https://techuni.edu', '123 Tech Road', 'tech_logo.png'),
    ('Business College', 'Madrid', 'Spain', 'Leading business school', '987654321', 'https://business.edu', '45 Commerce St', 'biz_logo.png'),
    ('Science Institute', 'Berlin', 'Germany', 'Premier science institute', '555666777', 'https://science.edu', '77 Science Blvd', 'science_logo.png');

-- 3 Campuses
INSERT INTO lernia.campus (university_id, name, description, country, city, capacity) VALUES
    (1, 'Lisbon Campus', 'Main campus', 'Portugal', 'Lisbon', 5000),
    (2, 'Madrid Campus', 'City campus', 'Spain', 'Madrid', 3000),
    (3, 'Berlin Campus', 'Research campus', 'Germany', 'Berlin', 4000);

-- 3 Areas of Study
INSERT INTO lernia.area_of_study (name) VALUES
    ('Computer Science'),
    ('Business Administration'),
    ('Physics');

-- 9 Courses
INSERT INTO lernia.course (university_id, name, description, area_of_study_id, course_type, is_remote, min_admission_grade) VALUES
    (1, 'CS BSc', 'Bachelor in Computer Science', 1, 'BACHELOR', false, 75),
    (1, 'AI MSc', 'Master in Artificial Intelligence', 1, 'MASTER', true, 80),
    (2, 'BA BSc', 'Bachelor in Business Administration', 2, 'BACHELOR', false, 70),
    (2, 'MBA', 'Master in Business Administration', 2, 'MASTER', true, 85),
    (3, 'Physics BSc', 'Bachelor in Physics', 3, 'BACHELOR', false, 78),
    (3, 'Astrophysics MSc', 'Master in Astrophysics', 3, 'MASTER', false, 82),
    (1, 'Software Engineering MSc', 'Master in Software Engineering', 1, 'MASTER', true, 79),
    (2, 'Marketing BSc', 'Bachelor in Marketing', 2, 'BACHELOR', false, 72),
    (3, 'Quantum Physics PhD', 'Doctorate in Quantum Physics', 3, 'DOCTORATE', false, 90);


-- 20 Curricular Units (spread across courses)
INSERT INTO lernia.curricular_unit (course_id, name, description, credits, year, semester, hours) VALUES
    (1, 'Intro CS', 'Basics of CS', 6, 1, 1, 45),
    (1, 'Data Structures', 'Understanding DS', 6, 2, 1, 45),
    (1, 'Algorithms', 'Algorithm Analysis', 6, 2, 2, 45),
    (2, 'ML Basics', 'Machine Learning intro', 6, 1, 1, 45),
    (2, 'Neural Networks', 'Deep learning', 6, 2, 2, 45),
    (3, 'Accounting 101', 'Basic accounting', 6, 1, 1, 40),
    (3, 'Finance', 'Financial principles', 6, 2, 1, 40),
    (4, 'Business Strategy', 'Strategic management', 6, 1, 2, 40),
    (4, 'Leadership', 'Leadership skills', 6, 2, 2, 40),
    (5, 'Classical Mechanics', 'Mechanics fundamentals', 6, 1, 1, 35),
    (5, 'Electromagnetism', 'EM Field theory', 6, 2, 1, 35),
    (6, 'Cosmology', 'Study of universe', 6, 1, 2, 35),
    (6, 'Particle Physics', 'Elementary particles', 6, 2, 2, 35),
    (7, 'Software Design', 'Software architecture', 6, 1, 1, 45),
    (7, 'DevOps', 'Deployment practices', 6, 2, 2, 45),
    (8, 'Advertising', 'Advertising concepts', 6, 1, 1, 40),
    (8, 'Consumer Behavior', 'Marketing research', 6, 2, 2, 40),
    (9, 'Quantum Theory', 'Quantum field theory', 6, 1, 2, 30),
    (9, 'Advanced Physics', 'Physics research', 6, 2, 2, 30),
    (9, 'Thesis', 'Research project', 12, 3, 1, 60);

-- 3 Scholarships
INSERT INTO lernia.scholarship (university_id, name, description, amount, course_type) VALUES
    (1, 'Tech Merit', 'Academic excellence scholarship', 1500, 'BACHELOR'),
    (2, 'Business Grant', 'Support for business students', 2000, 'MASTER'),
    (3, 'Science Fellowship', 'For promising researchers', 3000, 'DOCTORATE');

-- 3 User Bookmarked Courses
INSERT INTO lernia.user_bookmarked_courses (user_id, course_id) VALUES (1, 1), (2, 2), (3, 3);

-- 2 User Bookmarked Universities
INSERT INTO lernia.user_bookmarked_universities (user_id, university_id) VALUES (1, 1), (2, 2);

-- 7 User Courses
INSERT INTO lernia.user_course (user_id, course_id, start_date, end_date, is_finished) VALUES
    (1, 1, '2023-09-01', NULL, false),
    (1, 2, '2024-02-01', NULL, false),
    (2, 2, '2023-10-01', '2024-03-01', true),
    (3, 3, '2023-09-15', NULL, false),
    (4, 1, '2024-01-10', '2024-05-20', true),
    (5, 2, '2024-03-01', NULL, false),
    (5, 3, '2024-01-01', NULL, false);

-- 6 Course Reviews
INSERT INTO lernia.review (id, rating, title, description, review_date, user_id, dtype) VALUES
    (1, 4.5, 'Great course', 'Very informative.', CURRENT_DATE, 1, 'CourseReviewEntity'),
    (2, 3.9, 'Good course', 'Challenging but rewarding.', CURRENT_DATE, 2, 'CourseReviewEntity'),
    (3, 5.0, 'Excellent!', 'Highly recommended.', CURRENT_DATE, 3, 'CourseReviewEntity'),
    (4, 4.0, 'Well structured', 'Good materials.', CURRENT_DATE, 4, 'CourseReviewEntity'),
    (5, 3.5, 'Needs improvement', 'Could include more practice.', CURRENT_DATE, 5, 'CourseReviewEntity'),
    (6, 4.8, 'Loved it', 'Engaging and helpful.', CURRENT_DATE, 1, 'CourseReviewEntity');

-- Course Review associations
INSERT INTO lernia.course_review (id, course_id) VALUES
    (1, 1), (2, 2), (3, 3), (4, 1), (5, 2), (6, 3);

-- 2 University Reviews
INSERT INTO lernia.review (id, rating, title, description, review_date, user_id, dtype) VALUES
    (7, 4.7, 'Excellent university', 'Great environment.', CURRENT_DATE, 5, 'UniversityReviewEntity'),
    (8, 3.9, 'Good university', 'Solid programs.', CURRENT_DATE, 4, 'UniversityReviewEntity');

-- University Review associations
INSERT INTO lernia.university_review (id, university_id) VALUES (7, 1), (8, 2);
