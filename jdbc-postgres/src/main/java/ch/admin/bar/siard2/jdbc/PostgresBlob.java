package ch.admin.bar.siard2.jdbc;

import ch.enterag.utils.*;
import ch.enterag.utils.jdbc.*;
import java.io.*;
import java.sql.*;
import org.postgresql.jdbc.*;
import org.postgresql.largeobject.*;

public class PostgresBlob
  extends BaseBlob
{
  private static final int iBUFSIZ = 8192;
  private LargeObjectManager _lom = null;
  private long _lOid = -1;
  public long getOid() { return _lOid; }

  public PostgresBlob(PostgresConnection conn, long lOid)
    throws SQLException
  {
    super(null);
    PgConnection pgconn = (PgConnection)conn.unwrap(Connection.class);
    _lom = pgconn.getLargeObjectAPI();
    _lOid = lOid;
  } /* constructor PostgresBlob */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long length() throws SQLException
  {
    LargeObject lo = _lom.open(_lOid, LargeObjectManager.READ);
    long lLength = lo.size64();
    lo.close();
    return lLength;
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public byte[] getBytes(long pos, int length) throws SQLException
  {
    ByteArrayOutputStream baos = null;
    try
    {
      baos = new ByteArrayOutputStream();
      InputStream is = getBinaryStream();
      is.skip(pos-1);
      byte[] buf = new byte[iBUFSIZ];
      int iOffset = 0;
      int iLength = Math.min(length-iOffset, iBUFSIZ);
      for (int iRead = is.read(buf,0,iLength); (iRead != -1) && (iOffset < length); iRead = is.read(buf,0,iLength))
      {
        baos.write(buf,0,iRead);
        iOffset = iOffset + iRead;
        iLength = Math.min(length-iOffset, iBUFSIZ);
      }
      is.close();
      baos.close();
    }
    catch (IOException ie) { throw new SQLException("Bytes could not be read from PostgresClob ("+EU.getExceptionMessage(ie)+")!"); }
    return baos.toByteArray();
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long position(byte[] pattern, long start) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Position of byte patterns in PostgresBlob is not supported!");
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long position(Blob pattern, long start) throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Position of Blob in PostgresBlob is not supported!");
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int setBytes(long pos, byte[] bytes) throws SQLException
  {
    return setBytes(pos, bytes, 0, bytes.length);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int setBytes(long pos, byte[] bytes, int offset, int len)
    throws SQLException
  {
    int iWritten = -1;
    try
    {
      iWritten = 0;
      OutputStream os = setBinaryStream(pos);
      os.write(bytes, offset, len);
      os.close();
    }
    catch(IOException ie) { throw new SQLException("Setting a string in PostgresClob failed ("+EU.getExceptionMessage(ie)+")!"); }
    return iWritten;
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public InputStream getBinaryStream() throws SQLException
  {
    LargeObject lo = _lom.open(_lOid, LargeObjectManager.READ);
    InputStream is = lo.getInputStream();
    // lo.close();
    return is;
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public InputStream getBinaryStream(long pos, long length)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("BinaryStream with limited length in PostgresBlob is not supported!");
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public OutputStream setBinaryStream(long pos) throws SQLException
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
  public void truncate(long len) throws SQLException
  {
    LargeObject lo = _lom.open(_lOid, LargeObjectManager.WRITE);
    lo.truncate64(len);
    lo.close();
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void free() throws SQLException
  {
    _lOid = -1;
    _lom = null;
  }

} /* class PostgresBlob */
