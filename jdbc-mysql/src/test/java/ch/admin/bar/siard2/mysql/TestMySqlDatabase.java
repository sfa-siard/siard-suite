package ch.admin.bar.siard2.mysql;

import ch.admin.bar.siard2.jdbc.MySqlConnection;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.sqlparser.identifier.SchemaId;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.base.TestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertSame;

public class TestMySqlDatabase {
    public static final String _sTEST_SCHEMA = "TESTMYSQLSCHEMA";
    private static final String _sTEST_TABLE_SIMPLE = "TMYSQLSIMPLE";

    public static QualifiedId getQualifiedSimpleTable() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_TABLE_SIMPLE);
    }

    private static final String _sTEST_TABLE_COMPLEX = "TMYSQLCOMPLEX";

    public static QualifiedId getQualifiedComplexTable() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_TABLE_COMPLEX);
    }

    private static class ColumnDefinition extends TestColumnDefinition {
        @Override
        public String getValueLiteral() {
            String sValueLiteral = "NULL";
            if (_oValue != null) {
                if (_sType.equals("GEOMETRY") ||
                        _sType.equals("POINT") ||
                        _sType.equals("LINESTRING") ||
                        _sType.equals("POLYGON") ||
                        _sType.equals("MULTIPOINT") ||
                        _sType.equals("MULTILINESTRING") ||
                        _sType.equals("MULTIPOLYGON") ||
                        _sType.equals("GEOMETRYCOLLECTION"))
                    sValueLiteral = "ST_GeomFromText(" + super.getValueLiteral() + ")";
                else
                    sValueLiteral = super.getValueLiteral();
            }
            return sValueLiteral;
        }

        public ColumnDefinition(String sName, String sType, Object oValue) {
            super(sName, sType, oValue);
        }
    }

    public static int _iPrimarySimple = -1;
    public static int _iCandidateSimple = -1;

    @SuppressWarnings("deprecation")
    private static List<TestColumnDefinition> getCdSimple() {
        List<TestColumnDefinition> listCdSimple = new ArrayList<>();

        // Numeric Data Types: Integer Types (Exact Values)
        _iPrimarySimple = listCdSimple.size(); // next column will be primary key column
        listCdSimple.add(new ColumnDefinition("CINT", "INT", 1000000));
        listCdSimple.add(new ColumnDefinition("CINT_U", "INT UNSIGNED", 4294967295l));
        listCdSimple.add(new ColumnDefinition("CINTEGER", "INTEGER", -2147483648));
        listCdSimple.add(new ColumnDefinition("CINTEGER_U", "INTEGER UNSIGNED", 4294967295l));
        listCdSimple.add(new ColumnDefinition("CTINYINT", "TINYINT", (short) 100));
        listCdSimple.add(new ColumnDefinition("CTINYINT_U", "TINYINT UNSIGNED", (short) 193));
        listCdSimple.add(new ColumnDefinition("CSMALLINT", "SMALLINT", (short) -32767));
        listCdSimple.add(new ColumnDefinition("CSMALLINT_U", "SMALLINT UNSIGNED", 65535));
        _iCandidateSimple = listCdSimple.size(); // next column will be primary key column
        listCdSimple.add(new ColumnDefinition("CMEDIUMINT", "MEDIUMINT", -8388608));
        listCdSimple.add(new ColumnDefinition("CMEDIUMINT_U", "MEDIUMINT UNSIGNED", 16777215));
        listCdSimple.add(new ColumnDefinition("CBIGINT", "BIGINT", -2147483648L));
        listCdSimple.add(new ColumnDefinition("CBIGINT_U", "BIGINT UNSIGNED", BigInteger.valueOf(4294967295L)));

        // Numeric Data Types: Fixed-Point Types (Exact Values)
        listCdSimple.add(new ColumnDefinition("CNUMERIC_5_2", "NUMERIC(5,2)", BigDecimal.valueOf(12345, 2)));
        listCdSimple.add(new ColumnDefinition("CDECIMAL_15_5", "DECIMAL(15,5)", new BigDecimal("123455679.12345")));

        // Numeric Data Types: Floating-Point Types (Approximate Values)
        listCdSimple.add(new ColumnDefinition("CFLOAT_9_7", "FLOAT(9,7)", Double.valueOf(Math.PI)
                                                                                .floatValue()));
        listCdSimple.add(new ColumnDefinition("CDOUBLE_16_14", "DOUBLE PRECISION(16,14)", Math.E));
        listCdSimple.add(new ColumnDefinition("CREAL_9_7", "REAL(9,7)", Double.valueOf(Math.PI)
                                                                              .floatValue()));

        listCdSimple.add(new ColumnDefinition("CBIT", "BIT", Boolean.TRUE));
        listCdSimple.add(new ColumnDefinition("CBOOL", "BOOL", Boolean.FALSE));

        // Date and Time Types
        listCdSimple.add(new ColumnDefinition("CDATE", "DATE", new Date(2016 - 1900, 10, 30)));
        listCdSimple.add(new ColumnDefinition("CTIME", "TIME", new Time(12, 34, 56)));
        listCdSimple.add(new ColumnDefinition("CDATETIME", "DATETIME", new Timestamp(2016 - 1900, 10, 30, 12, 34, 56, 0)));
        listCdSimple.add(new ColumnDefinition("CTIMESTAMP", "TIMESTAMP", new Timestamp(2016 - 1900, 10, 30, 12, 34, 56, 0)));
        listCdSimple.add(new ColumnDefinition("CYEAR", "YEAR", new Date(2016 - 1900, 0, 1)));

        // CLOB
        listCdSimple.add(new ColumnDefinition("CTINYTEXT", "TINYTEXT", "http://www.google.ch"));
        listCdSimple.add(new ColumnDefinition("CMEDIUMTEXT", "MEDIUMTEXT", "<car brand=\"Audi\">A3</car>"));
        listCdSimple.add(new ColumnDefinition("CTEXT", "TEXT", TestUtils.getString(500)));
        listCdSimple.add(new ColumnDefinition("CLONGTEXT", "LONGTEXT", TestUtils.getString(5000)));

        // BLOB
        listCdSimple.add(new ColumnDefinition("CTINYBLOB", "TINYBLOB", TestUtils.getBytes(5)));
        listCdSimple.add(new ColumnDefinition("CMEDIUMBLOB", "MEDIUMBLOB", TestUtils.getBytes(50)));
        listCdSimple.add(new ColumnDefinition("CBLOB", "BLOB", TestUtils.getBytes(500)));
        listCdSimple.add(new ColumnDefinition("CLONGBLOB", "LONGBLOB", TestUtils.getBytes(5000)));

        // CHAR/VARCHAR
        listCdSimple.add(new ColumnDefinition("CCHAR_4", "CHAR(4)", TestUtils.getString(3)));
        listCdSimple.add(new ColumnDefinition("CVARCHAR_500", "VARCHAR(500)", TestUtils.getString(255)));
        listCdSimple.add(new ColumnDefinition("CLONG_VARCHAR", "LONG VARCHAR", TestUtils.getString(5000)));

        // NCHAR/NVARCHAR
        listCdSimple.add(new ColumnDefinition("CNCHAR_50", "NCHAR(50)", TestUtils.getString(45)));
        listCdSimple.add(new ColumnDefinition("CNVARCHAR_20000", "NVARCHAR(20000)", TestUtils.getNString(5000)));

        // BINARY/VARBINARY
        listCdSimple.add(new ColumnDefinition("CBINARY_255", "BINARY(255)", TestUtils.getBytes(100)));
        listCdSimple.add(new ColumnDefinition("CVARBINARY_255", "VARBINARY(255)", TestUtils.getBytes(100)));
        listCdSimple.add(new ColumnDefinition("CLONG_VARBINARY", "LONG VARBINARY", TestUtils.getBytes(5000)));
        return listCdSimple;
    }

    public static List<TestColumnDefinition> _listCdSimple = getCdSimple();

    public static int _iPrimaryComplex = -1;

    private static List<TestColumnDefinition> getCdComplex() {
        List<TestColumnDefinition> listCdComplex = new ArrayList<>();

        _iPrimaryComplex = listCdComplex.size(); // next column will be primary key column
        listCdComplex.add(new ColumnDefinition("CID", "INTEGER", 1));
        /* spatial */
        listCdComplex.add(new ColumnDefinition("CGEOMETRY", "GEOMETRY", "POINT (1 2)"));
        listCdComplex.add(new ColumnDefinition("CPOINT", "POINT", "POINT (1 2)"));
        listCdComplex.add(new ColumnDefinition("CLINESTRING", "LINESTRING", "LINESTRING (0 0, 1 1, 2 2)"));
        listCdComplex.add(new ColumnDefinition("CPOLYGON", "POLYGON", "POLYGON ((0 0, 10 0, 10 10, 0 10, 0 0), (5 5, 7 5, 7 7, 5 7, 5 5))"));
        listCdComplex.add(new ColumnDefinition("CMULTIPOINT", "MULTIPOINT", "MULTIPOINT (1 1, 2 2, 3 3)"));
        listCdComplex.add(new ColumnDefinition("CMULTILINESTRING", "MULTILINESTRING", "MULTILINESTRING ((10 10, 20 20), (15 15, 30 15))"));
        listCdComplex.add(new ColumnDefinition("CMULTIPOLYGON", "MULTIPOLYGON", "MULTIPOLYGON (((0 0, 10 0, 10 10, 0 10, 0 0)), ((5 5, 7 5, 7 7, 5 7, 5 5)))"));
        listCdComplex.add(new ColumnDefinition("CGEOMETRYCOLLECTION", "GEOMETRYCOLLECTION", "GEOMETRYCOLLECTION (POINT (1 1), LINESTRING (0 0, 1 1, 2 2, 3 3, 4 4))"));
        /* complex types */
        listCdComplex.add(new ColumnDefinition("CENUM", "ENUM('x-small', 'small', 'medium', 'large', 'x-large')", "small"));
        listCdComplex.add(new ColumnDefinition("CSET", "SET('a', 'b', 'c', 'd')", "a,c"));
        return listCdComplex;
    }

    public static List<TestColumnDefinition> _listCdComplex = getCdComplex();

    private Connection _conn;

    public static void grantSchemaUser(Connection conn, String sSchema,
                                       String sUser) throws SQLException {
        StringBuilder sb = new StringBuilder("GRANT ALL ON ");
        sb.append(SqlLiterals.formatId(sSchema));
        sb.append(".* TO ");
        sb.append(SqlLiterals.formatStringLiteral(sUser));
        sb.append("@");
        sb.append(SqlLiterals.formatStringLiteral("%"));
        Statement stmt = conn.createStatement();
        stmt.unwrap(Statement.class)
            .executeUpdate(sb.toString());
    }

    public static void revokeSchemaUser(Connection conn, String sSchema,
                                        String sUser) throws SQLException {
        StringBuilder sb = new StringBuilder("REVOKE ALL ON ");
        sb.append(SqlLiterals.formatId(sSchema));
        sb.append(".* FROM ");
        sb.append(SqlLiterals.formatStringLiteral(sUser));
        sb.append("@");
        sb.append(SqlLiterals.formatStringLiteral("%"));
        Statement stmt = conn.createStatement();
        stmt.unwrap(Statement.class)
            .executeUpdate(sb.toString());
        conn.commit();
    }

    public TestMySqlDatabase(MySqlConnection connMySql)
            throws SQLException {
        _conn = connMySql.unwrap(Connection.class);
        _conn.setAutoCommit(false);
        drop();
        create();
    }

    private void drop() {
        deleteTables();
        dropTables();
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
        executeDrop("DELETE FROM " + qiTable.format());
    } /* deleteTable */

    private void dropTables() {
        dropTable(getQualifiedSimpleTable());
        dropTable(getQualifiedComplexTable());
    }

    private void dropTable(QualifiedId qiTable) {
        executeDrop("DROP TABLE " + qiTable.format());
    }

    private void dropSchema() {
        executeDrop("DROP SCHEMA " + SqlLiterals.formatId(_sTEST_SCHEMA));
    }

    private void create()
            throws SQLException {
        createSchema();
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
        SchemaId sid = new SchemaId(null, _sTEST_SCHEMA);
        executeCreate("CREATE SCHEMA " + sid.format());
    }

    private void createTables()
            throws SQLException {
        createTable(getQualifiedSimpleTable(), _listCdSimple,
                    Arrays.asList(new String[]{_listCdSimple.get(_iPrimarySimple).getName()}),
                    Arrays.asList(new String[]{_listCdSimple.get(_iCandidateSimple).getName()}));
        createTable(getQualifiedComplexTable(), _listCdComplex,
                    Arrays.asList(new String[]{_listCdComplex.get(_iPrimaryComplex).getName()}),
                    null);
    }

    private void createTable(QualifiedId qiTable, List<TestColumnDefinition> listCd,
                             List<String> listPrimary, List<String> listUnique)
            throws SQLException {
        StringBuilder sbSql = new StringBuilder("CREATE TABLE ");
        sbSql.append(qiTable.format());
        sbSql.append("\r\n(\r\n  ");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            TestColumnDefinition tcd = listCd.get(iColumn);
            if (iColumn > 0)
                sbSql.append(",\r\n  ");
            sbSql.append(tcd.getName());
            sbSql.append(" ");
            sbSql.append(tcd.getType());
        }
        if (listPrimary != null) {
            sbSql.append(",\r\n  CONSTRAINT PK");
            sbSql.append(qiTable.getName());
            sbSql.append(" PRIMARY KEY(");
            sbSql.append(SqlLiterals.formatIdentifierCommaList(listPrimary));
            sbSql.append(")");
        }
        if (listUnique != null) {
            sbSql.append(",\r\n  CONSTRAINT UK");
            sbSql.append(qiTable.getName());
            sbSql.append(" UNIQUE(");
            sbSql.append(SqlLiterals.formatIdentifierCommaList(listUnique));
            sbSql.append(")");
        }
        sbSql.append("\r\n)");
        executeCreate(sbSql.toString());
    }

    private void insertTables()
            throws SQLException {
        insertTable(getQualifiedSimpleTable(), _listCdSimple);
        insertTable(getQualifiedComplexTable(), _listCdComplex);
    }

    private void insertTable(QualifiedId qiTable, List<TestColumnDefinition> listCd)
            throws SQLException {
        StringBuilder sbSql = new StringBuilder("INSERT INTO ");
        sbSql.append(qiTable.format());
        sbSql.append("\r\n(\r\n  ");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            TestColumnDefinition tcd = listCd.get(iColumn);
            if (tcd.getValue() != null) {
                if (iColumn > 0)
                    sbSql.append(",\r\n  ");
                sbSql.append(tcd.getName());
            }
        }
        sbSql.append("\r\n)\r\nVALUES\r\n(\r\n  ");
        List<Object> listLobs = new ArrayList<>();
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            TestColumnDefinition tcd = listCd.get(iColumn);
            if (tcd.getValue() != null) {
                if (iColumn > 0)
                    sbSql.append(",\r\n  ");
                String sLiteral = tcd.getValueLiteral();
                if (sLiteral.length() < 1000)
                    sbSql.append(sLiteral);
                else {
                    sbSql.append("?");
                    listLobs.add(tcd.getValue());
                }
            }
        }
        sbSql.append("\r\n)");
        PreparedStatement pstmt = _conn.prepareStatement(sbSql.toString());
        for (int iLob = 0; iLob < listLobs.size(); iLob++) {
            Object o = listLobs.get(iLob);
            if (o instanceof String) {
                Reader rdrClob = new StringReader((String) o);
                pstmt.setCharacterStream(iLob + 1, rdrClob);

            } else if (o instanceof byte[]) {
                InputStream isBlob = new ByteArrayInputStream((byte[]) o);
                pstmt.setBinaryStream(iLob + 1, isBlob);
            } else
                throw new SQLException("Invalid LOB type " + o.getClass()
                                                              .getName() + "!");
        }
        int iResult = pstmt.executeUpdate();
        assertSame("Insert failed!", 1, iResult);
        pstmt.close();
        _conn.commit();
    }
}
