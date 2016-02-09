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
//subject.replaceAll("'","0").replaceAll(",","1").replaceAll("/","2").replaceAll("\\(","3").replaceAll("\\)","4").replaceAll(" ", "_").toLowerCase().concat(".xml")
   //import org.springframework.jdbc.core.JdbcTemplate;
   //import org.springframework.jdbc.datasource.DriverManagerDataSource;
    //private DriverManagerDataSource ds;
    //private JdbcTemplate sql;
     /*ds = new DriverManagerDataSource();
     ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
     ds.setUrl( "jdbc:oracle:thin:@//eliot.library.ucla.edu:1521/VGER.VGER" );
     ds.setUsername( "UCLA_PREADDB" );
     ds.setPassword( "UCLA_PREADDB" );*/
      //capitalize( contents[i].replaceAll("0","'").replaceAll("1",",").
       /*private String capitalize(String input)
       {
         sql = new JdbcTemplate( ds );
         
         return sql.queryForObject("SELECT INITCAP(?) from dual", new Object[]{input}, String.class).toString();    
       }*/
