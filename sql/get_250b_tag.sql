create or replace function vger_support.get_250b_tag 
(
  p_bibid vger_subfields.ucladb_bib_subfield.record_id%type
)
return nvarchar2 as
  v_tag   nvarchar2(2000) := '';
  v_empty nvarchar2(2000) := '';
begin
  select coalesce(subfield, v_empty) into v_tag
  from vger_subfields.ucladb_bib_subfield
  where record_id = p_bibid and tag = '250b'
  and rownum < 2;
  
  return v_tag;
end get_250b_tag;
/
