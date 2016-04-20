--------------------------------------------------------
--  File created - Wednesday-April-20-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for View UCLADB_RSS_SUBJECT_CN_MARC
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VGER_SUPPORT"."UCLADB_RSS_SUBJECT_CN_MARC" ("SUBJECT", "LOCATION", "LOCATION_CODE", "CALL_NO_TYPE", "CALL_NUMBER", "SORT_CALL_NUMBER", "ADDED_DATE", "BIB_ID", "LANGUAGE_CODE", "LANGUAGE", "EDITION", "IMPRINT", "SERIES", "AUTHOR", "TITLE", "PUB_YEAR") AS 
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
)
SELECT DISTINCT
	cnsm.subject,
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
	INNER JOIN vger_support.CALL_NUMBER_SUBJECT_MAP cnsm ON mfhds.call_no_type = cnsm.call_no_type AND ( (mfhds.sort_call_number BETWEEN cnsm.norm_call_no_start AND cnsm.norm_call_no_end) OR (mfhds.sort_call_number LIKE cnsm.norm_call_no_end || '%') ) AND cnsm.subject_type = 'ERDB'
	LEFT JOIN vger_support.MARC_LANGUAGE_CODES mlc ON mfhds.language_code = mlc.code
	LEFT JOIN vger_subfields.UCLADB_BIB_SUBFIELD ubs ON  mfhds.bib_id = ubs.record_id AND ubs.tag = '250a'
 ;
