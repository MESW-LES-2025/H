-- 5 Users
INSERT INTO lernia.users (name, username, email, password, age, gender, location, profile_picture, job_title, creation_date, user_role, premium_start_date) VALUES
    ('Alice Smith', 'asmith', 'alice@example.com', 'pass1', 21, 'FEMALE', 'Lisbon', 'asmith.png', 'Student', CURRENT_DATE, 'REGULAR', NULL),
    ('Bob Johnson', 'bjohnson', 'bob@example.com', 'pass2', 25, 'MALE', 'Madrid', 'bjohnson.png', 'Analyst', CURRENT_DATE, 'PREMIUM', '2024-01-01'),
    ('Claire Lee', 'clee', 'claire@example.com', 'pass3', 30, 'FEMALE', 'Berlin', 'clee.png', 'Researcher', CURRENT_DATE, 'STUDENT', NULL),
    ('David Wilson', 'dwilson', 'david@example.com', 'pass4', 22, 'MALE', 'Lisbon', 'dwilson.png', 'Student', CURRENT_DATE, 'REGULAR', NULL),
    ('Eva Brown', 'ebrown', 'eva@example.com', 'pass5', 27, 'FEMALE', 'Madrid', 'ebrown.png', 'Manager', CURRENT_DATE, 'ADMIN', NULL);

INSERT INTO lernia.locations(city, country, cost_of_living) VALUES
    ('London', 'United Kingdom', 1500),
    ('Berlin', 'Germany', 900),
    ('Barcelona', 'Spain', 1000),
    ('Amsterdam', 'Netherlands', 1300),
    ('Paris', 'France', 1200),
    ('Vienna', 'Austria', 950),
    ('Porto', 'Portugal', 750);


INSERT INTO lernia.universities (name, description, location_id, contact_info, website, address, logo) VALUES
    ('Imperial College London', 'Leading science and technology university', 1, '+44-20-7589-5111', 'https://www.imperial.ac.uk', 'South Kensington Campus, London SW7 2AZ', 'imperial_logo.png'),
    ('Technical University of Berlin', 'Top technical university in Germany', 2, '+49-30-314-0', 'https://www.tu-berlin.de', 'Straße des 17. Juni 135, 10623 Berlin', 'tu_berlin_logo.png'),
    ('Universitat de Barcelona', 'Historic comprehensive university', 3, '+34-93-403-5474', 'https://www.ub.edu', 'Gran Via de les Corts Catalanes, 585, Barcelona', 'ub_logo.png'),
    ('University of Amsterdam', 'Premier Dutch research university', 4, '+31-20-525-9111', 'https://www.uva.nl', 'Spui 21, 1012 WX Amsterdam', 'uva_logo.png'),
    ('Sorbonne University', 'Prestigious French university', 5, '+33-1-40-46-22-11', 'https://www.sorbonne-universite.fr', '21 Rue de l''École de Médecine, 75006 Paris', 'sorbonne_logo.png'),
    ('University of Vienna', 'Oldest university in German-speaking world', 6, '+43-1-4277-0', 'https://www.univie.ac.at', 'Universitätsring 1, 1010 Wien', 'univie_logo.png'),
    ('University of Porto', 'Leading Portuguese research university', 7, '+351-22-040-8000', 'https://www.up.pt', 'Praça Gomes Teixeira, 4099-002 Porto', 'up_logo.png');


INSERT INTO lernia.campuses (university_id, name, description, country, city, capacity) VALUES
    (1, 'South Kensington Campus', 'Main campus in central London', 'United Kingdom', 'London', 17000),
    (2, 'Charlottenburg Campus', 'Historic main campus', 'Germany', 'Berlin', 35000),
    (3, 'Historic Building Campus', 'Central Barcelona campus', 'Spain', 'Barcelona', 63000),
    (4, 'City Centre Campus', 'Campus in Amsterdam heart', 'Netherlands', 'Amsterdam', 31000),
    (5, 'Latin Quarter Campus', 'Historic Parisian campus', 'France', 'Paris', 55000),
    (6, 'Main Campus', 'Central Vienna campus', 'Austria', 'Vienna', 90000),
    (7, 'Polo I Campus', 'Main university campus', 'Portugal', 'Porto', 31000);

