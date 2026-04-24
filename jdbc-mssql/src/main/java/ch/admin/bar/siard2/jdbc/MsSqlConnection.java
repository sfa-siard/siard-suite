/*======================================================================
MsSqlConnection implements a wrapped MSSQL Connection.
Version     : $Id: $
Application : SIARD2
Description : MsSqlConnection implements a wrapped MSSQL Connection.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.mssql.MsSqlSqlFactory;
import ch.enterag.sqlparser.DdlStatement;
import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.ddl.DropTableStatement;
import ch.enterag.sqlparser.ddl.enums.DropBehavior;
import ch.enterag.sqlparser.expression.QuerySpecification;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.jdbc.BaseConnection;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;
import ch.enterag.utils.logging.IndentLogger;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.*;


/** MsSqlConnection implements a wrapped MSSQL Connection.
 * @author Hartwig Thomas
 */
public class MsSqlConnection
        extends BaseConnection
        implements Connection {
    /** logger */
    private static IndentLogger _il = IndentLogger.getIndentLogger(MsSqlConnection.class.getName());
    private QualifiedId _qiTableDropCascade = null;

    public QualifiedId getTableDropCascade() {
        return _qiTableDropCascade;
    }

    public void resetTableDropCascade() {
        _qiTableDropCascade = null;
    }


    /** convert an MSSQL SQLServerException into an SQLFeatureNotSupportedException.
     * @param sse
     * @throws SQLFeatureNotSupportedException
     */
    private void throwNotSupportedException(SQLServerException sse)
            throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("MSSQL Exception!", sse);
    }


    /** constructor
     * @param connWrapped connection to be wrapped.
     */
    public MsSqlConnection(Connection connWrapped)
            throws SQLException {
        super(connWrapped);
        if (connWrapped != null) {
            /* a closer approach to ISO SQL syntax */
            Statement stmt = super.createStatement();
            stmt.executeUpdate("SET ANSI_DEFAULTS ON");
            stmt.close();
        }
    }


    /** {@inheritDoc}
     * wraps statement.
     */
    @Override
    public Statement createStatement()
            throws SQLException {
        Statement stmt = new MsSqlStatement(super.createStatement(), this);
        return stmt;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql)
            throws SQLException {
        String sNative = nativeSQL(sql);
        PreparedStatement ps = super.prepareStatement(sNative);
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public CallableStatement prepareCall(String sql)
            throws SQLException {
        CallableStatement cs = super.prepareCall(nativeSQL(sql));
        return cs;
    }


    /** {@inheritDoc} */
    @Override
    public String nativeSQL(String sql)
            throws SQLException {
        _il.enter(sql);
        BaseDatabaseMetaData dmd = (BaseDatabaseMetaData) getMetaData();
        MsSqlSqlFactory sf = new MsSqlSqlFactory();
        SqlStatement ss = sf.newSqlStatement();
        ss.parse(sql);
        QuerySpecification qs = ss.getQuerySpecification();
        if (qs != null) {
            /* store default catalog and schema for further processing ... */
            String sDefaultCatalog = null;
            String sDefaultSchema = null;
            Statement stmt = unwrap(Connection.class).createStatement();
            ResultSet rs = stmt.executeQuery("select DB_NAME() AS DB_NAME, SCHEMA_NAME() AS SCHEMA_NAME");
            while (rs.next()) {
                sDefaultCatalog = rs.getString("DB_NAME");
                sDefaultSchema = rs.getString("SCHEMA_NAME");
            }
            rs.close();
            stmt.close();
            sf.startQuery(ss.getQuerySpecification(), sDefaultCatalog, sDefaultSchema, dmd);
        }
        /* check, whether a DROP TABLE with option CASCADE was parsed. */
        resetTableDropCascade();
        DdlStatement ds = ss.getDdlStatement();
        if (ds != null) {
            DropTableStatement dts = ds.getDropTableStatement();
            if (dts != null) {
                if (dts.getDropBehavior() == DropBehavior.CASCADE)
                    _qiTableDropCascade = dts.getTableName();
            }
        }
        sql = ss.format();
        _il.exit(sql);
        return sql;
    }


    /** {@inheritDoc}
     * wraps database meta data.
     */
    @Override
    public DatabaseMetaData getMetaData()
            throws SQLException {
        DatabaseMetaData dmd = new MsSqlDatabaseMetaData(super.getMetaData(), this);
        return dmd;
    }


    /** {@inheritDoc}
     * wraps statement.
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        Statement stmt = new MsSqlStatement(super.createStatement(resultSetType, resultSetConcurrency), this);
        return stmt;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql,
                                              int resultSetType, int resultSetConcurrency)
            throws SQLException {
        PreparedStatement ps = super.prepareStatement(nativeSQL(sql), resultSetType, resultSetConcurrency);
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public CallableStatement prepareCall(String sql,
                                         int resultSetType, int resultSetConcurrency)
            throws SQLException {
        CallableStatement cs = super.prepareCall(nativeSQL(sql), resultSetType, resultSetConcurrency);
        return cs;
    }


    /** {@inheritDoc} */
    @Override
    public void releaseSavepoint(Savepoint savepoint)
            throws SQLException {
        try {
            super.releaseSavepoint(savepoint);
        } catch (SQLServerException sse) {
            throwNotSupportedException(sse);
        }
    }


    /** {@inheritDoc}
     * wraps statement.
     */
    @Override
    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        Statement stmt = null;
        try {
            stmt = new MsSqlStatement(super.createStatement(resultSetType,
                                                            resultSetConcurrency, resultSetHoldability), this);
        } catch (SQLServerException sse) {
            throwNotSupportedException(sse);
        }
        return stmt;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql,
                                              int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability)
            throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = super.prepareStatement(nativeSQL(sql), resultSetType,
                                        resultSetConcurrency, resultSetHoldability);
        } catch (SQLServerException sse) {
            throwNotSupportedException(sse);
        }
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType,
                                         int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        CallableStatement cs = null;
        try {
            cs = super.prepareCall(nativeSQL(sql), resultSetType,
                                   resultSetConcurrency, resultSetHoldability);
        } catch (SQLServerException sse) {
            throwNotSupportedException(sse);
        }
        return cs;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        PreparedStatement ps = super.prepareStatement(nativeSQL(sql), autoGeneratedKeys);
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
            throws SQLException {
        PreparedStatement ps = super.prepareStatement(nativeSQL(sql), columnIndexes);
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames)
            throws SQLException {
        PreparedStatement ps = super.prepareStatement(nativeSQL(sql), columnNames);
        return ps;
    }

    @Override
    public Blob createDatalinkObject() throws SQLException {
        return createBlob();
    }

    /** {@inheritDoc}
     * MS SQL Server does not support the ARRAY type.
     */
    @Override
    public Array createArrayOf(String typeName, Object[] elements)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("MS SQL Server does not support the ARRAY type");
    }
}
