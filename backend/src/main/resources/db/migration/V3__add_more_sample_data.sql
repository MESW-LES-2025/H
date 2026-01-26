INSERT INTO lernia.locations (city, country, cost_of_living) VALUES
    ('Zurich', 'Switzerland', 1800),
    ('Copenhagen', 'Denmark', 1600),
    ('Oslo', 'Norway', 1700),
    ('Prague', 'Czech Republic', 850),
    ('Budapest', 'Hungary', 700),
    ('Rome', 'Italy', 1100),
    ('Stockholm', 'Sweden', 1500),
    ('Dublin', 'Ireland', 1400),
    ('Helsinki', 'Finland', 1450),
    ('Warsaw', 'Poland', 650);


INSERT INTO lernia.universities (name, description, location_id, contact_info, website, address, logo) VALUES
    ('ETH Zurich', 'World-leading science and engineering university', 8, '+41-44-632-11-11', 'https://ethz.ch', 'Rämistrasse 101, 8092 Zürich', 'https://lernia-s3.s3.eu-north-1.amazonaws.com/eth-zurich.png'),
    ('University of Copenhagen', 'Top Scandinavian research university', 9, '+45-35-32-26-26', 'https://ku.dk', 'Nørregade 10, Copenhagen', 'https://lernia-s3.s3.eu-north-1.amazonaws.com/university-copenhagen.png'),
    ('University of Oslo', 'Leading Norwegian public university', 10, '+47-22-85-50-50', 'https://www.uio.no', 'Problemveien 7, Oslo', 'https://lernia-s3.s3.eu-north-1.amazonaws.com/oslo.png'),
    ('Charles University', 'Oldest university in Central Europe', 11, '+420-221-111-111', 'https://cuni.cz', 'Ovocný trh 3–5, Prague', 'https://lernia-s3.s3.eu-north-1.amazonaws.com/charles.png'),
    ('Eötvös Loránd University', 'Prestigious Hungarian university in Budapest', 12, '+36-1-411-6500', 'https://www.elte.hu', 'Egyetem tér 1-3, Budapest', 'https://lernia-s3.s3.eu-north-1.amazonaws.com/elte.png'),
    ('Sapienza University of Rome', 'One of Europes largest universities', 13, '+39-06-4991', 'https://www.uniroma1.it', 'Piazzale Aldo Moro 5, Rome', 'https://lernia-s3.s3.eu-north-1.amazonaws.com/sapienza.png'),
    ('Stockholm University', 'Top Swedish research university', 14, '+46-8-16-20-00', 'https://www.su.se', 'Universitetsvägen 10A, Stockholm', 'https://lernia-s3.s3.eu-north-1.amazonaws.com/stockholm.png'),
    ('Trinity College Dublin', 'Ireland’s oldest university', 15, '+353-1-896-1000', 'https://www.tcd.ie', 'College Green, Dublin', 'https://lernia-s3.s3.eu-north-1.amazonaws.com/trinity.png'),
    ('University of Helsinki', 'Top Finnish university', 16, '+358-2941-911', 'https://www.helsinki.fi', 'Yliopistonkatu 4, Helsinki', 'https://lernia-s3.s3.eu-north-1.amazonaws.com/helsinki.png'),
    ('University of Warsaw', 'Largest university in Poland', 17, '+48-22-55-20-00', 'https://www.uw.edu.pl', 'Krakowskie Przedmieście 26/28, Warsaw', 'https://lernia-s3.s3.eu-north-1.amazonaws.com/warsaw.png');
INSERT INTO lernia.campuses (university_id, name, description, country, city, capacity) VALUES
    (8, 'ETH Main Campus', 'Central Zurich campus', 'Switzerland', 'Zurich', 21000),
    (9, 'City Campus', 'Copenhagen historic district', 'Denmark', 'Copenhagen', 32000),
    (10, 'Blindern Campus', 'Major Oslo campus', 'Norway', 'Oslo', 28000),
    (11, 'Historic Prague Campus', 'Medieval university buildings', 'Czech Republic', 'Prague', 42000),
    (12, 'Lágymányos Campus', 'Modern Budapest campus', 'Hungary', 'Budapest', 33000),
    (13, 'Sapienza Central Campus', 'Main Roman academic center', 'Italy', 'Rome', 70000),
    (14, 'Frescati Campus', 'Stockholm academic district', 'Sweden', 'Stockholm', 36000),
    (15, 'Trinity Central', 'Historic Dublin campus', 'Ireland', 'Dublin', 19000),
    (16, 'City Centre Campus', 'University of Helsinki main campus', 'Finland', 'Helsinki', 26000),
    (17, 'Main Warsaw Campus', 'Central Polish academic hub', 'Poland', 'Warsaw', 45000);

