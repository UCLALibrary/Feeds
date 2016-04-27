CREATE OR REPLACE VIEW vger_support.ucladb_rss_law2 (
  location, location_code, call_no_type, call_number, sort_call_number, added_date, bib_id, language_code, language, edition, imprint, series, author, title, pub_year, bib_subjects, url
) AS
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
			l.location_code like 'lw%'
			OR
      (
        l.location_code = 'in' 
        and exists (
          select *
          from vger_subfields.ucladb_mfhd_subfield
          where record_id = mm.mfhd_id
          and tag = '852c'
          and subfield like 'lw%'
        )
      )
		)
), -- end of mfhds
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
			l.location_code like 'lw%'
			OR
      (
        l.location_code = 'in' 
        and exists (
          select *
          from vger_subfields.ucladb_mfhd_subfield
          where record_id = mm.mfhd_id
          and tag = '852c'
          and subfield like 'lw%'
        )
      )
		)
) -- end of shelfready
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
;
/

