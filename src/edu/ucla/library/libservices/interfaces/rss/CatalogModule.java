package edu.ucla.library.libservices.interfaces.rss;

import com.sun.syndication.feed.module.Module;

public interface CatalogModule extends Module
{
  public static final String URI = "http://lit108.library.ucla.edu/rss/catalog/1.0";
  
  public String getPubYear();
  public void setPubYear(String pubYear);

  /**
   * @return
   */
  public String getAddedDate();
  public void setAddedDate(String addedDate);

  public String getBibId();
  public void setBibId(String bibId);
  
  public String getSubject();
  public void setSubject(String subject);

  public String getLocation();
  public void setLocation(String location);

  public String getCallNumber();
  public void setCallNumber(String callNumber);

  public String getAuthor();
  public void setAuthor(String author);

  public String getTitle();
  public void setTitle(String title);
  
  public String getSortCallNumber();
  public void setSortCallNumber(String sortCallNumber);
  
  public String getLanguageCode();
  public void setLanguageCode(String languageCode);
}