INSERT INTO lernia.courses (
    university_id, name, description, course_type,
    is_remote, min_admission_grade, cost,
    duration, credits, language,
    start_date, application_deadline, contact_email, website
) VALUES
-- ETH Zurich
(8, 'BSc Electrical Engineering', 'Undergraduate EE program', 'BACHELOR', false, 90, 16000, 3, 180, 'English', '2025-09-01', '2025-07-01', 'ee@ethz.ch', 'https://ethz.ch/ee'),
(8, 'MSc Robotics', 'Master in robotics and intelligent systems', 'MASTER', true, 85, 21000, 2, 120, 'English', '2025-09-01', '2025-06-15', 'robotics@ethz.ch', 'https://ethz.ch/robotics'),
(8, 'PhD Computer Vision', 'Doctorate in visual computing', 'DOCTORATE', false, 92, 0, 4, 240, 'English', '2025-10-01', '2025-07-30', 'cv@ethz.ch', 'https://ethz.ch/cv'),

-- University of Copenhagen
(9, 'BA Anthropology', 'Anthropological studies program', 'BACHELOR', false, 75, 12000, 3, 180, 'Danish', '2025-09-01', '2025-07-01', 'anthro@ku.dk', 'https://ku.dk/anthropology'),
(9, 'MSc Computer Science', 'Advanced CS program', 'MASTER', true, 80, 14000, 2, 120, 'English', '2025-09-01', '2025-07-10', 'cs@ku.dk', 'https://ku.dk/cs'),
(9, 'PhD Linguistics', 'Doctorate in linguistics', 'DOCTORATE', false, 88, 0, 4, 240, 'English', '2025-10-01', '2025-08-01', 'ling@ku.dk', 'https://ku.dk/linguistics'),

-- University of Oslo
(10, 'BA Political Science', 'Intro to political systems', 'BACHELOR', false, 78, 10000, 3, 180, 'Norwegian', '2025-09-01', '2025-07-01', 'polsci@uio.no', 'https://uio.no/politics'),
(10, 'MSc Data Analytics', 'Master in big data and analytics', 'MASTER', true, 80, 13500, 2, 120, 'English', '2025-09-01', '2025-07-15', 'data@uio.no', 'https://uio.no/data'),
(10, 'PhD Sociology', 'Research in social structures', 'DOCTORATE', false, 84, 0, 4, 240, 'English', '2025-10-01', '2025-08-01', 'soc@uio.no', 'https://uio.no/sociology'),

-- Charles University (Prague)
(11, 'BA History', 'European and global history studies', 'BACHELOR', false, 70, 4000, 3, 180, 'Czech', '2025-09-01', '2025-07-01', 'history@cuni.cz', 'https://cuni.cz/history'),
(11, 'MSc Mathematics', 'Advanced mathematical methods', 'MASTER', true, 78, 5000, 2, 120, 'English', '2025-09-01', '2025-07-20', 'math@cuni.cz', 'https://cuni.cz/math'),
(11, 'PhD Philosophy', 'Doctorate in philosophy', 'DOCTORATE', false, 85, 0, 4, 240, 'English', '2025-10-01', '2025-08-05', 'philosophy@cuni.cz', 'https://cuni.cz/philosophy'),

-- ELTE Budapest
(12, 'BA Psychology', 'Undergraduate psychology program', 'BACHELOR', false, 72, 3500, 3, 180, 'Hungarian', '2025-09-01', '2025-07-01', 'psych@elte.hu', 'https://elte.hu/psych'),
(12, 'MSc Software Engineering', 'Modern SE training', 'MASTER', true, 78, 6000, 2, 120, 'English', '2025-09-01', '2025-07-10', 'se@elte.hu', 'https://elte.hu/se'),
(12, 'PhD Anthropology', 'Doctorate in anthropology', 'DOCTORATE', false, 82, 0, 4, 240, 'English', '2025-10-01', '2025-08-01', 'anthro@elte.hu', 'https://elte.hu/anthro'),

-- Sapienza University Rome
(13, 'BA Architecture', 'Foundations of architecture', 'BACHELOR', false, 75, 2500, 3, 180, 'Italian', '2025-09-01', '2025-07-01', 'arch@sapienza.it', 'https://uniroma1.it/architecture'),
(13, 'MSc Machine Learning', 'Advanced ML master', 'MASTER', true, 82, 8500, 2, 120, 'English', '2025-09-01', '2025-07-15', 'ml@sapienza.it', 'https://uniroma1.it/ml'),
(13, 'PhD Astrophysics', 'Research in cosmology', 'DOCTORATE', false, 90, 0, 4, 240, 'English', '2025-10-01', '2025-08-10', 'astro@sapienza.it', 'https://uniroma1.it/astro'),

