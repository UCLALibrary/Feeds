package edu.ucla.library.libservices.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.object.StoredProcedure;

public class Class3
{
  private static DriverManagerDataSource ds;
  private static PopulateProc proc;
  public Class3()
  {
  }

  public static void main( String[] args )
  {
    ds = new DriverManagerDataSource();
    ds.setDriverClassName( "oracle.jdbc.OracleDriver" );
    ds.setUrl( "jdbc:oracle:thin:@//eliot.library.ucla.edu:1521/VGER.VGER" );
    ds.setUsername( "VGER_SUPPORT" );
    ds.setPassword( "VGER_SUPPORT_PWD" );
    
    proc = new PopulateProc();
    System.out.println("executing proc @ " + new Date());
    proc.populate(ds);
    System.out.println("finished proc @ "  + new Date());
  }
}
  class PopulateProc extends StoredProcedure
  {
    private Map execute() 
    {
      Map input;
      Map out;

      out = null;
      input = new HashMap();

      out = execute(input);

      return out;
    }
    public void populate(DriverManagerDataSource dataSource)
    {
      prepProc(dataSource);
      execute();
    }

    private void prepProc(DriverManagerDataSource dataSource)
    {
      setDataSource(dataSource);
      setFunction(false);
      setSql("VGER_SUPPORT.Populate_RSS_ERDB_RESULTS");
      compile();
    }
  }

