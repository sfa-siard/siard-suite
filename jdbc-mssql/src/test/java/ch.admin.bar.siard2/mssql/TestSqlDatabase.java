package ch.admin.bar.siard2.mssql;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;

import static org.junit.Assert.*;

import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.dml.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.identifier.*;

public class TestSqlDatabase {
    public static final String _sTEST_SCHEMA = "TESTSQLSCHEMA";
    private static final String _sTEST_TABLE_SIMPLE = "TSQLSIMPLE";
    public static final String COLUMN_DATALINK = "COLUMN_DATALINK";

    public static QualifiedId getQualifiedSimpleTable() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_TABLE_SIMPLE);
    }

    private static final String _sTEST_TABLE_COMPLEX = "TSQLCOMPLEX";

    public static QualifiedId getQualifiedComplexTable() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_TABLE_COMPLEX);
    }

    private static final String _sTEST_VIEW_SIMPLE = "VSQLSIMPLE";

    public static QualifiedId getQualifiedSimpleView() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_VIEW_SIMPLE);
    }

    private static final String _sTEST_TYPE_DISTINCT = "TYPSQLDISTINCT";

    public static QualifiedId getQualifiedDistinctType() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_TYPE_DISTINCT);
    }

    private static List<TestColumnDefinition> getListBaseDistinct() {
        List<TestColumnDefinition> listBaseDistinct = new ArrayList<>();
        listBaseDistinct.add(new TestColumnDefinition(getQualifiedDistinctType().format(), "NCHAR VARYING(10)", "Auwääh"));
        return listBaseDistinct;
    }

    public static List<TestColumnDefinition> _listBaseDistinct = getListBaseDistinct();

    private static int _iPrimarySimple = -1;

    public static String getCircleJpgUrl() {
        return "file://localhost" + new File("testfiles/circle.jpg").getAbsolutePath();
    }

    @SuppressWarnings("deprecation")
    private static List<TestColumnDefinition> getListCdSimple() {
        List<TestColumnDefinition> listCdSimple = new ArrayList<>();
        listCdSimple.add(new TestColumnDefinition("CCHAR_5", "CHAR(5)", "Abcd"));
        listCdSimple.add(new TestColumnDefinition("CVARCHAR_255", "VARCHAR(255)", TestUtils.getString(196)));
        listCdSimple.add(new TestColumnDefinition("CCLOB_2M", "CLOB(2M)", TestUtils.getString(2000000)));
        listCdSimple.add(new TestColumnDefinition("CNCHAR_5", "NCHAR(5)", "Niño"));
        listCdSimple.add(new TestColumnDefinition("CNVARCHAR_127", "NCHAR VARYING(127)", TestUtils.getNString(100)));
        listCdSimple.add(new TestColumnDefinition("CNCLOB_1M", "NCLOB(1M)", TestUtils.getNString(1000000)));
        listCdSimple.add(new TestColumnDefinition("CXML", "XML", "<a>foöäpwkfèégopàèwerkgvoperkv &lt; and &amp; ifjeifj</a>"));
        listCdSimple.add(new TestColumnDefinition("CBINARY_5", "BINARY(5)", new byte[]{1, -2, 3, -4}));
        listCdSimple.add(new TestColumnDefinition("CVARBINARY_255", "VARBINARY(255)", TestUtils.getBytes(196)));
        listCdSimple.add(new TestColumnDefinition("CBLOB", "BLOB", TestUtils.getBytes(1000000)));
        listCdSimple.add(new TestColumnDefinition("CNUMERIC_31", "NUMERIC(31)", BigInteger.valueOf(1234567890123456789l)));
        listCdSimple.add(new TestColumnDefinition("CDECIMAL_15_5", "DECIMAL(15,5)", new BigDecimal(BigInteger.valueOf(3141592653210l), 5)));
        listCdSimple.add(new TestColumnDefinition("CSMALLINT", "SMALLINT", (short) -32000));
        _iPrimarySimple = listCdSimple.size(); // next column will be primary key column
        listCdSimple.add(new TestColumnDefinition("CINTEGER", "INTEGER", 1234567890));
        listCdSimple.add(new TestColumnDefinition("CBIGINT", "BIGINT", -123456789012345678l));
        listCdSimple.add(new TestColumnDefinition("CFLOAT_10", "FLOAT(10)", (float) Math.E));
        listCdSimple.add(new TestColumnDefinition("CREAL", "REAL", (float) Math.PI));
        listCdSimple.add(new TestColumnDefinition("CDOUBLE", "DOUBLE PRECISION", Math.E));
        listCdSimple.add(new TestColumnDefinition("CBOOLEAN", "BOOLEAN", Boolean.TRUE));
        listCdSimple.add(new TestColumnDefinition("CDATE", "DATE", new Date(2016 - 1900, 11, 28)));
        listCdSimple.add(new TestColumnDefinition("CTIME", "TIME", new Time(13, 45, 28)));
        listCdSimple.add(new TestColumnDefinition("CTIMESTAMP", "TIMESTAMP(9)", new Timestamp(2016 - 1900, 11, 28, 13, 45, 28, 123456789)));
        listCdSimple.add(new TestColumnDefinition("CINTERVAL_YEAR_3_MONTH", "INTERVAL YEAR(2) TO MONTH", new Interval(1, 123, 3)));
        listCdSimple.add(new TestColumnDefinition("CINTERVAL_DAY_2_SECONDS_6", "INTERVAL DAY(2) TO SECOND(6)", new Interval(1, 4, 17, 54, 23, 123456000l)));
        listCdSimple.add(new TestColumnDefinition(COLUMN_DATALINK, "DATALINK", getCircleJpgUrl()));
        return listCdSimple;
    }

    public static List<TestColumnDefinition> _listCdSimple = getListCdSimple();

    private static int _iPrimaryComplex = -1;

    private static List<TestColumnDefinition> getListCdComplex() {
        List<TestColumnDefinition> listCdComplex = new ArrayList<>();
        _iPrimaryComplex = listCdComplex.size(); // next column will be primary key column
        listCdComplex.add(new TestColumnDefinition("CID", "INTEGER", 1234567890));
        listCdComplex.add(new TestColumnDefinition("CDISTINCT", getQualifiedDistinctType().format(), "NIÑO"));
        return listCdComplex;
    }

    public static List<TestColumnDefinition> _listCdComplex = getListCdComplex();

    private SqlFactory _sf = new BaseSqlFactory();
    private Connection _conn = null;

    public TestSqlDatabase(Connection conn)
            throws SQLException {
        _conn = conn;
        drop();
        create();
    }

    private void rollback(String sMessage, SQLException se) {
        System.out.println(sMessage + ": " + EU.getExceptionMessage(se));
        try {
            _conn.rollback();
        } catch (SQLException seRollback) {
            System.err.println("Rollback failed: " +
                    EU.getExceptionMessage(seRollback));
        }
    }

    private void drop() {
        try {
            _conn.setAutoCommit(false);
        } catch (SQLException se) {
            rollback("Autocommit", se);
        }
        deleteTables();
        dropTables();
        dropTypes();
        dropSchema();
    }

    private void executeDrop(String sSql) {
        try {
            Statement stmt = _conn.createStatement();
            stmt.executeUpdate(sSql);
            stmt.close();
            _conn.commit();
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
        }
    }

    private void deleteTables() {
        deleteTable(getQualifiedSimpleTable());
        deleteTable(getQualifiedComplexTable());
    }

    private void deleteTable(QualifiedId qiTable) {
        DeleteStatement ds = _sf.newDeleteStatement();
        ds.initialize(qiTable, null);
        executeDrop(ds.format());
    }

    private void dropTables() {
        dropView(getQualifiedSimpleView());
        dropTable(getQualifiedComplexTable()); // first, because of foreign key ...
        dropTable(getQualifiedSimpleTable());
    }

    private void dropTable(QualifiedId qiTable) {
        DropTableStatement dts = _sf.newDropTableStatement();
        dts.initialize(qiTable, DropBehavior.RESTRICT);
        executeDrop(dts.format());
    }

    private void dropView(QualifiedId qiView) {
        DropViewStatement dvs = _sf.newDropViewStatement();
        dvs.initialize(qiView, DropBehavior.RESTRICT);
        executeDrop(dvs.format());
    }

    private void dropTypes() {
        dropType(getQualifiedDistinctType());
    } /* dropTypes */

    private void dropType(QualifiedId qiType) {
        DropTypeStatement dts = _sf.newDropTypeStatement();
        dts.initialize(qiType, DropBehavior.RESTRICT);
        executeDrop(dts.format());
    }

    private void dropSchema() {
        DropSchemaStatement dss = _sf.newDropSchemaStatement();
        dss.initialize(new SchemaId(null, _sTEST_SCHEMA), DropBehavior.RESTRICT);
        executeDrop(dss.format());
    }

    private void create()
            throws SQLException {
        _conn.setAutoCommit(false);
        createSchema();
        createTypes();
        createTables();
        insertTables();
    }

    private void executeCreate(String sSql)
            throws SQLException {
        Statement stmt = _conn.createStatement();
        stmt.executeUpdate(sSql);
        stmt.close();
        _conn.commit();
    }

    private void createSchema()
            throws SQLException {
        CreateSchemaStatement css = _sf.newCreateSchemaStatement();
        css.initialize(new SchemaId(null, _sTEST_SCHEMA), new Identifier());
        executeCreate(css.format());
    }

    private void createTypes()
            throws SQLException {
        createType(getQualifiedDistinctType(), _listBaseDistinct);
    }

    private void createType(QualifiedId qiType, List<TestColumnDefinition> listAd)
            throws SQLException {
        CreateTypeStatement cts = _sf.newCreateTypeStatement();
        assertEquals("", 1, listAd.size()); // MSSQL only supports DISTINCT types
        TestColumnDefinition tcd = listAd.get(0);
        PredefinedType ptBase = _sf.newPredefinedType();
        ptBase.parse(tcd.getType());
        cts.initDistinct(qiType, ptBase);
        executeCreate(cts.format());
    }

    private void createTables()
            throws SQLException {
        createTable(getQualifiedSimpleTable(), _listCdSimple,
                Arrays.asList(new String[]{_listCdSimple.get(_iPrimarySimple).getName()}),
                null, null, null);
        createTable(getQualifiedComplexTable(), _listCdComplex,
                Arrays.asList(new String[]{_listCdComplex.get(_iPrimaryComplex).getName()}),
                Arrays.asList(new String[]{_listCdComplex.get(_iPrimaryComplex).getName()}),
                getQualifiedSimpleTable(),
                Arrays.asList(new String[]{_listCdSimple.get(_iPrimarySimple).getName()}));
        createView(getQualifiedSimpleView(), _listCdSimple, getQualifiedSimpleTable());
    }

    private void createTable(QualifiedId qiTable, List<TestColumnDefinition> listCd,
                             List<String> listPrimary, List<String> listForeign,
                             QualifiedId qiTableReferenced, List<String> listReferenced)
            throws SQLException {
        CreateTableStatement cts = _sf.newCreateTableStatement();
        List<TableElement> listTableElements = new ArrayList<>();
        for (TestColumnDefinition tcd : listCd) {
            listTableElements.add(getTableElement(tcd, listPrimary.contains(tcd.getName())));
        }
        if (listPrimary != null)
            listTableElements.add(getPrimaryTableElement("PK" + qiTable.getName(), listPrimary));
        if (listForeign != null)
            listTableElements.add(getForeignTableElement("FK" + qiTable.getName(),
                    listForeign, qiTableReferenced, listReferenced));
        cts.initTableElements(null,
                qiTable,
                listTableElements, null);
        executeCreate(cts.format());
    }

    private TableElement getTableElement(TestColumnDefinition tcd, boolean bNotNull)
            throws SQLException {
        DataType dt = _sf.newDataType();
        if (tcd.getValue() instanceof List<?>) {
            try {
                QualifiedId qiType = new QualifiedId(tcd.getType());
                dt.initStructType(qiType);
            } catch (ParseException pe) {
                throw new SQLException("Type name " + tcd.getType() + " could not be parsed!", pe);
            }
        } else {
            PredefinedType pt = null;
            try {
                pt = _sf.newPredefinedType();
                pt.parse(tcd.getType());
                dt.initPredefinedDataType(pt);
            } catch (IllegalArgumentException iae) {
            }
            if (pt.getType() == null) {
                try {
                    QualifiedId qiType = new QualifiedId(tcd.getType());
                    dt.initStructType(qiType);
                } catch (ParseException pe) {
                    throw new SQLException("Type name " + tcd.getType() + " could not be parsed!", pe);
                }
            }
        }
        ColumnDefinition cd = _sf.newColumnDefinition();
        if (bNotNull) {
            ColumnConstraintDefinition ccd = cd.getColumnConstraintDefinition();
            if (ccd == null) {
                ccd = _sf.newColumnConstraintDefinition();
                cd.setColumnConstraintDefinition(ccd);
            }
            ccd.setType(ColumnConstraintType.NOT_NULL);
        }
        cd.initNameType(new Identifier(tcd.getName()), dt);
        TableElement te = _sf.newTableElement();
        te.initColumnDefinition(cd);
        return te;
    }

    private TableElement getPrimaryTableElement(String sPkName, List<String> listPrimary) {
        IdList ilPrimary = new IdList();
        for (String s : listPrimary) ilPrimary.get().add(s);
        TableConstraintDefinition tcd = _sf.newTableConstraintDefinition();
        tcd.initPrimaryKey(new QualifiedId(null, null, sPkName), ilPrimary);
        TableElement te = _sf.newTableElement();
        te.initTableConstraintDefinition(tcd);
        return te;
    }

    private TableElement getForeignTableElement(String sFkName, List<String> listForeign,
                                                QualifiedId qiTableReferenced, List<String> listReferenced) {
        IdList ilForeign = new IdList();
        for (String s : listForeign) ilForeign.get().add(s);
        IdList ilReferenced = new IdList();
        for (String s : listReferenced) ilReferenced.get().add(s);
        TableConstraintDefinition tcd = _sf.newTableConstraintDefinition();
        tcd.initialize(new QualifiedId(null, null, sFkName), TableConstraintType.FOREIGN_KEY,
                ilForeign, qiTableReferenced, ilReferenced, null, ReferentialAction.CASCADE,
                null, null, null, null);
        TableElement te = _sf.newTableElement();
        te.initTableConstraintDefinition(tcd);
        return te;
    }

    private void createView(QualifiedId qiView,
                            List<TestColumnDefinition> listCd, QualifiedId qiTable)
            throws SQLException {
        TablePrimary tp = _sf.newTablePrimary();
        tp.initialize(qiTable, new Identifier(), new ArrayList<>(),
                null, false, null, false, false, null, null, false, false);
        TableReference tr = _sf.newTableReference();
        tr.initialize(tp, null, null, null, null, null, new ArrayList<>(), null, null);
        List<TableReference> listTableReferences = new ArrayList<>();
        listTableReferences.add(tr);
        QuerySpecification qs = _sf.newQuerySpecification();
        qs.initialize(true, new ArrayList<>(), listTableReferences, null);
        QueryExpressionBody qeb = _sf.newQueryExpressionBody();
        qeb.initialize(null, null, null, null, false, new ArrayList<>(), null,
                qs, new QualifiedId(), new ArrayList<>());
        QueryExpression qe = _sf.newQueryExpression();
        qe.initialize(false, new ArrayList<>(), qeb);
        IdList ilColumnNames = new IdList();
        for (TestColumnDefinition tcd : listCd) {
            ilColumnNames.get().add(tcd.getName());
        }
        CreateViewStatement cvs = _sf.newCreateViewStatement();
        cvs.initialize(false, qiView,
                ilColumnNames, new QualifiedId(), new ArrayList<>(),
                new QualifiedId(), qe, false, null);
        executeCreate(cvs.format());
    }

    private void insertTables()
            throws SQLException {
        insertTable(getQualifiedSimpleTable(), _listCdSimple);
        insertTable(getQualifiedComplexTable(), _listCdComplex);
    }

    private void insertTable(QualifiedId qiTable, List<TestColumnDefinition> listCd)
            throws SQLException {
        IdList ilColumns = new IdList();
        List<AssignedRow> listValues = new ArrayList<>();
        AssignedRow ar = _sf.newAssignedRow();
        listValues.add(ar);
        List<Object> listLobs = new ArrayList<>();
        for (TestColumnDefinition tcd : listCd) {
            ilColumns.get().add(tcd.getName());
            ar.getUpdateSources().add(getUpdateSource(tcd, listLobs));
        }
        InsertStatement is = _sf.newInsertStatement();
        is.initialize(qiTable, ilColumns, listValues,
                null, null, false);
        PreparedStatement pstmt = _conn.prepareStatement(is.format());
        for (int iLob = 0; iLob < listLobs.size(); iLob++) {
            Object o = listLobs.get(iLob);
            if (o instanceof String) {
                Reader rdrClob = new StringReader((String) o);
                pstmt.setCharacterStream(iLob + 1, rdrClob);
            } else if (o instanceof byte[]) {
                InputStream isBlob = new ByteArrayInputStream((byte[]) o);
                pstmt.setBinaryStream(iLob + 1, isBlob);
            } else
                throw new SQLException("Invalid LOB type " + o.getClass().getName() + "!");
        }
        int iResult = pstmt.executeUpdate();
        if (iResult != 1)
            throw new SQLException("Insert into table " + qiTable.format() + " failed!");
        pstmt.close();
        _conn.commit();
    }

    private UpdateSource getUpdateSource(TestColumnDefinition tcd,
                                         List<Object> listLobs)
            throws SQLException {
        ValueExpression ve = getValueExpression(tcd, listLobs);
        UpdateSource us = _sf.newUpdateSource();
        us.initialize(ve, null);
        return us;
    }

    private ValueExpression getValueExpression(TestColumnDefinition tcd,
                                               List<Object> listLobs)
            throws SQLException {
        ValueExpression ve = _sf.newValueExpression();
        CommonValueExpression cve = null;
        Object o = tcd.getValue();
        if ((o instanceof String) ||
                (o instanceof byte[]) ||
                (o instanceof Boolean) ||
                (o instanceof Date) ||
                (o instanceof Time) ||
                (o instanceof Timestamp) ||
                (o instanceof Interval))
            cve = getCommonValueExpressionLiteral();
        else if ((o instanceof BigDecimal) ||
                (o instanceof BigInteger) ||
                (o instanceof Short) ||
                (o instanceof Integer) ||
                (o instanceof Long) ||
                (o instanceof Float) ||
                (o instanceof Double))
            cve = getCommonValueExpressionNumeric();
        else if (o instanceof List<?>)
            cve = getCommonValueExpression();

        if (o instanceof String) {
            String s = (String) o;
            if (s.length() < 256)
                cve.getValueExpressionPrimary().getUnsignedLit().
                        initCharacterString(s);
            else {
                cve = getCommonValueExpressionDynamic();
                listLobs.add(tcd.getValue());
            }
        } else if (o instanceof byte[]) {
            byte[] buf = (byte[]) o;
            if (buf.length < 256)
                cve.getValueExpressionPrimary().getUnsignedLit().
                        initBytes(buf);
            else {
                cve = getCommonValueExpressionDynamic();
                listLobs.add(tcd.getValue());
            }
        } else if (o instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) o;
            cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
                    initBigDecimal(bd.abs());
            if (bd.signum() < 0)
                cve.getNumericValueExpression().
                        setSign(Sign.MINUS_SIGN);
        } else if (o instanceof BigInteger) {
            BigInteger bi = (BigInteger) o;
            BigDecimal bd = new BigDecimal(bi);
            cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
                    initBigDecimal(bd.abs());
            if (bd.signum() < 0)
                cve.getNumericValueExpression().
                        setSign(Sign.MINUS_SIGN);
        } else if (o instanceof Short) {
            short sh = (Short) o;
            cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
                    initInteger(Math.abs(sh));
            if (sh < 0)
                cve.getNumericValueExpression().
                        setSign(Sign.MINUS_SIGN);
        } else if (o instanceof Integer) {
            int i = (Integer) o;
            cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
                    initInteger(Math.abs(i));
            if (i < 0)
                cve.getNumericValueExpression().
                        setSign(Sign.MINUS_SIGN);
        } else if (o instanceof Long) {
            long l = (Long) o;
            cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
                    initLong(Math.abs(l));
            if (l < 0)
                cve.getNumericValueExpression().
                        setSign(Sign.MINUS_SIGN);
        } else if (o instanceof Float) {
            double d = ((Float) o).doubleValue();
            cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
                    initDouble(Math.abs(d));
            if (d < 0)
                cve.getNumericValueExpression().
                        setSign(Sign.MINUS_SIGN);
        } else if (o instanceof Double) {
            double d = (Double) o;
            cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
                    initDouble(Math.abs(d));
            if (d < 0)
                cve.getNumericValueExpression().
                        setSign(Sign.MINUS_SIGN);
        } else if (o instanceof Boolean) {
            boolean b = (Boolean) o;
            cve.getValueExpressionPrimary().getUnsignedLit().
                    initBoolean(b ? BooleanLiteral.TRUE : BooleanLiteral.FALSE);
        } else if (o instanceof Date) {
            cve.getValueExpressionPrimary().getUnsignedLit().
                    initDate((Date) o);
        } else if (o instanceof Time) {
            cve.getValueExpressionPrimary().getUnsignedLit().
                    initTime((Time) o);
        } else if (o instanceof Timestamp) {
            cve.getValueExpressionPrimary().getUnsignedLit().
                    initTimestamp((Timestamp) o);
        } else if (o instanceof Interval) {
            Interval iv = (Interval) o;
            cve.getValueExpressionPrimary().getUnsignedLit().initInterval(iv);
        } else if (o instanceof List<?>) {
            try {
                QualifiedId qiType = new QualifiedId(tcd.getType());
                @SuppressWarnings("unchecked")
                List<TestColumnDefinition> listAd = (List<TestColumnDefinition>) o;
                List<SqlArgument> listAttributeValues = new ArrayList<>();
                for (TestColumnDefinition tcdAttribute : listAd) {
                    listAttributeValues.add(getAttributeValue(tcdAttribute, listLobs));
                }
                cve.getValueExpressionPrimary().
                        initUdtValueConstructor(qiType, listAttributeValues);
            } catch (ParseException pe) {
                throw new SQLException("Type name " + tcd.getType() + " could not be parsed!", pe);
            }
        } else
            throw new SQLException("Unexpected value type " + o.getClass().getName() + "!");
        ve.initialize(cve, null, null);
        return ve;
    }

    private CommonValueExpression getCommonValueExpressionLiteral() {
        UnsignedLiteral ul = _sf.newUnsignedLiteral();
        ValueExpressionPrimary vep = _sf.newValueExpressionPrimary();
        vep.initUnsignedLiteral(ul);
        CommonValueExpression cve = _sf.newCommonValueExpression();
        cve.initialize(null, null, null, null, null, null, vep);
        return cve;
    }

    private CommonValueExpression getCommonValueExpressionNumeric() {
        UnsignedLiteral ul = _sf.newUnsignedLiteral();
        ValueExpressionPrimary vep = _sf.newValueExpressionPrimary();
        vep.initUnsignedLiteral(ul);
        NumericValueExpression nve = _sf.newNumericValueExpression();
        nve.initialize(null, null, null, null, null, null, vep, null);
        CommonValueExpression cve = _sf.newCommonValueExpression();
        cve.initialize(nve, null, null, null, null, null, null);
        return cve;
    }

    private CommonValueExpression getCommonValueExpression() {
        ValueExpressionPrimary vep = _sf.newValueExpressionPrimary();
        CommonValueExpression cve = _sf.newCommonValueExpression();
        cve.initialize(null, null, null, null, null, null, vep);
        return cve;
    }

    private CommonValueExpression getCommonValueExpressionDynamic() {
        GeneralValueSpecification gvs = _sf.newGeneralValueSpecification();
        gvs.initialize(new ColonId(), new ColonId(), new IdChain(), GeneralValue.QUESTION_MARK);
        CommonValueExpression cve = getCommonValueExpression();
        cve.getValueExpressionPrimary().setGeneralValueSpecification(gvs);
        return cve;
    }

    private SqlArgument getAttributeValue(TestColumnDefinition tcd,
                                          List<Object> listLobs)
            throws SQLException {
        SqlArgument sa = _sf.newSqlArgument();
        ValueExpression ve = getValueExpression(tcd, listLobs);
        sa.initialize(ve, new QualifiedId(), null);
        return sa;
    }
}
