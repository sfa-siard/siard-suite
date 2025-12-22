/*======================================================================
BaseBlob implements a wrapped Blob
Application : SIARD2
Description : BaseBlob implements a wrapped Blob
              See https://docs.oracle.com/javase/7/docs/api/java/sql/Blob.html
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Enter AG, Rüti ZH, Switzerland
Created    : 02.09.2019, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jdbc;

import java.io.*;
import java.sql.*;

/**
 * BaseBlob implements a wrapped Blob and serves as a base
 * for derived JDBC wrappers.
 *
 * @author Hartwig Thomas
 */
public abstract class BaseBlob
        implements Blob {
    /**
     * wrapped blob
     */
    private Blob _blobWrapped = null;

    /**
     * constructor
     *
     * @param blobWrapped Blob to be wrapped or null.
     */
    public BaseBlob(Blob blobWrapped) {
        _blobWrapped = blobWrapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long length() throws SQLException {
        return _blobWrapped.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        return _blobWrapped.position(pattern, start);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long position(Blob pattern, long start) throws SQLException {
        return _blobWrapped.position(pattern, start);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        return _blobWrapped.getBytes(pos, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return _blobWrapped.setBytes(pos, bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len)
            throws SQLException {
        return _blobWrapped.setBytes(pos, bytes, offset, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getBinaryStream() throws SQLException {
        return _blobWrapped.getBinaryStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getBinaryStream(long pos, long length)
            throws SQLException {
        return _blobWrapped.getBinaryStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        return _blobWrapped.setBinaryStream(pos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void truncate(long len) throws SQLException {
        _blobWrapped.truncate(len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void free() throws SQLException {
        _blobWrapped.free();
    }

    /**
     * as for all other JDBC interfaces ....
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface == Blob.class);
    }

    /**
     * as for all other JDBC interfaces ....
     */
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T wrapped = null;
        if (isWrapperFor(iface))
            wrapped = (T) _blobWrapped;
        return wrapped;
    }
}
