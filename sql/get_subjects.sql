--------------------------------------------------------
--  File created - Monday-April-25-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Function GET_SUBJECTS
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "VGER_SUPPORT"."GET_SUBJECTS" 
(
  p_bibid VGER_SUBFIELDS.UCLADB_BIB_SUBFIELD.RECORD_ID%TYPE
)
RETURN NVARCHAR2 AS
	TYPE cur_type IS REF CURSOR;
	subjects_cur	cur_type;
	delimiter		CHAR(1) := '|';
	subjects		NVARCHAR2(4000) := '';
	a_subject		NVARCHAR2(4000);
BEGIN
	OPEN subjects_cur FOR
		'SELECT DISTINCT vger_subfields.GetFieldFromSubfields(record_id, field_seq) AS subject_field FROM vger_subfields.ucladb_bib_subfield WHERE record_id = :r AND (tag LIKE ''650%'' OR tag LIKE ''651%'') ORDER BY subject_field'
		USING p_bibid;
	subjects := '';
	LOOP
		FETCH subjects_cur INTO a_subject;
		EXIT WHEN subjects_cur%NOTFOUND;

		IF subjects_cur%ROWCOUNT > 1 THEN
			subjects := subjects || delimiter;
		END IF;
		subjects := subjects || a_subject;
	END LOOP;
	CLOSE subjects_cur;
	RETURN LTRIM(RTRIM(subjects));
END;
 

/
