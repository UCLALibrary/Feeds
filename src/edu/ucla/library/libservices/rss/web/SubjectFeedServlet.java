package edu.ucla.library.libservices.rss.web;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import edu.ucla.library.libservices.beans.CatalogModuleImpl;
import edu.ucla.library.libservices.beans.SubjectBean;
import edu.ucla.library.libservices.interfaces.rss.CatalogModule;
import edu.ucla.library.libservices.mappers.SubjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SubjectFeedServlet
  extends HttpServlet
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private static final String SUBJECT_QUERY = 
    "SELECT DISTINCT subject FROM vger_support.ucladb_rss_subject WHERE added_date >= SYSDATE - 2 ORDER BY subject";
  private static final String FILE_BASE = 
    "/opt/oracle/product/iAS10g/j2ee/home/applications/rss/webapp/feeds/";
    //"C:\\jdevstudio1013\\jdev\\mywork\\GeneralTester\\RssClean\\public_html\\feeds\\";
  private static final GregorianCalendar TODAY = new GregorianCalendar();
  private static final SimpleDateFormat PLAIN_DATE = 
    new SimpleDateFormat( "MM/dd/yyyy" );
  private static final String COULD_NOT_GENERATE_FEED_ERROR = 
    "Could not generate RSS feed";
  private static final String FILE_ERROR = 
    "File handling or creation error";
  private static final String DEFAULT_FEED_TYPE = "default.feed.type";
  private DriverManagerDataSource ds;
  private String _defaultFeedType;

  /**
   * @param config
   * @throws ServletException
   */
  public void init( ServletConfig config )
    throws ServletException
  {
    super.init(config);
    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "jdbc:oracle:thin:@//eliot.library.ucla.edu:1521/VGER.VGER" );
    ds.setUsername( "UCLA_PREADDB" );
    ds.setPassword( "UCLA_PREADDB" );
    _defaultFeedType = 
        getServletConfig().getInitParameter( DEFAULT_FEED_TYPE );
    _defaultFeedType = 
        ( _defaultFeedType != null ) ? _defaultFeedType : "atom_0.3";
  }

  /**Process the HTTP doGet request.
   */
  public void doGet( HttpServletRequest request, 
                     HttpServletResponse response )
    throws ServletException, IOException
  {
    doPost(request, response);
  }

  /**Process the HTTP doPost request.
   */
  public void doPost( HttpServletRequest request, 
                      HttpServletResponse response )
    throws ServletException, IOException
  {
    response.setContentType( CONTENT_TYPE );
    PrintWriter out;
    out = response.getWriter();
    buildFeeds();
    out.println("done building");
  }
  private void buildFeeds()
  {
    JdbcTemplate subjSql;
    List subjects;

    subjSql = new JdbcTemplate( ds );
    subjects = subjSql.queryForList(SUBJECT_QUERY, String.class);
    Iterator items = subjects.iterator();
    while ( items.hasNext() )
    {
      File outFile;
      String subject;
      String title;
      SyndFeed feed;
      SyndFeedOutput output;
      
      subject = items.next().toString();
      title = 
          subject.replaceAll("'","0").replaceAll(",","1").replaceAll("/","2").replaceAll(" ", "_").toLowerCase().concat(".xml");
      outFile = new File( FILE_BASE.concat(title) );
      try
      {
        feed = buildFeed(subject);
        feed.setFeedType( _defaultFeedType );
        output = new SyndFeedOutput();
        output.output( feed, outFile );
      }
      catch ( IOException ioe )
      {
        log( FILE_ERROR, ioe );
      }
      catch ( FeedException fe )
      {
        log( COULD_NOT_GENERATE_FEED_ERROR, fe );
      }
    }
  }
  private SyndFeed buildFeed(String subject)
    throws IOException, FeedException
  {
    SyndFeed feed;
    List entries;
    JdbcTemplate sql;
    List results;
    Iterator items;

    feed = new SyndFeedImpl();
    entries = new ArrayList();
    sql = new JdbcTemplate( ds );

    feed.setTitle( "Sample Feed for " + subject + " Acquisitions" );
    feed.setLink( "http://lit108.library.ucla.edu/rss/" );
    feed.setDescription( "Test feed for acquisitions display" );

    results = sql.query( 
      "SELECT * FROM vger_support.ucladb_rss_subject WHERE added_date >= " 
      + "SYSDATE - 2 AND subject = ? ORDER BY location, sort_call_number", 
      new Object[] {subject}, new SubjectMapper() );
    items = results.iterator();
    while ( items.hasNext() )
    {
      SyndEntry entry;
      SyndContent description;
      CatalogModule module;
      List modules;
      SubjectBean theBook;
      
      theBook = ( SubjectBean ) items.next();
      entry = new SyndEntryImpl();
      description = new SyndContentImpl();
      module = new CatalogModuleImpl();
      modules = new ArrayList();

      entry.setTitle( theBook.getTitle() );
      entry.setLink( 
        "http://catalog.library.ucla.edu/cgi-bin/Pwebrecon.cgi?DB=local&BBID=" 
        + theBook.getBib_id() );
      entry.setPublishedDate( TODAY.getTime() ); 

      description.setType( "text/plain" );
      description.setValue(  theBook.getTitle() + "; " + theBook.getAuthor() 
        + " (" + theBook.getLocation() + " " + theBook.getSort_call_number() + ")" );

      module.setAddedDate( PLAIN_DATE.format( theBook.getAdded_date() ) );
      module.setAuthor( theBook.getAuthor() );
      module.setBibId( theBook.getBib_id() );
      module.setCallNumber( theBook.getCall_number() );
      module.setLocation( theBook.getLocation() );
      module.setPubYear( theBook.getPub_year() );
      module.setSubject( theBook.getSubject() );
      module.setTitle( theBook.getTitle() );
      modules.add( module );

      entry.setDescription( description );
      entry.setModules( modules );
      entries.add( entry );
    }

    feed.setEntries( entries );
    return feed;
  }
}
/*
 * get list of subjects
 * convert subjects into*/