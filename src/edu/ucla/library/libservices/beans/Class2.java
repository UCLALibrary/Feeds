package edu.ucla.library.libservices.beans;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Guid;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Iterator;

import edu.ucla.library.libservices.beans.CatalogModuleImpl;
import edu.ucla.library.libservices.beans.MarcBean;
import edu.ucla.library.libservices.interfaces.rss.CatalogModule;
import edu.ucla.library.libservices.mappers.MarcMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Class2
{
  private static final GregorianCalendar TODAY = new GregorianCalendar();
  private static final SimpleDateFormat PLAIN_DATE = 
    new SimpleDateFormat( "MM/dd/yyyy" );
  private static final String QUERY = 
    "SELECT * FROM vger_support.ucladb_rss_law_results ORDER BY location, sort_call_number";
  private static DriverManagerDataSource ds;

  public Class2()
  {
  }

  public static void main( String[] args )
    throws IOException, FeedException
  {
    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "url" );
    ds.setUsername( "user" );
    ds.setPassword( "pwd" );

    File outFile;
    WireFeedOutput output;
    Channel feed;
    List entries;
    JdbcTemplate details;
    List results;
    Iterator items;

    outFile = 
        new File( "C:\\Program Files\\Apache Software Foundation\\Apache2.2\\htdocs\\rss\\law.xml" );

    feed = new Channel();
    entries = new ArrayList();
    details = new JdbcTemplate( ds );

    feed.setTitle( "UCLA - Law - Newly added titles" );
    feed.setLink( "http://www2.library.ucla.edu/libraries/6453.cfm" );
    feed.setDescription( "Titles related to Law added in the last 30 days" );
    feed.setEncoding( "UTF-8" );

    results = 
        details.query( QUERY, new MarcMapper() );

    items = results.iterator();
    while ( items.hasNext() )
    {
      MarcBean theBook;
      Item entry;
      Description description;
      CatalogModule module;
      Guid guid;
      List modules;

      theBook = ( MarcBean ) items.next();
      entry = new Item();
      description = new Description();
      guid = new Guid();
      module = new CatalogModuleImpl();
      modules = new ArrayList();

      entry.setTitle( theBook.getTitle() );
      entry.setLink( "http://catalog.library.ucla.edu/cgi-bin/Pwebrecon.cgi?DB=local&BBID=" + 
                     theBook.getBib_id() );
      entry.setPubDate( TODAY.getTime() );

      description.setType( "text/html" );

      description.setValue( 
        "<table><tr><td align=\"right\">Language:</td><td align=\"left\">" + 
        theBook.getLanguage() + "</td></tr>" + 
        ( !theBook.isEmpty( theBook.getEdition() ) ? 
          "<tr><td align=\"right\">Edition:</td><td align=\"left\">" + 
          theBook.getEdition() + "</td></tr>" : "" ) + 
        "<tr><td align=\"right\">Published/Distributed:</td><td align=\"left\">" + 
        theBook.getImprint() + "</td></tr>" + 
        ( !theBook.isEmpty( theBook.getBib_subjects() ) ? 
          buildSubjects( theBook.getBib_subjects() ) : "" ) + 
        ( !theBook.isEmpty( theBook.getSeries() ) ? 
          "<tr><td align=\"right\">Series:</td><td align=\"left\">" + 
          theBook.getSeries() + "</td></tr>" : "" ) + 
        "<tr><td align=\"right\">Location:</td><td align=\"left\">" + 
        theBook.getLocation() + ": " + theBook.getCall_number() + "</td></tr>" + 
        "</table>" );

      guid.setPermaLink( true );
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
      module.setTitle( theBook.getTitle() );
      modules.add( module );

      entry.setDescription( description );
      entry.setGuid( guid );
      entry.setModules( modules );
      entries.add( entry );
    }

    feed.setItems( entries );

    feed.setFeedType( "rss_2.0" );
    output = new WireFeedOutput();
    output.output( feed, outFile );
  }

  private static String buildSubjects( String theSubjects )
  {
    StringBuffer formatted;
    String[] subjects;

    formatted = 
        new StringBuffer( "<tr><td align=\"right\">Subject(s):</td><td align=\"left\">" );
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
/*
 File outFile;
 Channel feed;
 WireFeedOutput output;
 List entries;
 Item entry;
 Description description;
 Guid guid;

 outFile = new File( "C:\\Program Files\\Apache Group\\Apache2\\htdocs\\rss\\rss2.xml" );

 feed = new Channel();
 entries = new ArrayList();

 feed.setTitle( "UCLA - Testing New Format - Newly added titles" );
 feed.setLink( "http://www2.library.ucla.edu/libraries/6453.cfm" );
 feed.setDescription( "Titles related to blahblahblah added two days ago" );
 feed.setEncoding("UTF-8");

 entry = new Item();
 description = new Description();
 guid = new Guid();

 entry.setTitle( "Where angels fear to tread / E.M. Forster." );
 entry.setLink(
   "http://catalog.library.ucla.edu/cgi-bin/Pwebrecon.cgi?DB=local&BBID=5763764" );
 entry.setPubDate( TODAY.getTime() );

 description.setType( "text/html" );
 description.setValue( 
 //"<![CDATA" + 
 "<table><tr><td align=\"right\">Language:</td><td align=\"left\">English</td></tr>" + 
 "<tr><td align=\"right\">Edition:</td><td align=\"left\">New ed. / edited with an introduction by Zadie Smith.</td></tr>" + 
 "<tr><td align=\"right\">Published/Distributed:</td><td align=\"left\">London : Penguin, 2006.</td></tr>" + 
 "<tr><td align=\"right\">Subject(s):</td><td align=\"left\">English--Italy--Social life and customs--Fiction.</td></tr>" + 
 "<tr><td align=\"right\">&nbsp;</td><td align=\"left\">Social classes--Fiction.</td></tr>" + 
 "<tr><td align=\"right\">Series:</td><td align=\"left\">Penguin Classics</td></tr>" + 
 "<tr><td align=\"right\">Location:</td><td align=\"left\">YRL: PR6011.F77 A11</td></tr>" + 
 "</table>" ); //]]>" );

 guid.setPermaLink(false);
 guid.setValue( "http://rss.library.ucla.edu/item5763764" );

 entry.setDescription( description );
 entry.setGuid(guid);

 entries.add( entry );

 feed.setItems( entries );
 feed.setFeedType( "rss_2.0" );
 output = new WireFeedOutput ();
 output.output( feed, outFile );

 */
