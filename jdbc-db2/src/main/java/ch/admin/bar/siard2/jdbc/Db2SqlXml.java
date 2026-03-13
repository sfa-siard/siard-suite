/*======================================================================
Db2SqlXml implements an SQLXML instance.
Version     : $Id: $
Application : SIARD2
Description : Db2SqlXml implements an SQLXML instance.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 30.05.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import ch.enterag.utils.SU;
import ch.enterag.utils.jdbc.BaseSqlXml;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.*;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;


/** Db2SqlXml implements an SQLXML instance.
 * @author Hartwig Thomas
 */
public class Db2SqlXml
        extends BaseSqlXml
        implements SQLXML {
    /** content of an SQLXML object (could be externalized into a temporary file for very large XML data) */
    private String _sXml = null;

    /** XmlOutputStream captures output */
    private class XmlOutputStream extends ByteArrayOutputStream {
        @Override
        public void close() throws IOException {
            _sXml = SU.getUtf8String(toByteArray());
        }
    }

    /** XmlWriter captures output */
    private class XmlWriter extends StringWriter {
        @Override
        public void close() throws IOException {
            _sXml = toString();
        }
    }


    /** constructor
     * @param sXml content of the SQLXML object.
     */
    private Db2SqlXml(String sXml) {
        super(null);
        _sXml = sXml;
    }


    /** factory
     * @param sXml content of the SQLXML object.
     * @return new H2SqlXml instance.
     */
    public static Db2SqlXml newInstance(String sXml) {
        return new Db2SqlXml(sXml);
    }


    /** {@inheritDoc} */
    @Override
    public InputStream getBinaryStream() throws SQLException {
        return new ByteArrayInputStream(SU.putUtf8String(_sXml));
    }


    /** {@inheritDoc} */
    @Override
    public OutputStream setBinaryStream() throws SQLException {
        return new XmlOutputStream();
    }


    /** {@inheritDoc} */
    @Override
    public Reader getCharacterStream() throws SQLException {
        return new StringReader(_sXml);
    }


    /** {@inheritDoc} */
    @Override
    public Writer setCharacterStream() throws SQLException {
        return new XmlWriter();
    }


    /** {@inheritDoc} */
    @Override
    public String getString() throws SQLException {
        return _sXml;
    }


    /** {@inheritDoc} */
    @Override
    public void setString(String value) throws SQLException {
        _sXml = value;
    }


    /** {@inheritDoc} */
    @Override
    public <T extends Source> T getSource(Class<T> sourceClass)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("getSource() not supported!");
    }


    /** {@inheritDoc} */
    @Override
    public <T extends Result> T setResult(Class<T> resultClass)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("getResult() not supported!");
    }


    /** {@inheritDoc} */
    @Override
    public void free() throws SQLException {
        _sXml = null;
    }

}
