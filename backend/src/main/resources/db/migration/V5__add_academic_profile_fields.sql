-- V5: Add academic profile fields to users table for entry probability feature
ALTER TABLE users ADD COLUMN academic_grade INTEGER;
ALTER TABLE users ADD COLUMN education_level VARCHAR(50);
ALTER TABLE users ADD COLUMN study_area VARCHAR(255);
