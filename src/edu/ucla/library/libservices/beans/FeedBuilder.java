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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


public class FeedBuilder
{
  //private static final GregorianCalendar TODAY = new GregorianCalendar();
  //private static final SimpleDateFormat LONG_DATE =  new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss" );
  private static final SimpleDateFormat PLAIN_DATE = 
    new SimpleDateFormat( "MM/dd/yyyy" );
  private static final String LAW_FEED = "all_law";
  private static final char COPY_CHAR = '©';
  private static final String COPY_HTML = "&#169;";
  private static final char BAD_CHAR = 0x1f;
  private static final char GOOD_CHAR = 0x20;
  private static DriverManagerDataSource ds;
  private static Properties props;

  public static void main( String[] args )
  {
    loadProperties( args[ 0 ] );
    makeConnection();
    buildSubjectFiles( props.getProperty( "ALL_SUBJECTS_ERDB" ), 
                       props.getProperty( "FILE_BASE_ERDB" ), false );
    buildFeedFiles( props.getProperty( "FILE_BASE_ERDB" ), 
                    props.getProperty( "NEW_SUBJECTS_ERDB" ), 
                    props.getProperty( "FEED_SUBJECT_ERDB" ), false );
    buildLawFeed( props.getProperty( "FILE_BASE_ERDB" ), 
                  props.getProperty( "LAW_SQL" ) );
    buildLangFeed( props.getProperty( "FILE_BASE_ERDB" ), props.getProperty( "ONE_LANG_SQL" ), "chi.xml", new String[] {"Chinese"} );
    buildLangFeed( props.getProperty( "FILE_BASE_ERDB" ), props.getProperty( "ONE_LANG_SQL" ), "jpn.xml", new String[] {"Japanese"} );
    buildLangFeed( props.getProperty( "FILE_BASE_ERDB" ), props.getProperty( "ONE_LANG_SQL" ), "kor.xml", new String[] {"Korean"} );
    buildLangFeed( props.getProperty( "FILE_BASE_ERDB" ), props.getProperty( "THREE_LANG_SQL" ), "eng_fre_ger.xml", new String[] {"English", "French", "German"} );
  }

  private static void loadProperties( String propFile )
  {
    props = new Properties();
    try
    {
      props.load( new FileInputStream( new File( propFile ) ) );
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
    }
  }

  private static void makeConnection()
  {
    ds = new DriverManagerDataSource();
    ds.setDriverClassName( props.getProperty( "DRIVER_CLASSNAME" ) );
    ds.setUrl( props.getProperty( "DB_URL" ) );
    ds.setUsername( props.getProperty( "DB_USERNAME" ) );
    ds.setPassword( props.getProperty( "DB_PASSWORD" ) );
  }

  private static void buildSubjectFiles( String sql, String directory, 
                                         boolean isLC )
  {
    List allSubjects;
    Iterator items;

    allSubjects = getAllSubjects( sql );
    items = allSubjects.iterator();
    while ( items.hasNext() )
    {
      File checkFile;
      String fileName, subject;

      subject = items.next().toString();
      fileName = directory.concat( subjectToFile( subject ) );
      checkFile = new File( fileName );
      if ( !checkFile.exists() )
      {
        writeSubjectFile( new String[] {subject}, checkFile, isLC );
      }
    }
  }

  private static String subjectToFile( String subject )
  {
    return subject.replaceAll( "'", "0" ).replaceAll( ",", 
                                                      "1" ).replaceAll( "/", 
                                                                        "2" ).replaceAll( "\\(", 
                                                                                          "3" ).replaceAll( "\\)", 
                                                                                                            "4" ).replaceAll( "\\.", 
                                                                                                                              "5" ).replaceAll( " ", 
                                                                                                                                                "_" ).toLowerCase().concat( ".xml" );
  }

  private static List getAllSubjects( String sql )
  {
    return new JdbcTemplate( ds ).queryForList( sql, String.class );
  }

  private static void writeSubjectFile( String[] theSubject, File theFile, 
                                        boolean isLC )
  {
    BufferedWriter writer;

    try
    {
      writer = new BufferedWriter( new FileWriter( theFile ) );
      writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
      writer.write( "<rss xmlns:taxo=\"http://purl.org/rss/1.0/modules/taxonomy/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:catalog=\"http://rss.library.ucla.edu/rss/catalog/1.0\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"2.0\">\n" );
      writer.write( "<channel>\n" );
      writer.write( "<title>UCLA - " + titleFromSubject(theSubject) + 
                    ( isLC ? " (LC)" : "" ) + 
                    " - Newly added titles</title>\n" );
      writer.write( "<link>http://www.library.ucla.edu/search/new-books</link>\n" );
      writer.write( "<description>Titles related to " + titleFromSubject(theSubject) + 
                    " added in the last five days</description>\n" );
      writer.write( "</channel>\n" );
      writer.write( "</rss>\n" );
      writer.close();
    }
    catch ( IOException ioe )
    {
      ioe.printStackTrace();
    }
  }

