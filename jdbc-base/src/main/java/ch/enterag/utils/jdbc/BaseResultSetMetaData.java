/*======================================================================
BaseResultSetMetaData implements wrapped ResultSetMetaData.
Version     : $Id: $
Application : SIARD2
Description : BaseResultSetMetaData implements wrapped ResultSetMetaData.
              See https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSetMetaData.html
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 11.05.2016, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jdbc;

import java.sql.*;

/**
 * BaseResultSetMetaData implements wrapped ResultSetMetaData and serves
 * as a base for derived JDBC wrappers.
 *
 * @author Hartwig Thomas
 */
public abstract class BaseResultSetMetaData
        implements ResultSetMetaData {
    /**
     * wrapped result set meta data
     */
    private ResultSetMetaData _rsmdWrapped = null;

    /**
     * constructor
     *
     * @param dmdWrapped database meta data to be wrapped.
     */
    public BaseResultSetMetaData(ResultSetMetaData rsmdWrapped) {
        _rsmdWrapped = rsmdWrapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() throws SQLException {
        return _rsmdWrapped.getColumnCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCatalogName(int column) throws SQLException {
        return _rsmdWrapped.getCatalogName(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchemaName(int column) throws SQLException {
        return _rsmdWrapped.getSchemaName(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName(int column) throws SQLException {
        return _rsmdWrapped.getTableName(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(int column) throws SQLException {
        return _rsmdWrapped.getColumnName(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnLabel(int column) throws SQLException {
        return _rsmdWrapped.getColumnLabel(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnClassName(int column) throws SQLException {
        return _rsmdWrapped.getColumnClassName(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnType(int column) throws SQLException {
        return _rsmdWrapped.getColumnType(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return _rsmdWrapped.getColumnTypeName(column);
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnDisplaySize(int column) throws SQLException {
        return _rsmdWrapped.getColumnDisplaySize(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPrecision(int column) throws SQLException {
        return _rsmdWrapped.getPrecision(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getScale(int column) throws SQLException {
        return _rsmdWrapped.getScale(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return _rsmdWrapped.isAutoIncrement(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return _rsmdWrapped.isCaseSensitive(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSearchable(int column) throws SQLException {
        return _rsmdWrapped.isSearchable(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrency(int column) throws SQLException {
        return _rsmdWrapped.isCurrency(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int isNullable(int column) throws SQLException {
        return _rsmdWrapped.isNullable(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSigned(int column) throws SQLException {
        return _rsmdWrapped.isSigned(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return _rsmdWrapped.isReadOnly(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWritable(int column) throws SQLException {
        return _rsmdWrapped.isWritable(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return _rsmdWrapped.isDefinitelyWritable(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface == ResultSetMetaData.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T wrapped = null;
        if (isWrapperFor(iface))
            wrapped = (T) _rsmdWrapped;
        return wrapped;
    }
}
