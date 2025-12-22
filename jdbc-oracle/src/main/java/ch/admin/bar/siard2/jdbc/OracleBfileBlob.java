package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.sql.*;
import java.util.*;

@SuppressWarnings("deprecation")
public class OracleBfileBlob implements Blob
{
  private oracle.sql.BFILE _bfile = null;
  
  private class BfileInputStream extends InputStream
  {
    private InputStream _is = null;
    private oracle.sql.BFILE _bfile = null;
    
    BfileInputStream(oracle.sql.BFILE bfile)
      throws SQLException
    { 
      super();
      _bfile = bfile;
      _bfile.openFile();
      _is = _bfile.getBinaryStream();
    } /* constructor */
    
    @Override
    public int read()
      throws IOException
    {
      return _is.read();
    } /* read */
    
    @Override
    public int read(byte[] buf)
      throws IOException
    {
      return _is.read(buf);
    } /* read */
    
    @Override
    public int read(byte[] buf, int offset, int len)
      throws IOException
    {
      return _is.read(buf, offset, len);
    } /* read */
    
    @Override
    public void close()
      throws IOException
    {
      _is.close();
      try { _bfile.closeFile(); }
      catch(SQLException se) { throw new IOException(se); }
    } /* close */
    
  } /* class BfileInputStream */
  
  public OracleBfileBlob(oracle.sql.BFILE bfile)
  {
    _bfile = bfile;
  } /* constructor OracleBfileBlob */
  
  @Override
  public long length() throws SQLException
  {
    long lLength = _bfile.length();
    return lLength;
  } /* length */
  
  @Override
  public byte[] getBytes(long pos, int length) throws SQLException
  {
    byte[] buf = null;
    _bfile.open();
    buf = _bfile.getBytes(pos,length);
    _bfile.close();
    return buf;
  } /* getBytes */
  
  @Override
  public InputStream getBinaryStream() throws SQLException
  {
    return new BfileInputStream(_bfile);
  } /* getBinaryStream */
  
  @Override
  public long position(byte[] pattern, long start) throws SQLException
  {
    return _bfile.position(pattern, start);
  } /* position */
  
  @Override
  public long position(Blob pattern, long start) throws SQLException
  {
    return _bfile.position(pattern.getBytes(1l, (int)pattern.length()), start);
  } /* position */
  
  @Override
  public int setBytes(long pos, byte[] bytes) throws SQLException
  {
    return setBytes(pos, bytes, 0, bytes.length);
  } /* setBytes */
  
  @Override
  public int setBytes(long pos, byte[] bytes, int offset, int len)
    throws SQLException
  {
    int iSet = 0;
    if ((offset != 0) || (len != bytes.length))
      bytes = Arrays.copyOfRange(bytes, offset, len);
    if (pos == 1l)
    {
      _bfile.setBytes(bytes);
      iSet = (int)_bfile.getLength(); 
    }
    else
      throw new SQLFeatureNotSupportedException("BFILE cannot update partial value!");
    return iSet;
  } /* setBytes */
  
  @Override
  public OutputStream setBinaryStream(long pos) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("BFILE cannot be updated!");
  }
  @Override
  public void truncate(long len) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("BFILE cannot be updated!");
  }
  @Override
  public void free() throws SQLException
  {
    _bfile = null;
  }
  @Override
  public InputStream getBinaryStream(long pos, long length)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("BFILE sub stream not supported!");
  }
} /* class OracleBfileBlob */
