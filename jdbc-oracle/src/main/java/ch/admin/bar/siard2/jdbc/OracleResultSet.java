/*======================================================================
OracleResultSet implements a wrapped Oracle ResultSet.
Version     : $Id: $
Application : SIARD2
Description : OracleResultSet implements a wrapped Oracle ResultSet.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Simon Jutz
Created    : 01.06.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.math.*;
import java.net.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import javax.xml.datatype.*;

import ch.enterag.utils.jdbc.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.*;

import oracle.jdbc.driver.*;

/*====================================================================*/
/** OracleResultSet implements a wrapped Oracle ResultSet.
 * @author Simon Jutz
 */
public class OracleResultSet
  extends BaseResultSet
  implements ResultSet
{
  /** logger */
  @SuppressWarnings("unused")
  private static IndentLogger _il = IndentLogger.getIndentLogger(OracleResultSet.class.getName());
  private static final int iBUFSIZ = 8192;
  protected Connection _conn = null;
  private Statement _stmt = null;
  private int _iLongColumnIndex = -1;
  private Object _oLongValue = null;
  private boolean _bLongWasNull = false;
  
  /*------------------------------------------------------------------*/
  /** convert an OracleSQLException into an SQLFeatureNotSupportedException.
   * @param sse OracleSQLException
   * @throws SQLFeatureNotSupportedException
   */
  private void throwNotSupportedException(SQLException se)
    throws SQLFeatureNotSupportedException
  {
    throw new SQLFeatureNotSupportedException("SQLException!", se);
  } /* throwFeatureNotSupportedSqlException */

  /*------------------------------------------------------------------*/
  /** map Oracle object to appropriate java type.
   * @param o object.
   * @param iType its original type (from unwrapped ResultSetMetaData).
   * @return mapped object.
   * @throws SQLException if an exception occurs.
   */
  @SuppressWarnings("deprecation")
  private Object mapObject(Object o, int iType) throws SQLException
  {
    if (o instanceof oracle.sql.BFILE)
    {
      oracle.sql.BFILE bfile = (oracle.sql.BFILE)o; 
      o = new OracleBfileBlob(bfile);
    }
    else if (o instanceof oracle.sql.TIMESTAMPLTZ)
    {
      oracle.sql.TIMESTAMPLTZ otsltz = (oracle.sql.TIMESTAMPLTZ)o;
      o = otsltz.timestampValue(
        getStatement().getConnection().unwrap(Connection.class));
    }
    else if (o instanceof oracle.sql.TIMESTAMPTZ)
    {
      oracle.sql.TIMESTAMPTZ otstz = (oracle.sql.TIMESTAMPTZ)o;
      o = otstz.timestampValue(getStatement().getConnection().unwrap(Connection.class));
    }
    else if (o instanceof oracle.sql.TIMESTAMP)
    {
      oracle.sql.TIMESTAMP ots = (oracle.sql.TIMESTAMP)o;
      o = ots.timestampValue();
    }
    else if (o instanceof oracle.sql.INTERVALYM)
    {
      oracle.sql.INTERVALYM ivym = (oracle.sql.INTERVALYM)o;
      byte[] buf = ivym.getBytes();
      long lYear = 0;
      for (int i = 0; i < 4; i++)
      {
        int iByte = buf[i];
        if (iByte < 0)
          iByte = iByte + 256;
        lYear = 256*lYear + iByte;
      }
      int iYear = (int)(lYear & 0x7FFFFFFFl);
      int iMonth = buf[4];
      iMonth = iMonth - 60;
      try
      {
        DatatypeFactory df = DatatypeFactory.newInstance();
        o = df.newDurationYearMonth(true, iYear, iMonth);
      }
      catch (DatatypeConfigurationException dcf) { o = null; }
    }
    else if (o instanceof oracle.sql.INTERVALDS)
    {
      oracle.sql.INTERVALDS ivds = (oracle.sql.INTERVALDS)o;
      byte[] buf = ivds.getBytes();
      long lDay = 0;
      for (int i = 0; i < 4; i++)
      {
        int iByte = buf[i];
        if (iByte < 0)
          iByte = iByte + 256;
        lDay = 256*lDay + iByte;
      }
      int iDay = (int)(lDay & 0x7FFFFFFFl);
      int iHour = buf[4];
      long lHour = 24*iDay + (iHour - 60);
      int iMinute = buf[5];
      long lMinute = 60*lHour + (iMinute - 60);
      int iSecond = buf[6];
      long lSecond = 60*lMinute + (iSecond - 60);
      long lNanos = 0;
      for (int i = 7; i < 11; i++)
      {
        int iByte = buf[i];
        if (iByte < 0)
          iByte = iByte + 256;
        lNanos = 256*lNanos + iByte;
      }
      lNanos = lNanos & 0x7FFFFFFFl;
      long lMillis = 1000*lSecond + lNanos/1000000;
      try
      {
        DatatypeFactory df = DatatypeFactory.newInstance();
        Duration duration = df.newDurationDayTime(lMillis);
        o = duration;
      }
      catch (DatatypeConfigurationException dcf) { o = null; }
    }
    else if (o instanceof oracle.sql.ARRAY)
    {
      oracle.sql.ARRAY oarray = (oracle.sql.ARRAY)o;
      o = new OracleArray(oarray);
    }
    return o;
  } /* mapObject */
  
  /*------------------------------------------------------------------*/
  /** map Oracle object to appropriate java type.
   * @param o original object.
   * @param iType its original type (from unwrapped ResultSetMetaData).
   * @return mapped object.
   * @throws SQLException if an exception occurs.
   */
  @SuppressWarnings("unchecked")
  private <T> T mapObject(Object o, int iType, Class<T> type) throws SQLException
  {
    T oMapped = null;
    /* first map object to appropriate object */
    if (type == Date.class)
    {
      if (o instanceof Timestamp)
      {
        Timestamp ts = (Timestamp)o;
        oMapped = (T)new Date(ts.getTime());
      }
      else
        throw new SQLException("Type "+o.getClass().getName()+" cannot be converted to "+type.getName());
    }
    else
      oMapped = type.cast(mapObject(o, iType));
    /* then convert */
    return oMapped;
  } /* mapObject */
  
  /*------------------------------------------------------------------*/
  private Object mapJava(Object o)
  {
    if (o instanceof Duration)
    {
      Duration duration = (Duration)o;
      if (duration.getSign() >= 0)
      {
        if ((duration.getYears() == 0) && (duration.getMonths() == 0))
        {
          int iDays = duration.getDays();
          iDays = iDays | 0x80000000;
          long lMillis = duration.getTimeInMillis(new Date(0));
          long lNanos = 1000l*(lMillis % 1000l);
          lNanos = lNanos | 0x8000000000000000l;
          byte[] buf = new byte[11];
          for (int i = 0; i < 4; i++)
          {
            int iByte = iDays >>> 24;
            if (iByte >= 128)
              iByte = iByte - 256;
            buf[i] = (byte)iByte;
            iDays = iDays << 8;
          }
          buf[4] = (byte)(duration.getHours()+60);
          buf[5] = (byte)(duration.getMinutes()+60);
          buf[6] = (byte)(duration.getSeconds()+60);
          for (int i = 7; i < 11; i++)
          {
            int iByte = (int)(lNanos >>> 56);
            if (iByte >= 128)
              iByte = iByte - 256;
            buf[i] = (byte)iByte;
            lNanos = lNanos << 8;
          }
          oracle.sql.INTERVALDS ivds = new oracle.sql.INTERVALDS(buf);
          o = ivds;
        }
        else
        {
          int iYears = duration.getYears();
          iYears = iYears | 0x80000000;
          byte[] buf = new byte[5];
          for (int i = 0; i < 4; i++)
          {
            int iByte = iYears >>> 24;
            if (iByte >= 128)
              iByte = iByte - 256;
            buf[i] = (byte)iByte;
            iYears = iYears << 8;
          }
          buf[4] = (byte)(duration.getMonths()+60);
          oracle.sql.INTERVALYM ivym = new oracle.sql.INTERVALYM(buf);
          o = ivym;
        }
      }
      else
      {
        System.err.println("Oracle INTERVALs can not be negative! NULL stored instead.");
        o = null;
      }
    }
    return o;
  } /* mapJava */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param rsWrapped result set to be wrapped.
   * @param conn current connection.
   */
  public OracleResultSet(ResultSet rsWrapped, Connection conn, Statement stmt)
    throws SQLException
  {
    super(rsWrapped);
    _conn = conn;
    _stmt = stmt;
    /* determine LONG column, if one exists */
    _iLongColumnIndex = -1;
    ResultSetMetaData rsmd = rsWrapped.getMetaData();
    for (int iColumn = 1; (_iLongColumnIndex < 0) && (iColumn <= rsmd.getColumnCount()); iColumn++)
    {
      String sTypeName = rsmd.getColumnTypeName(iColumn).toUpperCase();
      if (sTypeName.startsWith("LONG"))
        _iLongColumnIndex = iColumn;
    }
  } /* constructor */

  /*------------------------------------------------------------------*/
  /** LONG values can only be read as the very first value in a record
   * which was obtained from a PreparedStatement. 
   * Therefore we cache them here.
   * @param bPositioned true if the record set was positioned on a new 
   *   data record. 
   * @throws SQLException
   */
  private void getLongValue(boolean bPositioned)
    throws SQLException
  {
    if (bPositioned && (_iLongColumnIndex > 0))
    {
      ResultSet rsWrapped = unwrap(ResultSet.class);
      if (rsWrapped.getConcurrency() == ResultSet.CONCUR_READ_ONLY)
      {
        _oLongValue = rsWrapped.getObject(_iLongColumnIndex);
        _bLongWasNull = rsWrapped.wasNull();
      }
      else
        throw new SQLException("LONG values can only be read, if the result set concurrency is read-only!");
    }
  } /* getLongValue */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Statement getStatement() throws SQLException
  {
    return _stmt;
  } /* getStatement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public ResultSetMetaData getMetaData() throws SQLException
  {
    ResultSetMetaData rsmd = super.getMetaData();
    rsmd = new OracleResultSetMetaData(rsmd,_conn);
    return rsmd;
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
    catch(SQLException se) { throwNotSupportedException(se); }
    return sCursorName;
  } /* getCursorName */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getString(int columnIndex) throws SQLException
  {
    String s = null;
    if (columnIndex != _iLongColumnIndex)
      s = super.getString(columnIndex);
    else
    {
      s = (String)_oLongValue;
      _bWasNull = _bLongWasNull;
    }
    return s;
  } /* getString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  @Deprecated
  public BigDecimal getBigDecimal(int columnIndex, int scale)
    throws SQLException
  {
      BigDecimal bd = super.getBigDecimal(columnIndex);
      if (bd != null)
        bd = bd.setScale(scale,RoundingMode.DOWN);
      return bd;
  } /* getBigDecimal */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Duration getDuration(int columnIndex)
    throws SQLException
  {
    Duration duration = (Duration)getObject(columnIndex);
    return duration;
  } /* getDuration */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Object getObject(int columnIndex) throws SQLException
  {
    int iType = getMetaData().unwrap(ResultSetMetaData.class).getColumnType(columnIndex);
    Object o = null;
    if (columnIndex != _iLongColumnIndex)
    {
      o = super.getObject(columnIndex);
      if (o instanceof oracle.sql.ANYDATA)
      {
        oracle.sql.ANYDATA a = (oracle.sql.ANYDATA)o;
        o = new OracleClob(a.stringValue());
      }
    }
    else
    {
      o = _oLongValue;
      _bWasNull = _bLongWasNull;
    }
    return mapObject(o,iType);
  } /* getObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public <T> T getObject(int columnIndex, Class<T> type)
    throws SQLException
  {
    Object o = null;
    T oMapped = null;
    int iType = getMetaData().unwrap(ResultSetMetaData.class).getColumnType(columnIndex);
    if (columnIndex != _iLongColumnIndex)
      o = super.getObject(columnIndex);
    else
    {
      o = _oLongValue;
      _bWasNull = _bLongWasNull;
    }
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
    try
    {
      if (columnIndex != _iLongColumnIndex)
        o = super.getObject(columnIndex, map);
      else
      {
        o = _oLongValue;
        _bWasNull = _bLongWasNull;
      }
    }
    catch(SQLException se) { throw new SQLFeatureNotSupportedException("getObject(,map) not supported!",se); }
    return o;
  } /* getObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Blob getBlob(int columnIndex) throws SQLException
  {
    Blob blob = null;
    String sTypeName = getMetaData().getColumnTypeName(columnIndex);
    if ("BFILE".equals(sTypeName))
      blob = (Blob)getObject(columnIndex);
    else if ("LONG RAW".equals(sTypeName))
      blob = new OracleLongBlob((byte[])getObject(columnIndex));
    else
      blob = super.getBlob(columnIndex); 
    return blob;
  } /* getBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Clob getClob(int columnIndex) throws SQLException
  {
    Clob clob = null;
    String sTypeName = getMetaData().getColumnTypeName(columnIndex);
    if ("LONG".equals(sTypeName))
      clob = new OracleLongClob((String)getObject(columnIndex));
    else if ("SYS.ANYDATA".equals(sTypeName))
      clob = (Clob)getObject(columnIndex);
    else
      clob = super.getClob(columnIndex); 
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
  public URL getURL(int columnIndex) throws SQLException
  {
    URL url = null;
    try 
    { 
      Object o = super.getObject(columnIndex);
      // o is a Struct with a single attribute URL
      if (o instanceof Struct)
      {
        Struct struct = (Struct)o;
        Object[] ao = struct.getAttributes();
        if (ao.length == 1)
        {
          String sUrl = (String)ao[0];
          try { url = new URL(sUrl); }
          catch(MalformedURLException mue)
          {
            try { url = new URL("http://"+sUrl); }
            catch(MalformedURLException mue1) { throw new SQLException(mue1); }
          }
        }
        else
          throw new SQLException("Invalid URITYPE encountered!");
      }
      else
        throw new SQLException("getURL() can only be applied to URITYPE columns!");
    }
    catch(OracleSQLException sse) { throwNotSupportedException(sse); }
    return url;
  } /* getURL */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public RowId getRowId(int columnIndex) throws SQLException
  {
    RowId ri = null;
    try { ri = super.getRowId(columnIndex); }
    catch(SQLException se) { throwNotSupportedException(se);; }
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
    Reader rdr = null;
    Object o = getObject(columnIndex);
    if (o instanceof NClob)
    {
      NClob nclob = (NClob)o;
      rdr = nclob.getCharacterStream();
    }
    else if (o instanceof Clob)
    {
      Clob clob = (Clob)o;
      rdr = clob.getCharacterStream();
    }
    else if (o instanceof String)
    {
      String s = (String)o;
      rdr = new StringReader(s);
    }
    else
      throw new SQLException(o.getClass().getName()+" cannot be accessed as a character stream!"); 
    return rdr;
  } /* getNCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public NClob getNClob(int columnIndex) throws SQLException
  {
    Clob clob = super.getClob(columnIndex);
    NClob nclob = getStatement().getConnection().createNClob();
    Reader rdr = clob.getCharacterStream();
    Writer wr = nclob.setCharacterStream(1l);
    try
    {
      char[] cbuf = new char[iBUFSIZ];
      for (int iRead = rdr.read(cbuf); iRead != -1; iRead = rdr.read(cbuf))
        wr.write(cbuf,0,iRead);
      wr.close();
      rdr.close();
    }
    catch(IOException ie) { throw new SQLException("Error copying Clob to NClob!",ie); }
    return nclob;
  } /* getNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateRowId(int columnIndex, RowId x) throws SQLException
  {
    try { super.updateRowId(columnIndex, x); }
    catch(SQLException se) { throwNotSupportedException(se); }
  } /* updateRowId */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNString(int columnIndex, String nString)
    throws SQLException
  {
    super.updateString(columnIndex, nString);
  } /* updateNString */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNCharacterStream(int columnIndex, Reader x,
    long length) throws SQLException
  {
    super.updateCharacterStream(columnIndex, x, length);
  } /* updateNCharacterStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNCharacterStream(int columnIndex, Reader x)
    throws SQLException
  {
    super.updateCharacterStream(columnIndex, x);
  } /* updateNCharacterStream */

  /*------------------------------------------------------------------*/
  public void updateDuration(int columnIndex, Duration x)
    throws SQLException
  {
    updateObject(columnIndex, x);
  } /* updateDuration */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateObject(int columnIndex, Object x)
    throws SQLException
  {
    if (x instanceof Reader)
      super.updateCharacterStream(columnIndex,(Reader)x);
    else if (x instanceof InputStream)
      super.updateBinaryStream(columnIndex,(InputStream)x);
    else if (x instanceof Array)
      updateArray(columnIndex, (Array)x);
    else
      super.updateObject(columnIndex, mapJava(x)); 
  } /* updateObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateObject(int columnIndex, Object x, int scaleOrLength)
    throws SQLException
  {
    super.updateObject(columnIndex, mapJava(x), scaleOrLength);
  } /* updateObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateArray(int columnIndex, Array x) throws SQLException
  {
    try 
    {
      OracleArray oa = (OracleArray)x;
      /* make sure, type conforms to table column */
      oracle.jdbc.OracleResultSetMetaData orsmd = (oracle.jdbc.OracleResultSetMetaData)getMetaData().unwrap(ResultSetMetaData.class);
      String sTypeName = orsmd.getColumnTypeName(columnIndex);
      super.updateArray(columnIndex, oa.getOracleArray(sTypeName)); 
    }
    catch(OracleSQLException sse) { throwNotSupportedException(sse); }
  } /* updateArray */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean absolute(int row) throws SQLException
  {
    boolean bPositioned = super.absolute(row);
    getLongValue(bPositioned);
    return bPositioned;
  } /* absolute */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean relative(int row) throws SQLException
  {
    boolean bPositioned = super.relative(row);
    getLongValue(bPositioned);
    return bPositioned;
  } /* relative */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean first() throws SQLException
  {
    boolean bPositioned = super.first();
    getLongValue(bPositioned);
    return bPositioned;
  } /* first */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean last() throws SQLException
  {
    boolean bPositioned = super.last();
    getLongValue(bPositioned);
    return bPositioned;
  } /* last */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean next() throws SQLException
  {
    boolean bPositioned = super.next();
    getLongValue(bPositioned);
    return bPositioned;
  } /* next */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean previous() throws SQLException
  {
    boolean bPositioned = super.previous();
    getLongValue(bPositioned);
    return bPositioned;
  } /* previous */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void close() throws SQLException
  {
    Statement stmt = getStatement();
    try { super.close(); }
    finally { if ((stmt != null) && (!stmt.isClosed())) stmt.close(); }
  } /* close */
  
  
} /* class OracleResultSet */
