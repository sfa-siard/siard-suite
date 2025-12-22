/*======================================================================
PostgresPreparedStatement implements a wrapped Postgres PreparedStatement.
Application : SIARD2
Description : PostgresPreparedStatement implements a wrapped Postgres PreparedStatement.
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 09.08.2019, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.math.*;
import java.net.*;
import java.sql.*;
import java.util.Calendar;

import org.postgresql.*;
import org.postgresql.largeobject.*;

import ch.enterag.utils.*;
import ch.admin.bar.siard2.postgres.*;

/*====================================================================*/
/** PostgresPreparedStatement implements a wrapped Postgres PreparedStatement.
 * @author Hartwig Thomas
 */
public class PostgresPreparedStatement
  extends PostgresStatement
  implements PreparedStatement
{
  private static final int iBUFSIZ = 65536;
  /** wrapped prepared statement */
  private PreparedStatement _pstmtWrapped = null;
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param stmtWrapped statement to be wrapped.
   * @param conn wrapped connection.
   */
  public PostgresPreparedStatement(PreparedStatement pstmtWrapped, Connection conn)
    throws SQLException
  {
    super(pstmtWrapped,conn);
    _pstmtWrapped = pstmtWrapped;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSetMetaData getMetaData() throws SQLException
  {
    return new PostgresResultSetMetaData(_pstmtWrapped.getMetaData(),this);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException
  {
    return _pstmtWrapped.getParameterMetaData();
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSet executeQuery() throws SQLException
  {
    return new PostgresResultSet(_pstmtWrapped.executeQuery(),this);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int executeUpdate() throws SQLException
  {
    return _pstmtWrapped.executeUpdate();
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean execute() throws SQLException
  {
    return _pstmtWrapped.execute();
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void addBatch() throws SQLException
  {
    _pstmtWrapped.addBatch();
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void clearParameters() throws SQLException
  {
    _pstmtWrapped.clearParameters();
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setNull(int parameterIndex, int sqlType)
    throws SQLException
  {
    _pstmtWrapped.setNull(parameterIndex, sqlType);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setNull(int parameterIndex, int sqlType, String typeName)
    throws SQLException
  {
    _pstmtWrapped.setNull(parameterIndex, sqlType, typeName);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException
  {
    _pstmtWrapped.setDate(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal)
    throws SQLException
  {
    _pstmtWrapped.setDate(parameterIndex, x, cal);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException
  {
    _pstmtWrapped.setTime(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal)
    throws SQLException
  {
    _pstmtWrapped.setTime(parameterIndex, x, cal);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setTimestamp(int parameterIndex, Timestamp x)
    throws SQLException
  {
    _pstmtWrapped.setTimestamp(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setTimestamp(int parameterIndex, Timestamp x,
    Calendar cal) throws SQLException
  {
    _pstmtWrapped.setTimestamp(parameterIndex, x, cal);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBoolean(int parameterIndex, boolean x)
    throws SQLException
  {
    _pstmtWrapped.setBoolean(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException
  {
    _pstmtWrapped.setByte(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setShort(int parameterIndex, short x) throws SQLException
  {
    _pstmtWrapped.setShort(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setInt(int parameterIndex, int x) throws SQLException
  {
    _pstmtWrapped.setInt(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setLong(int parameterIndex, long x) throws SQLException
  {
    _pstmtWrapped.setLong(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException
  {
    _pstmtWrapped.setFloat(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setDouble(int parameterIndex, double x)
    throws SQLException
  {
    _pstmtWrapped.setDouble(parameterIndex, x);
  }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x)
    throws SQLException
  {
    _pstmtWrapped.setBigDecimal(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setString(int parameterIndex, String x)
    throws SQLException
  {
    _pstmtWrapped.setString(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setNString(int parameterIndex, String value)
    throws SQLException
  {
    _pstmtWrapped.setString(parameterIndex, value);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setClob(int parameterIndex, Clob x) throws SQLException
  {
    PostgresClob px = (PostgresClob)x;
    _pstmtWrapped.setClob(parameterIndex, px.unwrap(Clob.class));
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setClob(int parameterIndex, Reader reader)
    throws SQLException
  {
    _pstmtWrapped.setClob(parameterIndex, reader);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setClob(int parameterIndex, Reader reader, long length)
    throws SQLException
  {
    _pstmtWrapped.setClob(parameterIndex, reader, length);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setNClob(int parameterIndex, NClob value)
    throws SQLException
  {
    setClob(parameterIndex, value);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setNClob(int parameterIndex, Reader reader)
    throws SQLException
  {
    setClob(parameterIndex, reader);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setNClob(int parameterIndex, Reader reader, long length)
    throws SQLException
  {
    setClob(parameterIndex, reader, length);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setAsciiStream(int parameterIndex, InputStream x)
    throws SQLException
  {
    try { setCharacterStream(parameterIndex,new InputStreamReader(x,"ASCII")); }
    catch(UnsupportedEncodingException uee) {}
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setAsciiStream(int parameterIndex, InputStream x,
    int length) throws SQLException
  {
    try { setCharacterStream(parameterIndex,new InputStreamReader(x,"ASCII"),length); }
    catch(UnsupportedEncodingException uee) {}
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setAsciiStream(int parameterIndex, InputStream x,
    long length) throws SQLException
  {
    try { setCharacterStream(parameterIndex,new InputStreamReader(x,"ASCII"),length); }
    catch(UnsupportedEncodingException uee) {}
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setUnicodeStream(int parameterIndex, InputStream x,
    int length) throws SQLException
  {
    try { setCharacterStream(parameterIndex,new InputStreamReader(x,"UTF-8"),length); }
    catch(UnsupportedEncodingException uee) {}
  }

  private long createClob(int parameterIndex, Reader reader)
    throws SQLException
  {
    long lLength = -1;
    try
    {
      LargeObjectManager lobj = ((PGConnection)_conn.unwrap(Connection.class)).getLargeObjectAPI();
      long loid = lobj.createLO();
      LargeObject lo = lobj.open(loid);
      char[] cbuf = new char[iBUFSIZ];
      lLength = 0;
      long lWritten = 0;
      for (int iRead = reader.read(cbuf); iRead != -1; iRead = reader.read(cbuf))
      {
        byte[] buf = SU.putUtf8CharArray(cbuf,0,iRead);
        lo.write(buf,0,buf.length);
        lWritten = lWritten + buf.length;
        lLength = lLength + iRead;
      }
      lo.close();
      reader.close();
      String sSql = "GRANT ALL ON LARGE OBJECT "+String.valueOf(loid)+" TO PUBLIC";
      Statement stmt = (_conn.unwrap(Connection.class)).createStatement();
      stmt.executeUpdate(sSql);
      stmt.close();
      _pstmtWrapped.setLong(parameterIndex, loid);
    }
    catch(IOException ie) { throw new SQLException("Reading CharacterStream failed"+EU.getExceptionMessage(ie)+"!"); }
    return lLength;
  }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setCharacterStream(int parameterIndex, Reader reader)
    throws SQLException
  {
    ParameterMetaData pmd = _pstmtWrapped.getParameterMetaData();
    if (!PostgresType.CLOB.getKeyword().equals(pmd.getParameterTypeName(parameterIndex)))
      _pstmtWrapped.setCharacterStream(parameterIndex, reader);
    else
      createClob(parameterIndex, reader);
  } /* setCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setCharacterStream(int parameterIndex, Reader reader,
    int length) throws SQLException
  {
    setCharacterStream(parameterIndex, reader, (long)length);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setCharacterStream(int parameterIndex, Reader reader,
    long length) throws SQLException
  {
    ParameterMetaData pmd = _pstmtWrapped.getParameterMetaData();
    if (!PostgresType.CLOB.getKeyword().equals(pmd.getParameterTypeName(parameterIndex)))
      _pstmtWrapped.setCharacterStream(parameterIndex, reader);
    else
    {
      long lLength = createClob(parameterIndex, reader);
      if (length != lLength)
        throw new SQLException("Invalid length in setCharacterStream of PreparedStatement!");
    }
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setNCharacterStream(int parameterIndex, Reader value)
    throws SQLException
  {
    setCharacterStream(parameterIndex, value);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setNCharacterStream(int parameterIndex, Reader value,
    long length) throws SQLException
  {
    setCharacterStream(parameterIndex, value, length);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject)
    throws SQLException
  {
    _pstmtWrapped.setSQLXML(parameterIndex, xmlObject);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException
  {
    _pstmtWrapped.setURL(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException
  {
    _pstmtWrapped.setRowId(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setRef(int parameterIndex, Ref x) throws SQLException
  {
    _pstmtWrapped.setRef(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException
  {
    _pstmtWrapped.setBytes(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBlob(int parameterIndex, Blob x) throws SQLException
  {
    PostgresBlob px = (PostgresBlob)x;
    _pstmtWrapped.setBlob(parameterIndex, px.unwrap(Blob.class));
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBlob(int parameterIndex, InputStream inputStream)
    throws SQLException
  {
    _pstmtWrapped.setBlob(parameterIndex, inputStream);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBlob(int parameterIndex, InputStream inputStream,
    long length) throws SQLException
  {
    _pstmtWrapped.setBlob(parameterIndex, inputStream, length);
  }

  private long createBlob(int parameterIndex, InputStream x)
    throws SQLException
  {
    long lLength = -1;
    try
    {
      LargeObjectManager lobj = ((PGConnection)_conn.unwrap(Connection.class)).getLargeObjectAPI();
      long loid = lobj.createLO();
      LargeObject lo = lobj.open(loid);
      byte[] buf = new byte[iBUFSIZ];
      lLength = 0;
      for (int iRead = x.read(buf); iRead != -1; iRead = x.read(buf))
      {
        lo.write(buf,0,iRead);
        lLength = lLength + iRead;
      }
      lo.close();
      String sSql = "GRANT ALL ON LARGE OBJECT "+String.valueOf(loid)+" TO PUBLIC";
      Statement stmt = (_conn.unwrap(Connection.class)).createStatement();
      stmt.executeUpdate(sSql);
      stmt.close();
      _pstmtWrapped.setLong(parameterIndex, loid);
    }
    catch(IOException ie) { throw new SQLException("Reading BinaryStream failed"+EU.getExceptionMessage(ie)+"!"); }
    return lLength;
  } /* createBlob */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBinaryStream(int parameterIndex, InputStream x)
    throws SQLException
  {
    ParameterMetaData pmd = _pstmtWrapped.getParameterMetaData();
    if (!PostgresType.BLOB.getKeyword().equals(pmd.getParameterTypeName(parameterIndex)))
      _pstmtWrapped.setBinaryStream(parameterIndex, x);
    else
      createBlob(parameterIndex,x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBinaryStream(int parameterIndex, InputStream x,
    int length) throws SQLException
  {
    setBinaryStream(parameterIndex,x,(long)length);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBinaryStream(int parameterIndex, InputStream x,
    long length) throws SQLException
  {
    ParameterMetaData pmd = _pstmtWrapped.getParameterMetaData();
    if (!PostgresType.BLOB.getKeyword().equals(pmd.getParameterTypeName(parameterIndex)))
      _pstmtWrapped.setBinaryStream(parameterIndex, x);
    else
    {
      long lLength = createBlob(parameterIndex,x);
      if (length != lLength)
        throw new SQLException("Invalid length in setBinaryStream of PreparedStatement!");
    }
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setArray(int parameterIndex, Array x) throws SQLException
  {
    _pstmtWrapped.setArray(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setObject(int parameterIndex, Object x)
    throws SQLException
  {
    /* TODO: ... if BLOB/CLOB */
    _pstmtWrapped.setObject(parameterIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType)
    throws SQLException
  {
    /* TODO: ... if BLOB/CLOB */
    _pstmtWrapped.setObject(parameterIndex, x, targetSqlType);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType,
    int scaleOrLength) throws SQLException
  {
    /* TODO: ... if BLOB/CLOB */
    _pstmtWrapped.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
  }

} /* PostgresPreparedStatement */
