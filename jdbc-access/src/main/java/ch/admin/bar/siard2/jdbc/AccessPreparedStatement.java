package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.access.ResultSetHeader;
import ch.admin.bar.siard2.access.Shunting;
import ch.enterag.sqlparser.DdlStatement;
import ch.enterag.sqlparser.DmlStatement;
import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.expression.GeneralValueSpecification;
import ch.enterag.sqlparser.expression.QuerySpecification;
import ch.enterag.sqlparser.expression.TablePrimary;
import ch.enterag.sqlparser.expression.TableReference;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.SU;
import ch.enterag.utils.database.SqlTypes;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.query.Query;
import com.healthmarketscience.jackcess.query.SelectQuery;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class AccessPreparedStatement
        extends AccessStatement
        implements PreparedStatement {
    private static final int _iBUFSIZ = 8192;
    private SqlStatement _sqlstmt = null;

    public AccessPreparedStatement(AccessConnection conn, String sSql)
            throws SQLException {
        super(conn);
        /* this uses AccessSqlFactory and thus creates an ordered list of question marks. */
        _sqlstmt = _sf.newSqlStatement();
        _sqlstmt.parse(sSql);
        _sqlstmt.setEvaluationContext(_conn.getUserName(), _conn.getCatalog(), _conn.getSchema());
        _sqlstmt.setQuestionMarks(_sf.getQuestionMarks());
    }


    /** {@link PreparedStatement} */
    @Override
    public ResultSet executeQuery() throws SQLException {
        return executeSelect(_sqlstmt);
    }


    /** {@link PreparedStatement} */
    @Override
    public int executeUpdate() throws SQLException {
        int iUpdated = -1;
        if (_sqlstmt.getDdlStatement() != null) {
            DdlStatement ds = _sqlstmt.getDdlStatement();
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
        } else if (_sqlstmt.getDmlStatement() != null) {
            DmlStatement ds = _sqlstmt.getDmlStatement();
            if (ds.getInsertStatement() != null)
                iUpdated = insert(_sqlstmt);
            else if (ds.getDeleteStatement() != null)
                iUpdated = delete(_sqlstmt);
            else if (ds.getUpdateStatement() != null)
                iUpdated = update(_sqlstmt);
        }
        return iUpdated;
    }


    /** {@link PreparedStatement} */
    @Override
    public boolean execute() throws SQLException {
        boolean bIsResultSet = false;
        _rs = null;
        _iUpdateCount = -1;
        if (_sqlstmt.getDdlStatement() != null) {
            DdlStatement ds = _sqlstmt.getDdlStatement();
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
        } else if (_sqlstmt.getDmlStatement() != null) {
            DmlStatement ds = _sqlstmt.getDmlStatement();
            if (ds.getInsertStatement() != null)
                insert(_sqlstmt);
            else if (ds.getDeleteStatement() != null)
                delete(_sqlstmt);
            else if (ds.getUpdateStatement() != null)
                update(_sqlstmt);
        } else if (_sqlstmt.getQuerySpecification() != null) {
            _rs = executeSelect(_sqlstmt);
            bIsResultSet = true;
        }
        return bIsResultSet;
    }

    private String getTableName()
            throws SQLException {
        String sTableName = null;
        try {
            QuerySpecification qs = _sqlstmt.getQuerySpecification();
            if ((qs != null) && (qs.getTableReferences()
                                   .size() == 1)) {
                TableReference tr = qs.getTableReferences()
                                      .get(0);
                if (tr.getTablePrimary() != null) {
                    TablePrimary tp = tr.getTablePrimary();
                    QualifiedId qiTable = tp.getTableName();
                    Table table = _conn.getDatabase()
                                       .getTable(qiTable.getName());
                    if (table != null)
                        sTableName = table.getName();
                    else {
                        List<Query> listQueries = _conn.getDatabase()
                                                       .getQueries();
                        for (Iterator<Query> iterQuery = listQueries.iterator(); (sTableName == null) && iterQuery.hasNext(); ) {
                            Query query = iterQuery.next();
                            if (query.getName()
                                     .equals(qiTable.getName()) && (query instanceof SelectQuery))
                                sTableName = query.getName();
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
        return sTableName;
    }


    /** {@link PreparedStatement} */
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        /* create result set from query */
        ResultSetHeader rsh = getSelectHeader(
                new QualifiedId(null, _conn.getUserName(), getTableName()), _sqlstmt);
        return new AccessResultSetMetaData(rsh, _conn);
    }


    /** {@link PreparedStatement} */
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return new AccessParameterMetaData(_sf, _sqlstmt);
    }


    /** {@link PreparedStatement} */
    @Override
    public void clearParameters() throws SQLException {
        for (int iParameter = 0; iParameter < _sf.getQuestionMarks()
                                                 .size(); iParameter++)
            this.setNull(iParameter + 1, Types.NULL);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setNull(int parameterIndex, int sqlType)
            throws SQLException {
        int iPrecision = -1;
        int iScale = -1;
        switch (sqlType) {
            case Types.VARBINARY:
            case Types.BINARY:
                iPrecision = _conn.getMetaData()
                                  .getMaxBinaryLiteralLength();
                break;
            case Types.NVARCHAR:
            case Types.VARCHAR:
            case Types.NCHAR:
            case Types.CHAR:
                iPrecision = _conn.getMetaData()
                                  .getMaxCharLiteralLength();
                break;
            case Types.NUMERIC:
            case Types.DECIMAL:
                iPrecision = 19;
                iScale = 4;
                break;
            default:
                break;
        }
        DataType dt = Shunting.convertTypeFromJdbc(sqlType, iPrecision, iScale);
        _sqlstmt.setQuestionMarkType(_sf.getQuestionMarks()
                                        .get(parameterIndex - 1), dt);
        _sqlstmt.setQuestionMarkValue(_sf.getQuestionMarks()
                                         .get(parameterIndex - 1), null);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName)
            throws SQLException {
        setNull(parameterIndex, sqlType);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setBoolean(int parameterIndex, boolean x)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initBooleanType();
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, Boolean.valueOf(x));
    }


    /** {@link PreparedStatement} */
    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initSmallIntType();
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, BigDecimal.valueOf(x));
    }


    /** {@link PreparedStatement} */
    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initSmallIntType();
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, BigDecimal.valueOf(x));
    }


    /** {@link PreparedStatement} */
    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initIntegerType();
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, BigDecimal.valueOf(x));
    }


    /** {@link PreparedStatement} */
    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initBigIntType();
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, BigDecimal.valueOf(x));
    }


    /** {@link PreparedStatement} */
    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initRealType();
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, Double.valueOf(x));
    }


    /** {@link PreparedStatement} */
    @Override
    public void setDouble(int parameterIndex, double x)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initDoubleType();
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, Double.valueOf(x));
    }


    /** {@link PreparedStatement} */
    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initDecimalType(19, 4);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, x);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setString(int parameterIndex, String x)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initVarCharType(_conn.getMetaData()
                                .getMaxCharLiteralLength());
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, x);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setNString(int parameterIndex, String value)
            throws SQLException {
        setString(parameterIndex, value);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setBytes(int parameterIndex, byte[] x)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initVarbinaryType(_conn.getMetaData()
                                  .getMaxBinaryLiteralLength());
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, x);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initDateType();
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, x);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("setDate with Calendar not supported!");
    }


    /** {@link PreparedStatement} */
    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initTimeType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, x);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("setTime with Calendar not supported!");
    }


    /** {@link PreparedStatement} */
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initTimestampType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, x);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("setTimestamp with Calendar not supported!");
    }


    /** {@link PreparedStatement} */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initClobType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[_iBUFSIZ];
            for (int iRead = x.read(buf); iRead != -1; iRead = x.read(buf))
                baos.write(buf, 0, iRead);
            baos.close();
            x.close();
            _sqlstmt.setQuestionMarkValue(gvs, SU.getWindows1252String(baos.toByteArray()));
        } catch (IOException ie) {
            throw new SQLException("setAsciiStream failed!", ie);
        }
    }


    /** {@link PreparedStatement} */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initClobType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[_iBUFSIZ];
            for (int iRead = x.read(buf); (iRead != -1) && (length > 0); iRead = x.read(buf)) {
                if (iRead > length)
                    iRead = (int) length;
                baos.write(buf, 0, iRead);
                length = length - iRead;
            }
            baos.close();
            x.close();
            _sqlstmt.setQuestionMarkValue(gvs, SU.getWindows1252String(baos.toByteArray()));
        } catch (IOException ie) {
            throw new SQLException("setAsciiStream failed!", ie);
        }
    }


    /** {@link PreparedStatement} */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        setAsciiStream(parameterIndex, x, (long) length);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initClobType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[_iBUFSIZ];
            for (int iRead = x.read(buf); (iRead != -1) && (length > 0); iRead = x.read(buf)) {
                if (iRead > length)
                    iRead = length;
                baos.write(buf, 0, iRead);
                length = length - iRead;
            }
            baos.close();
            x.close();
            _sqlstmt.setQuestionMarkValue(gvs, SU.getUtf8String(baos.toByteArray()));
        } catch (IOException ie) {
            throw new SQLException("setUnicodeStream failed!", ie);
        }
    }


    /** {@link PreparedStatement} */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initBlobType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[_iBUFSIZ];
            for (int iRead = x.read(buf); iRead != -1; iRead = x.read(buf))
                baos.write(buf, 0, iRead);
            baos.close();
            x.close();
            _sqlstmt.setQuestionMarkValue(gvs, baos.toByteArray());
        } catch (IOException ie) {
            throw new SQLException("setUnicodeStream failed!", ie);
        }
    }


    /** {@link PreparedStatement} */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initBlobType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[_iBUFSIZ];
            for (int iRead = x.read(buf); (iRead != -1) && (length > 0); iRead = x.read(buf)) {
                if (iRead > length)
                    iRead = (int) length;
                baos.write(buf, 0, iRead);
                length = length - iRead;
            }
            baos.close();
            x.close();
            _sqlstmt.setQuestionMarkValue(gvs, baos.toByteArray());
        } catch (IOException ie) {
            throw new SQLException("setUnicodeStream failed!", ie);
        }
    }


    /** {@link PreparedStatement} */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        setBinaryStream(parameterIndex, x, (long) length);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initClobType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        try {
            StringWriter sw = new StringWriter();
            char[] cbuf = new char[_iBUFSIZ];
            for (int iRead = reader.read(cbuf); iRead != -1; iRead = reader.read(cbuf))
                sw.write(cbuf, 0, iRead);
            sw.close();
            reader.close();
            _sqlstmt.setQuestionMarkValue(gvs, sw.toString());
        } catch (IOException ie) {
            throw new SQLException("setCharacterStream failed!", ie);
        }
    }


    /** {@link PreparedStatement} */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initClobType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        try {
            StringWriter sw = new StringWriter();
            char[] cbuf = new char[_iBUFSIZ];
            for (int iRead = reader.read(cbuf); (iRead != -1) && (length > 0); iRead = reader.read(cbuf)) {
                if (iRead > length)
                    iRead = (int) length;
                sw.write(cbuf, 0, iRead);
                length = length - iRead;
            }
            sw.close();
            reader.close();
            _sqlstmt.setQuestionMarkValue(gvs, sw.toString());
        } catch (IOException ie) {
            throw new SQLException("setCharacterStream failed!", ie);
        }
    }


    /** {@link PreparedStatement} */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length)
            throws SQLException {
        setCharacterStream(parameterIndex, reader, (long) length);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setNCharacterStream(int parameterIndex, Reader value)
            throws SQLException {
        setCharacterStream(parameterIndex, value);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length)
            throws SQLException {
        setCharacterStream(parameterIndex, value, length);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initBlobType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, x.getBytes(1L, (int) x.length()));
    }


    /** {@link PreparedStatement} */
    @Override
    public void setBlob(int parameterIndex, InputStream inputStream)
            throws SQLException {
        setBinaryStream(parameterIndex, inputStream);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setBlob(int parameterIndex, InputStream inputStream,
                        long length) throws SQLException {
        setBinaryStream(parameterIndex, inputStream, length);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initClobType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, x.getSubString(1L, (int) x.length()));
    }


    /** {@link PreparedStatement} */
    @Override
    public void setClob(int parameterIndex, Reader reader)
            throws SQLException {
        setCharacterStream(parameterIndex, reader);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setClob(int parameterIndex, Reader reader, long length)
            throws SQLException {
        setCharacterStream(parameterIndex, reader, length);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setNClob(int parameterIndex, NClob value)
            throws SQLException {
        setClob(parameterIndex, value);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setNClob(int parameterIndex, Reader reader)
            throws SQLException {
        setClob(parameterIndex, reader);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setNClob(int parameterIndex, Reader reader, long length)
            throws SQLException {
        setClob(parameterIndex, reader, length);
    }


    /** {@link PreparedStatement} */
    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject)
            throws SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        pt.initClobType(PredefinedType.iUNDEFINED, null);
        dt.initPredefinedDataType(pt);
        GeneralValueSpecification gvs = _sf.getQuestionMarks()
                                           .get(parameterIndex - 1);
        _sqlstmt.setQuestionMarkType(gvs, dt);
        _sqlstmt.setQuestionMarkValue(gvs, xmlObject.getString());
    }


    /** {@link PreparedStatement} */
    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        setString(parameterIndex, x.toString());
    }


    /** {@link PreparedStatement} */
    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("REF not supported!");
    }


    /** {@link PreparedStatement} */
    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("RowIds are not supported!");
    }


    /** {@link PreparedStatement} */
    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Arrays are not supported!");
    }


    /** {@link PreparedStatement} */
    @Override
    public void setObject(int parameterIndex, Object x)
            throws SQLException {
        if (x == null)
            setNull(parameterIndex, Types.NULL);
        else if (x instanceof Boolean)
            setBoolean(parameterIndex, ((Boolean) x).booleanValue());
        else if (x instanceof Byte)
            setByte(parameterIndex, ((Byte) x).byteValue());
        else if (x instanceof Short)
            setShort(parameterIndex, ((Short) x).shortValue());
        else if (x instanceof Integer)
            setInt(parameterIndex, ((Integer) x).intValue());
        else if (x instanceof Long)
            setLong(parameterIndex, ((Long) x).longValue());
        else if (x instanceof Float)
            setFloat(parameterIndex, ((Float) x).floatValue());
        else if (x instanceof Double)
            setDouble(parameterIndex, ((Double) x).doubleValue());
        else if (x instanceof BigDecimal)
            setBigDecimal(parameterIndex, (BigDecimal) x);
        else if (x instanceof String)
            setString(parameterIndex, (String) x);
        else if (x instanceof byte[])
            setBytes(parameterIndex, (byte[]) x);
        else if (x instanceof Date)
            setDate(parameterIndex, (Date) x);
        else if (x instanceof Time)
            setTime(parameterIndex, (Time) x);
        else if (x instanceof Timestamp)
            setTimestamp(parameterIndex, (Timestamp) x);
        else if (x instanceof InputStream)
            setBinaryStream(parameterIndex, (InputStream) x);
        else if (x instanceof Reader)
            setCharacterStream(parameterIndex, (Reader) x);
        else if (x instanceof Blob)
            setBlob(parameterIndex, (Blob) x);
        else if (x instanceof Clob)
            setClob(parameterIndex, (Clob) x);
        else if (x instanceof SQLXML)
            setSQLXML(parameterIndex, (SQLXML) x);
        else if (x instanceof URL)
            setURL(parameterIndex, (URL) x);
        else
            throw new IllegalArgumentException("Invalid data type for parameter!");
    }


    /** {@link PreparedStatement} */
    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType)
            throws SQLException {
        switch (targetSqlType) {
            case Types.BIGINT:
                setLong(parameterIndex, ((Long) x).longValue());
                break;
            case Types.BINARY:
            case Types.VARBINARY:
                setBytes(parameterIndex, (byte[]) x);
                break;
            case Types.BLOB:
            case Types.DATALINK:
                setBlob(parameterIndex, (Blob) x);
                break;
            case Types.LONGVARBINARY:
                setBinaryStream(parameterIndex, (InputStream) x);
                break;
            case Types.BOOLEAN:
                setBoolean(parameterIndex, ((Boolean) x).booleanValue());
                break;
            case Types.CHAR:
            case Types.VARCHAR:
                setString(parameterIndex, (String) x);
                break;
            case Types.CLOB:
                setClob(parameterIndex, (Clob) x);
                break;
            case Types.LONGVARCHAR:
                setCharacterStream(parameterIndex, (Reader) x);
                break;
            case Types.DATE:
                setDate(parameterIndex, (Date) x);
                break;
            case Types.NUMERIC:
            case Types.DECIMAL:
                setBigDecimal(parameterIndex, (BigDecimal) x);
                break;
            case Types.DOUBLE:
            case Types.FLOAT:
                setDouble(parameterIndex, ((Double) x).doubleValue());
                break;
            case Types.INTEGER:
                setInt(parameterIndex, ((Integer) x).intValue());
                break;
            case Types.NCHAR:
                setNString(parameterIndex, (String) x);
                break;
            case Types.NCLOB:
                setNClob(parameterIndex, (NClob) x);
                break;
            case Types.LONGNVARCHAR:
                setNCharacterStream(parameterIndex, (Reader) x);
                break;
            case Types.REAL:
                setFloat(parameterIndex, ((Float) x).floatValue());
                break;
            case Types.SMALLINT:
                setShort(parameterIndex, ((Short) x).shortValue());
                break;
            case Types.TINYINT:
                setByte(parameterIndex, ((Byte) x).byteValue());
                break;
            case Types.SQLXML:
                setSQLXML(parameterIndex, (SQLXML) x);
                break;
            case Types.TIME:
                setTime(parameterIndex, (Time) x);
                break;
            case Types.TIMESTAMP:
                setTimestamp(parameterIndex, (Timestamp) x);
                break;
            default:
                throw new IllegalArgumentException("Unsupported target type: " + SqlTypes.getTypeName(targetSqlType) + "!");
        }
    }


    /** {@link PreparedStatement} */
    @Override
    public void setObject(int parameterIndex, Object x,
                          int targetSqlType, int scaleOrLength) throws SQLException {
        setObject(parameterIndex, x, targetSqlType);
    }


    /** {@link PreparedStatement} */
    @Override
    public void addBatch() throws SQLException {
        throw new IllegalArgumentException("Batching not supported for PreparedStatement!");
    }


    /** {@link PreparedStatement} */
    @Override
    public void clearBatch() throws SQLException {
        throw new IllegalArgumentException("Batching not supported for PreparedStatement!");
    }


    /** {@link PreparedStatement} */
    @Override
    public int[] executeBatch() throws SQLException {
        throw new IllegalArgumentException("Batching not supported for PreparedStatement!");
    }

}
