--------------------------------------------------------
--  File created - Monday-April-25-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Function GET880FIELD
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "VGER_SUBFIELDS"."GET880FIELD" (
  p_record_id in vger_subfields.ucladb_bib_subfield.record_id%type
, p_tag in vger_subfields.ucladb_bib_subfield.tag%type
) return nvarchar2 as
  v_field_seq vger_subfields.ucladb_bib_subfield.field_seq%type;
  v_subfield vger_subfields.ucladb_bib_subfield.subfield%type;
  v_field nvarchar2(4000) := '';
  cursor subfield_cur is
    select subfield
    from vger_subfields.ucladb_bib_subfield
    where record_id = p_record_id
    and field_seq = v_field_seq
    and tag != '8806'
    order by subfield_seq;
begin
  select min(field_seq) into v_field_seq
    from vger_subfields.ucladb_bib_subfield
    where record_id = p_record_id
    and tag = '8806'
    and subfield like p_tag || '%';
    
  open subfield_cur;
  loop
    fetch subfield_cur into v_subfield;
    exit when subfield_cur%notfound;
    
    v_field := v_field || v_subfield || ' ';
  end loop;
  close subfield_cur;
  v_field := trim(v_field);
  --v_field := vger_subfields.GetFieldFromSubfields(p_record_id, v_field_seq);
  return v_field;
end Get880Field;

/
