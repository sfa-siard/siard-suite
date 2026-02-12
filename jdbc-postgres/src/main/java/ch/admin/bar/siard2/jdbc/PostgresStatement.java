/*======================================================================
PostgresStatement implements a wrapped Postgres Statement.
Application : SIARD2
Description : PostgresStatement implements a wrapped Postgres Statement.
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 25.07.2019, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.util.*;

import org.postgresql.*;
import org.postgresql.largeobject.*;

import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.dml.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.postgres.*;

/*====================================================================*/
/** PostgresStatement implements a wrapped Postgres Statement.
 * @author Hartwig Thomas
 */
public class PostgresStatement
  extends BaseStatement
  implements Statement
{
  protected Connection _conn;
  private long _lParse = -1;
  private long _lExecute = -1;
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param stmtWrapped statement to be wrapped.
   * @param conn wrapped connection.
   */
  public PostgresStatement(Statement stmtWrapped, Connection conn)
    throws SQLException
  {
    super(stmtWrapped);
    _conn = conn;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Connection getConnection() 
    throws SQLException
  {
    return _conn;
  } /* getConnection */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Return PostgresResultSet. 
   */
  @Override
  public ResultSet executeQuery(String sql) throws SQLException
  {
    ResultSet rs = null;
    _lParse = System.currentTimeMillis();
    String sNative = getConnection().nativeSQL(sql);
    _lExecute = System.currentTimeMillis();
    _lParse = _lExecute - _lParse;
    rs = new PostgresResultSet(super.executeQuery(sNative),this);
    _lExecute = System.currentTimeMillis() - _lExecute;
    return rs;
  } /* executeQuery */

  /*------------------------------------------------------------------*/
  /** unlink all indirectly reference large objects of the table records
   * to be deleted.
   */
  private void prepareUpdateDeleteStatement(DeleteStatement ds)
    throws SQLException
  {
    /* determine LOB columns of table */
    DatabaseMetaData dmd = _conn.getMetaData();
    ResultSet rsColumns = dmd.getColumns(null, 
      ((BaseDatabaseMetaData)dmd).toPattern(ds.getTableName().getSchema().toLowerCase()),
      ((BaseDatabaseMetaData)dmd).toPattern(ds.getTableName().getName().toLowerCase()),
      "%");
    Set<Integer> setLobPositions = new HashSet<Integer>();
    while (rsColumns.next())
    {
      int iDataType = rsColumns.getInt("DATA_TYPE");
      if ((iDataType == Types.BLOB) ||
          (iDataType == Types.CLOB))
        setLobPositions.add(Integer.valueOf(rsColumns.getInt("ORDINAL_POSITION")));
    }
    rsColumns.close();
    if (!setLobPositions.isEmpty())
    {
      /* create a SELECT statement with "DELETE" replaced by "SELECT *". */
      PostgresSqlFactory psf = new PostgresSqlFactory();
      TablePrimary tp = psf.newTablePrimary();
      tp.initialize(ds.getTableName());
      TableReference tr = psf.newTableReference();
      tr.initialize(tp, null, null, null, null, null, new ArrayList<Identifier> (), null, null);
      List<TableReference> listTr = new ArrayList<TableReference>();
      listTr.add(tr);
      QuerySpecification qs = psf.newQuerySpecification();
      qs.initialize(true, new ArrayList<SelectSublist>(), listTr, ds.getBooleanValueExpression());
      String sSql = qs.format();
      
      LargeObjectManager lobj = ((PGConnection)_conn.unwrap(Connection.class)).getLargeObjectAPI();
      ResultSet rs = executeQuery(sSql);
      while (rs.next()) // loop over records to be deleted
      {
        for (Iterator<Integer> iterLobPosition = setLobPositions.iterator(); iterLobPosition.hasNext(); )
        {
          Integer iLobPosition = iterLobPosition.next();
          long loid = rs.unwrap(ResultSet.class).getLong(iLobPosition.intValue());
          lobj.unlink(loid);
        }
      }
      rs.close();
      /* commit the unlink statements */
      _conn.commit();
    }
  } /* prepareUpdateDeleteStatement */
  
  /*------------------------------------------------------------------*/
  /** convert to native SQL and handle executeUpdate preparation.
   * @param sSql standard SQL.
   * @return native SQL.
   */
  private String nativeSqlUpdate(String sSql)
    throws SQLException
  {
    PostgresSqlFactory psf = new PostgresSqlFactory();
    psf.setConnection((PostgresConnection)_conn);
    SqlStatement ss = psf.newSqlStatement();
    ss.parse(sSql);
    DmlStatement dms = ss.getDmlStatement();
    if (dms != null)
    {
      DeleteStatement ds = dms.getDeleteStatement();
      if (ds != null)
        prepareUpdateDeleteStatement(ds);
    }
    sSql = ss.format();
    return sSql;
  }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} 
   */
  @Override
  public int executeUpdate(String sql) throws SQLException
  {
    int iResult = -1;
    String sNative = nativeSqlUpdate(sql);
    iResult = super.executeUpdate(sNative);
    return iResult;
  } /* executeUpdate */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   */
  @Override
  public int executeUpdate(String sql, int autoGeneratedKeys)
      throws SQLException
  {
    int iResult = -1;
    String sNative = nativeSqlUpdate(sql);
    iResult = super.executeUpdate(sNative, autoGeneratedKeys); 
    return iResult;
  } /* executeUpdate */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} 
   * Convert JdbcSQLException from Postgres into SQLFeatureNotSupportedError.
   */
  @Override
  public int executeUpdate(String sql, int[] columnIndexes)
      throws SQLException
  {
    int iResult = -1;
    try 
    { 
      String sNative = nativeSqlUpdate(sql);
      iResult = super.executeUpdate(sNative, columnIndexes);
    }
    catch(SQLException se)
    {
      if ("0A000".equals(se.getSQLState()))
        throw new SQLFeatureNotSupportedException(se.getMessage());
      else
        throw se;
    }
    return iResult;
  } /* executeUpdate */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} 
   * Convert JdbcSQLException from MSSQL into SQLFeatureNotSupportedError.
   */
  @Override
  public int executeUpdate(String sql, String[] columnNames)
      throws SQLException
  {
    int iResult = -1;
    String sNative = nativeSqlUpdate(sql);
    iResult = super.executeUpdate(sNative, columnNames); 
    return iResult;
  } /* executeUpdate */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean execute(String sql) throws SQLException
  {
    boolean bResult = false;
    String sNative = getConnection().nativeSQL(sql);
    bResult = super.execute(sNative);  
    return bResult;
  } /* execute */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean execute(String sql, int autoGeneratedKeys)
      throws SQLException
  {
    boolean bResult = false;
    String sNative = getConnection().nativeSQL(sql);
    bResult = super.execute(sNative, autoGeneratedKeys);  
    return bResult;
  } /* execute */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean execute(String sql, int[] columnIndexes)
      throws SQLException
  {
    boolean bResult = false;
    String sNative = getConnection().nativeSQL(sql);
    try { bResult = super.execute(sNative, columnIndexes); }
    catch(SQLException se)
    {
      if ("0A000".equals(se.getSQLState()))
        throw new SQLFeatureNotSupportedException(se.getMessage());
      else
        throw se;
    }
    return bResult;
  } /* execute */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean execute(String sql, String[] columnNames)
      throws SQLException
  {
    boolean bResult = false;
    String sNative = getConnection().nativeSQL(sql);
    bResult = super.execute(sNative, columnNames);  
    return bResult;
  } /* execute */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * Return MsSqlResultSet. 
   */
  @Override
  public ResultSet getResultSet() throws SQLException
  {
    PostgresResultSet prs = null;
    ResultSet rs = super.getResultSet();
    if (rs != null)
      prs = new PostgresResultSet(rs,this);
    return prs;
  } /* getResultSet */

} /* class PostgresStatement */