INSERT INTO lernia.areas_of_study (name) VALUES
    ('Computer Science'),
    ('Engineering'),
    ('Business Administration'),
    ('Medicine'),
    ('Law'),
    ('Arts & Humanities'),
    ('Natural Sciences');

INSERT INTO lernia.courses (
    university_id, name, description, course_type,
    is_remote, min_admission_grade, cost,
    duration, credits, language,
    start_date, application_deadline, contact_email, website
) VALUES
      (1, 'BSc Computing', 'Bachelor in Computer Science', 'BACHELOR', false, 85, 9250,
       3, 180, 'English', '2025-09-01', '2025-07-01', 'computing@university1.edu', 'https://www.imperial.ac.uk/computing/prospective-students/courses/bsc-computing/'),
      (1, 'MEng Aeronautical Engineering', 'Master in Aeronautical Engineering', 'MASTER', false, 88, 12500,
       2, 120, 'English', '2025-09-01', '2025-07-15', 'aeroeng@university1.edu', 'https://www.tu.berlin/en/studying/study-programs/all-study-programs/detail/aeronautical-and-aerospace-engineering-master-of-science/'),
      (1, 'PhD Bioengineering', 'Doctorate in Bioengineering', 'DOCTORATE', false, 90, 4500,
       4, 240, 'English', '2025-09-01', '2025-06-30', 'bioeng@university1.edu', 'https://www.ub.edu/web/ub/en/estudis/oferta_formativa/doctorats/fitxa/Bioengineering.html'),
      (2, 'BSc Computer Engineering', 'Bachelor in Computer Engineering', 'BACHELOR', false, 75, 8500,
       3, 180, 'Portuguese', '2025-09-01', '2025-07-01', 'compeng@university2.edu', 'https://www.uab.cat/web/estudis/grau/grau-in-computer-engineering-1345547789821.html'),
      (2, 'MSc Electrical Engineering', 'Master in Electrical Engineering', 'MASTER', true, 80, 10000,
       2, 120, 'English', '2025-09-01', '2025-07-10', 'ee@university2.edu', 'https://www.tu.berlin/en/studying/study-programs/all-study-programs/detail/electrical-engineering-master-of-science/'),
      (2, 'PhD Mechanical Engineering', 'Doctorate in Mechanical Engineering', 'DOCTORATE', false, 85, 4000,
       4, 240, 'Portuguese', '2025-09-01', '2025-06-30', 'mecheng@university2.edu', 'https://www.tu.berlin/en/studying/study-programs/all-study-programs/detail/mechanical-engineering-doctorate/'),
      (3, 'BA Business Administration', 'Bachelor in Business Administration', 'BACHELOR', false, 70, 7500,
       3, 180, 'English', '2025-09-01', '2025-07-15', 'business@university3.edu', 'https://www.ub.edu/web/ub/en/estudis/oferta_formativa/graus/empresa_i_gestion.html'),
      (3, 'MSc Economics', 'Master in Economics', 'MASTER', true, 75, 11000,
       2, 120, 'English', '2025-09-01', '2025-07-01', 'economics@university3.edu', 'https://www.ub.edu/web/ub/en/estudis/oferta_formativa/masters/economia.html'),
      (3, 'PhD Biology', 'Doctorate in Biology', 'DOCTORATE', false, 82, 3800,
       4, 240, 'English', '2025-09-01', '2025-06-15', 'biology@university3.edu', 'https://www.ub.edu/web/ub/en/estudis/oferta_formativa/doctorats/fitxa/Biology.html'),
      (4, 'BSc Psychology', 'Bachelor in Psychology', 'BACHELOR', false, 78, 8000,
       3, 180, 'Portuguese', '2025-09-01', '2025-07-10', 'psychology@university4.edu', 'https://www.uva.nl/en/programmes/bachelors/psychology/psychology.html'),
      (4, 'MSc Artificial Intelligence', 'Master in Artificial Intelligence', 'MASTER', true, 83, 13500,
       2, 120, 'English', '2025-09-01', '2025-07-01', 'ai@university4.edu', 'https://www.uva.nl/en/programmes/masters/artificial-intelligence/artificial-intelligence.html'),
      (4, 'LLM International Law', 'Master of Laws in International Law', 'MASTER', false, 80, 12000,
       1, 60, 'English', '2025-09-01', '2025-07-20', 'law@university4.edu', 'https://www.uva.nl/en/programmes/masters/international-and-european-law/international-law.html'),
      (5, 'Licence Mathematics', 'Bachelor in Mathematics', 'BACHELOR', false, 80, 200,
       3, 180, 'French', '2025-09-01', '2025-07-01', 'math@university5.edu', 'https://sciences.sorbonne-universite.fr/en/formation/licence/licence-de-mathematiques'),
      (5, 'Master Lettres Modernes', 'Master in Modern Literature', 'MASTER', false, 75, 250,
       2, 120, 'French', '2025-09-01', '2025-07-05', 'literature@university5.edu', 'https://www.sorbonne-universite.fr/fr/formation/master-lettres-modernes'),
      (5, 'Doctorat Physics', 'Doctorate in Physics', 'DOCTORATE', false, 88, 380,
       4, 240, 'French', '2025-09-01', '2025-06-30', 'physics@university5.edu', 'https://sciences.sorbonne-universite.fr/en/formation/doctorat/doctorat-en-physique'),
      (6, 'BA Philosophy', 'Bachelor in Philosophy', 'BACHELOR', false, 72, 7000,
       3, 180, 'English', '2025-09-01', '2025-07-15', 'philosophy@university6.edu', 'https://www.univie.ac.at/en/studies/bachelor/philosophy/'),
      (6, 'MSc Data Science', 'Master in Data Science', 'MASTER', true, 78, 14000,
       2, 120, 'English', '2025-09-01', '2025-07-01', 'datascience@university6.edu', 'https://www.modul.ac.at/programmes/master/data-science/'),
      (6, 'PhD History', 'Doctorate in History', 'DOCTORATE', false, 80, 4200,
       4, 240, 'English', '2025-09-01', '2025-06-15', 'history@university6.edu', 'https://www.univie.ac.at/en/studies/doctorate/history/'),
      (7, 'BSc Medicine', 'Bachelor in Medicine', 'BACHELOR', false, 90, 15000,
       6, 360, 'English', '2025-09-01', '2025-07-01', 'medicine@university7.edu', 'https://sigarra.up.pt/up/en/web_base.gera_pagina?p_pagina=1034559'),
      (7, 'MSc Architecture', 'Master in Architecture', 'MASTER', false, 82, 11500,
       2, 120, 'English', '2025-09-01', '2025-07-05', 'architecture@university7.edu', 'https://sigarra.up.pt/up/en/web_base.gera_pagina?p_pagina=1037099'),
      (7, 'PhD Chemistry', 'Doctorate in Chemistry', 'DOCTORATE', false, 85, 4100,
       4, 240, 'English', '2025-09-01', '2025-06-30', 'chemistry@university7.edu', 'https://sigarra.up.pt/up/en/web_base.gera_pagina?p_pagina=1035114');


