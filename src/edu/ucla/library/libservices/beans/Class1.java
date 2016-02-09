package edu.ucla.library.libservices.beans;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Guid;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;

import edu.ucla.library.libservices.interfaces.rss.CatalogModule;
import edu.ucla.library.libservices.mappers.SubjectMapper;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

//import com.sun.syndication.feed.synd.SyndFeed;
//import com.sun.syndication.feed.synd.SyndFeedImpl;
//import com.sun.syndication.io.SyndFeedOutput;


public class Class1
{
  private static final GregorianCalendar TODAY = new GregorianCalendar();
  private static final SimpleDateFormat PLAIN_DATE = 
    new SimpleDateFormat( "MM/dd/yyyy" );
  private static final String DEFAULT_FEED_TYPE = "rss_2.0";
  private static final String FEED_SUBJECT = 
    "SELECT * FROM vger_support.ucladb_rss_subject WHERE added_date >= SYSDATE - 2 AND subject = ? ORDER BY location, sort_call_number";
  private static final String FILE_BASE = "C:\\feeds\\";
  private static final String NEW_SUBJECTS = 
    "SELECT DISTINCT subject FROM vger_support.ucladb_rss_subject WHERE added_date >= SYSDATE - 2 ORDER BY subject";
  private static DriverManagerDataSource ds;
  public Class1()
  {
  }
  public static void main(String[] args)
  {
    List newSubjects;
    Iterator items;

    makeConnection();
    
    newSubjects = getNewSubjects();
    items = newSubjects.iterator();

    while ( items.hasNext() )
    {
      File outFile;
      String subject;
      String title;
      Channel feed;
      WireFeedOutput output;
      
      subject = items.next().toString();
      title = 
          subject.replaceAll("'","0").replaceAll(",","1").replaceAll("/","2").replaceAll(" ", "_").toLowerCase().concat(".xml");
      outFile = new File( FILE_BASE.concat(title) );
      try
      {
        feed = buildFeed(subject);
        feed.setFeedType( DEFAULT_FEED_TYPE );
        output = new WireFeedOutput();
        output.output( feed, outFile );
      }
      catch ( IOException ioe )
      {
        ioe.printStackTrace();
      }
      catch ( FeedException fe )
      {
        fe.printStackTrace();
      }
    }
  }
  private static void makeConnection()
  {
    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "jdbc:oracle:thin:@//eliot.library.ucla.edu:1521/VGER.VGER" );
    ds.setUsername( "UCLA_PREADDB" );
    ds.setPassword( "UCLA_PREADDB" );
  }
  private static List getNewSubjects()
  {
    return new JdbcTemplate( ds ).queryForList(NEW_SUBJECTS, String.class);
  }
  private static Channel buildFeed(String subject)
    throws IOException, FeedException
  {
    Channel feed;
    List entries;
    JdbcTemplate sql;
    List results;
    Iterator items;

    feed = new Channel();
    entries = new ArrayList();
    sql = new JdbcTemplate( ds );

    feed.setTitle( "Newly added titles in " + subject );
    feed.setLink( "http://lit108.library.ucla.edu/rss/" );
    feed.setDescription( "Titles related to " + subject + " added in the last 2 days" );

    results = sql.query( 
      FEED_SUBJECT, new Object[] {subject}, new SubjectMapper() );
    items = results.iterator();
    while ( items.hasNext() )
    {
      Item entry;
      Description description;
      Guid guid;
      CatalogModule module;
      List modules;
      SubjectBean theBook;

      theBook = ( SubjectBean ) items.next();
      entry = new Item();
      description = new Description();
      module = new CatalogModuleImpl();
      guid = new Guid();
      modules = new ArrayList();

      entry.setTitle( theBook.getTitle() );
      entry.setLink( 
        "http://catalog.library.ucla.edu/cgi-bin/Pwebrecon.cgi?DB=local&BBID=" 
        + theBook.getBib_id() );
      entry.setPubDate( TODAY.getTime() ); 

      description.setType( "text/plain" );
      description.setValue(  
        theBook.getTitle() + "; " + theBook.getAuthor() + " (" + theBook.getLocation() 
        + " " + theBook.getCall_number() + ")" );

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
      
      guid.setPermaLink(false);
      guid.setValue("http://rss.library.ucla.edu/item" + theBook.getBib_id());
      
      entry.setDescription( description );
      entry.setModules( modules );
      entry.setGuid(guid);
      entries.add( entry );
    }

    feed.setItems( entries );
    return feed;
  }
}
/*
 import com.sun.syndication.feed.synd.SyndContent;
 import com.sun.syndication.feed.synd.SyndContentImpl;
 import com.sun.syndication.feed.synd.SyndEntry;
 import com.sun.syndication.feed.synd.SyndEntryImpl;
 import com.sun.syndication.feed.synd.SyndFeed;
 import com.sun.syndication.feed.synd.SyndFeedImpl;
 import com.sun.syndication.io.SyndFeedOutput;
*/