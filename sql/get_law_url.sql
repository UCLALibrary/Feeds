--------------------------------------------------------
--  File created - Monday-April-25-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Function GET_LAW_URL
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "VGER_SUPPORT"."GET_LAW_URL" (
  p_bibid VGER_SUBFIELDS.UCLADB_BIB_SUBFIELD.RECORD_ID%TYPE
)
return nvarchar2 as 
	v_tag		NVARCHAR2(4000) := '';
	v_empty		NVARCHAR2(4000) := '';
begin
	SELECT subfield INTO v_tag FROM
  (
    SELECT subfield
    FROM vger_subfields.ucladb_bib_subfield 
    WHERE record_id = p_bibid AND tag = '856u'
  ) WHERE rownum = 1;
	
	RETURN v_tag;

  exception
    when no_data_found 
    then return v_empty;
end get_law_url;

/
