package edu.ucla.library.libservices.beans;

import java.io.File;

import java.util.Collections;
import java.util.Vector;

import org.apache.commons.lang.WordUtils;

public class FeedList
{
  private String directoryName;
  private File directory;
  private Vector list;

  public FeedList()
  {
  }

  /**
   * @param directoryName
   */
  public void setDirectoryName( String directoryName )
  {
    this.directoryName = directoryName;
    directory = new File( directoryName );
  }

  public Vector getList()
  {
    String[] contents;
    contents = directory.list();
    list = new Vector();

    for ( int i = 0; i < contents.length; i++ )
    {
      FeedListItem item;

      item = new FeedListItem();
      item.setFileName( contents[ i ] );
      item.setDisplayName( fileToSubject( contents[ i ] ) );

      list.addElement( item );
    }
    Collections.sort( list );
    return list;
  }
  private String fileToSubject(String file)
  {
    return WordUtils.capitalize( 
      file.replaceAll( "0", "'" ).replaceAll( "1", "," ).replaceAll( "2", "/" )
      .replaceAll("3","\\(").replaceAll("4","\\)").replaceAll("5","\\.")
      .replaceAll( "_", " " ).substring( 0, file.indexOf( "." ) ), 
      new char[] { '-', '/', ' ' } ).replaceAll( "/A", "/a" )
      .replaceAll( "\\(a", "\\(A" ).replaceAll( "\\(w", "\\(W" );
  }
}
