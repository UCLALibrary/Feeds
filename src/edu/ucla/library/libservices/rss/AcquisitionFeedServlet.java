package edu.ucla.library.libservices.rss;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import edu.ucla.library.libservices.beans.CatalogModuleImpl;
import edu.ucla.library.libservices.beans.NewAcquisition;
import edu.ucla.library.libservices.interfaces.rss.CatalogModule;
import edu.ucla.library.libservices.mappers.NewAcquisitionMapper;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import org.w3c.dom.Document;

public class AcquisitionFeedServlet
  extends HttpServlet
{
  private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
  private static final File XML_FILE = 
    //new File( "C:\\jdevstudio1013\\jdev\\mywork\\GeneralTester\\RssClean\\public_html\\feed.xml" );
    new File( "/opt/oracle/product/iAS10g/j2ee/home/applications/rss/webapp/feed.xml" );
  private static final File XSL_FILE = 
    //new File( "C:\\jdevstudio1013\\jdev\\mywork\\GeneralTester\\RssClean\\public_html\\styler.xsl" );
    new File( "/opt/oracle/product/iAS10g/j2ee/home/applications/rss/webapp/styler.xsl" );
  private static final GregorianCalendar TODAY = new GregorianCalendar();
  private static final SimpleDateFormat PLAIN_DATE = 
    new SimpleDateFormat( "MM/dd/yyyy" );
  private static final String COULD_NOT_GENERATE_FEED_ERROR = 
    "Could not generate RSS feed";
  private static final String COULD_NOT_PARSE_FEED_ERROR = 
    "Could not parse RSS feed";
  private static final String COULD_NOT_CONFIGURE_ERROR = 
    "Could not configure XSL transformer";
  private static final String COULD_NOT_TRANSFORM_ERROR = 
    "Could not transform RSS feed";
  private static final String DEFAULT_FEED_TYPE = "default.feed.type";
  private static final String QUERY = 
    "SELECT l.location_display_name AS location,bt.bib_id,unifix(bt.title) AS" + 
    " title,bt.begin_pub_date AS pub_year,mm.display_call_no,mh.action_date " + 
    "AS added_date FROM ucladb.mfhd_history mh INNER JOIN ucladb.mfhd_master" + 
    " mm ON mh.mfhd_id = mm.mfhd_id INNER JOIN ucladb.location l ON " + 
    "mm.location_id = l.location_id INNER JOIN ucladb.bib_mfhd bm ON " + 
    "mm.mfhd_id = bm.mfhd_id INNER JOIN ucladb.bib_text bt ON bm.bib_id = " + 
    "bt.bib_id WHERE mh.operator_id = 'uclaloader' AND mh.action_date >= " + 
    "SYSDATE - 2 ORDER BY added_date DESC";
  private DriverManagerDataSource ds;
  private String _defaultFeedType;
  private Document document;

  /**
   * @param config
   * @throws ServletException
   */
  public void init( ServletConfig config )
    throws ServletException
  {
    super.init( config );
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
    response.setContentType( CONTENT_TYPE );
    PrintWriter out;
    SyndFeed feed;
    SyndFeedOutput output;
    DocumentBuilderFactory factory;
    TransformerFactory tFactory;
    Transformer transformer;
    StreamResult result;
    DocumentBuilder builder;
    StreamSource stylesource;
    DOMSource source;

    out = response.getWriter();
    try
    {
      feed = buildFeed();
      feed.setFeedType( _defaultFeedType );

      output = new SyndFeedOutput();
      output.output( feed, XML_FILE );
      factory = DocumentBuilderFactory.newInstance();
      builder = factory.newDocumentBuilder();
      document = output.outputW3CDom( feed );
      tFactory = TransformerFactory.newInstance();
      stylesource = new StreamSource( XSL_FILE );
      transformer = tFactory.newTransformer( stylesource );
      source = new DOMSource( document );
      result = new StreamResult( out );
      transformer.transform( source, result );
    }
    catch ( FeedException ex )
    {
      String msg;

      msg = COULD_NOT_GENERATE_FEED_ERROR;
      log( msg, ex );
      ex.printStackTrace();
      response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                          msg );
    }
    catch ( ParserConfigurationException pce )
    {
      String msg;

      msg = COULD_NOT_PARSE_FEED_ERROR;
      log( msg, pce );
      pce.printStackTrace();
      response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                          msg + "<br>" + pce.getMessage() );
    }
    catch ( TransformerConfigurationException tce )
    {
      String msg;

      msg = COULD_NOT_CONFIGURE_ERROR;
      log( msg, tce );
      tce.printStackTrace();
      response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                          msg + "<br>" + tce.getMessage() );
    }
    catch ( TransformerException te )
    {
      String msg;

      msg = COULD_NOT_TRANSFORM_ERROR;
      log( msg, te );
      te.printStackTrace();
      response.sendError( 
        HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
        msg + "<br>" + te.getMessage() );
    }
  }

  private SyndFeed buildFeed()
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

    feed.setTitle( "Sample New-Acquisitions Feed" );
    feed.setLink( "http://lit108.library.ucla.edu/rss/" );
    feed.setDescription( "Test feed for acquisitions display" );
    //feed.setEncoding("UTF-8");

    results = sql.query( QUERY, new NewAcquisitionMapper() );
    items = results.iterator();
    while ( items.hasNext() )
    {
      SyndEntry entry;
      SyndContent description;
      CatalogModule module;
      List modules;
      NewAcquisition theBook;

      theBook = ( NewAcquisition ) items.next();
      entry = new SyndEntryImpl();
      description = new SyndContentImpl();
      module = new CatalogModuleImpl();
      modules = new ArrayList();

      entry.setTitle( theBook.getTitle() );
      entry.setLink( 
        "http://catalog.library.ucla.edu/cgi-bin/Pwebrecon.cgi?DB=local&BBID=" 
        + theBook.getBib_id() );
      entry.setPublishedDate( TODAY.getTime() ); //theBook.getAdded_date() );

      description.setType( "text/plain" );
      description.setValue( 
        theBook.getTitle() + "; Library: " + theBook.getLocation() 
        + "; Call Number: " + theBook.getDisplay_call_no() );

      module.setAddedDate( PLAIN_DATE.format( theBook.getAdded_date() ) );
      module.setBibId( theBook.getBib_id() );
      module.setPubYear( theBook.getPub_year() );
      modules.add( module );

      entry.setDescription( description );
      entry.setModules( modules );
      entries.add( entry );
    }

    feed.setEntries( entries );

    return feed;
  }
}
