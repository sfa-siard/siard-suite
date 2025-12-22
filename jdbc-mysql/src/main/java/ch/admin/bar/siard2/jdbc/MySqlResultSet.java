/*======================================================================
MySqlResultSet implements a wrapped MySql ResultSet.
Version     : $Id: $
Application : SIARD2
Description : MySqlResultSet implements a wrapped MySql ResultSet.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 31.10.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;

import javax.xml.datatype.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.*;

import ch.enterag.utils.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;

/* =============================================================================== */
/**
 * MySqlResultSet implements a wrapped MySql ResultSet
 * @author Simon Jutz
 */
public class MySqlResultSet 
  extends BaseResultSet 
  implements ResultSet 
{
	private MySqlConnection _conn = null;
	private QualifiedId _qiTable = null;
	private String _sPrimaryColumn = null;
	public void setPrimaryColumn(QualifiedId qiTable, String sPrimaryColumn) 
	{
	  _qiTable = qiTable;
	  _sPrimaryColumn = sPrimaryColumn;
  }

	/* ------------------------------------------------------------------------ */
	/** Constructor
	 * @param rsWrapped
	 */
	public MySqlResultSet(ResultSet rsWrapped, MySqlConnection conn)
	  throws SQLException
	{
		super(rsWrapped);
    _conn = conn;
	} /* constructor */

  /* ------------------------------------------------------------------------ */
  /** {@inheritDoc}
	 * Gets the wrapped Statement
	 */
	@Override
	public Statement getStatement() throws SQLException 
	{
		return new MySqlStatement(super.getStatement(),_conn);
	} /* getStatement */
	
  /* ------------------------------------------------------------------------ */
  /** {@inheritDoc}
	 * Gets the wrapped ResultSetMetaData
	 */
	public ResultSetMetaData getMetaData() throws SQLException 
	{
		return new MySqlResultSetMetaData(super.getMetaData(),_sPrimaryColumn);
	} /* getMetaData */

  /* ------------------------------------------------------------------------ */
  private Object mapObject(Object o, int iType) throws SQLException {
	  if ((o instanceof String) && (iType == Types.LONGVARCHAR)) {
		  Clob clob = getStatement().getConnection().createClob();
		  clob.setString(1l, (String) o);
		  o = clob;
	  } else if ((o instanceof String) && (iType == Types.LONGNVARCHAR)) {
		  NClob nclob = getStatement().getConnection().createNClob();
		  nclob.setString(1l, (String) o);
		  o = nclob;
	  } else if (o instanceof String && iType == Types.DATALINK) {
		  try {
			  o = new URL((String) o);
		  } catch (MalformedURLException e) {
			  throw new RuntimeException(e);
		  }
	  } else if ((o instanceof byte[]) && (iType == Types.LONGVARBINARY)) {
		  Blob blob = getStatement().getConnection().createBlob();
		  blob.setBytes(1l, (byte[]) o);
		  o = blob;
	  } else if ((o instanceof Integer) && (iType == Types.SMALLINT)) {
		  Integer i = (Integer) o;
		  Short sh = Short.valueOf(i.shortValue());
		  o = sh;
	  } else if ((o instanceof Integer) && (iType == Types.TINYINT)) {
		  Integer i = (Integer) o;
		  Short sh = Short.valueOf(i.shortValue());
		  o = sh;
	  }
	  return o;
  }

  /* ------------------------------------------------------------------------ */
  /** {@inheritDoc} */
  @Override
  public Object getObject(int columnIndex) throws SQLException {
	  Object o = super.getObject(columnIndex);
	  int iType = getMetaData().unwrap(ResultSetMetaData.class).getColumnType(columnIndex);
	  String sColumnType = getMetaData().getColumnTypeName(columnIndex);
	  int iDisplaySize = getMetaData().getColumnDisplaySize(columnIndex);
	  if (sColumnType.equals("GEOMETRY") ||
			  sColumnType.equals("POINT") ||
			  sColumnType.equals("LINESTRING") ||
			  sColumnType.equals("POLYGON") ||
			  sColumnType.equals("MULTIPOINT") ||
			  sColumnType.equals("MULTILINESTRING") ||
			  sColumnType.equals("MULTIPOLYGON") ||
			  sColumnType.equals("GEOMETRYCOLLECTION")) {
		  try {
			  o = getGeometryFromInputStream(new ByteArrayInputStream((byte[]) o)).toText();
		  } catch (Exception e) {
			  throw new SQLException("Parsing of Geometry failed!", e);
		  }
	  } else if (sColumnType.equals("SMALLINT UNSIGNED"))
		  o = mapObject(o, Types.INTEGER);
	  else if (sColumnType.equals("VARCHAR")) {
		  if (iDisplaySize < 256)
			  o = mapObject(o, Types.VARCHAR);
		  else
			  o = mapObject(o, iType);
	  } else if (o instanceof LocalDateTime) {
		  // seems to be a breaking change when updating MySql Connector Version 8.0 to 8.3
		  return Timestamp.valueOf((LocalDateTime)o);
	  } else
		  o = mapObject(o, iType);
	  return o;
  }

  /* ------------------------------------------------------------------------ */
  /** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException
	{
		Object o = null;
		T oMapped = null;
		int iType = Types.OTHER;
		o = super.getObject(columnIndex);
		String sColumnType = getMetaData().getColumnTypeName(columnIndex);
    if(sColumnType.equals("GEOMETRY") ||
       sColumnType.equals("POINT") ||
       sColumnType.equals("LINESTRING") ||
       sColumnType.equals("POLYGON") ||
       sColumnType.equals("MULTIPOINT") ||
       sColumnType.equals("MULTILINESTRING") ||
       sColumnType.equals("MULTIPOLYGON") ||
       sColumnType.equals("GEOMETRYCOLLECTION")) 
    { // geometry types
      try { o = getGeometryFromInputStream(new ByteArrayInputStream((byte[]) o)).toText(); }
      catch (Exception e) { throw new SQLException("Parsing of Geometry failed!",e); }
    }
		if (type.isInstance(o))
			oMapped = (T) o;
		else
			oMapped = mapObject(o, iType, type);
		return oMapped;
	} /* getObject */

  /* ------------------------------------------------------------------------ */
	private <T> T mapObject(Object o, int iType, Class<T> type) throws SQLException {
		T oMapped = null;
		oMapped = type.cast(mapObject(o, iType));
		return oMapped;
	} /* mapObject */
	
  /* ------------------------------------------------------------------------ */
  /** {@inheritDoc} */
	@Override
	public String getString(int columnIndex) throws SQLException 
	{
		String result = null;
		String sColumnType = getMetaData().getColumnTypeName(columnIndex);
    if(sColumnType.equals("GEOMETRY") ||
      sColumnType.equals("POINT") ||
      sColumnType.equals("LINESTRING") ||
      sColumnType.equals("POLYGON") ||
      sColumnType.equals("MULTIPOINT") ||
      sColumnType.equals("MULTILINESTRING") ||
      sColumnType.equals("MULTIPOLYGON") ||
      sColumnType.equals("GEOMETRYCOLLECTION")) 
   { // geometry types
     try { result = getGeometryFromInputStream(new ByteArrayInputStream(super.getBytes(columnIndex))).toText(); }
     catch (Exception e) { throw new SQLException("Parsing of Geometry failed!",e); }
   }
	 else
		 result = super.getString(columnIndex);
		return result;
	} /* getString */
	
  /* ------------------------------------------------------------------------ */
  /** {@inheritDoc} */
	@Override
	public String getNString(int columnIndex) throws SQLException 
	{
		return getString(columnIndex);
	} /* getNString */
	
  /* ------------------------------------------------------------------------ */
  /** {@inheritDoc} */
	@Override
  public Duration getDuration(int columnIndex) 
    throws SQLException, SQLFeatureNotSupportedException
  {
    byte[] buf = getBytes(columnIndex);
    Interval iv = SqlLiterals.deserialize(buf, Interval.class);
    return iv.toDuration();
  } /* getDuration */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException
	{
		byte[] buf = readByteArray(inputStream);
		Blob b = _conn.createBlob();
		b.setBytes(1l, buf);
		updateBlob(columnIndex, b);
	} /* updateBlob */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		byte[] buf = readByteArray(inputStream);
		Blob b = _conn.createBlob();
		b.setBytes(1l, buf);
		updateBlob(columnIndex, b);
	} /* updateBlob */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException
	{
		String s = readString(reader);
		updateString(columnIndex, s);
	} /* updateClob */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException
	{
		String s = readString(reader);
		updateString(columnIndex, s);
	} /* updateClob */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		updateBytes(columnIndex, readByteArray(x));
	} /* updateBinaryStream */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		updateBytes(columnIndex, readByteArray(x));
	} /* updateBinaryStream */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		updateBytes(columnIndex, readByteArray(x));
	} /* updateBinaryStream */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateCharacterStream(int columnIndex, Reader reader) throws SQLException {
		updateString(columnIndex, readString(reader));
	} /* updateCharacterStream */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateCharacterStream(int columnIndex, Reader reader, int length) throws SQLException {
		updateString(columnIndex, readString(reader));
	} /* updateCharacterStream */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateCharacterStream(int columnIndex, Reader reader, long length) throws SQLException {
		updateString(columnIndex, readString(reader));
	} /* updateCharacterStream */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		Clob clob = getClob(columnIndex);
		NClob nclob = _conn.createNClob();
		nclob.setString(1L, clob.getSubString(1L, (int) clob.length()));
		clob.free();
		return nclob;
	} /* getNClob */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return getCharacterStream(columnIndex);
	}

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateNClob(int columnIndex, NClob nclob) throws SQLException {
		Clob clob = _conn.createClob();
		clob.setString(1L, nclob.getSubString(1L, (int) nclob.length()));
		updateClob(columnIndex, clob);
		clob.free();
	} /* updateNClob */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * (https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-implementation-notes.html)
	 */
	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		updateCharacterStream(columnIndex, x);
	} /* updateNCharacterStream */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * (https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-implementation-notes.html)
	 */
	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		updateCharacterStream(columnIndex, x, length);
	} /* updateNCharacterStream */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateNClob(int columnIndex, Reader x) throws SQLException {
		updateClob(columnIndex, x);
	} /* updateNClob */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateNClob(int columnIndex, Reader x, long length) throws SQLException {
		updateClob(columnIndex, x, length);
	} /* updateNClob */

  private void setNoBackslashEscapes(boolean bNoBackslashEscapes)
    throws SQLException
  {
    String sSql = "ANSI";
    if (bNoBackslashEscapes)
      sSql = sSql + ",NO_BACKSLASH_ESCAPES";
    sSql = "SET SESSION sql_mode = '"+sSql+"';";
    Connection connNative = _conn.unwrap(Connection.class);
    Statement stmt = connNative.createStatement();
    stmt.executeUpdate(sSql);
    stmt.close();
  } /* setNoBackSlashEscapes */
  
	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc} */
	@Override
	public void insertRow() throws SQLException 
	{
    setNoBackslashEscapes(false);
		super.insertRow();
    setNoBackslashEscapes(true);
	} /* insertRow */
	
  /* ------------------------------------------------------------------------ */
  /** {@inheritDoc} */
  @Override
  public void updateRow() throws SQLException 
  {
    setNoBackslashEscapes(false);
    super.updateRow();
    setNoBackslashEscapes(true);
  } /* updateRow */
  
	/* ------------------------------------------------------------------------ */
	/**
	 * {@inheritDoc}
	 * Overridden as the MySql driver simply throws an NotUpdatable exception
	 * https://github.com/mysql/mysql-connector-j/blob/release/5.1/src/com/mysql/jdbc/JDBC4ResultSet.java
	 */
	@Override
	public void updateSQLXML(int columnIndex, SQLXML xml) throws SQLException {
		String sXML = xml.getString();
		xml.free();
		/*
		 * Check if XML is valid
		 */
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(sXML.getBytes(SU.sUTF8_CHARSET_NAME))));
		} catch (ParserConfigurationException e) {
			throw new SQLException(e);
		} catch (SAXException e) {
			throw new SQLException(e);
		} catch (IOException e) {
			throw new SQLException(e);
		}
		if(doc != null) {
			updateString(columnIndex, sXML);
		}
	} /* updateSQLXML */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateObject(int columnIndex, Object x) throws SQLException {
	  if (x instanceof SQLXML)
		  updateSQLXML(columnIndex, (SQLXML) x);
	  else if (x instanceof NClob)
		  updateNClob(columnIndex, (NClob) x);
	  else
		  super.updateObject(columnIndex, x);
  }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateDuration(int columnIndex, Duration x)
    throws SQLException
  {
    Interval iv = Interval.fromDuration(x);
    byte[] buf = SqlLiterals.serialize(iv);
    updateBytes(columnIndex,buf);
  } /* updateDuration */

	/* ------------------------------------------------------------------------ */
	/** Gets a Object from an InputStream
	 * @param inputStream an input stream
	 * @return a Geometry Object
	 * @throws Exception if the bytes in the input stream could not be parsed
	 */
	private Geometry getGeometryFromInputStream(InputStream inputStream) throws Exception {
	     Geometry geometry = null;
	     if (inputStream != null) {
	         byte[] buffer = new byte[255];
	         int bytesRead = 0;
	         ByteArrayOutputStream baos = new ByteArrayOutputStream();
	         while ((bytesRead = inputStream.read(buffer)) != -1) {
	             baos.write(buffer, 0, bytesRead);
	         }
	         byte[] geometryAsBytes = baos.toByteArray();
	 
	         // first four bytes of the geometry are the SRID
	         byte[] sridBytes = new byte[4];
	         System.arraycopy(geometryAsBytes, 0, sridBytes, 0, 4);
	         boolean bigEndian = (geometryAsBytes[4] == 0x00);
	 
	         int srid = 0;
	         if (bigEndian) {
	            for (int i = 0; i < sridBytes.length; i++) {
	               srid = (srid << 8) + (sridBytes[i] & 0xff);
	            }
	         } else {
	            for (int i = 0; i < sridBytes.length; i++) {
	              srid += (sridBytes[i] & 0xff) << (8 * i);
	            }
	         }
	 
	         WKBReader wkbReader = new WKBReader();
	         byte[] wkb = new byte[geometryAsBytes.length - 4];
	         System.arraycopy(geometryAsBytes, 4, wkb, 0, wkb.length);
	         geometry = wkbReader.read(wkb);
	         geometry.setSRID(srid);
	     }
	 
	     return geometry;
	 } /* getGeometryFromInputStream */
	
	/* ------------------------------------------------------------------------ */
	/** Reads an inputStream and returns a byte array
	 * @param inputStream the input stream to read
	 * @return a byte array consisting of the same bytes as the input stream
	 */
	private byte[] readByteArray(InputStream inputStream) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buf = new byte[0xFFFF];
		try {
			for(int len; (len = inputStream.read(buf)) != -1; ) {
				os.write(buf, 0, len);
			}
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return os.toByteArray();
	} /* getByteArrayFromInputStream */

	/* ------------------------------------------------------------------------ */
	/** Reads a string from a reader
	 * @param reader
	 * @return the string read
	 */
	private String readString(Reader reader) {
		StringBuilder builder = new StringBuilder();
		try {
			int c = -1;
			char[] chars = new char[0xFFFF];
			do {
					c = reader.read(chars, 0, chars.length);
				if (c>0) {
					builder.append(chars, 0, c);
				}
			} while(c>0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	} /* getStringFromReader */
	
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getCursorName() throws SQLException
  {
    String s = null;
    try { s = super.getCursorName(); }
    catch(SQLFeatureNotSupportedException sfnse) { throw sfnse; }
    catch(SQLException se) { throw new SQLFeatureNotSupportedException("getCursorName not supported!",se); }
    return s;
  } /* getCursorName */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x)
    throws SQLException
  {
    try { super.updateAsciiStream(columnIndex, x); }
    catch(SQLFeatureNotSupportedException sfnse) { throw sfnse; }
    catch(SQLException se) { throw new SQLFeatureNotSupportedException("updateAsciiStream not supported!",se); }
  } /* updateAsciiStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x,
    int length) throws SQLException
  {
    try { super.updateAsciiStream(columnIndex, x, length); }
    catch(SQLFeatureNotSupportedException sfnse) { throw sfnse; }
    catch(SQLException se) { throw new SQLFeatureNotSupportedException("updateAsciiStream not supported!",se); }
  } /* updateAsciiStream */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void updateAsciiStream(int columnIndex, InputStream x,
    long length) throws SQLException
  {
    try { super.updateAsciiStream(columnIndex, x, length); }
    catch(SQLFeatureNotSupportedException sfnse) { throw sfnse; }
    catch(SQLException se) { throw new SQLFeatureNotSupportedException("updateAsciiStream not supported!",se); }
  } /* updateAsciiStream */

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
  public void close() throws SQLException
  {
    MySqlStatement stmt = (MySqlStatement)getStatement();
    super.close();
    stmt.removePrimaryColumn(_qiTable,_sPrimaryColumn);
  }
  
} /* class MySqlResultSet */
