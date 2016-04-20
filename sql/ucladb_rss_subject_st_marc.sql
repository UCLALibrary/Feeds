--------------------------------------------------------
--  File created - Wednesday-April-20-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for View UCLADB_RSS_SUBJECT_ST_MARC
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VGER_SUPPORT"."UCLADB_RSS_SUBJECT_ST_MARC" ("SUBJECT", "LOCATION", "LOCATION_CODE", "CALL_NO_TYPE", "CALL_NUMBER", "SORT_CALL_NUMBER", "ADDED_DATE", "BIB_ID", "LANGUAGE_CODE", "LANGUAGE", "EDITION", "IMPRINT", "SERIES", "AUTHOR", "TITLE", "PUB_YEAR") AS 
  WITH mfhds AS 
(
	SELECT
		mm.mfhd_id,
		mm.call_no_type,
		mm.normalized_call_no AS sort_call_number,
		mm.display_call_no AS call_number,
		mh.action_date AS added_date,
		l.location_display_name AS location,
		l.location_code,
		bt.bib_id,
		bt.LANGUAGE AS language_code,
		Unifix(bt.author) AS author,
		Unifix(bt.title) AS title,
		TO_NUMBER(TRANSLATE(LOWER(bt.begin_pub_date), 'abcdefghijklmnopqrstuvwxyz', '00000000000000000000000000'), '9999')  AS pub_year,
		Unifix(bt.imprint) AS imprint,
		Unifix(bt.series) AS series,
		CASE
			WHEN contains(h.normal_heading, 'AFRICAN AMERICAN%') > 0 THEN 'African-American Studies'
			WHEN contains(h.normal_heading, 'ASIAN AMERICAN%') > 0 THEN 'Asian American Studies'
			WHEN contains(h.normal_heading, 'BIOMEDICAL ENGINEERING | BIOENGINEERING') > 0 THEN 'Bioengineering'
			WHEN contains(h.normal_heading, 'MEXICAN AMERICAN%') > 0 THEN 'Chicano/a Studies'
			WHEN contains(h.normal_heading, 'DANCE | DANCING') > 0 THEN 'Dance'
			WHEN contains(h.normal_heading, 'DENTAL | DENTISTRY') > 0 THEN 'Dentistry'
			WHEN contains(h.normal_heading, 'HUMANITIES') > 0 THEN 'Humanities'
			WHEN contains(h.normal_heading, 'AGRICULTURAL ASSISTANCE | ECONOMIC ASSISTANCE | ECONOMIC DEVELOPMENT | EDUCATIONAL ASSISTANCE | SUSTAINABLE DEVELOPMENT | TECHNICAL ASSISTANCE | WOMEN IN DEVELOPMENT') > 0 THEN 'International Development Studies'
			WHEN contains(h.normal_heading, 'BISEXUAL% | GAY | GAYS | HOMOSEXUALITY | LESBIAN% | SEXUAL MINORIT% | TRANSSEXUAL%') > 0 THEN 'Lesbian, Gay, Bisexual, and Transgender Studies'
			WHEN contains(h.normal_heading, 'MICROBIOLOGICAL | MICROBIOLOGY | MOLECULAR GENETICS') > 0 THEN 'Microbiology and Molecular Genetics'
			WHEN contains(h.normal_heading, 'CYTOLOGY | DEVELOPMENTAL BIOLOGY | MOLECULAR ASPECTS | MOLECULAR BIOLOGY') > 0 THEN 'Molecular, Cell, and Developmental Biology'
			WHEN contains(h.normal_heading, 'NURSING') > 0 THEN 'Nursing'
			WHEN contains(h.normal_heading, 'COMMERCIAL POLICY | CULTURAL POLICY | ECONOMIC POLICY | ENERGY POLICY | ENVIRONMENTAL POLICY | FAMILY POLICY | GOVERNMENT POLICY | HOUSING POLICY | INDUSTRIAL POLICY | INFORMATION POLICY | LABOR POLICY | MILITARY POLICY | POPULATION POLICY | SOCIAL POLICY | TELECOMMUNICATION POLICY | URBAN POLICY | POLICY SCIENCES') > 0 THEN 'Policy Studies'
		END AS subject
	FROM 
		ucladb.mfhd_master mm
		INNER JOIN ucladb.mfhd_history mh ON mm.mfhd_id = mh.mfhd_id
		INNER JOIN ucladb.location l ON mm.location_id = l.location_id
		INNER JOIN ucladb.bib_mfhd bm ON mm.mfhd_id = bm.mfhd_id
		INNER JOIN ucladb.bib_text bt ON bm.bib_id = bt.bib_id
		INNER JOIN ucladb.bib_heading bh ON bm.bib_id = bh.bib_id
		INNER JOIN ucladb.heading h ON bh.heading_id = h.heading_id
	WHERE 
		mh.operator_id = 'uclaloader'
		AND h.index_type = 'S' -- Subject
		AND h.heading_type IN ('a', 'c') -- a = lcsh, c = mesh
)
SELECT DISTINCT
	mfhds.subject,
	mfhds.location,
	mfhds.location_code,
	mfhds.call_no_type,
	mfhds.call_number,
	mfhds.sort_call_number,
	mfhds.added_date,
	mfhds.bib_id,
	mfhds.language_code,
	mlc.LANGUAGE,
	COALESCE(ubs.subfield, Cast('' AS NVARCHAR2(4000))) || vger_support.Get_250B_Tag(mfhds.bib_id) AS edition,
	mfhds.imprint,
	mfhds.series,
	mfhds.author,
	mfhds.title,
	mfhds.pub_year
FROM 
	mfhds
	LEFT JOIN vger_support.MARC_LANGUAGE_CODES mlc ON mfhds.language_code = mlc.code
	LEFT JOIN vger_subfields.UCLADB_BIB_SUBFIELD ubs ON mfhds.bib_id = ubs.record_id AND ubs.tag = '250a'
WHERE 
	subject IS NOT NULL
 ;
