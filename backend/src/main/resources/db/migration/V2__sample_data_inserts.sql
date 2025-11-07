------------------------------------------------------------
-- SAMPLE DATA SEED FILE
-- Run after V1__create_schema.sql
-- This inserts reference data and demo business data.
------------------------------------------------------------

------------------------------------------------------------
-- 2) Users
------------------------------------------------------------
-- Use the lernia schema
SET search_path TO lernia;

-- Insert sample users
-- Each insert below creates a sample user with name, birth date, gender, and auto-created timestamp

INSERT INTO lernia.user (name, username, gender)
VALUES
('Alice Martins', 'a.martins', 'FEMALE'),
('João Pereira', 'j.pereira', 'MALE'),
('Taylor Morgan', 't.morgan', 'OTHER');

------------------------------------------------------------
-- 3) Universities
------------------------------------------------------------
INSERT INTO lernia.university (name, city, country, description, website, logo)
VALUES
('University of Porto', 'Porto', 'Portugal', 'Top public university in Portugal.', 'https://www.up.pt', 'up-logo.png'),
('University of Coimbra', 'Coimbra', 'Portugal', 'Historic research university.', 'https://www.uc.pt', 'uc-logo.png');

------------------------------------------------------------
-- 4) Campus
------------------------------------------------------------
INSERT INTO lernia.campus (university_id, name, address, capacity, is_main_campus)
VALUES
(1, 'Asprela Campus', 'Rua Dr. Roberto Frias', 12000, true),
(2, 'Pólo I Alta Universitária', 'Paços da Universidade', 9000, true);

------------------------------------------------------------
-- 5) Courses
------------------------------------------------------------
INSERT INTO lernia.course (name, area_of_study, topic, course_type)
VALUES
('Computer Engineering', 'Engineering', 'Computing and Informatics', 'BACHELOR'),
('Software Engineering', 'Engineering', 'Software Systems', 'MASTER');

------------------------------------------------------------
-- 6) University offers Courses
------------------------------------------------------------
INSERT INTO lernia.university_course (university_id, course_id)
VALUES
(1, 1),
(1, 2),
(2, 1);

------------------------------------------------------------
-- 7) Scholarships
------------------------------------------------------------
INSERT INTO lernia.scholarship (university_id, name, course_type)
VALUES
(1, 'Merit Scholarship', 'MASTER'),
(2, 'International Student Scholarship', 'BACHELOR');

------------------------------------------------------------
-- 8) Admission Requirements
------------------------------------------------------------
INSERT INTO lernia.admission_requirement (course_id, name, description)
VALUES
(1, 'Math Placement Test', 'Basic calculus and algebra.'),
(2, 'Programming Skills Evaluation', 'Simple coding logic.');

------------------------------------------------------------
-- 9) Professors
------------------------------------------------------------
INSERT INTO lernia.professor (name, email, rating)
VALUES
('Dr. João Pereira', 'jpereira@up.pt', 4.6),
('Dr. Marta Lopes', 'mlopes@uc.pt', 4.9);

------------------------------------------------------------
-- 10) Curricular Units
------------------------------------------------------------
INSERT INTO lernia.curricular_unit (course_id, name, credits, semester, year, hours, professor_id)
VALUES
(1, 'Algorithms and Data Structures', 6, 1, 1, 60, 1),
(2, 'Software Architecture', 6, 1, 1, 60, 2);

------------------------------------------------------------
-- 11) User Enrollment (History)
------------------------------------------------------------
INSERT INTO lernia.user_course (user_id, course_id, start_date, end_date)
VALUES
(3, 2, '2024-09-01', null);  -- Carla is studying Software Engineering

------------------------------------------------------------
-- 12) Reviews
------------------------------------------------------------
INSERT INTO lernia.review (rating, description, user_id)
VALUES
(4.5, 'Challenging but worth it.', 3),
(5.0, 'Fantastic research environment!', 2);

-- Course Review
INSERT INTO lernia.course_review (review_id, user_id, course_id)
VALUES
(1, 3, 2);

-- University Review
INSERT INTO lernia.university_review (review_id, user_id, university_id)
VALUES
(2, 2, 1);

------------------------------------------------------------
-- 13) Favourites
------------------------------------------------------------
INSERT INTO lernia.favourite (user_id, favourite_type, course_id, favourite_date)
VALUES
(3, 'COURSE', 2, CURRENT_DATE);

INSERT INTO lernia.favourite (user_id, favourite_type, university_id, favourite_date)
VALUES
(2, 'UNIVERSITY', 1, CURRENT_DATE);
