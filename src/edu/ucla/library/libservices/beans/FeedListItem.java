package edu.ucla.library.libservices.beans;

public class FeedListItem implements Comparable
{
  private String fileName;
  private String displayName;
  
  public FeedListItem()
  {
  }

  public void setFileName( String fileName )
  {
    this.fileName = fileName;
  }

  public String getFileName()
  {
    return fileName;
  }

  /**
   * @param displayName
   */
  public void setDisplayName( String displayName )
  {
    this.displayName = displayName;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public int compareTo( Object o )
  {
    if ( ! (o instanceof FeedListItem) )
      throw new ClassCastException();
    else
    {
      FeedListItem obj = (FeedListItem)o;
      return this.getDisplayName().compareTo(obj.getDisplayName());
    }
  }
}