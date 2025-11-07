------------------------------------------------------------
-- Create application schema
------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS lernia;

------------------------------------------------------------
-- ENUM Types
------------------------------------------------------------
CREATE TYPE lernia.course_type AS ENUM ('BACHELOR', 'MASTER', 'DOCTORATE');
CREATE TYPE lernia.favourite_type AS ENUM ('UNIVERSITY', 'COURSE');
CREATE TYPE lernia.user_role AS ENUM ('REGULAR', 'PREMIUM', 'STUDENT', 'ADMIN');

------------------------------------------------------------
-- Users (Single-table inheritance using role discriminator)
------------------------------------------------------------
CREATE TABLE lernia.user (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR(200),
    username            VARCHAR(100) UNIQUE,
    email               VARCHAR(200) UNIQUE,
    password            VARCHAR(255),
    age                 INT,
    gender              VARCHAR(20) NOT NULL,
    location            VARCHAR(255),
    profile_picture     VARCHAR(255),
    job_title           VARCHAR(255),
    creation_date       DATE DEFAULT CURRENT_DATE,

    -- Discriminator indicating type of user
    user_role           lernia.user_role NOT NULL DEFAULT 'REGULAR',

    -- Applies only when user_role = 'PREMIUM'
    premium_start_date  DATE
);

CREATE INDEX ON lernia.user (username);
CREATE INDEX ON lernia.user (email);

------------------------------------------------------------
-- University
------------------------------------------------------------
CREATE TABLE lernia.university (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(300) NOT NULL,
    city            VARCHAR(150),
    country         VARCHAR(100),
    description     VARCHAR(255),
    contact_info    VARCHAR(255),
    website         VARCHAR(255),
    location        VARCHAR(255),
    logo            VARCHAR(255)
);

CREATE INDEX ON lernia.university (name);

------------------------------------------------------------
-- Campus (belongs to a University)
------------------------------------------------------------
CREATE TABLE lernia.campus (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    university_id   BIGINT NOT NULL REFERENCES lernia.university(id) ON DELETE CASCADE,
    name            VARCHAR(200) NOT NULL,
    address         VARCHAR(255),
    capacity        INTEGER,
    is_main_campus  BOOLEAN DEFAULT false
);

------------------------------------------------------------
-- Courses
------------------------------------------------------------
CREATE TABLE lernia.course (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(300) NOT NULL,
    area_of_study   VARCHAR(255),
    topic           VARCHAR(255),
    course_type     lernia.course_type NOT NULL
);

CREATE INDEX ON lernia.course (name);

------------------------------------------------------------
-- Many-to-Many: University offers many Courses
------------------------------------------------------------
CREATE TABLE lernia.university_course (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    university_id   BIGINT NOT NULL REFERENCES lernia.university(id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES lernia.course(id) ON DELETE CASCADE,
    UNIQUE (university_id, course_id)
);

------------------------------------------------------------
-- Scholarships per University
------------------------------------------------------------
CREATE TABLE lernia.scholarship (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    university_id   BIGINT REFERENCES lernia.university(id) ON DELETE CASCADE,
    name            VARCHAR(250),
    course_type     lernia.course_type
);

------------------------------------------------------------
-- Admission Requirements for a Course
------------------------------------------------------------
CREATE TABLE lernia.admission_requirement (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES lernia.course(id) ON DELETE CASCADE,
    name            VARCHAR(250) NOT NULL,
    description     VARCHAR(255)
);

------------------------------------------------------------
-- Curricular Units (Subjects within a course)
------------------------------------------------------------
CREATE TABLE lernia.curricular_unit (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES lernia.course(id) ON DELETE CASCADE,
    name            VARCHAR(300) NOT NULL,
    credits         INTEGER,
    semester        INTEGER,
    year            INTEGER,
    hours           INTEGER
);

------------------------------------------------------------
-- Professors
------------------------------------------------------------
CREATE TABLE lernia.professor (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    email           VARCHAR(200) UNIQUE,
    rating          REAL
);

-- Assign professors to curricular units (1 professor per unit)
ALTER TABLE lernia.curricular_unit
    ADD COLUMN professor_id BIGINT REFERENCES lernia.professor(id);

------------------------------------------------------------
-- User Course Enrollment / History
------------------------------------------------------------
CREATE TABLE lernia.user_course (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES lernia.user(id) ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES lernia.course(id) ON DELETE CASCADE,
    start_date      DATE,
    end_date        DATE,
    UNIQUE (user_id, course_id, start_date)
);

------------------------------------------------------------
-- Base Review Table
------------------------------------------------------------
CREATE TABLE lernia.review (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    rating          REAL,
    description     VARCHAR(255),
    review_date     DATE DEFAULT CURRENT_DATE,
    user_id         BIGINT REFERENCES lernia.user(id) ON DELETE SET NULL
);

------------------------------------------------------------
-- Review of Courses
------------------------------------------------------------
CREATE TABLE lernia.course_review (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    review_id       BIGINT NOT NULL UNIQUE REFERENCES lernia.review(id) ON DELETE CASCADE,
    user_id         BIGINT REFERENCES lernia.user(id) ON DELETE SET NULL,
    course_id       BIGINT NOT NULL REFERENCES lernia.course(id) ON DELETE CASCADE
);

------------------------------------------------------------
-- Review of Universities
------------------------------------------------------------
CREATE TABLE lernia.university_review (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    review_id       BIGINT NOT NULL UNIQUE REFERENCES lernia.review(id) ON DELETE CASCADE,
    user_id         BIGINT REFERENCES lernia.user(id) ON DELETE SET NULL,
    university_id   BIGINT NOT NULL REFERENCES lernia.university(id) ON DELETE CASCADE
);

------------------------------------------------------------
-- Favourites (User can favorite either a Course or a University)
------------------------------------------------------------
CREATE TABLE lernia.favourite (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES lernia.user(id) ON DELETE CASCADE,
    favourite_type      lernia.favourite_type NOT NULL,
    course_id           BIGINT REFERENCES lernia.course(id) ON DELETE CASCADE,
    university_id       BIGINT REFERENCES lernia.university(id) ON DELETE CASCADE,
    favourite_date      DATE DEFAULT CURRENT_DATE,

    CHECK (
        (favourite_type = 'COURSE' AND course_id IS NOT NULL AND university_id IS NULL)
     OR (favourite_type = 'UNIVERSITY' AND university_id IS NOT NULL AND course_id IS NULL)
    ),

    UNIQUE (user_id, favourite_type, course_id, university_id)
);
