package ch.admin.bar.siard2.oracle.legacy;

import static org.junit.Assert.*;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.oracle.OracleLiterals;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;

public class TestOracleDatabase {
    private static final OracleConnectionProperties _ocp = new OracleConnectionProperties();

    public static final String _sTEST_SCHEMA = "TESTORACLEUSER";
    public static final String _sTEST_TABLE_SIMPLE = "TORACLESIMPLE";

    public static QualifiedId getQualifiedSimpleTable() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_TABLE_SIMPLE);
    }

    public static final String _sTEST_TABLE_COMPLEX = "TORACLECOMPLEX";

    public static QualifiedId getQualifiedComplexTable() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_TABLE_COMPLEX);
    }

    public static final String _sUDT_VARRAY = "TESTVARRAYTYPE";

    public static QualifiedId getQualifiedArrayType() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sUDT_VARRAY);
    }

    public static final String _sUDT_OBJECT = "TESTOBJECTTYPE";

    public static QualifiedId getQualifiedObjectType() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sUDT_OBJECT);
    }

    public static final String _sUDT_COMPLEX = "TESTCOMPLEXTYPE";

    public static QualifiedId getQualifiedComplexType() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sUDT_COMPLEX);
    }

    public static final String _sDIRECTORY = "oradata";
    /* directory on Oracle server */
    public static final String _sBFILE_DIRECTORY = _ocp.getBFileDirectory();
    /* directory on local machine (mounted share) */
    public static final String _sBFILE_SHARE = _ocp.getBFileShare();
    /* directory on local machine (mounted share) */
    public static final String _sBFILE_NAME = _ocp.getBFileFilename();

    private static class ColumnDefinition extends TestColumnDefinition {
        @Override
        public String getValueLiteral() {
            String sValueLiteral = "NULL";
            if (_oValue != null) {
                if (_sType.equals("HTTPURITYPE"))
                    sValueLiteral = "NEW SYS.HTTPURITYPE(" + OracleLiterals.formatStringLiteral((String) _oValue) + ")";
                else if (_sType.equals("BFILE"))
                    sValueLiteral = "BFILENAME(" + OracleLiterals.formatStringLiteral(_sDIRECTORY) + "," + OracleLiterals.formatStringLiteral((String) _oValue) + ")";
                else if (_oValue instanceof byte[]) sValueLiteral = OracleLiterals.formatBytesLiteral((byte[]) _oValue);
                else if (_oValue instanceof String)
                    sValueLiteral = OracleLiterals.formatStringLiteral((String) _oValue);
                else if (_oValue instanceof Short) sValueLiteral = String.valueOf(((Short) _oValue).shortValue());
                else if (_oValue instanceof Integer) sValueLiteral = String.valueOf(((Integer) _oValue).intValue());
                else if (_oValue instanceof Long) sValueLiteral = String.valueOf(((Long) _oValue).longValue());
                else if (_oValue instanceof Boolean) sValueLiteral = String.valueOf(((Boolean) _oValue) ? 1 : 0);
                else if (_oValue instanceof BigDecimal)
                    sValueLiteral = OracleLiterals.formatExactLiteral((BigDecimal) _oValue);
                else if (_oValue instanceof Double)
                    sValueLiteral = OracleLiterals.formatApproximateLiteral(((Double) _oValue).doubleValue());
                else if (_oValue instanceof Date) sValueLiteral = OracleLiterals.formatDateLiteral((Date) _oValue);
                else if (_oValue instanceof Time) sValueLiteral = OracleLiterals.formatTimeLiteral((Time) _oValue);
                else if (_oValue instanceof Timestamp)
                    sValueLiteral = OracleLiterals.formatTimestampLiteral((Timestamp) _oValue);
                else if (_oValue instanceof List<?>) {
                    @SuppressWarnings("unchecked") List<ColumnDefinition> listCd = (List<ColumnDefinition>) _oValue;
                    StringBuilder sbStructValue = new StringBuilder(getType() + "()");
                    sbStructValue.append(_sType);
                    sbStructValue.append("(");
                    for (int iAttr = 0; iAttr < listCd.size(); iAttr++) {
                        ColumnDefinition cdElement = listCd.get(iAttr);
                        if (iAttr > 0) sbStructValue.append(",");
                        sbStructValue.append(cdElement.getValueLiteral());
                    }
                    sbStructValue.append(")");
                    sValueLiteral = sbStructValue.toString();
                } else sValueLiteral = super.getValueLiteral();
            }
            return sValueLiteral;
        }

        public ColumnDefinition(String sName, String sType, Object oValue) {
            super(sName, sType, oValue);
        }
    } /* ColumnDefinition */

    /* column definitions */
    public static int _iPrimarySimple = -1;
    public static int _iCandidateSimple = -1;

    @SuppressWarnings("deprecation")
    private static List<TestColumnDefinition> getCdSimple() {
        List<TestColumnDefinition> listCdSimple = new ArrayList<TestColumnDefinition>();
        /* binary */
        listCdSimple.add(new ColumnDefinition("CRAW_255", "RAW(255)", TestUtils.getBytes(200)));
        listCdSimple.add(new ColumnDefinition("CBLOB", "BLOB", TestUtils.getBytes(500)));
        if (_sBFILE_DIRECTORY != null) listCdSimple.add(new ColumnDefinition("CBFILE", "BFILE", _sBFILE_NAME));
        /* character */
        listCdSimple.add(new ColumnDefinition("CCHAR_50", "CHAR(50)", TestUtils.getString(40)));
        listCdSimple.add(new ColumnDefinition("CVARCHAR2_255", "VARCHAR2(255)", TestUtils.getString(200)));
        listCdSimple.add(new ColumnDefinition("CLONG", "LONG", TestUtils.getString(500)));
        listCdSimple.add(new ColumnDefinition("CCLOB", "CLOB", TestUtils.getString(500)));
        listCdSimple.add(new ColumnDefinition("CNCHAR_50", "NCHAR(50)", TestUtils.getString(40)));
        listCdSimple.add(new ColumnDefinition("CNVARCHAR2_255", "NVARCHAR2(255)", TestUtils.getString(200)));
        listCdSimple.add(new ColumnDefinition("CNCLOB", "NCLOB", TestUtils.getString(500)));
        listCdSimple.add(new ColumnDefinition("CURITYPE", "HTTPURITYPE", "http://www.oracle.com"));
        listCdSimple.add(new ColumnDefinition("CXMLTYPE", "XMLTYPE", "<people><person>A. Einstein</person></people>"));
        _iCandidateSimple = listCdSimple.size(); // next column will be unique key column
        listCdSimple.add(new ColumnDefinition("CSMALLINT", "SMALLINT", Short.valueOf((short) 12345)));
        _iPrimarySimple = listCdSimple.size(); // next column will be primary key column
        listCdSimple.add(new ColumnDefinition("CINTEGER", "INT", Integer.valueOf(1234567890)));
        listCdSimple.add(new ColumnDefinition("CNUMBER_15_5", "NUMBER(15,5)", BigDecimal.valueOf(12345678901l, 5)));
        /* approximate */
        listCdSimple.add(new ColumnDefinition("CFLOAT", "FLOAT(15)", Double.valueOf(2.7183)));
        listCdSimple.add(new ColumnDefinition("CBINARY_FLOAT", "BINARY_FLOAT", Float.valueOf(2.71828182f)));
        listCdSimple.add(new ColumnDefinition("CBINARY_DOUBLE", "BINARY_DOUBLE", Double.valueOf(2.71828182845904)));
        /* boolean */
        listCdSimple.add(new ColumnDefinition("CBOOLEAN", "NUMBER(1,0)", Boolean.TRUE));
        /* date and time */
        listCdSimple.add(new ColumnDefinition("CDATE", "DATE", new Date(2016 - 1900, 6, 21)));
        listCdSimple.add(new ColumnDefinition("CTIMESTAMP", "TIMESTAMP(6)", new Timestamp(2014 - 1900, 6, 21, 11, 4, 30, 123456000)));
        listCdSimple.add(new ColumnDefinition("CTIMESTAMP_TZ", "TIMESTAMP(6) WITH TIME ZONE", new Timestamp(2015 - 1900, 6, 21, 11, 4, 30, 123456000)));
        listCdSimple.add(new ColumnDefinition("CTIMESTAMP_LOCAL_TZ", "TIMESTAMP(6) WITH LOCAL TIME ZONE", new Timestamp(2016 - 1900, 6, 21, 11, 4, 30, 123456000)));
        listCdSimple.add(new ColumnDefinition("CINTERVAL_YEAR_TO_MONTH", "INTERVAL YEAR(2) TO MONTH", new Interval(1, 15, 2)));
        listCdSimple.add(new ColumnDefinition("CINTERVAL_DAY_TO_SECOND", "INTERVAL DAY(2) TO SECOND(6)", new Interval(1, 2, 3, 4, 5, 123456789l)));
        return listCdSimple;
    }

    public static List<TestColumnDefinition> _listCdSimple = getCdSimple();

    private static List<TestColumnDefinition> getListCdArray() {
        List<TestColumnDefinition> listCd = new ArrayList<TestColumnDefinition>();
        listCd.add(new ColumnDefinition("CARRAY_4[1]", "CHAR(3)", "CHF"));
        listCd.add(new ColumnDefinition("CARRAY_4[2]", "CHAR(3)", null));
        listCd.add(new ColumnDefinition("CARRAY_4[3]", "CHAR(3)", "EUR"));
        listCd.add(new ColumnDefinition("CARRAY_4[4]", "CHAR(3)", null));
        return listCd;
    }

    public static List<TestColumnDefinition> _listCdArray = getListCdArray();

    private static List<TestColumnDefinition> getListAdObject() {
        List<TestColumnDefinition> listCd = new ArrayList<TestColumnDefinition>();
        listCd.add(new ColumnDefinition("LABEL", "VARCHAR(255)", "PI"));
        listCd.add(new ColumnDefinition("VALUE", "BINARY_DOUBLE", Double.valueOf(Math.PI)));
        return listCd;
    }

    public static List<TestColumnDefinition> _listAdObject = getListAdObject();

    private static List<TestColumnDefinition> getListAdComplex() {
        List<TestColumnDefinition> listCd = new ArrayList<TestColumnDefinition>();
        listCd.add(new ColumnDefinition("LABEL", "VARCHAR(255)", "Complex"));
        listCd.add(new ColumnDefinition("LARGE", "BLOB", TestUtils.getBytes(1000)));
        listCd.add(new ColumnDefinition("OBJ", getQualifiedObjectType().format(), _listAdObject));
        return listCd;
    }

    public static List<TestColumnDefinition> _listAdComplex = getListAdComplex();

    private static int _iPrimaryComplex = -1;

    private static List<TestColumnDefinition> getListCdComplex() {
        List<TestColumnDefinition> listCdComplex = new ArrayList<TestColumnDefinition>();
        _iPrimaryComplex = listCdComplex.size(); // next column will be primary key column
        listCdComplex.add(new ColumnDefinition("CID", "INT", Integer.valueOf(987654321)));
        listCdComplex.add(new ColumnDefinition("CUDT_VARRAY", getQualifiedArrayType().format(), _listCdArray));
        listCdComplex.add(new ColumnDefinition("CUDT_OBJECT", getQualifiedObjectType().format(), _listAdObject));
        listCdComplex.add(new ColumnDefinition("CUDT_COMPLEX", getQualifiedComplexType().format(), _listAdComplex));
        return listCdComplex;
    }

    public static List<TestColumnDefinition> _listCdComplex = getListCdComplex();

    private Connection _conn = null;

    /*------------------------------------------------------------------*/
    public static void createUser(Connection conn, String sTestUser, String sTestPassword) throws SQLException {
        String sSql = "CREATE USER " + sTestUser + " IDENTIFIED BY " + sTestPassword + " QUOTA UNLIMITED ON USERS";
        Statement stmt = conn.createStatement();
        int iResult = stmt.executeUpdate(sSql);
        assertSame("User creation failed!", 0, iResult);
        sSql = "GRANT CONNECT, RESOURCE TO " + sTestUser;
        iResult = stmt.executeUpdate(sSql);
        assertSame("User grant failed!", 0, iResult);
        stmt.close();
        if (!conn.getAutoCommit()) conn.commit();
    } /* createUser */

    /*------------------------------------------------------------------*/
    public static void dropUser(Connection conn, String sTestUser) throws SQLException {
        Statement stmt = conn.createStatement()
                             .unwrap(Statement.class);
        String sSql = "DROP USER " + sTestUser + " CASCADE";
        int iResult = stmt.executeUpdate(sSql);
        assertSame("User drop failed!", 0, iResult);
        stmt.close();
        if (!conn.getAutoCommit()) conn.commit();
    } /* dropUser */

    /*------------------------------------------------------------------*/
    public static void grantSchema(Connection conn, String sSchema, String sTestUser) throws SQLException {
        Statement stmt = conn.createStatement()
                             .unwrap(Statement.class);
        Map<QualifiedId, String> mapObjectPrivileges = new HashMap<QualifiedId, String>();
        String sSql = "SELECT * FROM ALL_OBJECTS WHERE OWNER = '" + sSchema + "'";
        ResultSet rs = stmt.executeQuery(sSql);
        while (rs.next()) {
            String sObject = rs.getString("OBJECT_NAME");
            QualifiedId qi = new QualifiedId(null, sSchema, sObject);
            String sObjectType = rs.getString("OBJECT_TYPE");
            String sPrivileges = null;
            if (sObjectType.equals("TABLE") || sObjectType.equals("VIEW") || sObjectType.equals("TYPE"))
                sPrivileges = "ALL PRIVILEGES";
            else if (sObjectType.equals("PROCEDURE") || sObjectType.equals("FUNCTION") || sObjectType.equals("PACKAGE"))
                sPrivileges = "EXECUTE";
            else if (sObjectType.equals("SEQUENCE")) sPrivileges = "ALTER";
            if (sPrivileges != null) mapObjectPrivileges.put(qi, sPrivileges);
        }
        rs.close();
        for (Iterator<QualifiedId> iterObject = mapObjectPrivileges.keySet()
                                                                   .iterator(); iterObject.hasNext(); ) {
            QualifiedId qiObject = iterObject.next();
            String sPrivileges = mapObjectPrivileges.get(qiObject);
            sSql = "GRANT " + sPrivileges + " ON " + qiObject.format() + " TO " + sTestUser;
            int iReturn = stmt.executeUpdate(sSql);
            if (iReturn != 0) throw new SQLException("Grant on " + qiObject.format() + " failed!");
        }
        /* DIRECTORY entries are not part of a schema but belong all to SYS
         * nevertheless we need access to out BFILEs! */
        if (_sBFILE_DIRECTORY != null) {
            sSql = "GRANT READ, WRITE ON DIRECTORY " + SqlLiterals.formatId(_sDIRECTORY) + " TO " + sTestUser;
            int iReturn = stmt.executeUpdate(sSql);
            if (iReturn != 0)
                throw new SQLException("Grant on DIRECTORY " + SqlLiterals.formatId(_sDIRECTORY) + " failed!");
        }
        stmt.close();
        if (!conn.getAutoCommit()) conn.commit();
    } /* grantSchema */

    /*------------------------------------------------------------------*/
    public static void grantReadSchema(Connection conn, String sSchema, String sTestUser) throws SQLException {
        Statement stmt = conn.createStatement()
                             .unwrap(Statement.class);
        Map<QualifiedId, String> mapObjectPrivileges = new HashMap<QualifiedId, String>();
        String sSql = "SELECT * FROM ALL_OBJECTS WHERE OWNER = '" + sSchema + "'";
        ResultSet rs = stmt.executeQuery(sSql);
        while (rs.next()) {
            String sObject = rs.getString("OBJECT_NAME");
            QualifiedId qi = new QualifiedId(null, sSchema, sObject);
            String sObjectType = rs.getString("OBJECT_TYPE");
            String sPrivileges = null;
            if (sObjectType.equals("TABLE") || sObjectType.equals("VIEW") || sObjectType.equals("TYPE"))
                sPrivileges = "SELECT";
            else if (sObjectType.equals("PROCEDURE") || sObjectType.equals("FUNCTION") || sObjectType.equals("PACKAGE"))
                sPrivileges = "EXECUTE";
            if (sPrivileges != null) mapObjectPrivileges.put(qi, sPrivileges);
        }
        rs.close();
        for (Iterator<QualifiedId> iterObject = mapObjectPrivileges.keySet()
                                                                   .iterator(); iterObject.hasNext(); ) {
            QualifiedId qiObject = iterObject.next();
            String sPrivileges = mapObjectPrivileges.get(qiObject);
            sSql = "GRANT " + sPrivileges + " ON " + qiObject.format() + " TO " + sTestUser;
            int iReturn = stmt.executeUpdate(sSql);
            if (iReturn != 0) throw new SQLException("Grant on " + qiObject.format() + " failed!");
        }
        /* DIRECTORY entries are not part of a schema but belong all to SYS
         * nevertheless we need access to out BFILEs! */
        if (_sBFILE_DIRECTORY != null) {
            sSql = "GRANT READ ON DIRECTORY " + SqlLiterals.formatId(_sDIRECTORY) + " TO " + sTestUser;
            int iReturn = stmt.executeUpdate(sSql);
            if (iReturn != 0)
                throw new SQLException("Grant on DIRECTORY " + SqlLiterals.formatId(_sDIRECTORY) + " failed!");
        }
        stmt.close();
        if (!conn.getAutoCommit()) conn.commit();
    } /* grantReadSchema */

    /*------------------------------------------------------------------*/
    public static void grantReadViews(Connection conn, String sSchema, String sTestUser) throws SQLException {
        Statement stmt = conn.createStatement()
                             .unwrap(Statement.class);
        Map<QualifiedId, String> mapObjectPrivileges = new HashMap<QualifiedId, String>();
        String sSql = "SELECT * FROM ALL_OBJECTS WHERE OWNER = '" + sSchema + "'";
        ResultSet rs = stmt.executeQuery(sSql);
        while (rs.next()) {
            String sObject = rs.getString("OBJECT_NAME");
            QualifiedId qi = new QualifiedId(null, sSchema, sObject);
            String sObjectType = rs.getString("OBJECT_TYPE");
            String sPrivileges = null;
            if (sObjectType.equals("VIEW")) sPrivileges = "SELECT";
            else if (sObjectType.equals("PROCEDURE") || sObjectType.equals("FUNCTION") || sObjectType.equals("PACKAGE"))
                sPrivileges = "EXECUTE";
            if (sPrivileges != null) mapObjectPrivileges.put(qi, sPrivileges);
        }
        rs.close();
        for (Iterator<QualifiedId> iterObject = mapObjectPrivileges.keySet()
                                                                   .iterator(); iterObject.hasNext(); ) {
            QualifiedId qiObject = iterObject.next();
            String sPrivileges = mapObjectPrivileges.get(qiObject);
            sSql = "GRANT " + sPrivileges + " ON " + qiObject.format() + " TO " + sTestUser;
            int iReturn = stmt.executeUpdate(sSql);
            if (iReturn != 0) throw new SQLException("Grant on " + qiObject.format() + " failed!");
        }
        /* DIRECTORY entries are not part of a schema but belong all to SYS
         * nevertheless we need access to out BFILEs! */
        if (_sBFILE_DIRECTORY != null) {
            sSql = "GRANT READ ON DIRECTORY " + SqlLiterals.formatId(_sDIRECTORY) + " TO " + sTestUser;
            int iReturn = stmt.executeUpdate(sSql);
            if (iReturn != 0)
                throw new SQLException("Grant on DIRECTORY " + SqlLiterals.formatId(_sDIRECTORY) + " failed!");
        }
        stmt.close();
        if (!conn.getAutoCommit()) conn.commit();
    } /* grantReadSchema */

    /*------------------------------------------------------------------*/
    public TestOracleDatabase(OracleConnection connOracle) throws SQLException {
        _conn = connOracle.unwrap(Connection.class);
        try {
            _conn.setAutoCommit(false);
        } catch (SQLException se) {
            rollback("Autocommit", se);
        }
        drop();
        create();
    } /* constructor */

    /*------------------------------------------------------------------*/
    private void rollback(String sMessage, SQLException se) {
        System.out.println(sMessage + ": " + EU.getExceptionMessage(se));
        try {
            _conn.rollback();
        } catch (SQLException seRollback) {
            System.err.println("Rollback failed: " + EU.getExceptionMessage(seRollback));
        }
    } /* rollback */

    /*------------------------------------------------------------------*/
    private void drop() throws SQLException {
        deleteTables();
        dropTables();
        dropTypes();
        dropSchema();
        if (_sBFILE_DIRECTORY != null) dropDirectory();
    } /* drop */

    /*------------------------------------------------------------------*/
    private void deleteTables() throws SQLException {
        deleteTable(getQualifiedComplexTable());
        deleteTable(getQualifiedSimpleTable());
    } /* deleteTables */

    /*------------------------------------------------------------------*/
    private void dropTables() throws SQLException {
        dropTable(getQualifiedComplexTable());
        dropTable(getQualifiedSimpleTable());
    } /* dropTables */

    /*------------------------------------------------------------------*/
    private void dropTypes() throws SQLException {
        dropType(getQualifiedComplexType());
        dropType(getQualifiedObjectType());
        dropType(getQualifiedArrayType());
    } /* dropTypes */

    /*------------------------------------------------------------------*/
    private void dropSchema() {
        String sSql = "DROP USER " + _sTEST_SCHEMA + " CASCADE";
        executeDrop(sSql);
    } /* dropSchema */

    /*------------------------------------------------------------------*/
    private void dropDirectory() throws SQLException {
        String sSql = "DROP DIRECTORY " + OracleLiterals.formatId(_sDIRECTORY);
        executeDrop(sSql);
    } /* dropDirectory */

    /*------------------------------------------------------------------*/
    private void deleteTable(QualifiedId qiTable) {
        String sSql = "DELETE FROM " + qiTable.format();
        executeDrop(sSql);
    } /* deleteTable */

    /*------------------------------------------------------------------*/
    private void dropTable(QualifiedId qiTable) {
        String sSql = "DROP TABLE " + qiTable.format() + " CASCADE CONSTRAINTS";
        executeDrop(sSql);
    } /* dropTable */

    /*------------------------------------------------------------------*/
    private void dropType(QualifiedId qiType) {
        String sSql = "DROP TYPE " + qiType.format();
        executeDrop(sSql);
    } /* dropType */

    /*------------------------------------------------------------------*/
    private void executeDrop(String sSql) {
        try {
            Statement stmt = _conn.createStatement();
            stmt.executeUpdate(sSql);
            stmt.close();
            _conn.commit();
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
        }
    } /* executeDrop */

    /*------------------------------------------------------------------*/
    private void create() throws SQLException {
        if (_sBFILE_DIRECTORY != null) createDirectory();
        createSchema();
        createTypes();
        createTables();
        insertTables();
    } /* create */

    /*------------------------------------------------------------------*/
    private void createDirectory() throws SQLException {
        String sSql = "CREATE DIRECTORY " + OracleLiterals.formatId(_sDIRECTORY) + " AS " + OracleLiterals.formatStringLiteral(_sBFILE_DIRECTORY);
        executeCreate(sSql);
    } /* createDirectory */

    /*------------------------------------------------------------------*/
    private void createSchema() throws SQLException {
        String sSql = "CREATE USER " + _sTEST_SCHEMA + " IDENTIFIED BY " + _sTEST_SCHEMA + " QUOTA UNLIMITED ON USERS";
        executeCreate(sSql);
    } /* createSchema */

    /*------------------------------------------------------------------*/
    private void createTypes() throws SQLException {
        createType(getQualifiedArrayType(), _listCdArray);
        createType(getQualifiedObjectType(), _listAdObject);
        createType(getQualifiedComplexType(), _listAdComplex);
    } /* createTypes */

    /*------------------------------------------------------------------*/
    private void createTables() throws SQLException {
        createTable(getQualifiedSimpleTable(), _listCdSimple, Arrays.asList(new String[]{_listCdSimple.get(_iPrimarySimple).getName()}), Arrays.asList(new String[]{_listCdSimple.get(_iCandidateSimple).getName()}));
        createTable(getQualifiedComplexTable(), _listCdComplex, Arrays.asList(new String[]{_listCdComplex.get(_iPrimaryComplex).getName()}), null);
    } /* createTables */

    /*------------------------------------------------------------------*/
    private void insertTables() throws SQLException {
        insertTable(getQualifiedSimpleTable(), _listCdSimple);
        insertTable(getQualifiedComplexTable(), _listCdComplex);
    } /* insertTables */

    /*------------------------------------------------------------------*/
    private void createType(QualifiedId qiType, List<TestColumnDefinition> listAttributes) throws SQLException {
        StringBuilder sbSql = new StringBuilder("CREATE TYPE ");
        sbSql.append(qiType.format());
        sbSql.append(" AS ");
        if (listAttributes == _listCdArray) {
            sbSql.append("VARRAY(");
            sbSql.append(String.valueOf(listAttributes.size()));
            sbSql.append(") OF ");
            sbSql.append(listAttributes.get(0)
                                       .getType());
        } else {
            sbSql.append("OBJECT(");
            for (int iAttribute = 0; iAttribute < listAttributes.size(); iAttribute++) {
                if (iAttribute > 0) sbSql.append(", ");
                sbSql.append("\r\n  ");
                TestColumnDefinition tcd = listAttributes.get(iAttribute);
                sbSql.append(OracleLiterals.formatId(tcd.getName()));
                sbSql.append(" ");
                sbSql.append(tcd.getType());
            }
            sbSql.append("\r\n)");
        }
        executeCreate(sbSql.toString());
    } /* createType */

    /*------------------------------------------------------------------*/
    private void createTable(QualifiedId qiTable, List<TestColumnDefinition> listCd, List<String> listPrimary, List<String> listUnique) throws SQLException {
        StringBuilder sbSql = new StringBuilder("CREATE TABLE ");
        sbSql.append(qiTable.format());
        sbSql.append("(");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",");
            sbSql.append("\r\n  ");
            TestColumnDefinition tcd = listCd.get(iColumn);
            sbSql.append(OracleLiterals.formatId(tcd.getName()));
            sbSql.append(" ");
            sbSql.append(tcd.getType());
        }
        if ((listPrimary != null) && (listPrimary.size() > 0)) {
            sbSql.append(",\r\n  CONSTRAINT ");
            sbSql.append(OracleLiterals.formatId("PK_" + qiTable.getName()));
            sbSql.append(" PRIMARY KEY(");
            for (int i = 0; i < listPrimary.size(); i++) {
                if (i > 0) sbSql.append(", ");
                sbSql.append(listPrimary.get(i));
            }
            sbSql.append(")");
        }
        if ((listUnique != null) && (listUnique.size() > 0)) {
            sbSql.append(",\r\n  CONSTRAINT ");
            sbSql.append(OracleLiterals.formatId("CK_" + qiTable.getName()));
            sbSql.append(" UNIQUE(");
            for (int i = 0; i < listUnique.size(); i++) {
                if (i > 0) sbSql.append(", ");
                sbSql.append(listUnique.get(i));
            }
            sbSql.append(")");
        }
        sbSql.append("\r\n)");
        executeCreate(sbSql.toString());
    } /* createTable */

    /*------------------------------------------------------------------*/
    private String getLiteralValue(TestColumnDefinition tcd, List<TestColumnDefinition> listLobs) {
        StringBuilder sbValue = new StringBuilder();
        if (!(tcd.getValue() instanceof List<?>)) {
            if (tcd.getValueLiteral()
                   .length() <= 2000) sbValue.append(tcd.getValueLiteral());
            else {
                sbValue.append("?");
                listLobs.add(tcd);
            }
        } else {
            @SuppressWarnings("unchecked") List<TestColumnDefinition> listAttributes = (List<TestColumnDefinition>) tcd.getValue();
            sbValue.append(tcd.getType());
            sbValue.append("(");
            for (int i = 0; i < listAttributes.size(); i++) {
                if (i > 0) sbValue.append(",");
                TestColumnDefinition tcdAttribute = listAttributes.get(i);
                sbValue.append(getLiteralValue(tcdAttribute, listLobs));
            }
            sbValue.append(")");
        }
        return sbValue.toString();
    } /* getLiteralValue */

    /*------------------------------------------------------------------*/
    private void insertTable(QualifiedId qiTable, List<TestColumnDefinition> listCd) throws SQLException {
        StringBuilder sbSql = new StringBuilder("INSERT INTO ");
        sbSql.append(qiTable.format());
        sbSql.append("(");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",");
            sbSql.append("\r\n  ");
            TestColumnDefinition tcd = listCd.get(iColumn);
            sbSql.append(OracleLiterals.formatId(tcd.getName()));
        }
        sbSql.append("\r\n) VALUES (");
        List<TestColumnDefinition> listLobs = new ArrayList<TestColumnDefinition>();
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",");
            sbSql.append("\r\n  ");
            TestColumnDefinition tcd = listCd.get(iColumn);
            sbSql.append(getLiteralValue(tcd, listLobs));
        }
        sbSql.append("\r\n)");
        PreparedStatement pstmt = _conn.prepareStatement(sbSql.toString());
        for (int iLob = 0; iLob < listLobs.size(); iLob++) {
            TestColumnDefinition tcd = listLobs.get(iLob);
            if (tcd.getValue() instanceof String) {
                Reader rdrClob = new StringReader((String) tcd.getValue());
                pstmt.setCharacterStream(iLob + 1, rdrClob);
            } else if (tcd.getValue() instanceof byte[]) {
                InputStream isBlob = new ByteArrayInputStream((byte[]) tcd.getValue());
                pstmt.setBinaryStream(iLob + 1, isBlob);
            } else throw new SQLException("Invalid LOB!");
        }
        int iResult = pstmt.executeUpdate();
        if (iResult != 1) throw new SQLException("Insert into table " + qiTable.format() + " failed!");
        pstmt.close();
        _conn.commit();
    } /* insertTable */

    ;

    /*------------------------------------------------------------------*/
    private void executeCreate(String sSql) throws SQLException {
        Statement stmt = _conn.createStatement();
        int iResult = stmt.executeUpdate(sSql);
        if (iResult != 0) throw new SQLException(sSql + " failed!");
        stmt.close();
        _conn.commit();
    } /* executeCreate */

} /* class TestOracleDatabase */
