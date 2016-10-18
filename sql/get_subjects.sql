create or replace function vger_support.get_subjects
(
  p_bib_id vger_subfields.ucladb_bib_subfield.record_id%type
)
return nvarchar2 as
  type cur_type is ref cursor;
  subjects_cur  cur_type;
  delimiter char(1) := '|';
  subjects  nvarchar2(2000) := '';
  a_subject nvarchar2(2000);
begin
  open subjects_cur for
    select distinct
      vger_subfields.GetFieldFromSubfields(record_id, field_seq) as subject_field
      from vger_subfields.ucladb_bib_subfield
      where record_id = p_bib_id
      and (tag like '650%' or tag like '651%')
      order by subject_field;
  
  subjects := '';
  loop
    fetch subjects_cur into a_subject;
    exit when subjects_cur%notfound;
    
    -- Bail out if concatenated string is getting too long
    -- Limit is 2000 characters, since this is nvarchar2 and NLS_NCHAR_CHARACTERSET = AL16UTF16
    -- Leave room for delimiter
    exit when length(subjects) + length(a_subject) > 1999;
    if subjects_cur%rowcount > 1 then
      subjects := subjects || delimiter;
    end if;
    subjects := subjects || a_subject;
  end loop;
  close subjects_cur;
  return ltrim(rtrim(subjects));
end get_subjects;
/

