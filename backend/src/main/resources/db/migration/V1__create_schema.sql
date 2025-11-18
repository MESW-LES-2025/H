CREATE SCHEMA IF NOT EXISTS lernia;

------------------------------------------------------------
-- ENUM Types
------------------------------------------------------------
CREATE TYPE lernia.course_type AS ENUM ('BACHELOR', 'MASTER', 'DOCTORATE');
CREATE TYPE lernia.user_role AS ENUM ('REGULAR', 'PREMIUM', 'STUDENT', 'ADMIN');
CREATE TYPE lernia.gender AS ENUM ('MALE', 'FEMALE', 'OTHER');

CREATE TABLE lernia.users (
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

CREATE TABLE lernia.locations (
    id BIGSERIAL PRIMARY KEY,
    city VARCHAR(255),
    country VARCHAR(255),
    cost_of_living INT
);

CREATE TABLE lernia.universities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    location_id BIGINT REFERENCES lernia.locations(id),
    contact_info VARCHAR(20),
    website VARCHAR(255),
    address VARCHAR(255),
    logo VARCHAR(255)
);

CREATE TABLE lernia.campuses (
    id BIGSERIAL PRIMARY KEY,
    university_id BIGINT NOT NULL REFERENCES lernia.universities(id),
    name VARCHAR(50) NOT NULL,
    description TEXT,
    country VARCHAR(50),
    city VARCHAR(50),
    capacity INT
);

CREATE TABLE lernia.areas_of_study (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE lernia.courses (
    id BIGSERIAL PRIMARY KEY,
    university_id BIGINT NOT NULL REFERENCES lernia.universities(id),
    name VARCHAR(60) NOT NULL,
    description TEXT,
    course_type lernia.course_type,
    is_remote BOOLEAN,
    min_admission_grade INT,
    cost INT,
    duration INT,
    credits INT,
    language VARCHAR(50),
    start_date DATE,
    application_deadline DATE,
    contact_email VARCHAR(254),
    website TEXT
);


CREATE TABLE lernia.course_area_of_study (
    course_id BIGINT NOT NULL REFERENCES lernia.courses(id) ON DELETE CASCADE,
    area_of_study_id BIGINT NOT NULL REFERENCES lernia.areas_of_study(id) ON DELETE CASCADE,
    PRIMARY KEY (course_id, area_of_study_id)
);

CREATE TABLE lernia.curricular_units (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES lernia.courses(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    credits INT,
    year INT,
    semester INT,
    hours INT
);

CREATE TABLE lernia.reviews (
    id BIGSERIAL PRIMARY KEY,
    rating FLOAT NOT NULL,
    title VARCHAR(100),
    description TEXT,
    review_date DATE,
    user_id BIGINT NOT NULL REFERENCES lernia.users(id) ON DELETE CASCADE,
    dtype VARCHAR(31) -- For JPA Inheritance Type discriminator column if used
);

CREATE TABLE lernia.course_reviews (
    id BIGINT PRIMARY KEY REFERENCES lernia.reviews(id) ON DELETE CASCADE,
    course_id BIGINT NOT NULL REFERENCES lernia.courses(id)
);

CREATE TABLE lernia.university_reviews (
    id BIGINT PRIMARY KEY REFERENCES lernia.reviews(id) ON DELETE CASCADE,
    university_id BIGINT NOT NULL REFERENCES lernia.universities(id)
);

CREATE TABLE lernia.scholarships (
    id BIGSERIAL PRIMARY KEY,
    university_id BIGINT NOT NULL REFERENCES lernia.universities(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    amount INT NOT NULL,
    course_type lernia.course_type
);

CREATE TABLE lernia.user_bookmarked_courses (
    user_id BIGINT NOT NULL REFERENCES lernia.users(id) ON DELETE CASCADE,
    course_id BIGINT NOT NULL REFERENCES lernia.courses(id) ON DELETE CASCADE,
    PRIMARY KEY(user_id, course_id)
);

CREATE TABLE lernia.user_bookmarked_universities (
    user_id BIGINT NOT NULL REFERENCES lernia.users(id) ON DELETE CASCADE,
    university_id BIGINT NOT NULL REFERENCES lernia.universities(id)ON DELETE CASCADE,
    PRIMARY KEY(user_id, university_id)
);

CREATE TABLE lernia.user_courses (
     id BIGSERIAL PRIMARY KEY,
     user_id BIGINT NOT NULL REFERENCES lernia.users(id) ON DELETE CASCADE,
     course_id BIGINT NOT NULL REFERENCES lernia.courses(id) ON DELETE CASCADE,
     start_date DATE,
     end_date DATE,
     is_finished BOOLEAN
);
