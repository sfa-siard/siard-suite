/*======================================================================
OracleDataSource implements a wrapped Oracle DataSource.
Version     : $Id: $
Application : SIARD2
Description : OracleDataSource implements a wrapped Oracle DataSource.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rueti ZH, Switzerland
Created    : 15.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import ch.enterag.utils.logging.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;
import ch.admin.bar.siard2.oracle.*;
import oracle.jdbc.driver.*;

/*====================================================================*/
/**
 * OracleConnection implements a wrapped Oracle Connection.
 * 
 * @author Simon Jutz
 */
public class OracleConnection extends BaseConnection implements Connection {
	/** logger */
	private static IndentLogger _il = IndentLogger.getIndentLogger(OracleConnection.class.getName());
	private static final int iBUFFER_SIZE = 8192;

	/*------------------------------------------------------------------*/
	/** convert an OracleSQLException into an SQLException.
	 * @param ose SQLServerException
	 * @throws SQLException
	 */
	private void throwSqlException(OracleSQLException ose) throws SQLException {
		throw new SQLException("Oracle exception!",ose);
	} /* throwSqlException */

	/*------------------------------------------------------------------*/
	/** convert an OracleSQLException into an
	 * SQLFeatureNotSupportedException.
	 * @param ose
	 * @throws SQLFeatureNotSupportedException
	 */
	private void throwNotSupportedException(OracleSQLException ose) throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("ose Exception!", ose);
	} /* throwFeatureNotSupportedSqlException */

	/*------------------------------------------------------------------*/
	/** constructor
	 * @param connWrapped connection to wrapped
	 */
	public OracleConnection(Connection connWrapped) throws SQLException {
		super(connWrapped);
		if (connWrapped != null)
		{
		  _il.enter(connWrapped);
  		Statement stmt = super.createStatement();
  		String sSql = "ALTER SESSION SET NLS_LENGTH_SEMANTICS=CHAR";
  		_il.event("Unwrapped DML: "+sSql);
  		stmt.executeUpdate(sSql);
  		stmt.close();
  		_il.exit();
		}
	} /* constructor */

	/*------------------------------------------------------------------*/
	/**
	 * {@inheritDoc} wraps statement.
	 */
	@Override
	public Statement createStatement() throws SQLException 
	{
		Statement stmt = new OracleStatement(super.createStatement());
		return stmt;
	} /* createStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException 
	{
		PreparedStatement ps = super.prepareStatement(nativeSQL(sql));
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public CallableStatement prepareCall(String sql) throws SQLException
	{
		CallableStatement cs = super.prepareCall(nativeSQL(sql));
		return cs;
	} /* prepareCall */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public String nativeSQL(String sql) throws SQLException {
		_il.enter(sql);
		OracleSqlFactory osf = new OracleSqlFactory();
		osf.setConnection(this);
		SqlStatement ss = osf.newSqlStatement();
		ss.parse(sql);
		sql = ss.format();
		_il.exit(sql);
		return sql;
	} /* nativeSQL */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		try {
			super.setAutoCommit(autoCommit);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* setAutoCommit */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public boolean getAutoCommit() throws SQLException {
		boolean bAutoCommit = false;
		try {
			bAutoCommit = super.getAutoCommit();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return bAutoCommit;
	} /* getAutoCommit */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void commit() throws SQLException {
		try {
			super.commit();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* commit */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void rollback() throws SQLException {
		try {
			super.rollback();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* rollback */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void close() throws SQLException {
		try {
			super.close();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* close */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public boolean isClosed() throws SQLException {
		boolean bIsClosed = false;
		try {
			bIsClosed = super.isClosed();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return bIsClosed;
	} /* isClosed */

	/*------------------------------------------------------------------*/
	/**
	 * {@inheritDoc} wraps database meta data.
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		DatabaseMetaData dmd = null;
		try {
			dmd = new OracleDatabaseMetaData(super.getMetaData());
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return dmd;
	} /* getMetadata */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		try {
			super.setReadOnly(readOnly);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* setReadOnly */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public boolean isReadOnly() throws SQLException {
		boolean bIsReadOnly = false;
		try {
			bIsReadOnly = super.isReadOnly();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return bIsReadOnly;
	} /* isReadOnly */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void setCatalog(String catalog) throws SQLException {
		try {
			super.setCatalog(catalog);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* setCatalog */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public String getCatalog() throws SQLException {
		String sCatalog = null;
		try {
			sCatalog = super.getCatalog();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return sCatalog;
	} /* getCatalog */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		try {
			super.setTransactionIsolation(level);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* setTransactionIsolation */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public int getTransactionIsolation() throws SQLException {
		int iTransactionIsolation = -1;
		try {
			iTransactionIsolation = super.getTransactionIsolation();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return iTransactionIsolation;
	} /* getTransactionIsolation */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		SQLWarning sw = null;
		try {
			sw = super.getWarnings();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return sw;
	} /* getWarnings */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void clearWarnings() throws SQLException {
		try {
			super.clearWarnings();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* clearWarnings */

	/*------------------------------------------------------------------*/
	/**
	 * {@inheritDoc} wraps statement.
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		Statement stmt = null;
		try
		{
			stmt = new OracleStatement(super.createStatement(resultSetType, resultSetConcurrency));
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return stmt;
	} /* createStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		PreparedStatement ps = null;
		try 
		{
			ps = super.prepareStatement(nativeSQL(sql), resultSetType, resultSetConcurrency);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		CallableStatement cs = null;
		try {
			cs = super.prepareCall(nativeSQL(sql), resultSetType, resultSetConcurrency);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return cs;
	} /* prepareCall */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		Map<String, Class<?>> mapTypes = null;
		try {
			mapTypes = super.getTypeMap();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return mapTypes;
	} /* getTypeMap */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		try {
			super.setTypeMap(map);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* setTypeMap */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void setHoldability(int holdability) throws SQLException {
		try {
			super.setHoldability(holdability);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* setHoldability */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public int getHoldability() throws SQLException {
		int iHoldability = -1;
		try {
			iHoldability = super.getHoldability();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return iHoldability;
	} /* getHoldability */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public Savepoint setSavepoint() throws SQLException {
		Savepoint sp = null;
		try {
			sp = super.setSavepoint();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return sp;
	} /* setSavePoint */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		Savepoint sp = null;
		try {
			sp = super.setSavepoint(name);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return sp;
	} /* setSavePoint */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		try {
			super.rollback(savepoint);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* rollback */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		try {
			super.releaseSavepoint(savepoint);
		} catch (OracleSQLException ose) {
			throwNotSupportedException(ose);
		} catch (SQLException se) {
		  throw new SQLFeatureNotSupportedException(se);
		}
	} /* releaseSavePoint */

	/*------------------------------------------------------------------*/
	/**
	 * {@inheritDoc} wraps statement.
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		Statement stmt = null;
		try 
		{
			stmt = new OracleStatement(
					super.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
		} catch (OracleSQLException ose) {
			throwNotSupportedException(ose);
		}
		return stmt;
	} /* createStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		PreparedStatement ps = null;
		try 
		{
			ps = super.prepareStatement(nativeSQL(sql), resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch (OracleSQLException ose) {
			throwNotSupportedException(ose);
		}
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		CallableStatement cs = null;
		try {
			cs = super.prepareCall(nativeSQL(sql), resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch (OracleSQLException ose) {
			throwNotSupportedException(ose);
		}
		return cs;
	} /* prepareCall */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = super.prepareStatement(nativeSQL(sql), autoGeneratedKeys);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = super.prepareStatement(nativeSQL(sql), columnIndexes);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = super.prepareStatement(nativeSQL(sql), columnNames);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return ps;
	} /* prepareStatement */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public Clob createClob() throws SQLException {
		Clob clob = null;
		try {
			clob = super.createClob();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return clob;
	} /* createClob */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public Blob createBlob() throws SQLException {
		Blob blob = null;
		try {
			blob = super.createBlob();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return blob;
	} /* createBlob */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public NClob createNClob() throws SQLException 
	{
		NClob nclob = super.createNClob();
		return nclob;
	} /* createNClob */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public SQLXML createSQLXML() throws SQLException {
		SQLXML sqlxml = null;
		try {
			sqlxml = super.createSQLXML();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		catch (ClassCastException cce) {
		  throw new SQLFeatureNotSupportedException(cce);
		}
		return sqlxml;
	} /* createSQLXML */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public boolean isValid(int timeout) throws SQLException {
		boolean bIsValid = false;
		try {
			bIsValid = super.isValid(timeout);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return bIsValid;
	} /* isValid */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		super.setClientInfo(name, value);
	} /* setClientInfo */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		super.setClientInfo(properties);
	} /* setClientInfo */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public String getClientInfo(String name) throws SQLException {
		String sClientInfo = null;
		try {
			sClientInfo = super.getClientInfo(name);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return sClientInfo;
	} /* getClientInfo */

  /*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public Properties getClientInfo() throws SQLException {
		Properties propClientInfo = null;
		try {
			propClientInfo = super.getClientInfo();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return propClientInfo;
	} /* getClientInfo */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
  @Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    Array array = new OracleArray(this,typeName,elements);
		return array;
	} /* createArrayOf */

	/*------------------------------------------------------------------*/
	/**
	 * {@inheritDoc} The names and types of the attributes are derived from the
	 * type map.
	 */
	@Override
	public Struct createStruct(String typeName, Object[] attributes) 
	  throws SQLException {
		Struct struct = null;
		try 
		{
      oracle.jdbc.OracleConnection connOracle = (oracle.jdbc.OracleConnection)unwrap(Connection.class);
		  // TODO: Find which type the attributes should be (Blob, Clob, SQLXML, Struct, Array, ...
		  // and create the appropriate "unwrapped" structures with the given content.
		  for (int i = 0; i < attributes.length; i++)
		  {
		    if (attributes[i] instanceof Reader)
		    {
		      Reader rdr = (Reader)attributes[i];
          Clob clob = connOracle.createClob();
          Writer wr = clob.setCharacterStream(1L);
          char[] cbuf = new char[iBUFFER_SIZE];
          for (int iRead = rdr.read(cbuf); iRead != -1; iRead = rdr.read(cbuf))
            wr.write(cbuf,0,iRead);
          rdr.close();
          wr.close();
          attributes[i] = clob;
		    }
		    else if (attributes[i] instanceof InputStream)
        {
          InputStream is = (InputStream)attributes[i];
          Blob blob = connOracle.createBlob();
          OutputStream os = blob.setBinaryStream(1L);
          byte[] buffer = new byte[iBUFFER_SIZE];
          for (int iRead = is.read(buffer); iRead != -1; iRead = is.read(buffer))
            os.write(buffer,0,iRead);
          is.close();
          os.close();
          attributes[i] = blob;
        }
		    else if (attributes[i] instanceof byte[])
		    {
		      byte[] buf = (byte[])attributes[i];
		      if (buf.length > 4000)
		      {
		        Blob blob = connOracle.createBlob();
            OutputStream os = blob.setBinaryStream(1L);
            os.write(buf);
            os.close();
            attributes[i] = blob;
		      }
		    }
        else if (attributes[i] instanceof String)
        {
          String s = (String)attributes[i];
          if (s.length() > 2000)
          {
            Clob clob = connOracle.createClob();
            Writer wr = clob.setCharacterStream(1L);
            wr.write(s);
            wr.close();
            attributes[i] = clob;
          }
        }
		  }
		  struct = connOracle.createStruct(typeName, attributes); 
    }
		catch (IOException ie) { throw new SQLException("Input stream could not be read!",ie); }
		return struct;
	} /* createStruct */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void setSchema(String schema) throws SQLException {
		try {
			super.setSchema(schema);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* setSchema */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public String getSchema() throws SQLException {
		String sSchema = null;
		try {
			sSchema = super.getSchema();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return sSchema;
	} /* getSchema */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void abort(Executor executor) throws SQLException {
		try {
			super.abort(executor);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* abort */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		try {
			super.setNetworkTimeout(executor, milliseconds);
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
	} /* setNetworkTimeout */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public int getNetworkTimeout() throws SQLException {
		int iNetWorkTimeout = 0;
		try {
			iNetWorkTimeout = super.getNetworkTimeout();
		} catch (OracleSQLException ose) {
			throwSqlException(ose);
		}
		return iNetWorkTimeout;
	} /* getNetworkTimeout */

	@Override
	public Blob createDatalinkObject() throws SQLException {
		return createBlob();
	}

} /* class OracleConnection */