-- Stockholm University
(14, 'BA Economics', 'Introductory economics program', 'BACHELOR', false, 76, 6500, 3, 180, 'Swedish', '2025-09-01', '2025-07-01', 'eco@su.se', 'https://su.se/economics'),
(14, 'MSc Climate Science', 'Science of climate change', 'MASTER', true, 80, 11000, 2, 120, 'English', '2025-09-01', '2025-07-15', 'climate@su.se', 'https://su.se/climate'),
(14, 'PhD Marine Biology', 'Marine ecosystems research', 'DOCTORATE', false, 85, 0, 4, 240, 'English', '2025-10-01', '2025-08-15', 'marine@su.se', 'https://su.se/marine'),

-- Trinity College Dublin
(15, 'BA English Literature', 'Classical literature studies', 'BACHELOR', false, 78, 5000, 3, 180, 'English', '2025-09-01', '2025-07-01', 'lit@tcd.ie', 'https://tcd.ie/lit'),
(15, 'MSc Cybersecurity', 'Advanced cybersecurity master', 'MASTER', true, 85, 12000, 2, 120, 'English', '2025-09-01', '2025-07-20', 'cyber@tcd.ie', 'https://tcd.ie/cyber'),
(15, 'PhD Neuroscience', 'Neuro research doctorate', 'DOCTORATE', false, 90, 0, 4, 240, 'English', '2025-10-01', '2025-08-15', 'neuro@tcd.ie', 'https://tcd.ie/neuro'),

-- University of Helsinki
(16, 'BA Education', 'Teacher training program', 'BACHELOR', false, 74, 4000, 3, 180, 'Finnish', '2025-09-01', '2025-07-01', 'edu@helsinki.fi', 'https://helsinki.fi/education'),
(16, 'MSc Bioinformatics', 'Computational biology studies', 'MASTER', true, 84, 9000, 2, 120, 'English', '2025-09-01', '2025-07-10', 'bioinfo@helsinki.fi', 'https://helsinki.fi/bioinfo'),
(16, 'PhD Ecology', 'Advanced ecosystem research', 'DOCTORATE', false, 88, 0, 4, 240, 'English', '2025-10-01', '2025-08-10', 'eco@helsinki.fi', 'https://helsinki.fi/ecology'),

-- University of Warsaw
(17, 'BA Journalism', 'Media and communication studies', 'BACHELOR', false, 70, 3000, 3, 180, 'Polish', '2025-09-01', '2025-07-01', 'journalism@uw.edu.pl', 'https://uw.edu.pl/journalism'),
(17, 'MSc Artificial Intelligence', 'AI master program', 'MASTER', true, 82, 7000, 2, 120, 'English', '2025-09-01', '2025-07-15', 'ai@uw.edu.pl', 'https://uw.edu.pl/ai'),
(17, 'PhD Mathematics', 'Doctorate in mathematics', 'DOCTORATE', false, 88, 0, 4, 240, 'English', '2025-10-01', '2025-08-20', 'math@uw.edu.pl', 'https://uw.edu.pl/math');

INSERT INTO lernia.scholarships (university_id, name, description, amount, course_type) VALUES
    (8, 'Engineering Excellence', 'Merit scholarship for top engineers', 2500, 'BACHELOR'),
    (9, 'Research Innovators Award', 'Support for research students', 3000, 'MASTER'),
    (10, 'Nordic Fellowship', 'Doctorate funding for Nordic students', 3500, 'DOCTORATE'),
    (11, 'Central Europe Grant', 'Support for international students', 1500, 'MASTER'),
    (12, 'Budapest Bright Minds', 'Local academic achievement award', 1200, 'BACHELOR'),
    (15, 'Trinity Global Scholars', 'International talent scholarship', 4000, 'MASTER'),
    (17, 'Warsaw Technology Fellowship', 'Support for AI research students', 2500, 'MASTER');

INSERT INTO lernia.reviews (id, rating, title, description, review_date, user_id, dtype) VALUES
    (9, 4.6, 'Top Engineering School', 'ETH Zurich is unmatched.', CURRENT_DATE, 1, 'UniversityReviewEntity'),
    (10, 4.2, 'Great Student Life', 'Copenhagen offers amazing culture.', CURRENT_DATE, 2, 'UniversityReviewEntity'),
    (11, 4.8, 'Fantastic Professors', 'Oslo faculty are brilliant.', CURRENT_DATE, 3, 'UniversityReviewEntity'),
    (12, 3.9, 'Beautiful Campus', 'Prague campus is stunning.', CURRENT_DATE, 4, 'UniversityReviewEntity'),
    (13, 4.4, 'High Academic Standards', 'Budapest programs are demanding.', CURRENT_DATE, 5, 'UniversityReviewEntity');

INSERT INTO lernia.university_reviews (id, university_id) VALUES
    (9, 8), (10, 9), (11, 10), (12, 11), (13, 12);

SELECT setval('lernia.reviews_id_seq', (SELECT MAX(id) FROM lernia.reviews));