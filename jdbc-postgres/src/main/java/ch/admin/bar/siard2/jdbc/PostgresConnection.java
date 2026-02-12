/*======================================================================
PostgresConnection implements a wrapped PostgreSQL Connection.
Application : SIARD2
Description : PostgresConnection implements a wrapped PostgreSQL Connection.
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 22.07.2019, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import org.postgresql.jdbc.*;
import org.postgresql.largeobject.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.utils.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.postgres.*;

/*====================================================================*/
/** PostgresConnection implements a wrapped PostgreSQL Connection.
 * @author Hartwig Thomas
 */
public class PostgresConnection
extends BaseConnection
implements Connection
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(PostgresConnection.class.getName());
  /*** meta data */
  private DatabaseMetaData _dmd = null;
  
  private void createDomain(String sDomain, PostgresType postgresType)
    throws SQLException
  {
    /* if it exists already, do not do anything */
    try
    {
      String sSql = "CREATE DOMAIN public."+sDomain+" AS "+postgresType;
      Statement stmt = super.createStatement();
      stmt.executeUpdate(sSql);
      stmt.close();
      super.commit();
      stmt = super.createStatement();
      sSql = "GRANT ALL ON public."+sDomain+" TO public";
      stmt.executeUpdate(sSql);
      super.commit();
    }
    catch(SQLException se)
    {
      /* terminate transaction */
      try { super.rollback(); }
      catch(SQLException seRollback) { throw new SQLException("Rollback failed with "+EU.getExceptionMessage(seRollback)); }
    }
  } /* executeCreate */

  /*------------------------------------------------------------------*/
  /** constructor
   * @param connWrapped connection to be wrapped.
   */
  public PostgresConnection(Connection connWrapped)
    throws SQLException
  {
    super(connWrapped);
    boolean bAutoCommit = super.getAutoCommit();
    super.setAutoCommit(false);
    /* create standard domains BLOB, CLOB and NCLOB for oid */
    createDomain(PreType.BLOB.getKeyword(), PostgresType.OID);
    createDomain(PreType.CLOB.getKeyword(), PostgresType.OID);
    DatabaseMetaData dmd = super.getMetaData();
    if (dmd != null)
    {
      if (dmd instanceof PostgresDatabaseMetaData)
        throw new SQLException("PostgresConnection() returned a wrapped meta data instance!");
      dmd = new PostgresDatabaseMetaData(dmd,this);
    }
    _dmd = dmd;
    super.setAutoCommit(bAutoCommit);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * returns wrapped database meta data.
   */
  @Override
  public DatabaseMetaData getMetaData()
    throws SQLException
  {
    return _dmd;
  } /* getMetadata */

  /*------------------------------------------------------------------*/
  /**
   * {@inheritDoc} wraps statement.
   */
  @Override
  public Statement createStatement() throws SQLException 
  {
    Statement stmt = super.createStatement();
    return new PostgresStatement(stmt,this);
  } /* createStatement */

  /*------------------------------------------------------------------*/
  /**
   * {@inheritDoc} wraps statement.
   */
  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException 
  {
    Statement stmt = super.createStatement(resultSetType, resultSetConcurrency);
    return new PostgresStatement(stmt,this);
  } /* createStatement */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Statement createStatement(int resultSetType,
    int resultSetConcurrency, int resultSetHoldability)
    throws SQLException
  {
    Statement stmt = super.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    return new PostgresStatement(stmt,this);
  } /* createStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql) throws SQLException 
  {
    String sNative = nativeSQL(sql);
    PreparedStatement ps = super.prepareStatement(sNative);
    return new PostgresPreparedStatement(ps,this);
  } /* prepareStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException 
  {
    String sNative = nativeSQL(sql);
    PreparedStatement ps = super.prepareStatement(sNative, resultSetType, resultSetConcurrency);
    return new PostgresPreparedStatement(ps,this);
  } /* prepareStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
  {
    String sNative = nativeSQL(sql);
    PreparedStatement ps = super.prepareStatement(sNative, resultSetType, resultSetConcurrency, resultSetHoldability);
    return new PostgresPreparedStatement(ps,this);
  } /* prepareStatement */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException 
  {
    String sNative = nativeSQL(sql);
    PreparedStatement ps = super.prepareStatement(sNative, autoGeneratedKeys);
    return new PostgresPreparedStatement(ps,this);
  } /* prepareStatement */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException 
  {
    String sNative = nativeSQL(sql);
    PreparedStatement ps = super.prepareStatement(sNative, columnIndexes);
    return new PostgresPreparedStatement(ps,this);
  } /* prepareStatement */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException 
  {
    String sNative = nativeSQL(sql);
    PreparedStatement ps = super.prepareStatement(sNative, columnNames);
    return new PostgresPreparedStatement(ps,this);
  } /* prepareStatement */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public CallableStatement prepareCall(String sql) throws SQLException 
  {
    String sNative = nativeSQL(sql);
    CallableStatement cs = super.prepareCall(sNative);
    return cs;
  } /* prepareCall */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException 
  {
    String sNative = nativeSQL(sql);
    CallableStatement cs = super.prepareCall(sNative, resultSetType, resultSetConcurrency);
    return cs;
  } /* prepareCall */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException 
  {
    String sNative = nativeSQL(sql);
    CallableStatement cs = super.prepareCall(sNative, resultSetType, resultSetConcurrency, resultSetHoldability);
    return cs;
  } /* prepareCall */

  private long createLob()
    throws SQLException
  {
    PgConnection pgconn = (PgConnection)unwrap(Connection.class);
    LargeObjectManager lobj = pgconn.getLargeObjectAPI();
    long lOid = lobj.createLO();
    String sSql = "GRANT ALL ON LARGE OBJECT "+String.valueOf(lOid)+" TO PUBLIC";
    Statement stmt = pgconn.createStatement();
    stmt.executeUpdate(sSql);
    stmt.close();
    return lOid;
  }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Clob createClob()
    throws SQLException
  {
    long lOid = createLob();
    return new PostgresClob(this,lOid);
  } /* createClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public NClob createNClob()
    throws SQLException
  {
    long lOid = createLob();
    return new PostgresNClob(this,lOid);
  } /* createNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Blob createBlob()
    throws SQLException
  {
    long lOid = createLob();
    return new PostgresBlob(this,lOid);
  }

  @Override
  public Blob createDatalinkObject() throws SQLException {
    return createBlob();
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   */
  @Override
  public Struct createStruct(String typeName, Object[] attributes)
    throws SQLException
  {
    return new PostgresStruct(typeName,attributes);
  } /* createStruct */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Array createArrayOf(String typeName, Object[] elements)
    throws SQLException
  {
    int iParen = typeName.indexOf("(");
    if (iParen >= 0)
      typeName = typeName.substring(0,iParen).trim();
    PreType pt = PreType.getByKeyword(typeName);
    PostgresType pgt = PostgresType.getByPreType(pt);
    PgArray pgarray = (PgArray)super.createArrayOf(pgt.getKeyword(), elements);
    return new PostgresArray(pgarray);
  } /* createArrayOf */

} /* class PostgresConnection */
