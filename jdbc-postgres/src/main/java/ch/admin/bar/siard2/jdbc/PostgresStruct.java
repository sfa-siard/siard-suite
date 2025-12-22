package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import ch.enterag.utils.jdbc.*;

public class PostgresStruct
  extends BaseStruct
{
  private String _sType = null;
  private Object[] _ao = null;
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param sType type (qualified)
   * @param ao array of objects.
   */
  public PostgresStruct(String sType, Object[] ao)
  {
    super(null);
    _sType = sType;
    _ao = ao;
  } /* constructor PostgresStruct */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getSQLTypeName() throws SQLException
  {
    return _sType;
  } /* getSQLTypeName */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Object[] getAttributes() throws SQLException
  {
    return _ao;
  } /* getAttributes */

} /* class PostgresStruct */
