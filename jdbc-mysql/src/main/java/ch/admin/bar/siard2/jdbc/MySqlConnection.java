/*======================================================================
MySqlConnection implements a wrapped MySql Connection.
Version     : $Id: $
Application : SIARD2
Description : MySqlConnection implements a wrapped MySql Connection.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 26.10.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.mysql.MySqlSqlFactory;
import ch.enterag.sqlparser.DdlStatement;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.ddl.DropTableStatement;
import ch.enterag.sqlparser.ddl.enums.DropBehavior;
import ch.enterag.sqlparser.expression.QuerySpecification;
import ch.enterag.sqlparser.expression.TablePrimary;
import ch.enterag.sqlparser.expression.TableReference;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.jdbc.BaseConnection;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;
import ch.enterag.utils.logging.IndentLogger;

import java.sql.*;
import java.util.concurrent.Executor;


/**
 * MySqlConnection implements a wrapped MySql Connection
 * @author Simon Jutz
 */
public class MySqlConnection extends BaseConnection implements Connection {
    // logger
    private static IndentLogger _il = IndentLogger.getIndentLogger(MySqlConnection.class.getName());
    private QualifiedId _qiTableDropCascade = null;

    public QualifiedId getTableDropCascade() {
        return _qiTableDropCascade;
    }

    public void resetTableDropCascade() {
        _qiTableDropCascade = null;
    }

    private QualifiedId _qiTableWithoutPrimaryKey = null;

    public QualifiedId getTableWithoutPrimaryKey() {
        return _qiTableWithoutPrimaryKey;
    }

    public void resetTableWithoutPrimaryKey() {
        _qiTableWithoutPrimaryKey = null;
    }

    public static final String _sSET_FOREIGN_KEYS = "SET FOREIGN_KEY_CHECKS = {0};";


    /**
     * {@inheritDoc}
     *
     * @throws SQLException
     */
    public MySqlConnection(Connection connNative) throws SQLException {
        super(connNative);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String nativeSQL(String sql) {
        _il.enter(sql);
        SqlFactory sf = new MySqlSqlFactory();
        SqlStatement ss = sf.newSqlStatement();
        ss.parse(sql);
        resetTableDropCascade();
        DdlStatement ds = ss.getDdlStatement();
        if (ds != null) {
            DropTableStatement dts = ds.getDropTableStatement();
            if (dts != null) {
                if (dts.getDropBehavior() == DropBehavior.CASCADE)
                    _qiTableDropCascade = dts.getTableName();
            }
        }
        resetTableWithoutPrimaryKey();
        QuerySpecification qs = ss.getQuerySpecification();
        if (qs != null) {
            if (qs.getTableReferences()
                  .size() == 1) {
                TableReference tr = qs.getTableReferences()
                                      .get(0);
                if (tr != null) {
                    TablePrimary tp = tr.getTablePrimary();
                    if ((!tp.isOnly()) &&
                            (!tp.isTable()) &&
                            (!tp.isUnnest()) &&
                            (tp.getQueryExpression() == null) &&
                            (tp.getTableReference() == null)) {
                        QualifiedId qiTable = tp.getTableName();
                        try {
                            BaseDatabaseMetaData bdmd = (BaseDatabaseMetaData) getMetaData();
                            ResultSet rs = getMetaData().getPrimaryKeys(
                                    qiTable.getCatalog(),
                                    bdmd.toPattern(qiTable.getSchema()),
                                    bdmd.toPattern(qiTable.getName()));
                            if (!rs.next())
                                _qiTableWithoutPrimaryKey = qiTable;
                            rs.close();
                        } catch (SQLException se) {
                            _il.exception(se);
                        }
                    }
                }
            }
        }
        sql = ss.format();
        _il.exit(sql);
        return sql;
    }


    /**
     * {@inheritDoc} MySql does not allow the executor argument to be null
     */
    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        if (executor == null) {
            throw new SQLFeatureNotSupportedException("MySql does not allow the executor argument to be null");
        }
        super.setNetworkTimeout(executor, milliseconds);
    }


    /**
     * {@inheritDoc} Wraps database meta data
     * @throws SQLException
     */
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new MySqlDatabaseMetaData(super.getMetaData(), this);
    }


    /**
     * {@inheritDoc} wraps statement.
     */
    @Override
    public Statement createStatement() throws SQLException {
        Statement stmt = super.createStatement();
        return new MySqlStatement(stmt, this);
    }


    /**
     * {@inheritDoc} wraps statement.
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        Statement stmt = super.createStatement(resultSetType, resultSetConcurrency);
        return new MySqlStatement(stmt, this);
    }


    /** {@inheritDoc} */
    @Override
    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        Statement stmt = super.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        return new MySqlStatement(stmt, this);
    }


    /**
     * {@inheritDoc} wraps creation of an SQLXML object
     */
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return MySqlSqlXml.getInstance();
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        String sNative = nativeSQL(sql);
        PreparedStatement ps = super.prepareStatement(sNative);
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        String sNative = nativeSQL(sql);
        PreparedStatement ps = super.prepareStatement(sNative, resultSetType, resultSetConcurrency);
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        String sNative = nativeSQL(sql);
        PreparedStatement ps = super.prepareStatement(sNative, resultSetType, resultSetConcurrency, resultSetHoldability);
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        String sNative = nativeSQL(sql);
        PreparedStatement ps = super.prepareStatement(sNative, autoGeneratedKeys);
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        String sNative = nativeSQL(sql);
        PreparedStatement ps = super.prepareStatement(sNative, columnIndexes);
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        String sNative = nativeSQL(sql);
        PreparedStatement ps = super.prepareStatement(sNative, columnNames);
        return ps;
    }


    /** {@inheritDoc} */
    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        String sNative = nativeSQL(sql);
        CallableStatement cs = super.prepareCall(sNative);
        return cs;
    }


    /** {@inheritDoc} */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        String sNative = nativeSQL(sql);
        CallableStatement cs = super.prepareCall(sNative, resultSetType, resultSetConcurrency);
        return cs;
    }


    /** {@inheritDoc} */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        String sNative = nativeSQL(sql);
        CallableStatement cs = super.prepareCall(sNative, resultSetType, resultSetConcurrency, resultSetHoldability);
        return cs;
    }

    @Override
    public Object createDatalinkObject() throws SQLException {
        return createBlob();
    }

}
