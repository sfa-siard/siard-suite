package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.sql.*;

import ch.enterag.utils.SU;
import ch.enterag.utils.jdbc.*;

public class OracleClob
  extends BaseClob
  implements Clob
{
  private String _sValue = null;
  
  public OracleClob(String sValue)
  {
    super(null);
    _sValue = sValue;
  } /* constructor */
  
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long length() throws SQLException
  {
    return _sValue.length();
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getSubString(long pos, int length) throws SQLException
  {
    return _sValue.substring((int)(pos-1),(int)(pos+length-1));
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long position(String searchstr, long start) throws SQLException
  {
    return (long)_sValue.indexOf(searchstr, (int)(start-1))+1;
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long position(Clob searchclob, long start) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Cannot search for Clob in String!");
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int setString(long pos, String str) throws SQLException
  {
    _sValue = _sValue.substring(0,(int)(pos-1)) + str;
    return str.length();
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int setString(long pos, String str, int offset, int len)
    throws SQLException
  {
    _sValue = _sValue.substring(0,(int)(pos-1)) + str.substring(offset,offset+len);
    return len;
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public InputStream getAsciiStream() throws SQLException
  {
    return new ByteArrayInputStream(SU.putEncodedString(_sValue, "ASCII"));
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public OutputStream setAsciiStream(long pos) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("OracleClob is only used for reading!!");
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Reader getCharacterStream() throws SQLException
  {
    return new StringReader(_sValue);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Reader getCharacterStream(long pos, long length)
    throws SQLException
  {
    return new StringReader(_sValue.substring((int)(pos-1),(int)(pos+length-1)));
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Writer setCharacterStream(long pos) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("OracleClob is only used for reading!!");
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void truncate(long len) throws SQLException
  {
    _sValue = _sValue.substring(0,(int)len);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void free() throws SQLException
  {
    _sValue = null;
  }

} /* class OracleClob */
