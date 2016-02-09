package edu.ucla.library.libservices.beans;

import java.util.Date;

public class NewAcquisition
{
  private String location;
  private String bib_id;
  private String title;
  private String pub_year;
  private String display_call_no;
  private Date added_date;

  public NewAcquisition()
  {
  }

  /**
   * @param location
   */
  public void setLocation( String location )
  {
    this.location = location;
  }

  public String getLocation()
  {
    return location;
  }

  public void setBib_id( String bib_id )
  {
    this.bib_id = bib_id;
  }

  public String getBib_id()
  {
    return bib_id;
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

  public void setDisplay_call_no( String display_call_no )
  {
    this.display_call_no = display_call_no;
  }

  public String getDisplay_call_no()
  {
    return display_call_no;
  }

  public void setAdded_date( Date added_date )
  {
    this.added_date = added_date;
  }

  public Date getAdded_date()
  {
    return added_date;
  }
}
