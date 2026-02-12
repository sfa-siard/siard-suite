/*======================================================================
AccessResultSetMetaData implements wrapped Jackcess ResultSetMetaData
for MS Access.
Application : Access JDBC driver
Description : AccessResultSetMetaData implements wrapped Jackcess 
              ResultSetMetaData for MS Access.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 04.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.access.ResultSetHeader;
import ch.enterag.utils.database.SqlTypes;

import java.math.BigDecimal;
import java.sql.*;


/** AccessResultSetMetaData implements wrapped Jackcess ResultSetMetaData
 * for MS Access.
 * @author Hartwig Thomas
 */
public class AccessResultSetMetaData
        implements ResultSetMetaData {
    /** target ResultSet */
    private ResultSetHeader _rsh = null;
    /** connection */
    private Connection _conn = null;

    /** constructor
     * @param rs target ResultSet for which this object has the metadata.
     */
    AccessResultSetMetaData(ResultSetHeader rsh, Connection conn) {
        _rsh = rsh;
        _conn = conn;
    }
  
  /*======================================================================
  Wrapper 
  ======================================================================*/

    /** {@link ResultSetMetaData} */
    @Override
    public boolean isWrapperFor(Class<?> clsInterface) throws SQLException {
        return clsInterface.equals(ResultSetMetaData.class);
    }


    /** {@link ResultSetMetaData} */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> clsInterface) throws SQLException {
        T impl = null;
        if (isWrapperFor(clsInterface))
            impl = (T) this;
        else
            throw new IllegalArgumentException("AccessResultSetMetaData cannot be unwrapped to " + clsInterface.getName() + "!");
        return impl;
    }

  /*======================================================================
  Catalog, Schema, ... 
  ======================================================================*/

    /** {@link ResultSetMetaData} */
    @Override
    public String getCatalogName(int iColumnIndex) throws SQLException {
        /* we only work with ResultSets, that come from a single table ... */
        return _conn.getCatalog();
    }


    /** {@link ResultSetMetaData} */
    @Override
    public String getSchemaName(int column) throws SQLException {
        return _rsh.getSchemaName();
    }


    /** {@link ResultSetMetaData} */
    @Override
    public String getTableName(int column) throws SQLException {
        return _rsh.getTableName();
    }

  /*======================================================================
  ResultSet properties
  ======================================================================*/

    /** {@link ResultSetMetaData} */
    @Override
    public int getColumnCount() throws SQLException {
        return _rsh.getColumns();
    }

  /*======================================================================
  Column properties 
  ======================================================================*/

    /** {@link ResultSetMetaData} */
    @Override
    public String getColumnClassName(int iColumnIndex) throws SQLException {
        Class<?> cls = null;
        int iDataType = getColumnType(iColumnIndex);
        switch (iDataType) {
            case Types.CHAR:
            case Types.NCHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
                cls = String.class;
                break;
            case Types.CLOB:
            case Types.NCLOB:
            case Types.SQLXML:
                cls = Clob.class;
                break;
            case Types.BINARY:
            case Types.VARBINARY:
                cls = byte[].class;
                break;
            case Types.BLOB:
                cls = Blob.class;
                break;
            case Types.TINYINT:
            case Types.SMALLINT:
                cls = Short.class;
                break;
            case Types.INTEGER:
                cls = Integer.class;
                break;
            case Types.BIGINT:
                cls = Long.class;
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                cls = BigDecimal.class;
                break;
            case Types.REAL:
                cls = Float.class;
                break;
            case Types.FLOAT:
            case Types.DOUBLE:
                cls = Double.class;
                break;
            case Types.BOOLEAN:
                cls = Boolean.class;
                break;
            case Types.DATE:
                cls = Date.class;
                break;
            case Types.TIME:
                cls = Time.class;
                break;
            case Types.TIMESTAMP:
                cls = Timestamp.class;
                break;
            case Types.ARRAY:
                cls = Array.class;
                break;
            default:
                new IllegalArgumentException("Unexpected type " + SqlTypes.getTypeName(iDataType) + "!");
        }
        return cls.getName();
    }


    /** {@link ResultSetMetaData} */
    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return 8; // somewhat absurd ...
    }


    /** {@link ResultSetMetaData} */
    @Override
    public String getColumnLabel(int iColumnIndex) throws SQLException {
        return _rsh.getName(iColumnIndex - 1); // iColumnIndex is 1-based
    }


    /** {@link ResultSetMetaData} */
    @Override
    public String getColumnName(int iColumnIndex) throws SQLException {
        return _rsh.getName(iColumnIndex - 1); // iColumnIndex is 1-based
    }


    /** {@link ResultSetMetaData} */
    @Override
    public int getColumnType(int iColumnIndex) throws SQLException {
        return _rsh.getType(iColumnIndex - 1); // iColumnIndex is 1-based
    }


    /** {@link ResultSetMetaData} */
    @Override
    public String getColumnTypeName(int iColumnIndex) throws SQLException {
        return SqlTypes.getTypeName(getColumnType(iColumnIndex));
    }


    /** {@link ResultSetMetaData} */
    @Override
    public int getPrecision(int iColumnIndex) throws SQLException {
        return _rsh.getPrecision(iColumnIndex - 1); // iColumnIndex is 1-based
    }


    /** {@link ResultSetMetaData} */
    @Override
    public int getScale(int iColumnIndex) throws SQLException {
        return _rsh.getScale(iColumnIndex - 1); // iColumnIndex is 1-based
    }


    /** {@link ResultSetMetaData} */
    @Override
    public boolean isAutoIncrement(int iColumnIndex) throws SQLException {
        return false;
    }


    /** {@link ResultSetMetaData}
     * Definition not quite clear ... */
    @Override
    public boolean isCaseSensitive(int iColumnIndex) throws SQLException {
        return true;
    }


    /** {@link ResultSetMetaData}
     * We do not support currency types ... */
    @Override
    public boolean isCurrency(int iColumnIndex) throws SQLException {
        return false;
    }


    /** {@link ResultSetMetaData}
     * We do not really support writing in ResultSets ... */
    @Override
    public boolean isDefinitelyWritable(int iColumnIndex) throws SQLException {
        return false;
    }


    /** {@link ResultSetMetaData} */
    @Override
    public int isNullable(int iColumnIndex) throws SQLException {
        return ResultSetMetaData.columnNullableUnknown;
    }


    /** {@link ResultSetMetaData}
     * We do not really support writing in ResultSets ... */
    @Override
    public boolean isReadOnly(int iColumnIndex) throws SQLException {
        return true;
    }


    /** {@link ResultSetMetaData} */
    @Override
    public boolean isSearchable(int iColumnIndex) throws SQLException {
        /* more precise: should be false for large types ... */
        return true;
    }


    /** {@link ResultSetMetaData}
     * Where ever it makes sense to ask this, it is true. */
    @Override
    public boolean isSigned(int iColumnIndex) throws SQLException {
        return true;
    }


    /** {@link ResultSetMetaData}
     * We do not really support writing in ResultSets ... */
    @Override
    public boolean isWritable(int iColumnIndex) throws SQLException {
        return false;
    }

}
