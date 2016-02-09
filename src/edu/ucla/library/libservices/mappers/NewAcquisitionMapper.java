package edu.ucla.library.libservices.mappers;

import edu.ucla.library.libservices.beans.NewAcquisition;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class NewAcquisitionMapper implements RowMapper
{
  public NewAcquisitionMapper()
  {
  }

  /**
   * @param rs
   * @param i
   * @return
   * @throws SQLException
   */
  public Object mapRow( ResultSet rs, int i )
    throws SQLException
  {
    NewAcquisition bean;

    bean = new NewAcquisition();
    bean.setAdded_date(rs.getDate("added_date"));
    bean.setBib_id(rs.getString("bib_id"));
    bean.setDisplay_call_no(rs.getString("display_call_no"));
    bean.setLocation(rs.getString("location"));
    bean.setPub_year(rs.getString("pub_year"));
    bean.setTitle(rs.getString("title"));

    return bean;
  }
}
