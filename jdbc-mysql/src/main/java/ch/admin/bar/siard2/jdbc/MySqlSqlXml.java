/*======================================================================
MySqlSqlXml implements an SqlXml instance
Version     : $Id: $
Application : SIARD2
Description : MySqlSqlXml implements an SqlXml instance
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 20.12.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import ch.enterag.utils.SU;
import ch.enterag.utils.jdbc.BaseSqlXml;

/* =============================================================================== */
/**
 * MySqlSqlXml implements an SqlXml instance
 * @author Simon Jutz
 */
public class MySqlSqlXml extends BaseSqlXml implements SQLXML {
	private String _sXml = null;

	/* ------------------------------------------------------------------------ */
	
	/** XmlOutputStream captures output */
	private class XmlOutputStream extends ByteArrayOutputStream
	{
		@Override
		public void close() throws IOException
		{
			_sXml = SU.getUtf8String(toByteArray());
		}
	} /* class XmlOutputStream */

	/* ------------------------------------------------------------------------ */
	
	/** XmlWriter captures output */
	private class XmlWriter extends StringWriter
	{
		@Override
		public void close() throws IOException
		{
			_sXml = toString();
		}
	}

	/* ------------------------------------------------------------------------ */
	
	/**
	 * constructor
	 * @param sqlxmlWrapped sqlxml to be wrapped
	 */
	public MySqlSqlXml(SQLXML sqlxmlWrapped) {
		super(sqlxmlWrapped);
	} /* constructor */

	/* ------------------------------------------------------------------------ */
	
	/**
	 * gets a new MySqlSqlXml instance
	 * @return a new MySqlSqlXml instance
	 */
	public static MySqlSqlXml getInstance() {
		return new MySqlSqlXml(null);
	} /* getIntance */

	/* ------------------------------------------------------------------------ */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getBinaryStream() throws SQLException {
		return new ByteArrayInputStream(SU.putUtf8String(_sXml));
	} /* getBinaryStream */

	/* ------------------------------------------------------------------------ */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputStream setBinaryStream() throws SQLException {
		return new XmlOutputStream(); 
	} /* setBinaryStream */

	/* ------------------------------------------------------------------------ */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getCharacterStream() throws SQLException {
		return new StringReader(_sXml);
	} /* getCharacterStream */

	/* ------------------------------------------------------------------------ */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Writer setCharacterStream() throws SQLException {
		return new XmlWriter();
	} /* setCharacterStream */

	/* ------------------------------------------------------------------------ */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString() throws SQLException {
		return _sXml;
	} /* getString */

	/* ------------------------------------------------------------------------ */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setString(String value) throws SQLException {
		_sXml = value;
	} /* setString */

	/* ------------------------------------------------------------------------ */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Source> T getSource(Class<T> sourceClass) throws SQLException {
		throw new SQLFeatureNotSupportedException("getSource() not supported!");
	} /* getSource */

	/* ------------------------------------------------------------------------ */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Result> T setResult(Class<T> resultClass) throws SQLException {
		throw new SQLFeatureNotSupportedException("setResult() not supported!");
	} /* setResult */

	/* ------------------------------------------------------------------------ */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void free() throws SQLException {
		_sXml = null;
	} /* free */
	
} /* class MySqlSqlXml */
