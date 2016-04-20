package edu.ucla.library.libservices.beans;

import java.util.List;

import edu.ucla.library.libservices.beans.SubjectBean;
import edu.ucla.library.libservices.mappers.SubjectMapper;

import java.util.Iterator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Class7
{
  private static final String QUERY = 
    "SELECT * FROM vger_support.ucladb_rss_erdb_results WHERE language_code" 
    + " = ? OR language_code = ? OR language_code = ? ORDER BY location, sort_call_number";

  public Class7()
  {
  }

  public static void main( String[] args )
  {
    DriverManagerDataSource ds;
    JdbcTemplate details;
    Iterator items;
    List results;
    String[] langs;

    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "url" );
    ds.setUsername( "user" );
    ds.setPassword( "PWD" );

    details = new JdbcTemplate( ds );
    langs = new String[] { "eng","fre","ger" };
    results = 
        details.query( QUERY, langs, new SubjectMapper() );

    items = results.iterator();
    while ( items.hasNext() )
    {
      SubjectBean theBook;

      theBook = ( SubjectBean ) items.next();
      System.out.println( theBook.getTitle() + "\t" + 
                          theBook.getLanguage() );
    }
  }
}
