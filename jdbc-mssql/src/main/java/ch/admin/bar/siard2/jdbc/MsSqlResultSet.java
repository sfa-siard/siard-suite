/*======================================================================
MsSqlResultSet implements a wrapped MSSQL ResultSet.
Application : SIARD2
Description : MsSqlResultSet implements a wrapped MSSQL ResultSet.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.math.*;
import java.net.*;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;
import javax.xml.datatype.*;
import com.microsoft.sqlserver.jdbc.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.*;

/*====================================================================*/
/** MsSqlResultSet implements a wrapped MSSQL ResultSet.
 * @author Hartwig Thomas
 */
public class MsSqlResultSet
  extends BaseResultSet
  implements ResultSet
{
  /** logger */
  @SuppressWarnings("unused")
  private static IndentLogger _il = IndentLogger.getIndentLogger(MsSqlResultSet.class.getName());
  protected MsSqlConnection _conn = null;

  /*------------------------------------------------------------------*/
  /** convert an MSSQL SQLServerException into an SQLException.
   * @param sse SQLServerException
   * @throws SQLException
   */
  private void throwSqlException(SQLServerException sse)
    throws SQLException
  {
    throw new SQLException("MSSQL exception!", sse);
  } /* throwSqlException */
  
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
  /** map MSSQL object to appropriate java type.
   * @param o object.
   * @param iType its original type (from unwrapped ResultSetMetaData).
   * @return mapped object.
   * @throws SQLException if an exception occurs.
   */
  private Object mapObject(Object o, int iType) throws SQLException
  {
    if (o instanceof microsoft.sql.DateTimeOffset)
      o = o.toString();
    return o;
  } /* mapObject */
  
  /*------------------------------------------------------------------*/
  /** map MSSQL object to appropriate java type.
   * @param o original object.
   * @param iType its original type (from unwrapped ResultSetMetaData).
   * @return mapped object.
   * @throws SQLException if an exception occurs.
   */
  private <T> T mapObject(Object o, int iType, Class<T> type) throws SQLException
  {
    T oMapped = null;
    if (type == Array.class)
      throw new SQLFeatureNotSupportedException("MS SQL Server does not support Arrays!");
    /* first map object to appropriate object */
    oMapped = type.cast(mapObject(o, iType));
    /* then convert */
    return oMapped;
  } /* mapObject */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param rsWrapped result set to be wrapped.
   */
  public MsSqlResultSet(ResultSet rsWrapped, MsSqlConnection conn)
  {
    super(rsWrapped);
    _conn = conn;
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Statement getStatement() throws SQLException
  {
    return new MsSqlStatement(super.getStatement(),_conn);
  } /* getStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSetMetaData getMetaData() throws SQLException
  {
    return new MsSqlResultSetMetaData(super.getMetaData(),_conn);
  } /* getMetaData */

  /*------------------------------------------------------------------*/
  /** an extension of the ResultSet API supporting predefined type 
   * INTERVAL. 
   * @param columnIndex column index (starts with 1).
   * @return Interval value.
   * @throws SQLException
   */
  public Interval getInterval(int columnIndex) throws SQLException
  {
    return SqlLiterals.deserialize(super.getBytes(columnIndex),Interval.class);
  } /* getInterval */

  /*------------------------------------------------------------------*/
  /** an extension of the ResultSet API supporting predefined type 
   * INTERVAL. 
   * @param columnLabel column label.
   * @return Interval value.
   * @throws SQLException
   */
  public Interval getInterval(String columnLabel) throws SQLException
  {
    return getInterval(findColumn(columnLabel));
  } /* getInterval */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getCursorName() throws SQLException
  {
    String sCursorName = null; 
    try { sCursorName = super.getCursorName(); }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
    return sCursorName;
  } /* getCursorName */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setFetchDirection(int direction) throws SQLException
  {
    try { super.setFetchDirection(direction); }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
  } /* setFetchDirection */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int findColumn(String columnLabel) throws SQLException
  {
    int i = 0;
    try { i = super.findColumn(columnLabel); }
    catch(SQLServerException sse) { throwSqlException(sse); } 
    return i;
  } /* findColumn */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getString(int columnIndex) throws SQLException
  {
    String s = null;
    try { s = super.getString(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return s;
  } /* getString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean getBoolean(int columnIndex) throws SQLException
  {
    boolean b = false;
    try { b = super.getBoolean(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return b;
  } /* getBoolean */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public byte getByte(int columnIndex) throws SQLException
  {
    byte by = -1;
    try { by = super.getByte(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return by;
  } /* getByte */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public short getShort(int columnIndex) throws SQLException
  {
    short sh = -1;
    try { sh = super.getShort(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return sh;
  } /* getShort */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getInt(int columnIndex) throws SQLException
  {
    int i = -1;
    try { i = super.getInt(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return i;
  } /* getInt */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long getLong(int columnIndex) throws SQLException
  {
    long l = -1;
    try { l = super.getLong(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return l;
  } /* getLong */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public float getFloat(int columnIndex) throws SQLException
  {
    float f = -1.0f;
    try { f = super.getFloat(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return f;
  } /* getFloat */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public double getDouble(int columnIndex) throws SQLException
  {
    double d = -1.0;
    try { d = super.getDouble(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return d;
  } /* getDouble */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException
  {
    BigDecimal bd = BigDecimal.ZERO;
    try { bd = super.getBigDecimal(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return bd;
  } /* getBigDecimal */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  @Deprecated
  public BigDecimal getBigDecimal(int columnIndex, int scale)
    throws SQLException
  {
      BigDecimal bd = BigDecimal.ZERO;
      try { bd = super.getBigDecimal(columnIndex, scale); }
      catch(SQLServerException sse) { throwSqlException(sse); }
      return bd;
  } /* getBigDecimal */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public byte[] getBytes(int columnIndex) throws SQLException
  {
    byte[] buf = null;
    try { buf = super.getBytes(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return buf;
  } /* getBytes */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Date getDate(int columnIndex) throws SQLException
  {
    Date date = null;
    try { date = super.getDate(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return date;
  } /* getDate */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Date getDate(int columnIndex, Calendar cal)
    throws SQLException
  {
    Date date = null;
    try { date = super.getDate(columnIndex, cal); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return date;
  } /* getDate */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Time getTime(int columnIndex) throws SQLException
  {
    Time time = null;
    try { time = super.getTime(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return time;
  } /* getTime */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Time getTime(int columnIndex, Calendar cal)
    throws SQLException
  {
    Time time = null;
    try { time = super.getTime(columnIndex, cal); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return time;
  } /* getTime */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException
  {
    Timestamp ts = null;
    try { ts = super.getTimestamp(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return ts;
  } /* getTimestamp */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Timestamp getTimestamp(int columnIndex, Calendar cal)
    throws SQLException
  {
    Timestamp ts = null;
    try { ts = super.getTimestamp(columnIndex, cal); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return ts;
  } /* getTimestamp */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Duration getDuration(int columnIndex)
    throws SQLException
  {
    Duration duration = null;
    String s = getString(columnIndex);
    try
    {
      Interval iv = SqlLiterals.parseInterval(s);
      duration = iv.toDuration();
    }
    catch(ParseException pe) { throw new SQLException("Could not parse interval "+s+"!",pe); }
    return duration;
  } /* getDuration */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public InputStream getAsciiStream(int columnIndex)
    throws SQLException
  {
      InputStream is = null;
      try { is = super.getAsciiStream(columnIndex); }
      catch(SQLServerException sse) { throwSqlException(sse); }
      return is;
  } /* getAsciiStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  @Deprecated
  public InputStream getUnicodeStream(int columnIndex)
    throws SQLException
  {
      InputStream is = null;
      try { is = super.getUnicodeStream(columnIndex); }
      catch(SQLServerException sse) { throwNotSupportedException(sse); }
      return is;
  } /* getUnicodeStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Reader getCharacterStream(int columnIndex) throws SQLException
  {
    Reader rdr = null;
    try { rdr = super.getCharacterStream(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return rdr;
  } /* getCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public InputStream getBinaryStream(int columnIndex)
    throws SQLException
  {
      InputStream is = null;
      try { is = super.getBinaryStream(columnIndex); }
      catch(SQLServerException sse) { throwSqlException(sse); }
      return is;
  } /* getBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Object getObject(int columnIndex) throws SQLException {
    Object o = super.getObject(columnIndex);
    int iType = getMetaData().getColumnType(columnIndex);
    if (iType == Types.DATALINK) {
      o = getURL(columnIndex);
    }
    if (iType == Types.CLOB) {
      o = getClob(columnIndex);
    } else if (iType == Types.NCLOB) {
      o = getNClob(columnIndex);
    } else if (iType == Types.BLOB) {
      o = getBlob(columnIndex);
    } else if (iType == Types.SQLXML) {
      o = getSQLXML(columnIndex);
    } else {
      o = mapObject(o, iType);
    }
    return mapObject(o, iType);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T getObject(int columnIndex, Class<T> type)
    throws SQLException
  {
    Object o = null;
    T oMapped = null;
    int iType = Types.OTHER;
    o = super.getObject(columnIndex);
    if (type.isInstance(o))
      oMapped = (T)o;
    else
      oMapped = mapObject(o, iType, type);
    return oMapped;
  } /* getObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map)
    throws SQLException
  {
    Object o = null;
    try { o = super.getObject(columnIndex, map); }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
    return o;
  } /* getObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Ref getRef(int columnIndex) throws SQLException
  {
    Ref ref = null;
    try { ref = super.getRef(columnIndex); }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
    return ref;
  } /* getRef */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Blob getBlob(int columnIndex) throws SQLException
  {
    Blob blob = null;
    try { blob = super.getBlob(columnIndex); } 
    catch(SQLServerException sse) { throwSqlException(sse); }
    return blob;
  } /* getBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Clob getClob(int columnIndex) throws SQLException
  {
    Clob clob = null;
    try { clob = super.getClob(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
    return clob;
  } /* getClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Array getArray(int columnIndex) throws SQLException
  {
    return getObject(columnIndex, Array.class);
  } /* getArray */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public URL getURL(int columnIndex) throws SQLException {
    try {
      return new URL(super.getString(columnIndex));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public URL updateURL(int columnIndex, URL url) throws SQLException {
    super.updateObject(columnIndex, url.getPath());
    return url;
  }

  public URL updateURL(String columnLabel, URL url) throws SQLException {
    updateURL(this.findColumn(columnLabel), url);
    return url;
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public RowId getRowId(int columnIndex) throws SQLException
  {
    RowId ri = null;
    try { ri = super.getRowId(columnIndex); }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
    return ri;
  } /* getRowId */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getNString(int columnIndex) throws SQLException
  {
    return super.getNString(columnIndex);
  } /* getNString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Reader getNCharacterStream(int columnIndex)
    throws SQLException
  {
    return super.getNCharacterStream(columnIndex);
  } /* getNCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public NClob getNClob(int columnIndex) throws SQLException
  {
    return super.getNClob(columnIndex);
  } /* getNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNull(int columnIndex) throws SQLException
  {
    try { super.updateNull(columnIndex); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateNull */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBoolean(int columnIndex, boolean x)
    throws SQLException
  {
      try { super.updateBoolean(columnIndex, x); }
      catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateBoolean */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateByte(int columnIndex, byte x) throws SQLException
  {
    try { super.updateByte(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateByte */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateShort(int columnIndex, short x) throws SQLException
  {
    try { super.updateShort(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateShort */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateInt(int columnIndex, int x) throws SQLException
  {
    try { super.updateInt(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateInt */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateLong(int columnIndex, long x) throws SQLException
  {
    try { super.updateLong(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateLong */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateFloat(int columnIndex, float x) throws SQLException
  {
    try { super.updateFloat(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateFloat */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateDouble(int columnIndex, double x)
    throws SQLException
  {
    try { super.updateDouble(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateDouble */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBigDecimal(int columnIndex, BigDecimal x)
    throws SQLException
  {
    try { super.updateBigDecimal(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateBigDecimal */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateString(int columnIndex, String x)
    throws SQLException
  {
    try { super.updateString(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNString(int columnIndex, String nString)
    throws SQLException
  {
    try { super.updateString(columnIndex, nString); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateNString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBytes(int columnIndex, byte[] x)
    throws SQLException
  {
    try { super.updateBytes(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateBytes */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateDate(int columnIndex, Date x) throws SQLException
  {
    try { super.updateDate(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateDate */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateTime(int columnIndex, Time x) throws SQLException
  {
    try { super.updateTime(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateTime */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateTimestamp(int columnIndex, Timestamp x)
    throws SQLException
  {
    try { super.updateTimestamp(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateTimestamp */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public void updateDuration(int columnIndex, Duration x)
    throws SQLException
  {
    Interval iv = Interval.fromDuration(x);
    String s = SqlLiterals.formatIntervalLiteral(iv);
    updateString(columnIndex, s);
  } /* updateDuration */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x)
    throws SQLException
  {
    try { super.updateAsciiStream(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateAsciiStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x,
    int length) throws SQLException
  {
    try { super.updateAsciiStream(columnIndex, x, length); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateAsciiStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x,
    long length) throws SQLException
  {
    try { super.updateAsciiStream(columnIndex, x, length); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateAsciiStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x)
    throws SQLException
  {
    try { super.updateBinaryStream(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x,
    int length) throws SQLException
  {
    try { super.updateBinaryStream(columnIndex, x, length); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x,
    long length) throws SQLException
  {
    try { super.updateBinaryStream(columnIndex, x, length); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x)
    throws SQLException
  {
    try { super.updateCharacterStream(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x,
    int length) throws SQLException
  {
    try { super.updateCharacterStream(columnIndex, x, length); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateCharacterStream(int columnIndex, Reader x,
    long length) throws SQLException
  {
    try { super.updateCharacterStream(columnIndex, x, length); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNCharacterStream(int columnIndex, Reader x,
    long length) throws SQLException
  {
    try { super.updateCharacterStream(columnIndex, x, length); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateNCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNCharacterStream(int columnIndex, Reader x)
    throws SQLException
  {
    try { super.updateCharacterStream(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateNCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateObject(int columnIndex, Object x) throws SQLException {
    if (x instanceof URL) {
      updateURL(columnIndex, (URL) x);
    }
    if (x instanceof NClob)
      updateNClob(columnIndex, (NClob) x);
    else if (x instanceof Clob)
      updateClob(columnIndex, (Clob) x);
    else if (x instanceof Blob)
      updateBlob(columnIndex, (Blob) x);
    else if (x instanceof SQLXML)
      updateSQLXML(columnIndex, (SQLXML) x);
    else if (x instanceof Duration) {
      Duration duration = (Duration) x;
      Interval iv = Interval.fromDuration(duration);
      updateString(columnIndex, SqlLiterals.formatIntervalLiteral(iv));
    } else
      super.updateObject(columnIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateObject(int columnIndex, Object x, int scaleOrLength)
    throws SQLException
  {
    // TODO: invert above mapping!
    try 
    { 
      super.updateObject(columnIndex, x, scaleOrLength);
    }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateRef(int columnIndex, Ref x) throws SQLException
  {
    try { super.updateRef(columnIndex, x); }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
  } /* updateRef */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBlob(int columnIndex, Blob x) throws SQLException
  {
    try { super.updateBlob(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBlob(int columnIndex, InputStream inputStream)
    throws SQLException
  {
    try { super.updateBlob(columnIndex, inputStream); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBlob(int columnIndex, InputStream inputStream,
    long length) throws SQLException
  {
    try { super.updateBlob(columnIndex, inputStream, length); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateClob(int columnIndex, Clob x) throws SQLException
  {
    try { super.updateClob(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateClob(int columnIndex, Reader reader)
    throws SQLException
  {
    try { super.updateClob(columnIndex, reader); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateClob(int columnIndex, Reader reader, long length)
    throws SQLException
  {
    try { super.updateClob(columnIndex, reader, length); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNClob(int columnIndex, NClob nclob)
    throws SQLException
  {
    try { super.updateNClob(columnIndex, nclob); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNClob(int columnIndex, Reader reader)
    throws SQLException
  {
    try { super.updateNClob(columnIndex, reader); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNClob(int columnIndex, Reader reader, long length)
    throws SQLException
  {
    try { super.updateNClob(columnIndex, reader, length); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject)
    throws SQLException
  {
    try { super.updateSQLXML(columnIndex, xmlObject); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateSQLXML */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateArray(int columnIndex, Array x) throws SQLException
  {
    try { super.updateArray(columnIndex, x); }
    catch(SQLServerException sse) { throwNotSupportedException(sse); }
  } /* updateArray */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateRowId(int columnIndex, RowId x) throws SQLException
  {
    try { super.updateRowId(columnIndex, x); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateRowId */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void insertRow() throws SQLException
  {
    try { super.insertRow(); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* insertRow */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateRow() throws SQLException
  {
    try { super.updateRow(); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* updateRow */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void deleteRow() throws SQLException
  {
    try { super.deleteRow(); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* deleteRow */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void refreshRow() throws SQLException
  {
    try { super.refreshRow(); }
    catch(SQLServerException sse) { throwSqlException(sse); }
  } /* refreshRow */

} /* class MsSqlResultSet */
