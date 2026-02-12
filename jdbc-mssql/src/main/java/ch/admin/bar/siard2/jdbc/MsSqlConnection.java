/*======================================================================
MsSqlConnection implements a wrapped MSSQL Connection.
Version     : $Id: $
Application : SIARD2
Description : MsSqlConnection implements a wrapped MSSQL Connection.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import com.microsoft.sqlserver.jdbc.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.jdbc.*;
import ch.admin.bar.siard2.mssql.*;

/*====================================================================*/
/** MsSqlConnection implements a wrapped MSSQL Connection.
 * @author Hartwig Thomas
 */
public class MsSqlConnection
  extends BaseConnection
  implements Connection
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(MsSqlConnection.class.getName());
  private QualifiedId _qiTableDropCascade = null;
  public QualifiedId getTableDropCascade() { return _qiTableDropCascade; }
  public void resetTableDropCascade() { _qiTableDropCascade = null; }

  /*------------------------------------------------------------------*/
  /** convert an MSSQL SQLServerException into an SQLFeatureNotSupportedException.
   * @param sse
   * @throws SQLFeatureNotSupportedException
   */
  private void throwNotSupportedException(SQLServerException sse)
    throws SQLFeatureNotSupportedException
  {
    throw new SQLFeatureNotSupportedException("MSSQL Exception!", sse);
  } /* throwFeatureNotSupportedSqlException */

  /*------------------------------------------------------------------*/
  /** constructor
   * @param connWrapped connection to be wrapped.
   */
  public MsSqlConnection(Connection connWrapped)
    throws SQLException
  {
    super(connWrapped);
    if (connWrapped != null)
    {
      /* a closer approach to ISO SQL syntax */
      Statement stmt = super.createStatement();
      stmt.executeUpdate("SET ANSI_DEFAULTS ON");
      stmt.close();
    }
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * wraps statement.
   */
  @Override
  public Statement createStatement()
      throws SQLException
  {
    Statement stmt = new MsSqlStatement(super.createStatement(),this);
    return stmt;
  } /* createStatement */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql)
    throws SQLException
  {
    String sNative = nativeSQL(sql);
    PreparedStatement ps = super.prepareStatement(sNative); 
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
  public String nativeSQL(String sql) 
    throws SQLException
  {
    _il.enter(sql);
    BaseDatabaseMetaData dmd = (BaseDatabaseMetaData)getMetaData();
    MsSqlSqlFactory sf = new MsSqlSqlFactory();
    SqlStatement ss = sf.newSqlStatement();
    ss.parse(sql);
    QuerySpecification qs = ss.getQuerySpecification();
    if (qs != null)
    {
      /* store default catalog and schema for further processing ... */ 
      String sDefaultCatalog = null;
      String sDefaultSchema = null;
      Statement stmt = unwrap(Connection.class).createStatement();
      ResultSet rs = stmt.executeQuery("select DB_NAME() AS DB_NAME, SCHEMA_NAME() AS SCHEMA_NAME");
      while (rs.next())
      {
        sDefaultCatalog = rs.getString("DB_NAME");
        sDefaultSchema = rs.getString("SCHEMA_NAME");
      }
      rs.close();
      stmt.close();
      sf.startQuery(ss.getQuerySpecification(), sDefaultCatalog, sDefaultSchema, dmd);
    }
    /* check, whether a DROP TABLE with option CASCADE was parsed. */
    resetTableDropCascade();
    DdlStatement ds = ss.getDdlStatement();
    if (ds != null)
    {
      DropTableStatement dts = ds.getDropTableStatement();
      if (dts != null)
      {
        if (dts.getDropBehavior() == DropBehavior.CASCADE)
          _qiTableDropCascade = dts.getTableName();
      }
    }
    sql = ss.format();
    _il.exit(sql);
    return sql;
  } /* nativeSQL */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * wraps database meta data.
   */
  @Override
  public DatabaseMetaData getMetaData()
    throws SQLException
  {
    DatabaseMetaData dmd = new MsSqlDatabaseMetaData(super.getMetaData(),this);
    return dmd;
  } /* getMetadata */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} 
   * wraps statement.
   */
  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency) 
    throws SQLException
  {
    Statement stmt = new MsSqlStatement(super.createStatement(resultSetType, resultSetConcurrency),this);
    return stmt;
  } /* createStatement */

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
  public void releaseSavepoint(Savepoint savepoint)
    throws SQLException
  {
    try { super.releaseSavepoint(savepoint); }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
  } /* releaseSavePoint */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} 
   * wraps statement.
   */
  @Override
  public Statement createStatement(int resultSetType,
    int resultSetConcurrency, int resultSetHoldability)
    throws SQLException
  {
    Statement stmt = null;
    try
    {
      stmt = new MsSqlStatement(super.createStatement(resultSetType, 
        resultSetConcurrency, resultSetHoldability),this);
    }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
    return stmt;
  } /* createStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql,
    int resultSetType, int resultSetConcurrency,
    int resultSetHoldability)
    throws SQLException
  {
    PreparedStatement ps = null;
    try { ps = super.prepareStatement(nativeSQL(sql), resultSetType, 
      resultSetConcurrency, resultSetHoldability); }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
    return ps;
  } /* prepareStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public CallableStatement prepareCall(String sql, int resultSetType,
    int resultSetConcurrency, int resultSetHoldability)
    throws SQLException
  {
    CallableStatement cs = null;
    try { cs = super.prepareCall(nativeSQL(sql), resultSetType, 
      resultSetConcurrency, resultSetHoldability); }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
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

  @Override
  public Blob createDatalinkObject() throws SQLException {
    return createBlob();
  }

} /* class MsSqlConnection */
