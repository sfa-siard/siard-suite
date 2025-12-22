/*======================================================================
Db2Connection implements a wrapped DB/2 Connection.
Version     : $Id: $
Application : SIARD2
Description : Db2Connection implements a wrapped DB/2 Connection.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 04.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;

import ch.enterag.utils.jdbc.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.*;
import ch.admin.bar.siard2.db2.*;

/*====================================================================*/
/** Db2Connection implements a wrapped DB/2 Connection.
 * @author Hartwig Thomas
 */
public class Db2Connection
  extends BaseConnection
  implements Connection
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(Db2Connection.class.getName());

  /*------------------------------------------------------------------*/
  /** constructor
   * @param connWrapped connection to be wrapped.
   */
  public Db2Connection(Connection conn)
  {
    super(conn);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String nativeSQL(String sql) 
    throws SQLException
  {
    _il.enter(sql);
    Db2SqlFactory sf = new Db2SqlFactory();
    sf.setConnection(this);
    SqlStatement ss = sf.newSqlStatement();
    ss.parse(sql);
    sql = ss.format();
    _il.exit(sql);
    return sql;
  } /* nativeSQL */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * wraps statement.
   */
  @Override
  public Statement createStatement()
      throws SQLException
  {
    Statement stmt = new Db2Statement(super.createStatement());
    return stmt;
  } /* createStatement */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * wraps statement.
   */
  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency)
      throws SQLException
  {
    Statement stmt = new Db2Statement(super.createStatement(resultSetType,resultSetConcurrency));
    return stmt;
  } /* createStatement */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * wraps statement.
   */
  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
      throws SQLException
  {
    Statement stmt = new Db2Statement(super.createStatement(resultSetType,resultSetConcurrency,resultSetHoldability));
    return stmt;
  } /* createStatement */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql)
    throws SQLException
  {
    PreparedStatement ps = super.prepareStatement(nativeSQL(sql));
    return ps;
  } /* prepareStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public CallableStatement prepareCall(String sql) 
    throws SQLException
  {
    CallableStatement cs = super.prepareCall(nativeSQL(sql));
    return cs;
  } /* prepareCall */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql,
    int resultSetType, int resultSetConcurrency)
    throws SQLException
  {
    PreparedStatement ps = super.prepareStatement(nativeSQL(sql), resultSetType, resultSetConcurrency);
    return ps;
  } /* prepareStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public CallableStatement prepareCall(String sql, 
    int resultSetType, int resultSetConcurrency)
    throws SQLException
  {
    CallableStatement cs = super.prepareCall(nativeSQL(sql), resultSetType, resultSetConcurrency);
    return cs;
  } /* prepareCall */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql,
    int resultSetType, int resultSetConcurrency,
    int resultSetHoldability)
    throws SQLException
  {
    PreparedStatement ps = super.prepareStatement(nativeSQL(sql), resultSetType, 
      resultSetConcurrency, resultSetHoldability);
    return ps;
  } /* prepareStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public CallableStatement prepareCall(String sql, int resultSetType,
    int resultSetConcurrency, int resultSetHoldability)
    throws SQLException
  {
    CallableStatement cs = super.prepareCall(nativeSQL(sql), resultSetType, 
      resultSetConcurrency, resultSetHoldability); 
    return cs;
  } /* prepareCall */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
    throws SQLException
  {
    PreparedStatement ps = super.prepareStatement(nativeSQL(sql), autoGeneratedKeys);
    return ps;
  } /* prepareStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
    throws SQLException
  {
    PreparedStatement ps = super.prepareStatement(nativeSQL(sql), columnIndexes);
    return ps;
  } /* prepareStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql, String[] columnNames)
    throws SQLException
  {
    PreparedStatement ps = super.prepareStatement(nativeSQL(sql), columnNames);
    return ps;
  } /* prepareStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public DatabaseMetaData getMetaData()
    throws SQLException
  {
      DatabaseMetaData dmd = new Db2DatabaseMetaData(super.getMetaData());
      return dmd;
  } /* getMetadata */
  
  @Override
  public NClob createNClob()
    throws SQLException
  {
    NClob nclob = new Db2NClob(createClob());
    return nclob;
  } /* createNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public SQLXML createSQLXML()
    throws SQLException
  {
    Db2SqlXml sqlxml = Db2SqlXml.newInstance(null);
    return sqlxml;
  } /* createSQLXML */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Array createArrayOf(String typeName, Object[] elements)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("DB/2 supports no arrays as table columns!");
  } /* createArrayOf */

  @Override
  public Blob createDatalinkObject() throws SQLException {
    return createBlob();
}

} /* class Db2Connection */
