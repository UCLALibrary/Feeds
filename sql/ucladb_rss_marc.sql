--------------------------------------------------------
--  File created - Wednesday-April-20-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for View UCLADB_RSS_MARC
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VGER_SUPPORT"."UCLADB_RSS_MARC" ("SUBJECT", "LOCATION", "LOCATION_CODE", "CALL_NO_TYPE", "CALL_NUMBER", "SORT_CALL_NUMBER", "ADDED_DATE", "BIB_ID", "LANGUAGE_CODE", "LANGUAGE", "EDITION", "IMPRINT", "SERIES", "AUTHOR", "TITLE", "PUB_YEAR", "BIB_SUBJECTS") AS 
  SELECT 
	subject, 
	location, 
	location_code,
	call_no_type,
	call_number, 
	sort_call_number, 
	added_date, 
	bib_id, 
	language_code, 
	LANGUAGE, 
	edition, 
	imprint, 
	series, 
	author, 
	trim(title || ' ' || vger_subfields.Get880Field(bib_id, '245')) AS title, 
	pub_year, 
	vger_support.Get_Subjects(bib_id) AS bib_subjects
FROM 
	vger_support.ucladb_rss_subject_cn_marc
UNION
SELECT 
	subject, 
	location, 
	location_code,
	call_no_type,
	call_number, 
	sort_call_number, 
	added_date, bib_id, 
	language_code, 
	LANGUAGE, 
	edition, 
	imprint, 
	series, 
	author, 
	trim(title || ' ' || vger_subfields.Get880Field(bib_id, '245')) AS title,
	pub_year,
	vger_support.Get_Subjects(bib_id) AS bib_subjects
FROM 
	vger_support.ucladb_rss_subject_st_marc;
