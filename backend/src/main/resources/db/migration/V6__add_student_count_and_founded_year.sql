ALTER TABLE lernia.universities
	ADD COLUMN IF NOT EXISTS student_count INTEGER;

ALTER TABLE lernia.universities
	ADD COLUMN IF NOT EXISTS founded_year INTEGER;

UPDATE lernia.universities
SET student_count = CASE name
		WHEN 'Imperial College London' THEN 19000
		WHEN 'Technical University of Berlin' THEN 34000
		WHEN 'Universitat de Barcelona' THEN 63000
		WHEN 'University of Amsterdam' THEN 41000
		WHEN 'Sorbonne University' THEN 43000
		WHEN 'University of Vienna' THEN 89000
		WHEN 'University of Porto' THEN 32000
		WHEN 'ETH Zurich' THEN 24000
		WHEN 'University of Copenhagen' THEN 38000
		WHEN 'University of Oslo' THEN 28000
		WHEN 'Charles University' THEN 43000
		WHEN 'Eötvös Loránd University' THEN 28000
		WHEN 'Sapienza University of Rome' THEN 70000
		WHEN 'Stockholm University' THEN 33000
		WHEN 'Trinity College Dublin' THEN 19000
		WHEN 'University of Helsinki' THEN 31000
		WHEN 'University of Warsaw' THEN 45000
		ELSE COALESCE(student_count, 0)
	END,
	founded_year = CASE name
		WHEN 'Imperial College London' THEN 1907
		WHEN 'Technical University of Berlin' THEN 1879
		WHEN 'Universitat de Barcelona' THEN 1450
		WHEN 'University of Amsterdam' THEN 1632
		WHEN 'Sorbonne University' THEN 2018
		WHEN 'University of Vienna' THEN 1365
		WHEN 'University of Porto' THEN 1911
		WHEN 'ETH Zurich' THEN 1855
		WHEN 'University of Copenhagen' THEN 1479
		WHEN 'University of Oslo' THEN 1811
		WHEN 'Charles University' THEN 1348
		WHEN 'Eötvös Loránd University' THEN 1635
		WHEN 'Sapienza University of Rome' THEN 1303
		WHEN 'Stockholm University' THEN 1878
		WHEN 'Trinity College Dublin' THEN 1592
		WHEN 'University of Helsinki' THEN 1640
		WHEN 'University of Warsaw' THEN 1816
		ELSE COALESCE(founded_year, 0)
	END;

ALTER TABLE lernia.universities
	ALTER COLUMN student_count SET DEFAULT 0;

ALTER TABLE lernia.universities
	ALTER COLUMN founded_year SET DEFAULT 0;
