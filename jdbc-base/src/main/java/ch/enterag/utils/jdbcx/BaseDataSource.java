/*======================================================================
BaseDataSource implements a wrapped DataSource.
Version     : $Id: $
Application : SIARD2
Description : BaseDataSource implements a wrapped DataSource.
              See https://docs.oracle.com/javase/7/docs/api/javax/sql/DataSource.html
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 08.03.2016, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jdbcx;

import java.io.*;
import java.sql.*;
import java.util.logging.*;
import javax.sql.*;

import ch.enterag.utils.logging.*;

/**
 * BaseDataSource implements a wrapped DataSource and serves as a base
 * for derived JDBC wrappers.
 *
 * @author Hartwig Thomas
 */
public abstract class BaseDataSource
        implements DataSource {
    /**
     * logger
     */
    private static IndentLogger _il = IndentLogger.getIndentLogger(BaseDataSource.class.getName());
    /**
     * wrapped DataSource implementation
     */
    private DataSource _dsWrapped = null;

    /**
     * constructor
     *
     * @param dsWrapped DataSource implementation to be wrapped.
     */
    public BaseDataSource(DataSource dsWrapped) {
        _dsWrapped = dsWrapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        return _dsWrapped.getConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        return _dsWrapped.getConnection(username, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return _dsWrapped.getLoginTimeout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        _dsWrapped.setLoginTimeout(seconds);
    }

    /**
     * {@inheritDoc}
     * Ancient! Pre-java.util.logging was used for tracing with System.out.
     * Is ignored.
     */
    @Override
    @Deprecated
    public PrintWriter getLogWriter() throws SQLException {
        return _dsWrapped.getLogWriter();
    }

    /**
     * {@inheritDoc}
     * Ancient! Pre-java.util.logging was used for tracing with System.out.
     * Is ignored.
     */
    @Override
    @Deprecated
    public void setLogWriter(PrintWriter out) throws SQLException {
        _dsWrapped.setLogWriter(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getParentLogger()
            throws SQLFeatureNotSupportedException {
        Logger loggerParent = null;
        /* throws AbstractMethodError if dsWrapped was compiled under 1.6 */
        try {
            loggerParent = _dsWrapped.getParentLogger();
        } catch (Exception e) {
            _il.exception(e);
        } catch (Error e) {
            _il.error(e);
        }
        if (loggerParent == null)
            loggerParent = _il.getParent();
        return loggerParent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface == DataSource.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T wrapped = null;
        if (isWrapperFor(iface))
            wrapped = (T) _dsWrapped;
        return wrapped;
    }
}
