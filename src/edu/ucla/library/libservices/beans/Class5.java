package edu.ucla.library.libservices.beans;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Guid;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;

import edu.ucla.library.libservices.beans.CatalogModuleImpl;
import edu.ucla.library.libservices.beans.SubjectBean;
import edu.ucla.library.libservices.interfaces.rss.CatalogModule;
import edu.ucla.library.libservices.mappers.SubjectMapper;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Class5
{
  private static List results;
  private static DriverManagerDataSource ds;
  private static JdbcTemplate sql;
  private static Iterator items;
  private static Channel feed;
  private static List entries;
  private static final SimpleDateFormat PLAIN_DATE =
    new SimpleDateFormat( "MM/dd/yyyy" );
  private static WireFeedOutput output;

  public Class5()
  {
  }

  public static void main( String[] args )
    throws IOException, FeedException
  {
    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "jdbc:oracle:thin:@//eliot.library.ucla.edu:1521/VGER.VGER" );
    ds.setUsername( "VGER_SUPPORT" );
    ds.setPassword( "VGER_SUPPORT_PWD" );
    
    sql = new JdbcTemplate(ds);
    results = sql.query(
      "SELECT * FROM VGER_SUPPORT.UCLADB_RSS_MARC WHERE BIB_ID IN (6123483, 6128075, 5152112) ORDER BY LOCATION, SORT_CALL_NUMBER", 
      new SubjectMapper() );
      
    items = results.iterator();

    feed = new Channel();
    entries = new ArrayList();

    feed.setTitle( "UCLA - Char Replace Test - Newly added titles" );
    feed.setLink( "http://www2.library.ucla.edu/libraries/6453.cfm" );
    feed.setDescription( "Titles related to Bad Data added in the last five days" );
    feed.setEncoding("UTF-8");
    
    char badChar = 0x1f;
    char goodChar = 0x20;

    while ( items.hasNext() )
    {
      Item entry;
      Description description;
      CatalogModule module;
      Guid guid;
      List modules;
      SubjectBean theBook;

      theBook = ( SubjectBean ) items.next();
      entry = new Item();
      description = new Description();
      guid = new Guid();
      module = new CatalogModuleImpl();
      modules = new ArrayList();

      entry.setTitle( theBook.getTitle().replace(badChar, goodChar) );
      entry.setLink(
        "http://catalog.library.ucla.edu/cgi-bin/Pwebrecon.cgi?DB=local&BBID="
        + theBook.getBib_id() );
      //entry.setPubDate( TODAY.getTime() );
      entry.setPubDate( theBook.getAdded_date() );

      description.setType( "text/html" );
      description.setValue(
        "<table><tr><td align=\"right\" valign=\"top\">Language:</td><td align=\"left\">" +
        theBook.getLanguage() + "</td></tr>" +
        ( !theBook.isEmpty( theBook.getEdition() ) ?
          "<tr><td align=\"right\" valign=\"top\">Edition:</td><td align=\"left\">" +
          theBook.getEdition() + "</td></tr>" : "" ) +
        ( !theBook.isEmpty( theBook.getImprint() ) ?
          "<tr><td align=\"right\" valign=\"top\">Published/Distributed:</td><td align=\"left\">" +
          theBook.getImprint() + "</td></tr>" : "" ) +
        ( !theBook.isEmpty( theBook.getBib_subjects() ) ?
          buildSubjects( theBook.getBib_subjects() ) : "" ) +
        ( !theBook.isEmpty( theBook.getSeries() ) ?
          "<tr><td align=\"right\" valign=\"top\">Series:</td><td align=\"left\">" +
          theBook.getSeries() + "</td></tr>" : "" ) +
        "<tr><td align=\"right\" valign=\"top\">Location:</td><td align=\"left\">" +
        theBook.getLocation() + ": " + theBook.getCall_number() + "</td></tr>" +
        "</table>" );

      guid.setPermaLink(true);
      guid.setValue(
        "http://catalog.library.ucla.edu/cgi-bin/Pwebrecon.cgi?DB=local&BBID=" +
        theBook.getBib_id() );

      module.setAddedDate( PLAIN_DATE.format( theBook.getAdded_date() ) );
      module.setAuthor( theBook.getAuthor() );
      module.setBibId( theBook.getBib_id() );
      module.setCallNumber( theBook.getCall_number() );
      module.setLanguageCode( theBook.getLanguage_code() );
      module.setLocation( theBook.getLocation() );
      module.setPubYear( theBook.getPub_year() );
      module.setSubject( theBook.getSubject() );
      module.setTitle( theBook.getTitle().replace(badChar, goodChar) );
      modules.add( module );

      entry.setDescription( description );
      entry.setGuid(guid);
      entry.setModules( modules );
      entries.add( entry );
    }
    feed.setItems( entries );
    feed.setFeedType( "rss_2.0" );
    output = new WireFeedOutput ();
    output.output( feed, new File( "C:\\temp\\test.xml" ) );
  }
  private static String buildSubjects( String theSubjects )
  {
    StringBuffer formatted;
    String[] subjects;

    formatted =
        new StringBuffer( "<tr><td align=\"right\" valign=\"top\">Subject(s):</td><td align=\"left\">" );
    subjects = theSubjects.split( "[|]" );

    formatted.append( subjects[ 0 ].replaceAll( "\\$[a-zA-Z]",
                                                "-" ).replaceFirst( "-",
                                                                    "" ).trim() +
                      "</td></tr>" );

    for ( int i = 1; i < subjects.length; i++ )
    {
      formatted.append( "<tr><td align=\"right\">&nbsp;</td><td align=\"left\">" +
                        subjects[ i ].replaceAll( "\\$[a-zA-Z]",
                                                  "-" ).replaceFirst( "-",
                                                                      "" ).trim() +
                        "</td></tr>" );
    }

    return formatted.toString();
  }
}
//SELECT * FROM vger_support.ucladb_rss_subject_cn_lc_marc WHERE (added_date BETWEEN Trunc(SYSDATE - 5) AND Trunc(SYSDATE - 1)) AND subject = ? ORDER BY location, sort_call_number
/*
 * SELECT * FROM vger_support.ucladb_rss_erdb_results WHERE subject = ? ORDER BY location, sort_call_number
 System.out.println("title = " + theBook.getTitle() + "\tbib subjects = " + theBook.getBib_subjects() + "\tpub year = " + theBook.getPub_year());
 0x1F
 */