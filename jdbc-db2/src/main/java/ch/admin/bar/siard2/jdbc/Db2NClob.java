package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.sql.*;

public class Db2NClob 
  implements NClob
{
  private Clob _clob = null;
  public Db2NClob(Clob clob)
  {
    _clob = clob;
  }
  @Override
  public long length() throws SQLException
  {
    return _clob.length();
  }
  @Override
  public String getSubString(long pos, int length) throws SQLException
  {
    return _clob.getSubString(pos, length);
  }
  @Override
  public Reader getCharacterStream() throws SQLException
  {
    return _clob.getCharacterStream();
  }
  @Override
  public InputStream getAsciiStream() throws SQLException
  {
    return _clob.getAsciiStream();
  }
  @Override
  public long position(String searchstr, long start)
    throws SQLException
  {
    return _clob.position(searchstr, start);
  }
  @Override
  public long position(Clob searchstr, long start) throws SQLException
  {
    return _clob.position(searchstr, start);
  }
  @Override
  public int setString(long pos, String str) throws SQLException
  {
    return _clob.setString(pos, str);
  }
  @Override
  public int setString(long pos, String str, int offset, int len)
    throws SQLException
  {
    return _clob.setString(pos, str, offset, len);
  }
  @Override
  public OutputStream setAsciiStream(long pos) throws SQLException
  {
    return _clob.setAsciiStream(pos);
  }
  @Override
  public Writer setCharacterStream(long pos) throws SQLException
  {
    return _clob.setCharacterStream(pos);
  }
  @Override
  public void truncate(long len) throws SQLException
  {
    _clob.truncate(len);
  }
  @Override
  public void free() throws SQLException
  {
    _clob.free();
  }
  @Override
  public Reader getCharacterStream(long pos, long length)
    throws SQLException
  {
    return _clob.getCharacterStream();
  }
  
}
