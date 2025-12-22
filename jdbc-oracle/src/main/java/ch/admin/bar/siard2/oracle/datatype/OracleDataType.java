/*======================================================================
OracleDataType implements the type translation from ISO SQL to Oracle for 
              complex types.
Version     : $Id: $
Application : SIARD2
Description : OracleDataType implements the type translation from ISO 
              SQL:2008 to Oracle for complex types.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rueti ZH, Switzerland
Created    : 27.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.oracle.datatype;

import java.sql.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.oracle.*;
import ch.admin.bar.siard2.jdbc.*;

public class OracleDataType
  extends DataType
{
  /*------------------------------------------------------------------*/
  /** format an ARRAY type.
   * @return SQL for ARRAY type.
   */
  @Override
  protected String formatArrayType()
  {
    String sDataType = null;
    String sBaseType = getDataType().format();
    int iLength = getLength();
    if (iLength == iUNDEFINED)
      throw new IllegalArgumentException("Oracle cannot handle arrays of undefined length!");
    try
    {
      OracleSqlFactory osf = (OracleSqlFactory)getSqlFactory();
      OracleConnection oconn = osf.getConnection();
      oracle.jdbc.OracleConnection conn = (oracle.jdbc.OracleConnection)oconn.unwrap(Connection.class);
      QualifiedId qiVarray = OracleArray.findOrCreateVarray(conn,sBaseType,iLength);
      sDataType = qiVarray.quote();
    }
    catch (SQLException se) { throw new IllegalArgumentException(se); }
    return sDataType;
  } /* formatArrayType */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public OracleDataType(SqlFactory sf) 
  {
    super(sf);
  } /* constructor */

} /* OracleDataType */
