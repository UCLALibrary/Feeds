package edu.ucla.library.libservices.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.object.StoredProcedure;

public class RssPopulateProc
  extends StoredProcedure
{
  private DriverManagerDataSource ds;
  private Properties props;
  private String propFile;
  
  public RssPopulateProc()
  {
  }
  
  public RssPopulateProc(String propFile)
  {
    setPropFile(propFile);
    loadProperties();
    makeConnection();
  }

  public void populate()
  {
    prepProc();
    execute();
  }

  private void prepProc()
  {
    setDataSource(ds);
    setFunction(false);
    setSql("VGER_SUPPORT.POPULATE_RSS_ERDB_RESULTS");
    compile();
  }

  private Map execute() 
  {
    return execute(new HashMap());
  }

  private void loadProperties()
  {
    props = new Properties();
    try
    {
      props.load(new FileInputStream(new File(getPropFile())));
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  private void makeConnection()
  {
    ds = new DriverManagerDataSource();
    ds.setDriverClassName( props.getProperty("DRIVER_CLASSNAME") );
    ds.setUrl( props.getProperty("DB_URL") );
    ds.setUsername( props.getProperty("DB_USERNAME") );
    ds.setPassword( props.getProperty("DB_PASSWORD") );
  }

  private void setPropFile( String propFile )
  {
    this.propFile = propFile;
  }

  private String getPropFile()
  {
    return propFile;
  }
}