INSERT INTO lernia.course_area_of_study (course_id, area_of_study_id) VALUES
    (1, 1),
    (2, 2),
    (3, 7),
    (4, 1),
    (5, 2),
    (6, 2),
    (7, 3),
    (8, 3),
    (9, 7),
    (10, 6),
    (11, 1),
    (12, 5),
    (13, 7),
    (14, 6),
    (15, 7),
    (16, 6),
    (17, 1),
    (18, 6),
    (19, 4),
    (20, 6),
    (21, 7);

INSERT INTO lernia.curricular_units (course_id, name, description, credits, year, semester, hours) VALUES
    (1, 'Introduction to Programming', 'Fundamentals of programming', 6, 1, 1, 45),
    (1, 'Data Structures & Algorithms', 'Core CS concepts', 6, 2, 1, 45),
    (1, 'Software Engineering', 'Software development practices', 6, 3, 1, 45),

    (2, 'Aerodynamics', 'Principles of flight', 6, 1, 1, 50),
    (2, 'Aircraft Structures', 'Structural analysis', 6, 2, 1, 50),
    (2, 'Propulsion Systems', 'Engine design', 6, 3, 1, 50),

    (3, 'Biomaterials', 'Materials in biomedical applications', 8, 1, 1, 40),
    (3, 'Tissue Engineering', 'Regenerative medicine', 8, 2, 1, 40),
    (3, 'Research Thesis', 'Independent research', 20, 3, 1, 100),

    (4, 'Digital Logic Design', 'Computer hardware basics', 6, 1, 1, 45),
    (4, 'Computer Architecture', 'System design', 6, 2, 1, 45),
    (4, 'Embedded Systems', 'Hardware-software integration', 6, 3, 1, 45),

    (5, 'Power Systems', 'Electrical power networks', 6, 1, 1, 40),
    (5, 'Signal Processing', 'Digital signal analysis', 6, 1, 2, 40),
    (5, 'Master Thesis', 'Research project', 12, 2, 1, 60),

    (6, 'Advanced Mechanics', 'Mechanical systems theory', 8, 1, 1, 40),
    (6, 'Robotics', 'Autonomous systems', 8, 2, 1, 40),
    (6, 'Doctoral Dissertation', 'Original research', 20, 3, 1, 100),

    (7, 'Principles of Management', 'Management fundamentals', 6, 1, 1, 40),
    (7, 'Financial Accounting', 'Accounting basics', 6, 2, 1, 40),
    (7, 'Strategic Management', 'Business strategy', 6, 3, 1, 40),

    (8, 'Microeconomics', 'Market behavior', 6, 1, 1, 40),
    (8, 'Macroeconomics', 'National economy', 6, 1, 2, 40),
    (8, 'Econometrics', 'Economic analysis', 6, 2, 1, 40),

    (9, 'Molecular Biology', 'Cell biology fundamentals', 8, 1, 1, 40),
    (9, 'Genetics', 'Heredity and variation', 8, 2, 1, 40),
    (9, 'Dissertation', 'Biology research', 20, 3, 1, 100),

    (10, 'Introduction to Psychology', 'Psychology basics', 6, 1, 1, 40),
    (10, 'Cognitive Psychology', 'Mental processes', 6, 2, 1, 40),
    (10, 'Clinical Psychology', 'Mental health', 6, 3, 1, 40),

    (11, 'Machine Learning', 'ML fundamentals', 6, 1, 1, 45),
    (11, 'Deep Learning', 'Neural networks', 6, 1, 2, 45),
    (11, 'AI Research Project', 'Capstone project', 12, 2, 1, 60),

    (12, 'Public International Law', 'State relations', 6, 1, 1, 35),
    (12, 'Human Rights Law', 'Rights protection', 6, 1, 2, 35),
    (12, 'International Trade Law', 'Trade regulations', 6, 2, 1, 35),

    (13, 'Calculus I', 'Differential calculus', 6, 1, 1, 45),
    (13, 'Linear Algebra', 'Vector spaces', 6, 2, 1, 45),
    (13, 'Real Analysis', 'Advanced calculus', 6, 3, 1, 45),

    (14, 'French Literature', '19th-20th century texts', 6, 1, 1, 35),
    (14, 'Literary Theory', 'Critical approaches', 6, 1, 2, 35),
    (14, 'Research Memoir', 'Literary analysis', 12, 2, 1, 50),

    (15, 'Quantum Mechanics', 'Quantum theory', 8, 1, 1, 40),
    (15, 'Particle Physics', 'Elementary particles', 8, 2, 1, 40),
    (15, 'Physics Thesis', 'Original research', 20, 3, 1, 100),

    (16, 'History of Philosophy', 'Ancient to modern', 6, 1, 1, 35),
    (16, 'Ethics', 'Moral philosophy', 6, 2, 1, 35),
    (16, 'Metaphysics', 'Reality and existence', 6, 3, 1, 35),

    (17, 'Statistical Methods', 'Data analysis', 6, 1, 1, 45),
    (17, 'Big Data Technologies', 'Data engineering', 6, 1, 2, 45),
    (17, 'Data Science Project', 'Applied project', 12, 2, 1, 60),

    (18, 'Historical Methods', 'Research methodology', 8, 1, 1, 35),
    (18, 'European History', 'Modern Europe', 8, 2, 1, 35),
    (18, 'Historical Dissertation', 'Historical research', 20, 3, 1, 100),

    (19, 'Anatomy', 'Human body structure', 8, 1, 1, 60),
    (19, 'Physiology', 'Body functions', 8, 2, 1, 60),
    (19, 'Clinical Practice', 'Medical practice', 10, 5, 1, 80),

    (20, 'Architectural Design', 'Design principles', 8, 1, 1, 50),
    (20, 'Urban Planning', 'City design', 6, 1, 2, 40),
    (20, 'Design Thesis', 'Architectural project', 12, 2, 1, 60),

    (21, 'Organic Chemistry', 'Carbon compounds', 8, 1, 1, 45),
    (21, 'Physical Chemistry', 'Chemical physics', 8, 2, 1, 45),
    (21, 'Chemistry Dissertation', 'Chemical research', 20, 3, 1, 100);


