package edu.ucla.library.libservices.beans;

import java.util.Date;

public class SubjectBean
{
  private String subject;
  private String location;
  private String call_number;
  private String sort_call_number;
  private Date added_date;
  private String bib_id;
  private String language_code;
  private String language;
  private String edition;
  private String imprint;
  private String series;
  private String author;
  private String title;
  private String pub_year;
  private String bib_subjects;

  public SubjectBean()
  {
  }

  public void setSubject( String subject )
  {
    this.subject = subject;
  }

  public String getSubject()
  {
    return subject;
  }

  public void setLocation( String location )
  {
    this.location = location;
  }

  public String getLocation()
  {
    return location;
  }

  public void setCall_number( String call_number )
  {
    this.call_number = call_number;
  }

  public String getCall_number()
  {
    return call_number;
  }

  public void setSort_call_number( String sort_call_number )
  {
    this.sort_call_number = sort_call_number;
  }

  public String getSort_call_number()
  {
    return sort_call_number;
  }

  public void setAdded_date( Date added_date )
  {
    this.added_date = added_date;
  }

  public Date getAdded_date()
  {
    return added_date;
  }

  public void setBib_id( String bib_id )
  {
    this.bib_id = bib_id;
  }

  public String getBib_id()
  {
    return bib_id;
  }

  public void setLanguage_code( String language_code )
  {
    this.language_code = language_code;
  }

  public String getLanguage_code()
  {
    return language_code;
  }

  public void setLanguage( String language )
  {
    this.language = language;
  }

  public String getLanguage()
  {
    return language;
  }

  public void setEdition( String edition )
  {
    this.edition = edition;
  }

  public String getEdition()
  {
    return edition;
  }

  public void setImprint( String imprint )
  {
    this.imprint = imprint;
  }

  public String getImprint()
  {
    return imprint;
  }

  public void setSeries( String series )
  {
    this.series = series;
  }

  public String getSeries()
  {
    return series;
  }

  public void setAuthor( String author )
  {
    this.author = author;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setTitle( String title )
  {
    this.title = title;
  }

  public String getTitle()
  {
    return title;
  }

  public void setPub_year( String pub_year )
  {
    this.pub_year = pub_year;
  }

  public String getPub_year()
  {
    return pub_year;
  }

  public void setBib_subjects( String bib_subjects )
  {
    this.bib_subjects = bib_subjects;
  }

  public String getBib_subjects()
  {
    return bib_subjects;
  }
  public boolean isEmpty(String field)
  {
    return ( field == null || field.equals("") || field.length() == 0 );
  }
  public boolean isEmpty(Object field)
  {
    return ( field == null || isEmpty( field.toString() ) );
  }
}

