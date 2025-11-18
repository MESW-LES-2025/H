CREATE SCHEMA IF NOT EXISTS lernia;

------------------------------------------------------------
-- ENUM Types
------------------------------------------------------------
CREATE TYPE lernia.course_type AS ENUM ('BACHELOR', 'MASTER', 'DOCTORATE');
CREATE TYPE lernia.user_role AS ENUM ('REGULAR', 'PREMIUM', 'STUDENT', 'ADMIN');
CREATE TYPE lernia.gender AS ENUM ('MALE', 'FEMALE', 'OTHER');

CREATE TABLE lernia.user (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    username VARCHAR(255),
    email VARCHAR(254),
    password VARCHAR(255),
    age INT,
    gender lernia.gender NOT NULL,
    location VARCHAR(255),
    profile_picture VARCHAR(255),
    job_title VARCHAR(255),
    creation_date DATE,
    user_role lernia.user_role,
    premium_start_date DATE
);

CREATE TABLE lernia.location (
    id BIGSERIAL PRIMARY KEY,
    city VARCHAR(255),
    country VARCHAR(255),
    cost_of_living INT
);

CREATE TABLE lernia.university (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    location_id BIGINT REFERENCES lernia.location(id),
    contact_info VARCHAR(20),
    website VARCHAR(255),
    address VARCHAR(255),
    logo VARCHAR(255)
);

CREATE TABLE lernia.campus (
    id BIGSERIAL PRIMARY KEY,
    university_id BIGINT NOT NULL REFERENCES lernia.university(id),
    name VARCHAR(50) NOT NULL,
    description TEXT,
    country VARCHAR(50),
    city VARCHAR(50),
    capacity INT
);

CREATE TABLE lernia.area_of_study (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE lernia.course (
    id BIGSERIAL PRIMARY KEY,
    university_id BIGINT NOT NULL REFERENCES lernia.university(id),
    name VARCHAR(60) NOT NULL,
    description TEXT,
    course_type lernia.course_type,
    is_remote BOOLEAN,
    min_admission_grade INT,
    cost INT,
    duration VARCHAR(50),
    credits INT,
    language VARCHAR(50),
    start_date DATE,
    application_deadline DATE,
    contact_email VARCHAR(254),
    website TEXT
);


CREATE TABLE lernia.course_area_of_study (
    course_id BIGINT NOT NULL REFERENCES lernia.course(id) ON DELETE CASCADE,
    area_of_study_id BIGINT NOT NULL REFERENCES lernia.area_of_study(id) ON DELETE CASCADE,
    PRIMARY KEY (course_id, area_of_study_id)
);

CREATE TABLE lernia.curricular_unit (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES lernia.course(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    credits INT,
    year INT,
    semester INT,
    hours INT
);

CREATE TABLE lernia.review (
    id BIGSERIAL PRIMARY KEY,
    rating FLOAT NOT NULL,
    title VARCHAR(100),
    description TEXT,
    review_date DATE,
    user_id BIGINT NOT NULL REFERENCES lernia.user(id) ON DELETE CASCADE,
    dtype VARCHAR(31) -- For JPA Inheritance Type discriminator column if used
);

CREATE TABLE lernia.course_review (
    id BIGINT PRIMARY KEY REFERENCES lernia.review(id) ON DELETE CASCADE,
    course_id BIGINT NOT NULL REFERENCES lernia.course(id)
);

CREATE TABLE lernia.university_review (
    id BIGINT PRIMARY KEY REFERENCES lernia.review(id) ON DELETE CASCADE,
    university_id BIGINT NOT NULL REFERENCES lernia.university(id)
);

CREATE TABLE lernia.scholarship (
    id BIGSERIAL PRIMARY KEY,
    university_id BIGINT NOT NULL REFERENCES lernia.university(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    amount INT NOT NULL,
    course_type lernia.course_type
);

CREATE TABLE lernia.user_bookmarked_courses (
    user_id BIGINT NOT NULL REFERENCES lernia.user(id) ON DELETE CASCADE,
    course_id BIGINT NOT NULL REFERENCES lernia.course(id) ON DELETE CASCADE,
    PRIMARY KEY(user_id, course_id)
);

CREATE TABLE lernia.user_bookmarked_universities (
    user_id BIGINT NOT NULL REFERENCES lernia.user(id) ON DELETE CASCADE,
    university_id BIGINT NOT NULL REFERENCES lernia.university(id)ON DELETE CASCADE,
    PRIMARY KEY(user_id, university_id)
);

CREATE TABLE lernia.user_course (
     id BIGSERIAL PRIMARY KEY,
     user_id BIGINT NOT NULL REFERENCES lernia.user(id) ON DELETE CASCADE,
     course_id BIGINT NOT NULL REFERENCES lernia.course(id) ON DELETE CASCADE,
     start_date DATE,
     end_date DATE,
     is_finished BOOLEAN
);
