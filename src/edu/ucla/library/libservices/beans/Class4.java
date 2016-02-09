package edu.ucla.library.libservices.beans;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.object.StoredProcedure;

public class Class4
  extends StoredProcedure
{
  public Class4()
  {
  }

  private Map execute() 
  {
    return execute(new HashMap());
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
