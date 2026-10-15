/*======================================================================
BaseDataSource implements a wrapped DataSource.
Version     : $Id: $
Application : SIARD2
Description : BaseDataSource implements a wrapped DataSource.
              See https://docs.oracle.com/javase/7/docs/api/javax/sql/DataSource.html
Platform    : Java 21   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 08.03.2016, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jdbcx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * BaseDataSource implements a wrapped DataSource and serves as a base
 * for derived JDBC wrappers.
 *
 * @author Hartwig Thomas
 */
public abstract class BaseDataSource
        implements DataSource {

    private static final Logger log = LoggerFactory.getLogger(BaseDataSource.class);
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
    public java.util.logging.Logger getParentLogger()
            throws SQLFeatureNotSupportedException {
        java.util.logging.Logger loggerParent = null;
        /* throws AbstractMethodError if dsWrapped was compiled under 1.6 */
        try {
            loggerParent = _dsWrapped.getParentLogger();
        } catch (Exception e) {
            log.error("Exception logged", e);
        } catch (Error e) {
            log.error("Error logged", e);
        }
        if (loggerParent == null)
            loggerParent = java.util.logging.Logger.getLogger(log.getName())
                                 .getParent();
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
