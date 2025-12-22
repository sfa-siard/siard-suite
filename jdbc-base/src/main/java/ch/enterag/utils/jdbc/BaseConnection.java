/*======================================================================
BaseConnection implements a wrapped Connection.
Version     : $Id: $
Application : SIARD2
Description : BaseConnection implements a wrapped Connection.
              See https://docs.oracle.com/javase/7/docs/api/java/sql/Connection.html
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 09.03.2016, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jdbc;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * BaseConnection implements a wrapped Connection and serves as a base
 * for derived JDBC wrappers.
 *
 * @author Hartwig Thomas
 */
public abstract class BaseConnection
        implements Connection {
    /**
     * wrapped connection
     */
    private Connection _connWrapped = null;

    /**
     * convert an AbstractMethodError into an SQLFeatureNotSupportedException.
     * This error indicates that the JDBC driver wrapped implements an
     * earlier version of JDBC which did not include this method.
     *
     * @param ame
     * @throws SQLFeatureNotSupportedException
     */
    private void throwUndefinedMethod(AbstractMethodError ame)
            throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Undefined JDBC method!", ame);
    }

    /**
     * constructor
     *
     * @param connWrapped connection to be wrapped.
     */
    public BaseConnection(Connection connWrapped) {
        _connWrapped = connWrapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement()
            throws SQLException {
        return _connWrapped.createStatement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql)
            throws SQLException {
        return _connWrapped.prepareStatement(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(String sql)
            throws SQLException {
        return _connWrapped.prepareCall(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String nativeSQL(String sql)
            throws SQLException {
        return _connWrapped.nativeSQL(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoCommit(boolean autoCommit)
            throws SQLException {
        _connWrapped.setAutoCommit(autoCommit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAutoCommit()
            throws SQLException {
        return _connWrapped.getAutoCommit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit()
            throws SQLException {
        _connWrapped.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback()
            throws SQLException {
        _connWrapped.rollback();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
            throws SQLException {
        _connWrapped.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed()
            throws SQLException {
        boolean bClosed = true;
        if (_connWrapped != null)
            bClosed = _connWrapped.isClosed();
        return bClosed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseMetaData getMetaData()
            throws SQLException {
        return _connWrapped.getMetaData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(boolean readOnly)
            throws SQLException {
        _connWrapped.setReadOnly(readOnly);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly()
            throws SQLException {
        return _connWrapped.isReadOnly();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCatalog(String catalog)
            throws SQLException {
        _connWrapped.setCatalog(catalog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCatalog()
            throws SQLException {
        return _connWrapped.getCatalog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionIsolation(int level)
            throws SQLException {
        _connWrapped.setTransactionIsolation(level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTransactionIsolation()
            throws SQLException {
        return _connWrapped.getTransactionIsolation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return _connWrapped.getWarnings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearWarnings() throws SQLException {
        _connWrapped.clearWarnings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return _connWrapped.createStatement(resultSetType, resultSetConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql,
                                              int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return _connWrapped.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(String sql,
                                         int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return _connWrapped.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Class<?>> getTypeMap()
            throws SQLException {
        return _connWrapped.getTypeMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeMap(Map<String, Class<?>> map)
            throws SQLException {
        _connWrapped.setTypeMap(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHoldability(int holdability)
            throws SQLException {
        _connWrapped.setHoldability(holdability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHoldability()
            throws SQLException {
        return _connWrapped.getHoldability();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Savepoint setSavepoint()
            throws SQLException {
        return _connWrapped.setSavepoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Savepoint setSavepoint(String name)
            throws SQLException {
        return _connWrapped.setSavepoint(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback(Savepoint savepoint)
            throws SQLException {
        _connWrapped.rollback(savepoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseSavepoint(Savepoint savepoint)
            throws SQLException {
        _connWrapped.releaseSavepoint(savepoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return _connWrapped.createStatement(resultSetType, resultSetConcurrency,
                resultSetHoldability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql,
                                              int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability)
            throws SQLException {
        return _connWrapped.prepareStatement(sql, resultSetType,
                resultSetConcurrency, resultSetHoldability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType,
                                         int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return _connWrapped.prepareCall(sql, resultSetType,
                resultSetConcurrency, resultSetHoldability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        return _connWrapped.prepareStatement(sql, autoGeneratedKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
            throws SQLException {
        return _connWrapped.prepareStatement(sql, columnIndexes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames)
            throws SQLException {
        return _connWrapped.prepareStatement(sql, columnNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Clob createClob()
            throws SQLException {
        return _connWrapped.createClob();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Blob createBlob()
            throws SQLException {
        return _connWrapped.createBlob();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NClob createNClob()
            throws SQLException {
        return _connWrapped.createNClob();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLXML createSQLXML()
            throws SQLException {
        return _connWrapped.createSQLXML();
    }

    public Object createDatalinkObject() throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int timeout)
            throws SQLException {
        return _connWrapped.isValid(timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientInfo(String name, String value)
            throws SQLClientInfoException {
        _connWrapped.setClientInfo(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientInfo(Properties properties)
            throws SQLClientInfoException {
        _connWrapped.setClientInfo(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientInfo(String name)
            throws SQLException {
        return _connWrapped.getClientInfo(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getClientInfo()
            throws SQLException {
        return _connWrapped.getClientInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Array createArrayOf(String typeName, Object[] elements)
            throws SQLException {
        return _connWrapped.createArrayOf(typeName, elements);
    }

    /**
     * {@inheritDoc}
     * The names and types of the attributes are derived from the type map.
     */
    @Override
    public Struct createStruct(String typeName, Object[] attributes)
            throws SQLException {
        return _connWrapped.createStruct(typeName, attributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSchema(String schema)
            throws SQLException {
        try {
            _connWrapped.setSchema(schema);
        } catch (AbstractMethodError ame) {
            throwUndefinedMethod(ame);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchema()
            throws SQLException {
        String sSchema = null;
        try {
            sSchema = _connWrapped.getSchema();
        } catch (AbstractMethodError ame) {
            throwUndefinedMethod(ame);
        }
        return sSchema;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abort(Executor executor)
            throws SQLException {
        try {
            _connWrapped.abort(executor);
        } catch (AbstractMethodError ame) {
            throwUndefinedMethod(ame);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds)
            throws SQLException {
        try {
            _connWrapped.setNetworkTimeout(executor, milliseconds);
        } catch (AbstractMethodError ame) {
            throwUndefinedMethod(ame);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNetworkTimeout()
            throws SQLException {
        int iNetWorkTimeout = 0;
        try {
            iNetWorkTimeout = _connWrapped.getNetworkTimeout();
        } catch (AbstractMethodError ame) {
            throwUndefinedMethod(ame);
        }
        return iNetWorkTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface == Connection.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T wrapped = null;
        if (isWrapperFor(iface))
            wrapped = (T) _connWrapped;
        return wrapped;
    }
}
