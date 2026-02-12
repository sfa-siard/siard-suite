package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.sql.*;

public class OracleLongBlob implements Blob
{
  private byte[] _bufLong = null;
  
  public OracleLongBlob(byte[] bufLong)
  {
    _bufLong = bufLong;
  } /* constructor */
  
  @Override
  public long length() throws SQLException
  {
    return _bufLong.length;
  }

  @Override
  public byte[] getBytes(long pos, int length) throws SQLException
  {
    return _bufLong;
  }

  @Override
  public InputStream getBinaryStream() throws SQLException
  {
    return new ByteArrayInputStream(_bufLong);
  }

  @Override
  public long position(byte[] pattern, long start) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Search in LONG RAW not supported!");
  }

  @Override
  public long position(Blob pattern, long start) throws SQLException
  {
    return position(pattern.getBytes(1l, (int)pattern.length()),start);
  }

  @Override
  public int setBytes(long pos, byte[] bytes) throws SQLException
  {
    return setBytes(pos,bytes,0,bytes.length);
  }

  @Override
  public int setBytes(long pos, byte[] bytes, int offset, int len)
    throws SQLException
  {
      throw new SQLFeatureNotSupportedException("LONG RAW blob cannot be updated!");
  }

  @Override
  public OutputStream setBinaryStream(long pos) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("LONG RAW blob cannot be updated!");
  }

  @Override
  public void truncate(long len) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("LONG RAW blob cannot be truncated!");
  }

  @Override
  public void free() throws SQLException
  {
    _bufLong = null;
  }

  @Override
  public InputStream getBinaryStream(long pos, long length)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Positioned input stream stream for LONG RAW value is not supported!");
  }

}
