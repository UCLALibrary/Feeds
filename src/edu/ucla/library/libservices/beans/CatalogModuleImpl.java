package edu.ucla.library.libservices.beans;

import com.sun.syndication.feed.module.ModuleImpl;
import edu.ucla.library.libservices.interfaces.rss.CatalogModule;

public class CatalogModuleImpl
  extends ModuleImpl implements CatalogModule
{
  private String pubYear;
  private String addedDate;
  private String bibId;
  private String subject;
  private String location;
  private String callNumber;
  private String author;
  private String title;
  private String sortCallNumber;
  private String languageCode;

  public CatalogModuleImpl()
  {
    super(CatalogModule.class,CatalogModule.URI);
  }

  /**
   * @return
   */
  public Class getInterface()
  {
    return CatalogModule.class;
  }

  public void copyFrom( Object object )
  {
    CatalogModule cm = (CatalogModule) object;
    setPubYear(cm.getPubYear());
    setAddedDate(cm.getAddedDate());
    setBibId(cm.getBibId());
    setSubject(cm.getSubject());
    setLocation(cm.getLocation());
    setCallNumber(cm.getCallNumber());
    setAuthor(cm.getAuthor());
    setTitle(cm.getTitle());
    setSortCallNumber(cm.getSortCallNumber());
    setLanguageCode(cm.getLanguageCode());
  }

  public String getPubYear()
  {
    return pubYear;
  }

  public void setPubYear( String pubYear )
  {
    this.pubYear = pubYear;
  }

  public String getAddedDate()
  {
    return addedDate;
  }

  public void setAddedDate( String addedDate )
  {
    this.addedDate = addedDate;
  }

  public String getBibId()
  {
    return bibId;
  }

  public void setBibId( String bibId )
  {
    this.bibId = bibId;
  }

  public String getSubject()
  {
    return subject;
  }
  public void setSubject(String subject)
  {
    this.subject = subject;
  }

  public String getLocation()
  {
    return location;
  }
  public void setLocation(String location)
  {
    this.location = location;
  }

  public String getCallNumber()
  {
    return callNumber;
  }
  public void setCallNumber(String callNumber)
  {
    this.callNumber = callNumber;
  }

  public String getAuthor()
  {
    return author;
  }
  public void setAuthor(String author)
  {
    this.author = author;
  }

  public String getTitle()
  {
    return title;
  }
  public void setTitle(String title)
  {
    this.title = title;
  }
  public String getSortCallNumber()
  {
    return sortCallNumber;
  }
  public void setSortCallNumber(String sortCallNumber)
  {
    this.sortCallNumber = sortCallNumber;
  }
  public String getLanguageCode()
  {
    return languageCode;
  }
  public void setLanguageCode(String languageCode)
  {
    this.languageCode = languageCode;
  }
}
