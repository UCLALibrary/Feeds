--------------------------------------------------------
--  File created - Wednesday-April-20-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for View UCLADB_RSS_LAW2
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VGER_SUPPORT"."UCLADB_RSS_LAW2" ("LOCATION", "LOCATION_CODE", "CALL_NO_TYPE", "CALL_NUMBER", "SORT_CALL_NUMBER", "ADDED_DATE", "BIB_ID", "LANGUAGE_CODE", "LANGUAGE", "EDITION", "IMPRINT", "SERIES", "AUTHOR", "TITLE", "PUB_YEAR", "BIB_SUBJECTS", "URL") AS 
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
		Unifix(bt.series) AS series
	FROM 
		ucladb.mfhd_master mm
		INNER JOIN ucladb.mfhd_history mh ON mm.mfhd_id = mh.mfhd_id
		INNER JOIN ucladb.location l ON mm.location_id = l.location_id
		INNER JOIN ucladb.bib_mfhd bm ON mm.mfhd_id = bm.mfhd_id
		INNER JOIN ucladb.bib_text bt ON bm.bib_id = bt.bib_id
	WHERE 
		mh.operator_id = 'uclaloader'
		AND 
		(
			mm.mfhd_id IN (SELECT record_id FROM vger_subfields.ucladb_mfhd_subfield WHERE tag = '852b' AND subfield like '%lw%')
			OR
			mm.mfhd_id IN (SELECT ms1.record_id FROM vger_subfields.ucladb_mfhd_subfield ms1 inner join vger_subfields.ucladb_mfhd_subfield ms2 on ms1.record_id = ms2.record_id WHERE (ms1.tag = '852b' AND ms1.subfield = 'in') AND (ms2.tag = '852c' AND ms2.subfield like '%lw%'))
		)
		
),
shelfready AS
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
		Unifix(bt.series) AS series
	FROM 
		ucladb.mfhd_master mm
		INNER JOIN ucladb.mfhd_history mh ON mm.mfhd_id = mh.mfhd_id
                INNER JOIN ucladb.MFHD_ITEM mi ON mm.mfhd_id = mi.mfhd_id
                INNER JOIN ucladb.ITEM i ON mi.item_id = i.item_id
		INNER JOIN ucladb.location l ON mm.location_id = l.location_id
		INNER JOIN ucladb.bib_mfhd bm ON mm.mfhd_id = bm.mfhd_id
		INNER JOIN ucladb.bib_text bt ON bm.bib_id = bt.bib_id
	WHERE 
		i.create_operator_id = 'promptcat'
		AND 
		(
			mm.mfhd_id IN (SELECT record_id FROM vger_subfields.ucladb_mfhd_subfield WHERE tag = '852b' AND subfield like '%lw%')
			OR
			mm.mfhd_id IN (SELECT ms1.record_id FROM vger_subfields.ucladb_mfhd_subfield ms1 inner join vger_subfields.ucladb_mfhd_subfield ms2 on ms1.record_id = ms2.record_id WHERE (ms1.tag = '852b' AND ms1.subfield = 'in') AND (ms2.tag = '852c' AND ms2.subfield like '%lw%'))
		)
)
SELECT DISTINCT
	location,
	location_code,
	mfhds.call_no_type,
	call_number,
	sort_call_number,
	added_date,
	bib_id,
	language_code,
	mlc.LANGUAGE,
	COALESCE(ubs.subfield, vger_support.get_empty_nvarchar4k()) || vger_support.Get_250B_Tag(bib_id) AS edition,
	imprint,
	series,
	author,
	title,
	pub_year, 
	vger_support.Get_Subjects(bib_id) AS bib_subjects,
	vger_support.get_law_url(bib_id) AS url
FROM 
	mfhds 
	LEFT JOIN vger_support.marc_language_codes mlc ON language_code = mlc.code
	LEFT JOIN vger_subfields.ucladb_bib_subfield ubs ON bib_id = ubs.record_id AND ubs.tag = '250a'
WHERE
	trunc(added_date) >= trunc(sysdate - 30)
	--AND pub_year >= TO_NUMBER(TO_CHAR(sysdate - 730, 'YYYY'), '9999')
UNION
SELECT DISTINCT
	location,
	location_code,
	shelfready.call_no_type,
	call_number,
	sort_call_number,
	added_date,
	bib_id,
	language_code,
	mlc.LANGUAGE,
	COALESCE(ubs.subfield, vger_support.get_empty_nvarchar4k()) || vger_support.Get_250B_Tag(bib_id) AS edition,
	imprint,
	series,
	author,
	title,
	pub_year, 
	vger_support.Get_Subjects(bib_id) AS bib_subjects,
	vger_support.get_law_url(bib_id) AS url
FROM 
	shelfready 
	LEFT JOIN vger_support.marc_language_codes mlc ON language_code = mlc.code
	LEFT JOIN vger_subfields.ucladb_bib_subfield ubs ON  bib_id = ubs.record_id AND ubs.tag = '250a'
WHERE
	trunc(added_date) >= trunc(sysdate - 30)
	--AND pub_year >= TO_NUMBER(TO_CHAR(sysdate - 730, 'YYYY'), '9999');