  private static void buildFeedFiles( String directory, String subjectSql, 
                                      String detailSql, boolean isLC )
  {
    List newSubjects;
    Iterator items;

    newSubjects = getNewSubjects( subjectSql );
    items = newSubjects.iterator();
    while ( items.hasNext() )
    {
      File outFile;
      String subject;
      String title;
      Channel feed;
      WireFeedOutput output;

      subject = items.next().toString();
      title = subjectToFile( subject );
      outFile = new File( directory.concat( title ) );
      try
      {
        feed = buildFeed( new String[] {subject}, detailSql, isLC );
        feed.setFeedType( props.getProperty( "DEFAULT_FEED_TYPE" ) );
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
      catch ( org.jdom.IllegalDataException ide )
      {
        ide.printStackTrace();
      }

    }
  }

  private static List getNewSubjects( String sql )
  {
    return new JdbcTemplate( ds ).queryForList( sql, String.class );
  }

  private static Channel buildFeed( String[] subject, String sql, 
                                    boolean isAllLaw )
    throws IOException, FeedException
  {
    Channel feed;
    List entries;
    JdbcTemplate details;
    List results;
    Iterator items;

    feed = new Channel();
    entries = new ArrayList();
    details = new JdbcTemplate( ds );

    //+ ( isAllLaw ? " (LC)" : "" ) 
    feed.setTitle( "UCLA - " + titleFromSubject(subject) + " - Newly added titles" );
    if ( isAllLaw )
      feed.setLink( "http://feeds.library.ucla.edu/uclalib/all_law.rss" );
    else
      feed.setLink( "http://www.library.ucla.edu/search/new-books" );
    feed.setDescription( "Titles related to " + titleFromSubject(subject) + 
                         " added in the last " + 
                         ( isAllLaw ? "30" : "5" ) + " days" );
    feed.setEncoding( "UTF-8" );

    results = 
        ( isAllLaw ? details.query( sql, new SubjectMapper() ) : details.query( sql, 
                                                                                //new Object[] { subject }, 
                                                                                subject, 
                                                                                new SubjectMapper() ) );
    items = results.iterator();
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

      entry.setTitle( theBook.getTitle().replace( BAD_CHAR, GOOD_CHAR ) );
      entry.setLink( "http://catalog.library.ucla.edu/vwebv/holdingsInfo?bibId=" + 
                     theBook.getBib_id() );
      //entry.setPubDate( TODAY.getTime() );
      entry.setPubDate( theBook.getAdded_date() );

      description.setType( "text/html" );
      description.setValue( "<table><tr><td align=\"right\" valign=\"top\">Language:</td><td align=\"left\">" + 
                            theBook.getLanguage() + "</td></tr>" + 
                            ( !theBook.isEmpty( theBook.getEdition() ) ? 
                              "<tr><td align=\"right\" valign=\"top\">Edition:</td><td align=\"left\">" + 
                              theBook.getEdition() + "</td></tr>" : "" ) + 
                            ( !theBook.isEmpty( theBook.getImprint() ) ? 
                              "<tr><td align=\"right\" valign=\"top\">Published/Distributed:</td><td align=\"left\">" + 
                              theBook.getImprint().replaceAll( String.valueOf( COPY_CHAR ), COPY_HTML ) + "</td></tr>" : "" ) + 
                            ( !theBook.isEmpty( theBook.getBib_subjects() ) ? 
                              buildSubjects( theBook.getBib_subjects() ) : 
                              "" ) + 
                            ( !theBook.isEmpty( theBook.getSeries() ) ? 
                              "<tr><td align=\"right\" valign=\"top\">Series:</td><td align=\"left\">" + 
                              theBook.getSeries() + "</td></tr>" : "" ) + 
                            "<tr><td align=\"right\" valign=\"top\">Location:</td><td align=\"left\">" + 
                            theBook.getLocation() + ": " + 
                            theBook.getCall_number() + "</td></tr>" + 
                            "</table>" );

      guid.setPermaLink( true );
      guid.setValue( "http://catalog.library.ucla.edu/vwebv/holdingsInfo?bibId=" + 
                     theBook.getBib_id() );

      module.setAddedDate( PLAIN_DATE.format( theBook.getAdded_date() ) );
      module.setAuthor( theBook.getAuthor() );
      module.setBibId( theBook.getBib_id() );
      module.setCallNumber( theBook.getCall_number() );
      module.setLanguageCode( theBook.getLanguage_code() );
      module.setLocation( theBook.getLocation() );
      module.setPubYear( theBook.getPub_year() );
      module.setSubject( theBook.getSubject() );
      module.setTitle( theBook.getTitle().replace( BAD_CHAR, GOOD_CHAR ) );
      modules.add( module );

      entry.setDescription( description );
      entry.setGuid( guid );
      entry.setModules( modules );
      entries.add( entry );
    }

    feed.setItems( entries );
    return feed;
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

  private static void buildLawFeed( String directory, String query )
  {
    File outFile;
    String fileName;

    fileName = directory.concat( subjectToFile( LAW_FEED ) );
    outFile = new File( fileName );
    if ( !outFile.exists() )
    {
      writeSubjectFile( new String[] {"Law"}, outFile, false );
    }

    Channel feed;
    WireFeedOutput output;

    try
    {
      feed = buildFeed( new String[] {"Law"}, query, true );
      feed.setFeedType( props.getProperty( "DEFAULT_FEED_TYPE" ) );
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
    catch ( org.jdom.IllegalDataException ide )
    {
      ide.printStackTrace();
    }
  }

  
  private static String titleFromSubject(String[] subject)
  {
    String output;
    output = "";

    for ( String value : subject )
      output = output.concat(value).concat(" ");
    
    return output.trim();
  }

  private static void buildLangFeed( String directory, String query, String fileName, String[] langs )
  {
    File outFile;
    String outFileName;

    outFileName = directory.concat( fileName );
    outFile = new File( outFileName );
    if ( !outFile.exists() )
    {
      writeSubjectFile( langs, outFile, false );
    }

    Channel feed;
    WireFeedOutput output;

    try
    {
      feed = buildFeed( langs, query, false );
      feed.setFeedType( props.getProperty( "DEFAULT_FEED_TYPE" ) );
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
    catch ( org.jdom.IllegalDataException ide )
    {
      ide.printStackTrace();
    }
  }

}
