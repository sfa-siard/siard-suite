/*======================================================================
AccessStatement implements wrapped Jackcess Statement for MS Access.
Application : Access JDBC driver
Description : AccessStatement implements wrapped Jackcess Statement for MS Access.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 07.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.access.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.datatype.enums.PreType;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.dml.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.identifier.IdChain;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.jdbc.BaseStatement;
import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.impl.DatabaseImpl;
import com.healthmarketscience.jackcess.query.Query;
import com.healthmarketscience.jackcess.query.SelectQuery;

import java.io.IOException;
import java.sql.*;
import java.util.*;


/** AccessStatement implements wrapped Jackcess Statement for MS Access.
 * @author Hartwig Thomas
 */
public class AccessStatement
        extends BaseStatement
        implements Statement {
    /** list of batched statements */
    private final List<String> _listBatch = new ArrayList<String>();
    /** SqlFactory */
    protected AccessSqlFactory _sf = new AccessSqlFactory();
    /** Connection object */
    protected AccessConnection _conn = null;
    /** update count from last call to execute() */
    protected int _iUpdateCount = Integer.MIN_VALUE;
    /** ResultSet from last call to execute() */
    protected ResultSet _rs = null;
    /** maximum field size */
    private int _iMaxFieldSize = 0;
    /** maximum number of rows */
    private int _iMaxRows = 0;
    /** escape processing */
    @SuppressWarnings("unused")
    private boolean _bEscapeProcessing = false;
    /** query timeout in seconds */
    private int _iQueryTimeoutSeconds = 0;
    /** cursor name */
    @SuppressWarnings("unused")
    private String _sCursorName = null;
    /** fetch direction */
    private int _iFetchDirection = ResultSet.FETCH_FORWARD;
    /** fetch size */
    private int _iFetchSize = 1;
    /** poolability */
    private boolean _bPoolable = false;
  
  /*====================================================================
  Wrapper 
  ====================================================================*/

    /** constructor starts with an H2 session to be used for SQL parsing.
     * The H2 database contains the full empty schema of the MS Access
     * database and is used for parsing.
     * @param conn Connection to (Jackcess-) MS Access database.
     * @throws SQLException if a database error occurred.
     */
    public AccessStatement(AccessConnection conn)
            throws SQLException {
        super(null);
        _conn = conn;
    }


    /*====================================================================
    Queries
    ====================================================================*/
    private static void addColumn(ResultSetHeader rsh, String sColumnName, DataType dt)
            throws SQLException {
        if (dt.getLength() == DataType.iUNDEFINED) {
            PredefinedType pt = dt.getPredefinedType();
            dt.initPredefinedDataType(pt);
            if (pt != null) {
                switch (pt.getType()) {
                    case CHAR:
                    case VARCHAR:
                    case CLOB:
                    case NCHAR:
                    case NVARCHAR:
                    case NCLOB:
                    case BINARY:
                    case VARBINARY:
                    case BLOB:
                    case DATALINK:
                        rsh.addColumn(sColumnName, pt.getType()
                                                     .getSqlType(), pt.getLength());
                        break;
                    case FLOAT:
                    case TIME:
                    case TIMESTAMP:
                        rsh.addColumn(sColumnName, pt.getType()
                                                     .getSqlType(), pt.getPrecision());
                        break;
                    case NUMERIC:
                    case DECIMAL:
                        rsh.addColumn(sColumnName, pt.getType()
                                                     .getSqlType(), pt.getScale(), pt.getPrecision());
                        break;
                    case SMALLINT:
                    case INTEGER:
                    case BIGINT:
                    case REAL:
                    case DOUBLE:
                    case BOOLEAN:
                    case DATE:
                    case XML:
                        rsh.addColumn(sColumnName, pt.getType()
                                                     .getSqlType());
                        break;
                    case INTERVAL:
                        throw new SQLException("Interval type not supported by JDBC!");
                }
            }
        } else // <base> ARRAY[length]
            rsh.addColumn(sColumnName, Types.ARRAY, PredefinedType.iUNDEFINED, PredefinedType.iUNDEFINED);
    }

  /*====================================================================
  Warnings 
  ====================================================================*/

    /** {@link Connection} */
    @Override
    public boolean isWrapperFor(Class<?> clsInterface) throws SQLException {
        return clsInterface.equals(Statement.class);
    }


    /** {@link Connection} */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> clsInterface) throws SQLException {
        T impl = null;
        if (isWrapperFor(clsInterface))
            impl = (T) this;
        else
            throw new IllegalArgumentException("AccessStatement cannot be unwrapped to " + clsInterface.getName() + "!");
        return impl;
    }
  
  /*====================================================================
  Statement 
  ====================================================================*/

    /** {@link Connection} */
    @Override
    public void clearWarnings() throws SQLException {
        _conn.clearWarnings();
    }


    /** {@link Connection} */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return _conn.getWarnings();
    }


    /** {@link Statement} */
    @Override
    public void close() throws SQLException {
        _conn = null;
    }


    /** {@link Statement} */
    @Override
    public void cancel() throws SQLException {
        throw new SQLFeatureNotSupportedException("JdbcAccess cannot cancel a Statement!");
    }


    /** {@link Statement} */
    @Override
    public Connection getConnection() throws SQLException {
        return _conn;
    }


    /** {@link Statement} */
    @Override
    public boolean isClosed() throws SQLException {
        return (_conn == null);
    }


    /** {@link Statement} for JDK 1.7 */
    @Override
    public void closeOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException("close on completion is not supported!");
    }


    /** {@link Statement} for JDK 1.7 */
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }


    /** {@link Statement} */
    @Override
    public int getMaxFieldSize() throws SQLException {
        return _iMaxFieldSize;
    }


    /** {@link Statement} */
    @Override
    public void setMaxFieldSize(int iMaxFieldSize) throws SQLException {
        _iMaxFieldSize = iMaxFieldSize;
    }


    /** {@link Statement} */
    @Override
    public int getMaxRows() throws SQLException {
        return _iMaxRows;
    }


    /** {@link Statement} */
    @Override
    public void setMaxRows(int iMaxRows) throws SQLException {
        _iMaxRows = iMaxRows;
    }


    /** {@link Statement} */
    @Override
    public void setEscapeProcessing(boolean bEscapeProcessing) throws SQLException {
        _bEscapeProcessing = bEscapeProcessing;
    }


    /** {@link Statement} */
    @Override
    public int getQueryTimeout() throws SQLException {
        return _iQueryTimeoutSeconds;
    }


    /** {@link Statement} */
    @Override
    public void setQueryTimeout(int iQueryTimeoutSeconds) throws SQLException {
        _iQueryTimeoutSeconds = iQueryTimeoutSeconds;
    }


    /** {@link Statement} */
    @Override
    public void setCursorName(String sCursorName) throws SQLException {
        _sCursorName = sCursorName;
    }


    /** {@link Statement} */
    @Override
    public int getFetchDirection() throws SQLException {
        return _iFetchDirection;
    }


    /** {@link Statement} */
    @Override
    public void setFetchDirection(int iFetchDirection) throws SQLException {
        _iFetchDirection = iFetchDirection;
    }


    /** {@link Statement} */
    @Override
    public int getFetchSize() throws SQLException {
        return _iFetchSize;
    }


    /** {@link Statement} */
    @Override
    public void setFetchSize(int iFetchSize) throws SQLException {
        _iFetchSize = iFetchSize;
    }


    /** {@link Statement} */
    @Override
    public int getResultSetConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }


    /** {@link Statement} */
    @Override
    public int getResultSetType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }


    /** {@link Statement} */
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLFeatureNotSupportedException("JdbcAccess does not support generated keys!");
    }


    /** {@link Statement} */
    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }


    /** {@link Statement} */
    @Override
    public boolean isPoolable() throws SQLException {
        return _bPoolable;
    }
  
  /*====================================================================
  Exceptions 
  ====================================================================*/

    /** {@link Statement} */
    @Override
    public void setPoolable(boolean bPoolable) throws SQLException {
        _bPoolable = bPoolable;
    }


    /** Convert IOException to SQLException.
     * @param ie IOException
     * @return SQLException with SQLSTATE.
     */
    protected SQLException getSQLException(IOException ie) {
        SQLException se = new SQLException(ie.getMessage(), "90067");
        return se;
    }

    /** Convert IllegalArgumentException (from Jackcess) to SQLException.
     * @param iae IllegalArgumentException
     * @return SQLException with SQLSTATE.
     */
    protected SQLException getSQLException(IllegalArgumentException iae) {
        String sMessage = iae.getMessage();
        String sSqlState = "HY000";
        if (sMessage.equals("Cannot create table with name of existing table"))
            sSqlState = "42101";
        else if (sMessage.startsWith("Column with name ") && sMessage.endsWith(" does not exist in this table"))
            sSqlState = "42122";
        else if (sMessage.startsWith("Index with name ") && sMessage.endsWith(" does not exist on this table"))
            sSqlState = "42112";
        else if (sMessage.startsWith("Table ") && sMessage.endsWith(" does not have a primary key index"))
            sSqlState = "90057";
        else if (sMessage.startsWith("Table ") && (sMessage.indexOf(" does not have a foreign key reference to ") >= 0))
            sSqlState = "90057";
        SQLException se = new SQLException(iae.getMessage(), sSqlState);
        return se;
    }


    /** Create a header for the SELECT result set.
     * @param qiTable table name.
     * @param ss sql statement.
     * @return ResultSetHeader.
     * @throws SQLException if an error occurs.
     */
    protected ResultSetHeader getSelectHeader(
            QualifiedId qiTable,
            SqlStatement sqlstmt)
            throws SQLException {
        QuerySpecification qs = sqlstmt.getQuerySpecification();
        ResultSetHeader rsh = new ResultSetHeader(qiTable.getSchema(), qiTable.getName());
        for (int iSelect = 0; iSelect < qs.getSelectSublists()
                                          .size(); iSelect++) {
            SelectSublist ss = qs.getSelectSublists()
                                 .get(iSelect);
            String sColumnName = Shunting.getColumnName(ss);
            DataType dt = ss.getDataType(sqlstmt);
            addColumn(rsh, sColumnName, dt);
        }
        return rsh;
    }


    /** add a select sublist with the column name to the query
     * @param qs query specification.
     * @param sColumnName column name.
     */
    private void addColumnName(QuerySpecification qs, String sColumnName) {
        IdChain idc = new IdChain();
        idc.get()
           .add(sColumnName);
        GeneralValueSpecification gvs = _sf.newGeneralValueSpecification();
        gvs.initialize(idc);
        ValueExpressionPrimary vep = _sf.newValueExpressionPrimary();
        vep.setGeneralValueSpecification(gvs);
        CommonValueExpression cve = _sf.newCommonValueExpression();
        cve.setValueExpressionPrimary(vep);
        ValueExpression ve = _sf.newValueExpression();
        ve.setCommonValueExpression(cve);
        SelectSublist ss = _sf.newSelectSublist();
        ss.setValueExpression(ve);
        qs.addSelectSublist(ss);
    }


    /** retrieve the SQL data type of a Jackcess column.
     * @param column Jackcess column.
     * @return SQL data type.
     */
    private DataType getColumnType(Column column)
            throws IOException, SQLException {
        int iScale = column.getScale();
        int iPrecision = column.getPrecision();
        int iLengthInUnits = column.getLengthInUnits();
        int iLength = column.getLength();
        DataType dt = Shunting.convertTypeFromAccess(column,
                                                     iPrecision, iScale, iLength, iLengthInUnits, _conn.getMetaData());
        return dt;
    }


    /** result set header for base table/query.
     * @param qiBase qualified table/query name.
     * @param listColumnNames column names.
     * @param listDataTypes data types.
     * @return result set header.
     */
    private ResultSetHeader getBaseHeader(QualifiedId qiBase,
                                          List<String> listColumnNames, List<DataType> listDataTypes)
            throws SQLException, IOException {
        ResultSetHeader rsh = new ResultSetHeader(qiBase.getSchema(), qiBase.getName());
        for (int iColumn = 0; iColumn < listColumnNames.size(); iColumn++) {
            String sColumnName = listColumnNames.get(iColumn);
            DataType dt = listDataTypes.get(iColumn);
            addColumn(rsh, sColumnName, dt);
        }
        return rsh;
    }


    /** Execute a SELECT query on a table
     * @param table MS Access table for SELECT query.
     * @param tfTop table filter for SELECT query.
     * @param listSelectSublists list of column expressions of SELECT query.
     * @param bveWhere WHERE condition of select query.
     * @return ResultSet.
     * @throws SQLException if an error occurs.
     */
    private AccessResultSet executeTableSelect(
            Table table,
            SqlStatement ss)
            throws SQLException, IOException {
        AccessResultSet rs = null;
        QuerySpecification qs = ss.getQuerySpecification();
        /* initialize column names and types from table */
        List<String> listColumnNames = new ArrayList<String>();
        List<DataType> listColumnTypes = new ArrayList<DataType>();
        for (int iColumn = 0; iColumn < table.getColumnCount(); iColumn++) {
            Column column = table.getColumns()
                                 .get(iColumn);
            DataType dt = getColumnType(column);
            if (dt != null) {
                listColumnNames.add(column.getName());
                listColumnTypes.add(dt);
            }
        }
        TablePrimary tp = qs.getTableReferences()
                            .get(0)
                            .getTablePrimary();
        tp.setColumnNames(listColumnNames);
        for (int iColumn = 0; iColumn < listColumnNames.size(); iColumn++)
            tp.setColumnType(listColumnNames.get(iColumn), listColumnTypes.get(iColumn));
        /* replace asterisk by full column name list */
        if (qs.isAsterisk()) {
            qs.setAsterisk(false);
            for (int iColumn = 0; iColumn < table.getColumnCount(); iColumn++) {
                Column column = table.getColumns()
                                     .get(iColumn);
                DataType dt = getColumnType(column);
                if (dt != null)
                    addColumnName(qs, column.getName());
            }
        }
        ResultSetHeader rshSelect = getSelectHeader(
                new QualifiedId(null, _conn.getUserName(), table.getName()), ss);
        if (!qs.isGrouped()) {
            TableCursor tc = new TableCursor(table, ss);
            rs = new AccessResultSet(_conn, this, rshSelect, tc);
        } else if (qs.isCount()) {
            CountCursor cc = new CountCursor(table.getRowCount(), rshSelect.getName(0));
            rs = new AccessResultSet(_conn, this, rshSelect, cc);
        } else {
            SqlStatement ssBase = _sf.newSqlStatement();
            ssBase.parse(ss.format());
            ssBase.setEvaluationContext(_conn.getUserName(), _conn.getCatalog(), _conn.getSchema());
            QuerySpecification qsBase = ssBase.getQuerySpecification();
            qsBase.getSelectSublists()
                  .clear();
            qsBase.setAsterisk(false);
            for (int iColumn = 0; iColumn < listColumnNames.size(); iColumn++)
                addColumnName(qsBase, listColumnNames.get(iColumn));
            TablePrimary tpBase = qsBase.getTableReferences()
                                        .get(0)
                                        .getTablePrimary();
            tpBase.setColumnNames(listColumnNames);
            for (int iColumn = 0; iColumn < table.getColumnCount(); iColumn++)
                tpBase.setColumnType(listColumnNames.get(iColumn), listColumnTypes.get(iColumn));
            ResultSetHeader rshBase = getBaseHeader(new QualifiedId(null, _conn.getUserName(), table.getName()),
                                                    listColumnNames, listColumnTypes);
            TableCursor tcBase = new TableCursor(table, ssBase);
            AccessResultSet rsBase = new AccessResultSet(_conn, this, rshBase, tcBase);
            GroupedCursor gc = new GroupedCursor(rsBase, ss);
            rs = new AccessResultSet(_conn, this, rshSelect, gc);
        }
        return rs;
    }


    /** retrieve the type of a column of a result set.
     * @param rsm result set meta data.
     * @param iPosition position of column (1-based).
     * @return data type of column.
     */
    private DataType getColumnType(ResultSetMetaData rsm, int iPosition)
            throws SQLException {
        int iSqlType = rsm.getColumnType(iPosition);
        int iPrecision = rsm.getPrecision(iPosition);
        int iScale = rsm.getScale(iPosition);
        DataType dt = Shunting.convertTypeFromJdbc(iSqlType, iPrecision, iScale);
        return dt;
    }


    /** Execute a SELECT query on a query
     * @param sq Jackcess SelectQuery instance
     * @param qs query specification.
     * @return ResultSet.
     * @throws SQLException if an error occurs.
     */
    private AccessResultSet executeQuerySelect(
            SelectQuery sq,
            SqlStatement ss)
            throws SQLException, IOException {
        AccessResultSet rs = null;
        try {
            QuerySpecification qs = ss.getQuerySpecification();
            String sSql = sq.toSQLString()
                            .trim();
            if (sSql.endsWith(";"))
                sSql = sSql.substring(0, sSql.length() - 1);
            AccessResultSet rsQuery = (AccessResultSet) executeQuery(sSql);
            ResultSetMetaData rsmQuery = rsQuery.getMetaData();
            /* initialize column names and types from result set */
            List<String> listColumnNames = new ArrayList<String>();
            List<DataType> listColumnTypes = new ArrayList<DataType>();
            for (int iColumn = 0; iColumn < rsmQuery.getColumnCount(); iColumn++) {
                listColumnNames.add(rsmQuery.getColumnLabel(iColumn + 1));
                listColumnTypes.add(getColumnType(rsmQuery, iColumn + 1));
            }
            TablePrimary tp = qs.getTableReferences()
                                .get(0)
                                .getTablePrimary();
            tp.setColumnNames(listColumnNames);
            for (int iColumn = 0; iColumn < rsmQuery.getColumnCount(); iColumn++)
                tp.setColumnType(listColumnNames.get(iColumn), listColumnTypes.get(iColumn));
            /* replace asterisk by full column name list */
            if (qs.isAsterisk()) {
                qs.setAsterisk(false);
                for (int iColumn = 0; iColumn < rsmQuery.getColumnCount(); iColumn++)
                    addColumnName(qs, rsmQuery.getColumnLabel(iColumn + 1));
            }
            ResultSetHeader rshSelect = getSelectHeader(
                    new QualifiedId(null, _conn.getUserName(), sq.getName()), ss);
            if (!qs.isGrouped()) {
                SelectCursor sc = new SelectCursor(rsQuery, ss);
                rs = new AccessResultSet(_conn, this, rshSelect, sc);
            } else {
                SqlStatement ssBase = _sf.newSqlStatement();
                ssBase.parse(ss.format());
                ssBase.setEvaluationContext(_conn.getUserName(), _conn.getCatalog(), _conn.getSchema());
                QuerySpecification qsBase = ssBase.getQuerySpecification();
                qsBase.getSelectSublists()
                      .clear();
                qsBase.setAsterisk(false);
                for (int iColumn = 0; iColumn < listColumnNames.size(); iColumn++)
                    addColumnName(qsBase, listColumnNames.get(iColumn));
                TablePrimary tpBase = qsBase.getTableReferences()
                                            .get(0)
                                            .getTablePrimary();
                tpBase.setColumnNames(listColumnNames);
                for (int iColumn = 0; iColumn < rsmQuery.getColumnCount(); iColumn++)
                    tpBase.setColumnType(listColumnNames.get(iColumn), listColumnTypes.get(iColumn));
                ResultSetHeader rshBase = getBaseHeader(new QualifiedId(null, _conn.getUserName(), sq.getName()),
                                                        listColumnNames, listColumnTypes);
                SelectCursor scBase = new SelectCursor(rsQuery, ssBase);
                AccessResultSet rsBase = new AccessResultSet(_conn, this, rshBase, scBase);
                GroupedCursor gc = new GroupedCursor(rsBase, ss);
                rs = new AccessResultSet(_conn, this, rshSelect, gc);
            }
        } catch (IllegalArgumentException iae) {
            throw getSQLException(iae);
        }
        return rs;
    }


    /** execute the given Select query.
     * Currently limited to single table SELECT commands with simple
     * expressions.
     * @param ss parsed Select query.
     * @return ResultSet.
     * @throws SQLException if an error occurs.
     * @throws IOException if an I/O error occurred.
     */
    protected AccessResultSet executeSelect(SqlStatement ss)
            throws SQLException {
        AccessResultSet rs = null;
        QuerySpecification qs = ss.getQuerySpecification();
        try {
            if (qs.getTableReferences()
                  .size() == 1) {
                TableReference tr = qs.getTableReferences()
                                      .get(0);
                if (tr.getTablePrimary() != null) {
                    TablePrimary tp = tr.getTablePrimary();
                    QualifiedId qiTable = tp.getTableName();
                    Table table = _conn.getDatabase()
                                       .getTable(qiTable.getName());
                    if (table != null)
                        rs = executeTableSelect(table, ss);
                    else {
                        List<Query> listQueries = _conn.getDatabase()
                                                       .getQueries();
                        for (Iterator<Query> iterQuery = listQueries.iterator(); (rs == null) && iterQuery.hasNext(); ) {
                            Query query = iterQuery.next();
                            if (query.getName()
                                     .equals(qiTable.getName()) && (query instanceof SelectQuery))
                                rs = executeQuerySelect((SelectQuery) query, ss);
                        }
                    }
                } else
                    throw new SQLException("Currently only SELECT from a single simple table is supported!", "50100");
            } else
                throw new SQLException("Currently only SELECT from a single table is supported!", "50100");
        } catch (IOException ie) {
            throw getSQLException(ie);
        } catch (IllegalArgumentException iae) {
            throw getSQLException(iae);
        }
        return rs;
    }


    /** {@link Statement}
     * will parse everything but only execute very simple queries
     * like SELECT column1, column2, ... FROM table
     * WHERE (table.)column = value AND ...
     */
    @Override
    public ResultSet executeQuery(String sSql) throws SQLException {
        ResultSet rs = null;
        SqlFactory sf = new AccessSqlFactory();
        SqlStatement ss = sf.newSqlStatement();
        ss.parse(sSql);
        ss.setEvaluationContext(_conn.getUserName(), _conn.getCatalog(), _conn.getSchema());
        rs = executeSelect(ss);
        return rs;
    }
  
  /*====================================================================
  Updates 
  ====================================================================*/

    /** DROP TABLE command
     * @param dt parsed DropTable statement
     * @return updates.
     * @throws SQLException if a database error occurred.
     */
    protected int dropTable(DropTableStatement dts)
            throws SQLException {
        int iDropped = -1;
        DatabaseImpl dbi = (DatabaseImpl) (_conn.getDatabase());
        String sTableName = dts.getTableName()
                               .getName();
        if (dts.getDropBehavior() == DropBehavior.CASCADE) {
            try {
                String sbSql = "SELECT * FROM " + SqlLiterals.formatId(sTableName);
                SqlStatement ss = _sf.newSqlStatement();
                ss.parse(sbSql);
                Table table = dbi.getTable(sTableName);
                if (table != null) {
                    TableCursor tc = new TableCursor(table, ss);
                    for (Row row = tc.getNextRow(); row != null; row = tc.getNextRow())
                        tc.deleteCurrentRow();
                } else
                    throw new SQLException("Table " + sTableName + " not found!");

            } catch (IOException ie) {
                throw new SQLException("Table " + sTableName + " could not be deleted!", ie);
            }
        }
        try {
            dbi.dropTable(sTableName);
            iDropped = 0;
        } catch (IOException ie) {
            throw new SQLException("Table " + sTableName + " could not be dropped!", ie);
        }
        return iDropped;
    }


    /** CREATE TABLE command
     * @param cts parsed CreateTable statement
     * @return updates.
     * @throws SQLException on unsupported features.
     */
    protected int createTable(CreateTableStatement cts)
            throws SQLException {
        String sTableName = AccessLiterals.normalizeId(cts.getTableName()
                                                          .getName());
        TableBuilder tb = new TableBuilder(sTableName);
        try {
            /* columns */
            for (int iTableElement = 0; iTableElement < cts.getTableElements()
                                                           .size(); iTableElement++) {
                TableElement te = cts.getTableElements()
                                     .get(iTableElement);
                if (te.getType() == TableElementType.COLUMN_DEFINITION) {
                    ColumnDefinition cd = te.getColumnDefinition();
                    String sColumnName = AccessLiterals.normalizeId(cd.getColumnName()
                                                                      .get());
                    DataType dt = cd.getDataType();
                    PredefinedType pt = dt.getPredefinedType();
                    if (pt != null) {
                        com.healthmarketscience.jackcess.DataType dta = null;
                        switch (pt.getType()) {
                            case BOOLEAN:
                                dta = com.healthmarketscience.jackcess.DataType.BOOLEAN;
                                break;
                            case SMALLINT:
                                dta = com.healthmarketscience.jackcess.DataType.INT;
                                break;
                            case INTEGER:
                                dta = com.healthmarketscience.jackcess.DataType.LONG;
                                break;
                            case NUMERIC:
                            case DECIMAL:
                            case BIGINT:
                                dta = com.healthmarketscience.jackcess.DataType.NUMERIC;
                                break;
                            case REAL:
                                dta = com.healthmarketscience.jackcess.DataType.FLOAT;
                                break;
                            case FLOAT:
                            case DOUBLE:
                                dta = com.healthmarketscience.jackcess.DataType.DOUBLE;
                                break;
                            case TIMESTAMP:
                            case TIME:
                            case DATE:
                                dta = com.healthmarketscience.jackcess.DataType.SHORT_DATE_TIME;
                                break;
                            case BINARY:
                            case VARBINARY:
                                dta = com.healthmarketscience.jackcess.DataType.BINARY;
                                break;
                            case BLOB:
                            case DATALINK:
                                dta = com.healthmarketscience.jackcess.DataType.OLE;
                                break;
                            case CHAR:
                            case NCHAR:
                            case VARCHAR:
                            case NVARCHAR:
                                dta = com.healthmarketscience.jackcess.DataType.TEXT;
                                break;
                            case XML:
                            case CLOB:
                            case NCLOB:
                                dta = com.healthmarketscience.jackcess.DataType.MEMO;
                                break;
                            default:
                                throw new RuntimeException("SQL data type " + dt.format() + " cannot be handled!");
                        }
                        ColumnBuilder cb = new ColumnBuilder(sColumnName, dta);
                        if (dta.getHasScalePrecision()) {
                            int iPrecision = pt.getPrecision();
                            int iScale = pt.getScale();
                            boolean bPrecisionDefined = (iPrecision != PredefinedType.iUNDEFINED);
                            boolean bScaleDefined = (iScale != PredefinedType.iUNDEFINED);
                            if (pt.getType() == PreType.BIGINT) {
                                iPrecision = dta.getMaxPrecision();
                                iScale = 0;
                            } else if ((!bPrecisionDefined) && (!bScaleDefined)) {
                                iPrecision = dta.getMaxPrecision();
                                iScale = iPrecision / 2;
                            } else if (bPrecisionDefined && (!bScaleDefined))
                                iScale = 0;
                            else if ((!bPrecisionDefined) && bScaleDefined)
                                iPrecision = iScale;
                            if (iPrecision < dta.getMinPrecision())
                                iPrecision = dta.getMinPrecision();
                            if (iPrecision > dta.getMaxPrecision())
                                iPrecision = dta.getMaxPrecision();
                            if (iScale < dta.getMinScale())
                                iScale = dta.getMinScale();
                            if (iScale > dta.getMaxScale())
                                iScale = dta.getMaxScale();
                            cb.setPrecision(iPrecision);
                            cb.setScale(iScale);
                        } else {
                            int iLengthInUnits = pt.getLength();
                            if (iLengthInUnits == PredefinedType.iUNDEFINED) {
                                iLengthInUnits = dta.getMaxSize();
                                if ((pt.getType() == PreType.CHAR) ||
                                        (pt.getType() == PreType.NCHAR) ||
                                        (pt.getType() == PreType.BINARY))
                                    iLengthInUnits = 1;
                            }
                            Multiplier m = pt.getMultiplier();
                            if (m != null)
                                iLengthInUnits = iLengthInUnits * m.getValue();
                            int iLength = iLengthInUnits * dta.getUnitSize();
                            if (iLength < dta.getMinSize())
                                iLength = dta.getMinSize();
                            if (iLength > dta.getMaxSize()) {
                                if (dta == com.healthmarketscience.jackcess.DataType.TEXT)
                                    dta = com.healthmarketscience.jackcess.DataType.MEMO;
                                else if (dta == com.healthmarketscience.jackcess.DataType.BINARY)
                                    dta = com.healthmarketscience.jackcess.DataType.OLE;
                                else
                                    iLength = dta.getMaxSize();
                                cb = new ColumnBuilder(sColumnName, dta);
                            }
                            iLengthInUnits = iLength / dta.getUnitSize();
                            if (dta.isVariableLength())
                                cb.setLength(iLengthInUnits);
                            else
                                cb.setLength(dta.getFixedSize());
                            if (dta.isTrueVariableLength()) {
                                cb.setLength(iLength);
                                cb.setLengthInUnits(iLengthInUnits);
                            }
                        }
                        tb = tb.addColumn(cb);
                        /* now handle column constraints */
                        ColumnConstraintDefinition ccd = cd.getColumnConstraintDefinition();
                        if (ccd != null) {
                            String sConstraintName = AccessLiterals.normalizeId(ccd.getConstraint()
                                                                                   .getName());
                            if (sConstraintName == null)
                                sConstraintName = "CC_" + sTableName + "_" + sColumnName;
                            IndexBuilder ib = new IndexBuilder(sConstraintName);
                            switch (ccd.getType()) {
                                case NOT_NULL:
                                    ib.setRequired();
                                    break;
                                case UNIQUE:
                                    ib.setUnique();
                                    break;
                                case PRIMARY_KEY:
                                    ib.setPrimaryKey();
                                    break;
                                case REFERENCES:
                                    throw new SQLException("FOREIGN KEYS not yet supported!");
                                case CHECK:
                                    throw new SQLException("CHECK constraints not yet supported!");
                            }
                            ib.addColumns(sColumnName);
                            tb = tb.addIndex(ib);
                        }
                    } else
                        throw new SQLException("Only predefined types supported!");
                } else if (te.getType() == TableElementType.TABLE_CONSTRAINT_DEFINITION) {
                    TableConstraintDefinition tcd = te.getTableConstraintDefinition();
                    if ((tcd.getType() == TableConstraintType.PRIMARY_KEY) ||
                            (tcd.getType() == TableConstraintType.UNIQUE)) {
                        String sConstraintName = AccessLiterals.normalizeId(tcd.getConstraint()
                                                                               .getName());
                        if (sConstraintName == null) {
                            if (tcd.getType() == TableConstraintType.PRIMARY_KEY)
                                sConstraintName = "PK_" + sTableName;
                            else {
                                sConstraintName = "UK_" + sTableName;
                                for (int iColumn = 0; iColumn < tcd.getColumns()
                                                                   .get()
                                                                   .size(); iColumn++)
                                    sConstraintName = sConstraintName + "_" + AccessLiterals.normalizeId(tcd.getColumns()
                                                                                                            .get()
                                                                                                            .get(iColumn));
                            }
                        }
                        IndexBuilder ib = new IndexBuilder(sConstraintName);
                        if (tcd.getType() == TableConstraintType.PRIMARY_KEY)
                            ib.setPrimaryKey();
                        else if (tcd.getType() == TableConstraintType.UNIQUE)
                            ib.setUnique();
                        for (int iColumn = 0; iColumn < tcd.getColumns()
                                                           .get()
                                                           .size(); iColumn++) {
                            String sColumnName = AccessLiterals.normalizeId(tcd.getColumns()
                                                                               .get()
                                                                               .get(iColumn));
                            ib.addColumns(sColumnName);
                        }
                        tb = tb.addIndex(ib);
                    } else if (tcd.getType() == TableConstraintType.FOREIGN_KEY) {
                        String sReferencedTable = AccessLiterals.normalizeId(tcd.getReferencedTable()
                                                                                .getName());
                        RelationshipBuilder rb = new RelationshipBuilder(sTableName, sReferencedTable);
                        for (int iColumn = 0; iColumn < tcd.getColumns()
                                                           .get()
                                                           .size(); iColumn++) {
                            String sColumnName = AccessLiterals.normalizeId(tcd.getColumns()
                                                                               .get()
                                                                               .get(iColumn));
                            String sReferencedColumn = AccessLiterals.normalizeId(tcd.getReferencedColumns()
                                                                                     .get()
                                                                                     .get(iColumn));
                            rb.addColumns(sColumnName, sReferencedColumn);
                            if (tcd.getDeleteAction() != null) {
                                if (tcd.getDeleteAction() == ReferentialAction.CASCADE)
                                    rb.setCascadeDeletes();
                                if (tcd.getDeleteAction() == ReferentialAction.SET_NULL)
                                    rb.setCascadeNullOnDelete();
                            } else if ((tcd.getUpdateAction() != null) && (tcd.getUpdateAction() == ReferentialAction.CASCADE))
                                rb.setCascadeUpdates();
                        }
                        // rb.setReferentialIntegrity();
                        rb.toRelationship(_conn.getDatabase());
                    } else
                        throw new SQLException("CHECK constraints are not supported!");
                }
            }
            tb.toTable(_conn.getDatabase());
        } catch (IOException ie) {
            throw new SQLException("Table " + sTableName + " could not be created!", ie);
        }
        int iCreated = 0;
        return iCreated;
    }


    /** ALTER TABLE DROP CONSTRAINT command
     * @param ats parsed AlterTableDropConstraint statement
     * @return updates.
     * @throws SQLException if a database error occurred.
     */
    protected int alterTable(AlterTableStatement ats)
            throws SQLException {
        int iAltered = -1;
        String sTableName = ats.getTableName()
                               .getName();
        /* Only FOREIGN KEY drops are supported */
    /*
    ColumnDefinition cd = ats.getColumnDefinition(); // ADD COLUMN: not supported!
    Identifier idColumn = ats.getColumnName(); // DROP COLUMN: not supported!
    AlterColumnAction aca = ats.getAlterColumnAction(); // ALTER COLUMN: not supported!
    TableConstraintDefinition tcd = ats.getTableConstraintDefinition(); // ADD CONSTRAINT
    */
        try {
            String sConstraintName = ats.getConstraintName()
                                        .getName(); // DROP constraint
            DatabaseImpl dbi = (DatabaseImpl) _conn.getDatabase();
            /* search for foreign key with this name */
            List<Relationship> listRelationships = dbi.getRelationships();
            Relationship rsFk = null;
            for (Iterator<Relationship> iterRelationship = listRelationships.iterator(); iterRelationship.hasNext(); ) {
                Relationship rs = iterRelationship.next();
                if (rs.getName()
                      .equals(sConstraintName))
                    rsFk = rs;
            }
            if (rsFk != null) {
                dbi.dropRelationship(sConstraintName);
                iAltered = 0;
            } else {
                Table table = dbi.getTable(sTableName);
                /* search for index with this name */
                Index indexConstraint = null;
                List<? extends Index> listIndexes = table.getIndexes();
                for (Iterator<? extends Index> iterIndex = listIndexes.iterator();
                     (indexConstraint == null) && iterIndex.hasNext(); ) {
                    Index index = iterIndex.next();
                    if (index.getName()
                             .equals(sConstraintName))
                        indexConstraint = index;
                }
                if (indexConstraint != null)
                    throw new SQLException("(Unique) constraint " + sConstraintName + " cannot (yet) be dropped!");
                throw new SQLException("Constraint " + sConstraintName + " not found!");
            }
        } catch (IOException ie) {
            throw new SQLException("Table " + sTableName + " could not be altered!", ie);
        }
        return iAltered;
    }


    /** DROP VIEW command
     * @param dv parsed DropView statement.
     * @return updates.
     * @throws SQLException if the view could not be dropped.
     */
    protected int dropView(DropViewStatement dvs)
            throws SQLException {
        int iDropped = -1;
        DatabaseImpl dbi = (DatabaseImpl) (_conn.getDatabase());
        String sViewName = dvs.getViewName()
                              .getName();
        try {
            dbi.dropQuery(sViewName);
            iDropped = 0;
        } catch (IOException ie) {
            throw new SQLException("View " + sViewName + " could not be dropped!", ie);
        }
        return iDropped;
    }


    /** CREATE VIEW command
     * @param cv parsed CreateView statement.
     * @return updates.
     * @throws SQLException if view could not be created.
     */
    protected int createView(CreateViewStatement cvs)
            throws SQLException {
        /* SelectQuery needs something like
         * List<String> listSelectColumns
         * List<String> listFromTables
         * String sWhereExpression
         * String List<String> listGroupings
         * String sHavingExpression
         * List<String> listOrderings
         */
        throw new SQLException("CREATE VIEW statements are not (yet) supported!");
    }


    /** INSERT command
     * @param ins parsed Insert statement.
     * @return number of inserted rows.
     * @throws SQLException on unsupported features.
     */
    protected int insert(SqlStatement ss)
            throws SQLException {
        int iInserted = -1;
        InsertStatement is = ss.getDmlStatement()
                               .getInsertStatement();
        String sTableName = is.getTableName()
                              .getName();
        try {
            Table table = _conn.getDatabase()
                               .getTable(sTableName);
            if (table != null) {
                iInserted = 0;
                if ((is.getValues() != null) && (is.getValues()
                                                   .size() > 0)) {
                    List<String> listColumnNames = is.getColumnNames()
                                                     .get();
                    if (listColumnNames == null)
                        listColumnNames = new ArrayList<String>();
                    if (listColumnNames.size() == 0) {
                        for (int iColumn = 0; iColumn < table.getColumnCount(); iColumn++) {
                            Column column = table.getColumns()
                                                 .get(iColumn);
                            DataType dt = getColumnType(column);
                            if (dt != null)
                                listColumnNames.add(column.getName());
                        }
                    } else {
                        for (int iColumn = 0; iColumn < listColumnNames.size(); iColumn++) {
                            String sColumnName = listColumnNames.get(iColumn);
                            listColumnNames.set(iColumn, sColumnName);
                        }
                    }
                    for (int iRow = 0; iRow < is.getValues()
                                                .size(); iRow++) {
                        AssignedRow ar = is.getValues()
                                           .get(iRow);
                        List<UpdateSource> listUpdateSources = ar.getUpdateSources();
                        if (listUpdateSources == null)
                            listUpdateSources = new ArrayList<UpdateSource>();
                        if (listUpdateSources.size() == 0)
                            listUpdateSources.add(ar.getUpdateSource());
                        Map<String, Object> mapRow = new HashMap<String, Object>();
                        for (int iValue = 0; iValue < listUpdateSources.size(); iValue++) {
                            UpdateSource us = listUpdateSources.get(iValue);
                            Object oValue = us.getValueExpression()
                                              .evaluate(ss, false);
                            mapRow.put(listColumnNames.get(iValue), oValue);
                        }
                        try {
                            if (table.addRowFromMap(mapRow) != null)
                                iInserted++;
                        } catch (ConstraintViolationException cve) {
                            // don't throw an exception, just return 0!
                            _conn.getLogWriter()
                                 .println(cve.getClass()
                                             .getName() + ": " + cve.getMessage());
                        }
                    }
                } else if (is.getQueryExpression() != null) {
                    QueryExpressionBody qeb = is.getQueryExpression()
                                                .getQueryExpressionBody();
                    if (qeb != null) {
                        QuerySpecification qs = qeb.getQuerySpecification();
                        if (qs != null) {
                            SqlStatement sqlstmt = _sf.newSqlStatement();
                            sqlstmt.setQuerySpecification(qs);
                            ResultSet rs = executeSelect(sqlstmt);
                            ResultSetMetaData rsmd = rs.getMetaData();
                            while (rs.next()) {
                                Map<String, Object> mapRow = new HashMap<String, Object>();
                                for (int iColumn = 0; iColumn < rsmd.getColumnCount(); iColumn++) {
                                    String sColumnName = rsmd.getColumnName(iColumn + 1);
                                    Object oValue = rs.getObject(iColumn + 1);
                                    mapRow.put(sColumnName, oValue);
                                }
                                try {
                                    if (table.addRowFromMap(mapRow) != null)
                                        iInserted++;
                                } catch (ConstraintViolationException cve) {
                                    // don't throw an exception, just return 0!
                                    _conn.getLogWriter()
                                         .println(cve.getClass()
                                                     .getName() + ": " + cve.getMessage());
                                }
                            }
                            rs.close();
                        }
                    }
                }
            } else
                throw new SQLException("Table " + sTableName + " not found!");
        } catch (IOException ie) {
            throw new SQLException("INSERT into table " + sTableName + " failed!", ie);
        }
        return iInserted;
    }


    /** DELETE command
     * @param sqlstmt sql statement
     * @return number of rows deleted.
     * @throws IOException if an I/O error occurred.
     * @throws SQLException on unsupported features.
     */
    protected int delete(SqlStatement sqlstmt)
            throws SQLException {
        int iDeleted = -1;
        DeleteStatement ds = sqlstmt.getDmlStatement()
                                    .getDeleteStatement();
        String sTableName = ds.getTableName()
                              .getName();
        StringBuilder sbSql = new StringBuilder("SELECT * FROM ");
        sbSql.append(SqlLiterals.formatId(sTableName));
        String sWhere = null;
        if (ds.getBooleanValueExpression() != null)
            sWhere = ds.getBooleanValueExpression()
                       .format();
        if ((sWhere != null) && (sWhere.length() > 0)) {
            sbSql.append("WHERE ");
            sbSql.append(sWhere);
        }
        SqlStatement ss = _sf.newSqlStatement();
        ss.parse(sbSql.toString());
        try {
            Table table = _conn.getDatabase()
                               .getTable(sTableName);
            if (table != null) {
                iDeleted = 0;
                TableCursor tc = new TableCursor(table, ss);
                for (Row row = tc.getNextRow(); row != null; row = tc.getNextRow()) {
                    tc.deleteCurrentRow();
                    iDeleted++;
                }
            } else
                throw new SQLException("Table " + sTableName + " not found!");
        } catch (IOException ie) {
            throw new SQLException("DELETE from table " + sTableName + " failed!", ie);
        }
        return iDeleted;
    }


    /** UPDATE command
     * @param sqlstmt parsed Update statement.
     * @return number of rows updated.
     * @throws IOException if an I/O error occurred.
     */
    protected int update(SqlStatement sqlstmt)
            throws SQLException {
        int iUpdated = -1;
        UpdateStatement us = sqlstmt.getDmlStatement()
                                    .getUpdateStatement();
        String sTableName = us.getTableName()
                              .getName();
        StringBuilder sbSql = new StringBuilder("SELECT * FROM ");
        sbSql.append(SqlLiterals.formatId(sTableName));
        String sWhere = null;
        if (us.getBooleanValueExpression() != null)
            sWhere = us.getBooleanValueExpression()
                       .format();
        if ((sWhere != null) && (sWhere.length() > 0)) {
            sbSql.append("WHERE ");
            sbSql.append(sWhere);
        }
        SqlStatement ss = _sf.newSqlStatement();
        ss.parse(sbSql.toString());
        try {
            Table table = _conn.getDatabase()
                               .getTable(sTableName);
            if (table != null) {
                iUpdated = 0;
                TableCursor tc = new TableCursor(table, ss);
                for (Row row = tc.getNextRow(); row != null; row = tc.getNextRow()) {
                    /* set new values in row */
                    for (int iSet = 0; iSet < us.getSetClauses()
                                                .size(); iSet++) {
                        SetClause sc = us.getSetClauses()
                                         .get(iSet);
                        String sColumnName = sc.getSetTarget()
                                               .getUpdateTarget()
                                               .getColumnName()
                                               .get();
                        row.put(sColumnName, sc.getUpdateSource()
                                               .getValueExpression()
                                               .evaluate(sqlstmt, false));
                    }
                    tc.updateCurrentRow(row);
                    iUpdated++;
                }
            } else
                throw new SQLException("Table " + sTableName + " not found!");
        } catch (IOException ie) {
            throw new SQLException("UPDATE table " + sTableName + " failed!", ie);
        }
        return iUpdated;
    }


    /** {@link Statement}
     * will parse everything but only execute very simple update statements.
     */
    @Override
    public int executeUpdate(String sSql)
            throws SQLException {
        int iUpdated = -1;
        SqlFactory sf = new AccessSqlFactory();
        SqlStatement ss = sf.newSqlStatement();
        ss.parse(sSql);
        if (ss.getDdlStatement() != null) {
            DdlStatement ds = ss.getDdlStatement();
            if (ds.getDropTableStatement() != null)
                iUpdated = dropTable(ds.getDropTableStatement());
            else if (ds.getCreateTableStatement() != null)
                iUpdated = createTable(ds.getCreateTableStatement());
            else if (ds.getAlterTableStatement() != null)
                iUpdated = alterTable(ds.getAlterTableStatement());
            else if (ds.getDropViewStatement() != null)
                iUpdated = dropView(ds.getDropViewStatement());
            else if (ds.getCreateViewStatement() != null)
                iUpdated = createView(ds.getCreateViewStatement());
            else if (ds.getCreateSchemaStatement() != null)
                iUpdated = 0;
            else if (ds.getDropSchemaStatement() != null)
                iUpdated = 0;
        } else if (ss.getDmlStatement() != null) {
            DmlStatement ds = ss.getDmlStatement();
            if (ds.getInsertStatement() != null)
                iUpdated = insert(ss);
            else if (ds.getDeleteStatement() != null)
                iUpdated = delete(ss);
            else if (ds.getUpdateStatement() != null)
                iUpdated = update(ss);
        }
        return iUpdated;
    }


    /** {@link Statement} */
    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("JdbcAccess does not support generated keys!");
    }


    /** {@link Statement} */
    @Override
    public int executeUpdate(String sql, int[] columnIndexes)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("JdbcAccess does not support generated keys!");
    }


    /** {@link Statement} */
    @Override
    public int executeUpdate(String sql, String[] columnNames)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("JdbcAccess does not support generated keys!");
    }

  /*====================================================================
  Execute 
  ====================================================================*/

    /** {@link Statement} */
    @Override
    public boolean execute(String sSql) throws SQLException {
        boolean bIsResultSet = false;
        _rs = null;
        _iUpdateCount = -1;
        SqlFactory sf = new AccessSqlFactory();
        SqlStatement ss = sf.newSqlStatement();
        ss.parse(sSql);
        if (ss.getDdlStatement() != null) {
            DdlStatement ds = ss.getDdlStatement();
            if (ds.getDropTableStatement() != null)
                dropTable(ds.getDropTableStatement());
            else if (ds.getCreateTableStatement() != null)
                createTable(ds.getCreateTableStatement());
            else if (ds.getAlterTableStatement() != null)
                alterTable(ds.getAlterTableStatement());
            else if (ds.getDropViewStatement() != null)
                dropView(ds.getDropViewStatement());
            else if (ds.getCreateViewStatement() != null)
                createView(ds.getCreateViewStatement());
        } else if (ss.getDmlStatement() != null) {
            DmlStatement ds = ss.getDmlStatement();
            if (ds.getInsertStatement() != null)
                insert(ss);
            else if (ds.getDeleteStatement() != null)
                delete(ss);
            else if (ds.getUpdateStatement() != null)
                update(ss);
        } else if (ss.getQuerySpecification() != null) {
            _rs = executeSelect(ss);
            bIsResultSet = true;
        }
        return bIsResultSet;
    }


    /** {@link Statement} */
    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException("JdbcAccess does not support generated keys!");
    }

    /** {@link Statement} */
    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException("JdbcAccess does not support generated keys!");
    }

    /** {@link Statement} */
    @Override
    public boolean execute(String sql, String[] columnNames)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("JdbcAccess does not support generated keys!");
    }

    /** {@link Statement} */
    @Override
    public ResultSet getResultSet() throws SQLException {
        return _rs;
    }


    /** {@link Statement} */
    @Override
    public int getUpdateCount() throws SQLException {
        return _iUpdateCount;
    }


    /** {@link Statement} */
    @Override
    public boolean getMoreResults() throws SQLException {
        throw new SQLException("JdbcAccessStatement does not support complex execute()!");
    }


    /** {@link Statement} */
    @Override
    public boolean getMoreResults(int iCurrent) throws SQLException {
        throw new SQLFeatureNotSupportedException("JdbcAccessStatement does not support complex execute()!");
    }

  /*====================================================================
  Batch processing 
  ====================================================================*/

    /** {@link Statement} */
    @Override
    public void addBatch(String sSql) throws SQLException {
        _listBatch.add(sSql);
    }


    /** {@link Statement} */
    @Override
    public void clearBatch() throws SQLException {
        _listBatch.clear();
    }


    /** {@link Statement} */
    @Override
    public int[] executeBatch() throws SQLException {
        int[] aiUpdated = new int[_listBatch.size()];
        for (int iSql = 0; iSql < _listBatch.size(); iSql++) {
            String sSql = _listBatch.get(iSql);
            try {
                aiUpdated[iSql] = executeUpdate(sSql);
            } catch (SQLException se) {
                throw new java.sql.BatchUpdateException(se.getClass()
                                                          .getName() + ": " + se.getMessage(), aiUpdated);
            }
        }
        return aiUpdated;
    }

}
