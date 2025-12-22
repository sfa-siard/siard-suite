/*======================================================================
PostgresResultSet implements a wrapped PostgreSQL ResultSet.
Application : SIARD2
Description : PostgresResultSet implements a wrapped PostgreSQL ResultSet.
Platform    : Java 17
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 09.08.2019, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.math.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.xml.datatype.*;
import org.postgresql.jdbc.*;
import org.postgresql.util.*;
import ch.enterag.utils.*;
import ch.enterag.utils.jdbc.*;
import ch.admin.bar.siard2.postgres.*;

/*====================================================================*/
/** PostgresResultSet implements a wrapped PostgreSQL ResultSet.
 * @author Hartwig Thomas
 */
public class PostgresResultSet
extends BaseResultSet
implements ResultSet
{
  private static final int iBUFSIZ = 8192;
  private Statement _stmt = null;
  private ResultSetMetaData _rsmd = null;
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param rsWrapped result set to be wrapped.
   * @param stmt wrapped statement.
   */
  public PostgresResultSet(ResultSet rsWrapped, Statement stmt)
    throws SQLException
  {
    super(rsWrapped);
    _stmt = stmt;
    _rsmd = new PostgresResultSetMetaData(super.getMetaData(),_stmt);
  } /* constructor */

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
    return _rsmd;
  } /* getMetaData */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public byte[] getBytes(int columnIndex) throws SQLException
  {
    byte[] buf = null;
    try
    {
      Object o = super.getObject(columnIndex);
      if (o instanceof byte[])
        buf = (byte[])o;
      else if (o instanceof PGobject)
      {
        PGobject po = (PGobject)o;
        PostgresType pt = PostgresType.getByKeyword(po.getType());
        if (pt == PostgresType.VARBIT)
        {
          String sBitString = po.getValue();
          if (sBitString != null)
            buf = PostgresLiterals.parseBitString(sBitString);
        }
        else if ((pt == PostgresType.MACADDR) || 
          (pt == PostgresType.MACADDR8))
          buf = PostgresLiterals.parseMacAddr(po.getValue());
      }
      else if (o instanceof UUID)
      {
        UUID uuid = (UUID)o;
        buf = PostgresLiterals.convertUuidToByteArray(uuid);
      }
    }
    catch(SQLException se)
    {
      String sBitString = super.getString(columnIndex);
      if (sBitString != null)
        buf = PostgresLiterals.parseBitString(sBitString);
    }
    return buf;
  } /* getBytes */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBytes(int columnIndex, byte[] x)
    throws SQLException
  {
    ResultSetMetaData rsmd = getMetaData();
    int iColumnType = rsmd.getColumnType(columnIndex);
    if ((iColumnType == Types.BIT) ||
        (iColumnType == Types.OTHER))
      super.updateString(columnIndex, PostgresLiterals.formatBitString(x, 8*x.length));
    else
      super.updateBytes(columnIndex, x);
  } /* updateBytes */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException
  {
    BigDecimal bd = null;
    try
    {
      Object o = super.getObject(columnIndex);
      if (o instanceof BigDecimal)
        bd = (BigDecimal)o;
      if (o instanceof Long)
      {
        Long l = (Long)o;
        bd = BigDecimal.valueOf(l.longValue());
      }
    }
    catch(SQLException se)
    {
      // most likely Postgres JDBC uses the client locale for formatting the string ...
      String s = super.getString(columnIndex);
      // in JAVA 8 the Locale de_CH uses apostrophes rather than right single quotes!
      Locale locDefault = Locale.getDefault();
      DecimalFormatSymbols dfs = new DecimalFormatSymbols(locDefault);
      DecimalFormat df = new DecimalFormat("#,##0.0#",dfs);
      df.setParseBigDecimal(true);
      s = s.substring(df.getCurrency().getCurrencyCode().length()).trim();
      s = s.replace('\u2019', '\'');
      try { bd = (BigDecimal)df.parse(s); }
      catch(ParseException pe) { throw new SQLException("Error parsing string ("+EU.getExceptionMessage(pe)+")!"); }
    }
    return bd;
  } /* getBigDecimal */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public InputStream getBinaryStream(int columnIndex)
    throws SQLException
  {
    InputStream is = null;
    ResultSetMetaData rsmd = getMetaData();
    int iColumnType = rsmd.getColumnType(columnIndex);
    if ((iColumnType == Types.BIT) ||
      (iColumnType == Types.OTHER))
      is = new ByteArrayInputStream(getBytes(columnIndex));
    else
      is = super.getBinaryStream(columnIndex);
    return is;
  } /* getBinaryStream */

  /*------------------------------------------------------------------*/
  private byte[] getByteArray(InputStream is)
    throws SQLException
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buf = new byte[iBUFSIZ];
      for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
        baos.write(buf,0,iRead);
      baos.close();
      is.close();
      return baos.toByteArray();
    }
    catch(IOException ie) { throw new SQLException("UpdateBinaryStream failed for data type BIT ("+EU.getExceptionMessage(ie)+")!"); }
  } /* getByteArray */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x)
    throws SQLException
  {
    ResultSetMetaData rsmd = getMetaData();
    int iColumnType = rsmd.getColumnType(columnIndex);
    if ((iColumnType == Types.BIT) ||
      (iColumnType == Types.OTHER))
      updateBytes(columnIndex,getByteArray(x));
    else
      super.updateBinaryStream(columnIndex, x);
  } /* updateBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x,
    int length) throws SQLException
  {
    updateBinaryStream(columnIndex, x, (long)length);
  } /* updateBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x,
    long length) throws SQLException
  {
    ResultSetMetaData rsmd = getMetaData();
    int iColumnType = rsmd.getColumnType(columnIndex);
    if ((iColumnType == Types.BIT) ||
      (iColumnType == Types.OTHER))
    {
      byte[] buf = getByteArray(x);
      if (buf.length == (int)length)
        updateBytes(columnIndex, buf);
      else
        throw new SQLException("Invalid length of binary stream in updateBinaryStream!");
    }
    else
      super.updateBinaryStream(columnIndex, x, length);
  } /* updateBinaryStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Array getArray(int columnIndex) throws SQLException
  {
    PgArray pgarray = (PgArray)super.getArray(columnIndex);
    Array array = pgarray != null ? new PostgresArray(pgarray) : null;
    return array;
  } /* getArray */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateArray(int columnIndex, Array x) throws SQLException
  {
    PostgresArray pa = (PostgresArray)x;
    PgArray pga = (PgArray)pa.unwrap(Array.class);
    super.updateArray(columnIndex, pga);
  } /* updateArray */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Blob getBlob(int columnIndex) throws SQLException
  {
    long lOid = super.getLong(columnIndex);
    Blob blob = new PostgresBlob((PostgresConnection)getStatement().getConnection(),lOid);
    return blob;
  } /* getBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateBlob(int columnIndex, Blob x) throws SQLException
  {
    PostgresBlob pb = (PostgresBlob)x;
    super.updateLong(columnIndex, pb.getOid());
  } /* updateBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Clob getClob(int columnIndex) throws SQLException
  {
    long lOid = super.getLong(columnIndex);
    Clob clob = new PostgresClob((PostgresConnection)getStatement().getConnection(),lOid);
    return clob;
  } /* getClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateClob(int columnIndex, Clob x) throws SQLException
  {
    PostgresClob pc = (PostgresClob)x;
    super.updateLong(columnIndex, pc.getOid());
  } /* updateClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public NClob getNClob(int columnIndex) throws SQLException
  {
    long lOid = super.getLong(columnIndex);
    NClob nclob = new PostgresNClob((PostgresConnection)getStatement().getConnection(),lOid);
    return nclob;
  } /* getNClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateNClob(int columnIndex, NClob x) throws SQLException
  {
    PostgresNClob pnc = (PostgresNClob)x;
    super.updateLong(columnIndex, pnc.getOid());
  } /* updateClob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Time getTime(int columnIndex) throws SQLException
  {
    Time time = null;
    String s = super.getString(columnIndex);
    if (s != null)
    {
      if (s.length() > 8)
        s = s.substring(0,8); // ignore offset, return local time
      time = Time.valueOf(s);
    }
    return time;
  } /* getTime */

  /*------------------------------------------------------------------*/
  public Duration getDuration(int columnIndex) 
    throws SQLException, SQLFeatureNotSupportedException
  {
    Duration duration = null;
    Object o = super.getObject(columnIndex);
    if (o instanceof PGInterval)
    {
      PGInterval pgi = (PGInterval)o;
      try
      {
        DatatypeFactory df = DatatypeFactory.newInstance();
        boolean bPositive = true;
        if ((pgi.getDays() == 0) && 
            (pgi.getMinutes() == 0) && 
            (pgi.getSeconds() == 0.0))
        {
          int iYears = pgi.getYears();
          int iMonths = pgi.getMonths();
          if ((iYears < 0) || (iMonths < 0))
          {
            bPositive = false;
            iYears = -iYears;
            iMonths = -iMonths;
          }
          duration = df.newDurationYearMonth(bPositive, iYears, iMonths);
        }
        else
        {
          int iDays = pgi.getDays();
          int iHours = pgi.getHours();
          int iMinutes = pgi.getMinutes();
          double dSeconds = pgi.getSeconds();
          if ((iDays < 0) || (iHours < 0) || (iMinutes < 0) || (dSeconds < 0.0))
          {
            bPositive = false;
            iDays = -iDays;
            iHours = -iHours;
            iMinutes = -iMinutes;
            dSeconds = -dSeconds;
          }
          int iSeconds = (int)Math.round(dSeconds);
          int iMilliSeconds = (int)Math.round(1000*(dSeconds-iSeconds));  
          long lMilliSeconds = iDays;
          lMilliSeconds = 24*lMilliSeconds+iHours;
          lMilliSeconds = 60*lMilliSeconds+iMinutes;
          lMilliSeconds = 60*lMilliSeconds+iSeconds;
          lMilliSeconds = 1000*lMilliSeconds+iMilliSeconds;
          if (!bPositive)
            lMilliSeconds = -lMilliSeconds;
          duration = df.newDurationDayTime(lMilliSeconds);
        }
      }
      catch(DatatypeConfigurationException dcfe){}
    }
    return duration;
  } /* getDuration */

  /*------------------------------------------------------------------*/
  public void updateDuration(int columnIndex, Duration x)
    throws SQLException
  {
    int iSign = x.getSign();
    int iYears = iSign*x.getYears();
    int iMonths = iSign*x.getMonths();
    int iDays = iSign*x.getDays();
    int iHours = iSign*x.getHours();
    int iMinutes = iSign*x.getMinutes();
    long lMillis = x.getTimeInMillis(new java.util.Date(0)) % 60000;
    double dSeconds = iSign*lMillis/1000.0;
    PGInterval pgi = new PGInterval(iYears, iMonths, iDays, iHours, iMinutes, dSeconds);
    super.updateObject(columnIndex, pgi);
  } /* updateDuration */

  /**
   * {@inheritDoc}
   */
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
  public Object getObject(int columnIndex) throws SQLException
  {
    Object o;
    int iType = getMetaData().getColumnType(columnIndex);
    if (iType == Types.CLOB)
      o = getClob(columnIndex);
    else if (iType == Types.BLOB || iType == Types.DATALINK)
      o = getBlob(columnIndex);
    else if ((iType == Types.BINARY) ||
      (iType == Types.VARBINARY))
      o = getBytes(columnIndex);
    else if (iType == Types.SMALLINT)
      o = getShort(columnIndex);
    else if ((iType == Types.DECIMAL) ||
      (iType == Types.NUMERIC))
      o = getBigDecimal(columnIndex);
    else if (iType == Types.TIME)
      o = getTime(columnIndex);
    else if (iType == Types.VARCHAR)
      o = super.getString(columnIndex);
    else if (iType == Types.OTHER)
      o = getDuration(columnIndex);
    else if (iType == Types.ARRAY)
      o = getArray(columnIndex);
    else if (iType == Types.STRUCT)
    {
      o = super.getObject(columnIndex);
      PGobject po = (PGobject)o;
      try
      {
        PostgresObject pobj = new PostgresObject(po.getValue(), iType, po.getType(),
          (PostgresConnection)getStatement().getConnection(),"");
        o = pobj.getObject();
      }
      catch(ParseException pe) {
        throw new SQLException("Parsing of STRUCT failed ("+EU.getExceptionMessage(pe)+")!");
      }
    }
    else if (iType == Types.DISTINCT)
    {
      o = super.getObject(columnIndex);
      if (o instanceof PGobject)
      {
        PGobject po = (PGobject)o;
        try
        {
          
          PostgresObject pobj = new PostgresObject(po.getValue(), iType, po.getType(),
            (PostgresConnection)getStatement().getConnection(),"");
          o = pobj.getObject();
        }
        catch(ParseException pe) {
          throw new SQLException("Parsing of DISTINCT failed ("+EU.getExceptionMessage(pe)+")!");
        }
      }
    }
    else
      o = super.getObject(columnIndex);
    return o;
  } /* getObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map)
    throws SQLException
  {
    Object o = getObject(columnIndex);
    return o;
  } /* getObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getObject(int columnIndex, Class<T> type)
    throws SQLException
  {
    T o = (T)getObject(columnIndex);
    return o;
  } /* getObject */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public void updateObject(int columnIndex, Object x)
    throws SQLException
  {
    int iType = getMetaData().getColumnType(columnIndex);
    if (iType == Types.CLOB)
      updateClob(columnIndex,(Clob)x);
    else if (iType == Types.BLOB || iType == Types.DATALINK)
      updateBlob(columnIndex,(Blob)x);
    else if ((iType == Types.BINARY) ||
      (iType == Types.VARBINARY))
      updateBytes(columnIndex,(byte[])x);
    else if ((iType == Types.SMALLINT) || (iType == Types.INTEGER) || (iType == Types.BIGINT))
    {
      if (x instanceof Short)
        updateShort(columnIndex,(Short)x);
      else if (x instanceof Integer)
        updateInt(columnIndex,(Integer)x);
      else if (x instanceof Long)
        updateLong(columnIndex,(Long)x);
      else if (x instanceof BigInteger)
        updateBigDecimal(columnIndex,new BigDecimal((BigInteger)x));
      else if (x instanceof BigDecimal)
        updateBigDecimal(columnIndex,(BigDecimal)x);
      else if (x == null)
        super.updateObject(columnIndex,x);
    }
    else if ((iType == Types.DECIMAL) ||
      (iType == Types.NUMERIC))
      updateBigDecimal(columnIndex,(BigDecimal)x);
    else if (iType == Types.TIME)
      updateTime(columnIndex,(Time)x);
    else if (iType == Types.VARCHAR)
      updateString(columnIndex,(String)x);
    else if (iType == Types.OTHER)
      updateDuration(columnIndex,(Duration)x);
    else if (iType == Types.ARRAY)
      updateArray(columnIndex,(Array)x);
    else if (iType == Types.STRUCT)
    {
      try
      {
        Struct struct = (Struct)x;
        PostgresObject po = new PostgresObject(struct,Types.STRUCT,struct.getSQLTypeName(),(PostgresConnection)getStatement().getConnection(),"");
        PGobject pgo = new PGobject();
        pgo.setType(struct.getSQLTypeName());
        pgo.setValue(po.getValue());
        super.updateObject(columnIndex,pgo);
      }
      catch(ParseException pe) { throw new SQLException("PostgresObject could not be constructed ("+EU.getExceptionMessage(pe)+")!"); } 
    }
    else if (iType == Types.DISTINCT)
    {
      String sTypeName = getMetaData().getColumnTypeName(columnIndex);
      try
      {
        PostgresObject po = new PostgresObject(x,Types.DISTINCT,sTypeName,(PostgresConnection)getStatement().getConnection(),"");
        PGobject pgo = new PGobject();
        pgo.setType(sTypeName);
        pgo.setValue(po.getValue());
        super.updateObject(columnIndex,pgo);
      }
      catch(ParseException pe) { throw new SQLException("PostgresObject could not be constructed ("+EU.getExceptionMessage(pe)+")!"); }
    }
    else
      super.updateObject(columnIndex,x);
  } /* updateObject */

} /* class PostgresResultSet */
