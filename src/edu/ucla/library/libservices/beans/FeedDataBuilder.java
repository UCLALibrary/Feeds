package edu.ucla.library.libservices.beans;

import edu.ucla.library.libservices.db.RssPopulateProc;

public class FeedDataBuilder
{
  public static void main( String[] args )
  {
    RssPopulateProc proc;
    
    proc = new RssPopulateProc( args[0] ); //"C:\\Temp\\feeds\\rss.properties";
    proc.populate();
  }
}
