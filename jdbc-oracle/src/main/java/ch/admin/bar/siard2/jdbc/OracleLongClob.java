package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.sql.*;

public class OracleLongClob
  implements Clob
{
  private String _sLong;
  
  public OracleLongClob(String sLong)
  {
    _sLong = sLong;
  } /* constructor */
  
  @Override
  public long length() throws SQLException
  {
    return _sLong.length();
  } /* length */

  @Override
  public String getSubString(long pos, int length) throws SQLException
  {
    return _sLong.substring((int)pos-1,(int)pos-1+length);
  } /* getSubString */

  @Override
  public Reader getCharacterStream() throws SQLException
  {
    return new StringReader(_sLong);
  } /* getCharacterStream */

  @Override
  public InputStream getAsciiStream() throws SQLException
  {
    throw new SQLFeatureNotSupportedException("ASCII stream LONG value is not supported!");
  } /* getAsciiStream */

  @Override
  public long position(String searchstr, long start)
    throws SQLException
  {
    return _sLong.indexOf(searchstr,(int)start-1)+1;
  } /* position */

  @Override
  public long position(Clob searchstr, long start) throws SQLException
  {
    String s = searchstr.getSubString(1l, (int)searchstr.length()); 
    return position(s,start);
  } /* position */

  @Override
  public int setString(long pos, String str) throws SQLException
  {
    return setString(pos,str,0,str.length());
  } /* setString */

  @Override
  public int setString(long pos, String str, int offset, int len)
    throws SQLException
  {
    StringBuilder sb = new StringBuilder();
    if (_sLong != null)
      sb.append(_sLong);
    sb.setLength((int)pos-1);
    sb.append(str.substring(offset,len));
    _sLong = sb.toString();
    return len;
  } /* setString */

  @Override
  public OutputStream setAsciiStream(long pos) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("ASCII stream for LONG value is not supported!");
  }

  @Override
  public Writer setCharacterStream(long pos) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("LONG Clob cannot be updated!");
  }

  @Override
  public void truncate(long len) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Truncate for LONG value is not supported!");
  }

  @Override
  public void free() throws SQLException
  {
    _sLong = null;
  }

  @Override
  public Reader getCharacterStream(long pos, long length)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Positioned character stream for LONG value is not supported!");
  }

}
