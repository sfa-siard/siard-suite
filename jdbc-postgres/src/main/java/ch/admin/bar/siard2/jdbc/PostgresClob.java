package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.nio.charset.*;
import java.sql.*;
import org.postgresql.jdbc.*;
import org.postgresql.largeobject.*;

import ch.enterag.utils.*;
import ch.enterag.utils.jdbc.*;

public class PostgresClob
  extends BaseClob
{
  private static final int iBUFSIZ = 8192;
  private LargeObjectManager _lom = null;
  private long _lOid = -1;
  public long getOid() { return _lOid; }
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param pgClob
   */
  public PostgresClob(PostgresConnection conn, long lOid)
    throws SQLException
  {
    super(null);
    PgConnection pgconn = (PgConnection)conn.unwrap(Connection.class);
    _lom = pgconn.getLargeObjectAPI();
    _lOid = lOid;
  } /* constructor PostgresClob */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long length() throws SQLException
  {
    long lLength = -1;
    /* we need the length in characters, not in bytes ... */
    try
    {
      lLength = 0;
      Reader rdr = getCharacterStream();
      char[] cbuf = new char[iBUFSIZ];
      for (int iRead = rdr.read(cbuf); iRead != -1; iRead = rdr.read(cbuf))
        lLength = lLength + iRead;
      rdr.close();
    }
    catch (IOException ie) { throw new SQLException("Length of PostgresClob could not be determined ("+EU.getExceptionMessage(ie)+")!"); }
    return lLength;
  } /* length */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getSubString(long pos, int length) throws SQLException
  {
    StringWriter swr = null;
    try
    {
      swr = new StringWriter();
      Reader rdr = getCharacterStream();
      rdr.skip(pos-1);
      char[] cbuf = new char[iBUFSIZ]; 
      int iOffset = 0;
      int iLength = Math.min(length-iOffset, iBUFSIZ);
      for (int iRead = rdr.read(cbuf,0,iLength); (iRead != -1) && (iOffset < length); iRead = rdr.read(cbuf,0,iLength))
      {
        swr.write(cbuf,0,iRead);
        iOffset = iOffset + iRead;
        iLength = Math.min(length-iOffset, iBUFSIZ);
      }
      rdr.close();
      swr.close();
    }
    catch (IOException ie) { throw new SQLException("String could not be read from PostgresClob ("+EU.getExceptionMessage(ie)+")!"); }
    return swr.toString();
  } /* getSubstring */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long position(String searchstr, long start) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Position of substrings in PostgresClob is not supported!");
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long position(Clob searchclob, long start) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Position of substrings in PostgresClob is not supported!");
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int setString(long pos, String str) throws SQLException
  {
    return setString(pos, str, 0, str.length());
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int setString(long pos, String str, int offset, int len)
    throws SQLException
  {
    int iWritten = -1;
    try
    {
      iWritten = 0;
      Writer wr = setCharacterStream(pos);
      wr.write(str, offset, len);
      wr.close();
    }
    catch(IOException ie) { throw new SQLException("Setting a string in PostgresClob failed ("+EU.getExceptionMessage(ie)+")!"); }
    return iWritten;
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public InputStream getAsciiStream() throws SQLException
  {
    LargeObject lo = _lom.open(_lOid, LargeObjectManager.READ);
    InputStream is = lo.getInputStream();
    // lo.close();
    return is;
  } /* getAsciiStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public OutputStream setAsciiStream(long pos) throws SQLException
  {
    LargeObject lo = _lom.open(_lOid, LargeObjectManager.WRITE);
    lo.seek64(pos-1,LargeObject.SEEK_SET);
    OutputStream os = lo.getOutputStream();
    // lo.close();
    return os;
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Reader getCharacterStream() throws SQLException
  {
    return new InputStreamReader(getAsciiStream(),Charset.forName("UTF-8"));
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Reader getCharacterStream(long pos, long length)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("CharacterStream with limited length in PostgresClob is not supported!");
  }

  private long getBytePos(long lPos)
    throws IOException, SQLException
  {
    long lBytePos = lPos;
    /* pos is in characters rather than bytes! */
    if (lPos > 1)
    {
      LargeObject lo = _lom.open(_lOid, LargeObjectManager.READ);
      InputStream is = lo.getInputStream();
      Reader rdr = new InputStreamReader(is,Charset.forName("UTF-8"));
      rdr.skip(lPos-1);
      /* position in bytes */
      lBytePos = lo.tell64()+1;
      rdr.close();
    }
    return lBytePos;
  }
    
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Writer setCharacterStream(long pos) throws SQLException
  {
    Writer wr = null;
    try
    {
      long lPos = getBytePos(pos);
      wr = new OutputStreamWriter(setAsciiStream(lPos),Charset.forName("UTF-8"));
    }
    catch(IOException ie) { throw new SQLException("CharacterStream could not be positioned for writing in PostgresClob ("+EU.getExceptionMessage(ie)+")!"); }
    return wr;
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void truncate(long len) throws SQLException
  {
    try
    {
      long lLength = getBytePos(len);
      LargeObject lo = _lom.open(_lOid, LargeObjectManager.WRITE);
      lo.truncate64(lLength);
      lo.close();
    }
    catch(IOException ie) { throw new SQLException("CharacterStream could not be positioned for truncation in PostgresClob ("+EU.getExceptionMessage(ie)+")!"); }
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void free() throws SQLException
  {
    _lOid = -1;
    _lom = null;
  }

} /* class PostgresClob */