INSERT INTO lernia.scholarships (university_id, name, description, amount, course_type) VALUES
    (1, 'Tech Merit', 'Academic excellence scholarship', 1500, 'BACHELOR'),
    (2, 'Business Grant', 'Support for business students', 2000, 'MASTER'),
    (3, 'Science Fellowship', 'For promising researchers', 3000, 'DOCTORATE');

INSERT INTO lernia.user_bookmarked_courses (user_id, course_id) VALUES (1, 1), (2, 2), (3, 3);

INSERT INTO lernia.user_bookmarked_universities (user_id, university_id) VALUES (1, 1), (2, 2);

INSERT INTO lernia.user_courses (user_id, course_id, start_date, end_date, is_finished) VALUES
    (1, 1, '2023-09-01', NULL, false),
    (1, 2, '2024-02-01', NULL, false),
    (2, 2, '2023-10-01', '2024-03-01', true),
    (3, 3, '2023-09-15', NULL, false),
    (4, 1, '2024-01-10', '2024-05-20', true),
    (5, 2, '2024-03-01', NULL, false),
    (5, 3, '2024-01-01', NULL, false);

INSERT INTO lernia.reviews (id, rating, title, description, review_date, user_id, dtype) VALUES
    (1, 4.5, 'Great course', 'Very informative.', CURRENT_DATE, 1, 'CourseReviewEntity'),
    (2, 3.9, 'Good course', 'Challenging but rewarding.', CURRENT_DATE, 2, 'CourseReviewEntity'),
    (3, 5.0, 'Excellent!', 'Highly recommended.', CURRENT_DATE, 3, 'CourseReviewEntity'),
    (4, 4.0, 'Well structured', 'Good materials.', CURRENT_DATE, 4, 'CourseReviewEntity'),
    (5, 3.5, 'Needs improvement', 'Could include more practice.', CURRENT_DATE, 5, 'CourseReviewEntity'),
    (6, 4.8, 'Loved it', 'Engaging and helpful.', CURRENT_DATE, 1, 'CourseReviewEntity');

INSERT INTO lernia.course_reviews (id, course_id) VALUES
    (1, 1), (2, 2), (3, 3), (4, 1), (5, 2), (6, 3);

INSERT INTO lernia.reviews (id, rating, title, description, review_date, user_id, dtype) VALUES
    (7, 4.7, 'Excellent university', 'Great environment.', CURRENT_DATE, 5, 'UniversityReviewEntity'),
    (8, 3.9, 'Good university', 'Solid programs.', CURRENT_DATE, 4, 'UniversityReviewEntity');

INSERT INTO lernia.university_reviews (id, university_id) VALUES (7, 1), (8, 2);
