--------------------------------------------------------
--  File created - Monday-April-25-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Function GET_250B_TAG
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "VGER_SUPPORT"."GET_250B_TAG" 
(
  p_bibid VGER_SUBFIELDS.UCLADB_BIB_SUBFIELD.RECORD_ID%TYPE
)
RETURN NVARCHAR2 AS
	v_tag		NVARCHAR2(4000) := '';
	v_empty		NVARCHAR2(4000) := '';
BEGIN              
	SELECT COALESCE(subfield, v_empty) INTO v_tag
	FROM vger_subfields.ucladb_bib_subfield 
	WHERE record_id = p_bibid AND tag = '250b';
	
	RETURN v_tag;
END;
 

/
