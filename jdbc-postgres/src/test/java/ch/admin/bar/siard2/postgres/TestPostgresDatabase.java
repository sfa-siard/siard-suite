package ch.admin.bar.siard2.postgres;

import static org.junit.Assert.assertSame;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;

import org.postgresql.*;
import org.postgresql.largeobject.*;

import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.postgres.identifier.*;

public class TestPostgresDatabase {
    public static final String _sTEST_SCHEMA = "TESTPGSCHEMA";
    public static final String _sPUBLIC_SCHEMA = "PUBLIC";
    public static final String _sTEST_TABLE_SIMPLE = "TPGSIMPLE";

    public static PostgresQualifiedId getQualifiedSimpleTable() {
        return new PostgresQualifiedId(null, _sTEST_SCHEMA, _sTEST_TABLE_SIMPLE);
    }

    public static final String _sTEST_TABLE_COMPLEX = "TPGCOMPLEX";

    public static PostgresQualifiedId getQualifiedComplexTable() {
        return new PostgresQualifiedId(null, _sTEST_SCHEMA, _sTEST_TABLE_COMPLEX);
    }

    private static final String _sTEST_INTEGER_DOMAIN = "TYPPGDOMAIN";

    public static PostgresQualifiedId getQualifiedDomainType() {
        return new PostgresQualifiedId(null, _sTEST_SCHEMA, _sTEST_INTEGER_DOMAIN);
    }

    private static final String _sTEST_YEAR_DOMAIN = "year";

    public static PostgresQualifiedId getQualifiedYearType() {
        return new PostgresQualifiedId(null, _sPUBLIC_SCHEMA, _sTEST_YEAR_DOMAIN);
    }

    private static final String _sTEST_BUILTIN_RANGE = "INT4RANGE";

    public static PostgresQualifiedId getQualifiedBuiltinRange() {
        return new PostgresQualifiedId(null, null, _sTEST_BUILTIN_RANGE);
    }

    private static final String _sTEST_TYPE_ENUM = "TYPPGENUM";

    public static PostgresQualifiedId getQualifiedEnumType() {
        return new PostgresQualifiedId(null, _sTEST_SCHEMA, _sTEST_TYPE_ENUM);
    }

    private static final String _sTEST_TYPE_COMP = "TYPPGCOMP";

    public static PostgresQualifiedId getQualifiedCompositeType() {
        return new PostgresQualifiedId(null, _sTEST_SCHEMA, _sTEST_TYPE_COMP);
    }

    private static final String _sTEST_TYPE_RANGE = "TYPPGRANGE";

    public static PostgresQualifiedId getQualifiedRangeType() {
        return new PostgresQualifiedId(null, _sTEST_SCHEMA, _sTEST_TYPE_RANGE);
    }

    private static final String _sTEST_STRING_ARRAY = "TPGARRAY";

    public static PostgresQualifiedId getQualifiedArrayType() {
        return new PostgresQualifiedId(null, _sTEST_SCHEMA, _sTEST_STRING_ARRAY);
    }

    private static final String _sTEST_DOUBLE_MATRIX = "TPGMATRIX";

    public static PostgresQualifiedId getQualifiedMatrixType() {
        return new PostgresQualifiedId(null, _sTEST_SCHEMA, _sTEST_DOUBLE_MATRIX);
    }

    private static int iBUFSIZ = 8192;

    public static void grantSchemaUser(Connection conn, String sSchema,
                                       String sDbUser) throws SQLException {
        Statement stmt = conn.createStatement().unwrap(Statement.class);
        stmt.executeUpdate("ALTER DEFAULT PRIVILEGES IN SCHEMA " + sSchema + " GRANT ALL ON TABLES TO " + sDbUser);
        stmt.executeUpdate("GRANT ALL ON ALL TABLES IN SCHEMA " + sSchema + " TO " + sDbUser);
        stmt.executeUpdate("ALTER DEFAULT PRIVILEGES IN SCHEMA " + sSchema + " GRANT ALL ON TYPES TO " + sDbUser);
        // stmt.executeUpdate("GRANT ALL ON ALL TYPES IN SCHEMA "+sSchema+" TO "+sDbUser);
        stmt.executeUpdate("ALTER DEFAULT PRIVILEGES IN SCHEMA " + sSchema + " GRANT ALL ON SEQUENCES TO " + sDbUser);
        stmt.executeUpdate("GRANT ALL ON ALL SEQUENCES IN SCHEMA " + sSchema + " TO " + sDbUser);
        stmt.executeUpdate("ALTER DEFAULT PRIVILEGES IN SCHEMA " + sSchema + " GRANT ALL ON FUNCTIONS TO " + sDbUser);
        stmt.executeUpdate("GRANT ALL ON ALL FUNCTIONS IN SCHEMA " + sSchema + " TO " + sDbUser);
        conn.commit();
    }

    public static void revokeSchemaUser(Connection conn, String sSchema,
                                        String sDbUser) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.unwrap(Statement.class).executeUpdate("ALTER DEFAULT PRIVILEGES IN SCHEMA " + sSchema + " REVOKE ALL ON TABLES TO " + sDbUser);
        stmt.unwrap(Statement.class).executeUpdate("ALTER DEFAULT PRIVILEGES IN SCHEMA " + sSchema + " REVOKE ALL ON TYPES TO " + sDbUser);
        stmt.unwrap(Statement.class).executeUpdate("ALTER DEFAULT PRIVILEGES IN SCHEMA " + sSchema + " REVOKE ALL ON SEQUENCES TO " + sDbUser);
        stmt.unwrap(Statement.class).executeUpdate("ALTER DEFAULT PRIVILEGES IN SCHEMA " + sSchema + " REVOKE ALL ON FUNCTIONS TO " + sDbUser);
        conn.commit();
    }

    public static class ColumnDefinition extends TestColumnDefinition {
        @Override
        public String getValueLiteral() {
            String sValueLiteral = "NULL";
            if (_oValue != null) {
                if (getName().equals("CBYTEA"))
                    sValueLiteral = PostgresLiterals.formatBytesLiteral((byte[]) getValue());
                else if (getName().equals("CINT_BUILTIN") || getName().equals("CSTRING_RANGE")) {
                    StringBuilder sb = new StringBuilder();
                    @SuppressWarnings("unchecked")
                    List<ColumnDefinition> listCd = (List<ColumnDefinition>) getValue();
                    ColumnDefinition cdStart = listCd.get(0);
                    ColumnDefinition cdEnd = listCd.get(1);
                    ColumnDefinition cdSignature = listCd.get(2);
                    try {
                        PostgresQualifiedId qiType = new PostgresQualifiedId(getType());
                        sb.append(qiType.format());
                        sb.append("(");
                        sb.append(cdStart.getValueLiteral());
                        sb.append(",");
                        sb.append(cdEnd.getValueLiteral());
                        sb.append(",");
                        sb.append(PostgresLiterals.formatStringLiteral((String) cdSignature.getValue()));
                        sb.append(")");
                        sValueLiteral = sb.toString();
                    } catch (ParseException pe) {
                        System.err.println("ParseException in getValueLiteral()!");
                    }
                } else if (getName().equals("CINT_DOMAIN") || getName().equals("CYEAR") || getName().equals("CENUM_SUIT")) {
                    @SuppressWarnings("unchecked")
                    List<ColumnDefinition> listCd = (List<ColumnDefinition>) getValue();
                    ColumnDefinition cd = listCd.get(0);
                    sValueLiteral = cd.getValueLiteral();
                } else if (getName().equals("CCOMPOSITE")) {
                    StringBuilder sb = new StringBuilder("(");
                    @SuppressWarnings("unchecked")
                    List<ColumnDefinition> listCd = (List<ColumnDefinition>) getValue();
                    for (int iAttribute = 0; iAttribute < listCd.size(); iAttribute++) {
                        ColumnDefinition cd = listCd.get(iAttribute);
                        if (iAttribute > 0)
                            sb.append(", ");
                        sb.append(cd.getValueLiteral());
                    }
                    sb.append(")");
                    sValueLiteral = sb.toString();
                } else if (getName().equals("CSTRING_ARRAY")) {
                    StringBuilder sb = new StringBuilder("{");
                    @SuppressWarnings("unchecked")
                    List<ColumnDefinition> listCd = (List<ColumnDefinition>) getValue();
                    for (int iElement = 0; iElement < listCd.size(); iElement++) {
                        ColumnDefinition cd = listCd.get(iElement);
                        if (iElement > 0)
                            sb.append(",");
                        sb.append("\"");
                        sb.append(cd.getValue());
                        sb.append("\"");
                    }
                    sb.append("}");
                    sValueLiteral = PostgresLiterals.formatStringLiteral(sb.toString());
                } else if (getName().equals("CDOUBLE_MATRIX")) {
                    StringBuilder sb = new StringBuilder("{");
                    @SuppressWarnings("unchecked")
                    List<ColumnDefinition> listRowCd = (List<ColumnDefinition>) getValue();
                    for (int iRow = 0; iRow < listRowCd.size(); iRow++) {
                        ColumnDefinition cdRow = listRowCd.get(iRow);
                        if (iRow > 0)
                            sb.append(", ");
                        sb.append("{");
                        @SuppressWarnings("unchecked")
                        List<ColumnDefinition> listColumnCd = (List<ColumnDefinition>) cdRow.getValue();
                        for (int iColumn = 0; iColumn < listColumnCd.size(); iColumn++) {
                            ColumnDefinition cdColumn = listColumnCd.get(iColumn);
                            if (iColumn > 0)
                                sb.append(", ");
                            sb.append(cdColumn.getValueLiteral());
                        }
                        sb.append("}");
                    }
                    sb.append("}");
                    sValueLiteral = PostgresLiterals.formatStringLiteral(sb.toString());
                } else if (getName().startsWith("CINTERVAL")) {
                    Interval iv = (Interval) getValue();
                    sValueLiteral = PostgresLiterals.formatIntervalLiteral(iv);
                } else if (getName().equals("CUUID")) {
                    UUID uuid = (UUID) getValue();
                    sValueLiteral = PostgresLiterals.formatStringLiteral(uuid.toString());
                } else if (getName().equals("CMACADDR") || (getName().equals("CMACADDR8"))) {
                    byte[] buf = (byte[]) getValue();
                    sValueLiteral = PostgresLiterals.formatStringLiteral(PostgresLiterals.formatMacAddr(buf));
                } else
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
        listCdSimple.add(new ColumnDefinition("CINTEGER", PostgresType.INTEGER.getKeyword(), -1000000));
        listCdSimple.add(new ColumnDefinition("CSMALLINT", PostgresType.SMALLINT.getKeyword(), (short) -32767));
        listCdSimple.add(new ColumnDefinition("CBIGINT", PostgresType.BIGINT.getKeyword(), -2147483648L));
        listCdSimple.add(new ColumnDefinition("COID", PostgresType.OID.getKeyword(), TestUtils.getBytes(1000000)));
        _iCandidateSimple = listCdSimple.size(); // next column will be candidate key column
        listCdSimple.add(new ColumnDefinition("CSERIAL", PostgresType.SERIAL.getKeyword(), 1000000));
        listCdSimple.add(new ColumnDefinition("CSMALLSERIAL", PostgresType.SMALLSERIAL.getKeyword(), (short) 32767));
        listCdSimple.add(new ColumnDefinition("CBIGSERIAL", PostgresType.BIGSERIAL.getKeyword(), 2147483648L));
        // the money type depends on the db locale - and caused the tests to fail.
        //listCdSimple.add(new ColumnDefinition("CMONEY",PostgresType.MONEY.getKeyword(),BigDecimal.valueOf(12345678901234l, 2)));

        // Numeric Data Types: Fixed-Point Types (Exact Values)
        listCdSimple.add(new ColumnDefinition("CNUMERIC_5_2", PostgresType.NUMERIC.getKeyword() + "(5,2)", BigDecimal.valueOf(12345, 2)));
        listCdSimple.add(new ColumnDefinition("CDECIMAL_15_5", PostgresType.NUMERIC.getAliases().toArray()[0] + "(15,5)", new BigDecimal("123455679.12345")));
        // Numeric Data Types: Floating-Point Types (Approximate Values)
        listCdSimple.add(new ColumnDefinition("CDOUBLE", PostgresType.DOUBLE.getKeyword(), Math.E));
        listCdSimple.add(new ColumnDefinition("CREAL", PostgresType.REAL.getKeyword(), Double.valueOf(Math.PI).floatValue()));
        listCdSimple.add(new ColumnDefinition("CBOOL", PostgresType.BOOLEAN.getKeyword(), Boolean.FALSE));

        // Date and Time Types
        listCdSimple.add(new ColumnDefinition("CDATE", PostgresType.DATE.getKeyword(), new Date(2016 - 1900, 10, 30)));
        listCdSimple.add(new ColumnDefinition("CTIME", PostgresType.TIME.getKeyword(), new Time(12, 34, 56)));
        listCdSimple.add(new ColumnDefinition("CTIMETZ", PostgresType.TIMETZ.getKeyword(), new Time(9, 34, 56)));
        listCdSimple.add(new ColumnDefinition("CTIMESTAMP", PostgresType.TIMESTAMP.getKeyword(), new Timestamp(2016 - 1900, 10, 30, 12, 34, 56, 0)));
        listCdSimple.add(new ColumnDefinition("CTIMESTAMPTZ", PostgresType.TIMESTAMP.getKeyword(), new Timestamp(2019 - 1900, 07, 29, 9, 34, 56, 0)));
        listCdSimple.add(new ColumnDefinition("CINTERVALYM", PostgresType.INTERVAL.getKeyword() + " year to month", new Interval(1, 1, 3)));
        listCdSimple.add(new ColumnDefinition("CINTERVALDM", PostgresType.INTERVAL.getKeyword() + " day to second(6)", new Interval(-1, 1, 11, 23, 34, 456789000l)));

        // CHAR/VARCHAR
        listCdSimple.add(new ColumnDefinition("CCHAR_4", PostgresType.CHAR.getKeyword() + "(4)", TestUtils.getString(3)));
        listCdSimple.add(new ColumnDefinition("CVARCHAR_500", PostgresType.VARCHAR.getKeyword() + "(500)", TestUtils.getString(255)));
        listCdSimple.add(new ColumnDefinition("CTEXT", PostgresType.TEXT.getKeyword(), TestUtils.getString(5000)));
        listCdSimple.add(new ColumnDefinition("CJSONB", PostgresType.JSONB.getKeyword(), "{ \"name\":\"John\", \"age\":30, \"car\":null }"));
        listCdSimple.add(new ColumnDefinition("CJSON", PostgresType.JSON.getKeyword(), "{\r\n" +
                "    \"glossary\": {\r\n" +
                "        \"title\": \"example glossary\",\r\n" +
                "    \"GlossDiv\": {\r\n" +
                "            \"title\": \"S\",\r\n" +
                "      \"GlossList\": {\r\n" +
                "                \"GlossEntry\": {\r\n" +
                "                    \"ID\": \"SGML\",\r\n" +
                "          \"SortAs\": \"SGML\",\r\n" +
                "          \"GlossTerm\": \"Standard Generalized Markup Language\",\r\n" +
                "          \"Acronym\": \"SGML\",\r\n" +
                "          \"Abbrev\": \"ISO 8879:1986\",\r\n" +
                "          \"GlossDef\": {\r\n" +
                "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\r\n" +
                "            \"GlossSeeAlso\": [\"GML\", \"XML\"]\r\n" +
                "                    },\r\n" +
                "          \"GlossSee\": \"markup\"\r\n" +
                "                }\r\n" +
                "            }\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}"));
        listCdSimple.add(new ColumnDefinition("CXML", PostgresType.XML.getKeyword(), "<a>Ein schöööönes XML Fragment</a>"));
        listCdSimple.add(new ColumnDefinition("CTSVECTOR", PostgresType.TSVECTOR.getKeyword(), "a fat cat sat on a mat and ate a fat rat"));
        listCdSimple.add(new ColumnDefinition("CTSQUERY", PostgresType.TSQUERY.getKeyword(), "fat:ab & cat"));

        // BINARY/VARBINARY
        listCdSimple.add(new ColumnDefinition("CBIT_256", PostgresType.BIT.getKeyword() + "(256)", PostgresLiterals.formatBitString(TestUtils.getBytes(32), 256)));
        listCdSimple.add(new ColumnDefinition("CVARBIT_805", PostgresType.VARBIT.getKeyword() + "(805)", PostgresLiterals.formatBitString(TestUtils.getBytes(101), 805)));
        listCdSimple.add(new ColumnDefinition("CBYTEA", PostgresType.BYTEA.getKeyword(), TestUtils.getBytes(5000)));
        listCdSimple.add(new ColumnDefinition("CUUID", PostgresType.UUID.getKeyword(), UUID.randomUUID()));
        listCdSimple.add(new ColumnDefinition("CMACADDR", PostgresType.MACADDR.getKeyword(), TestUtils.getBytes(6)));
        listCdSimple.add(new ColumnDefinition("CMACADDR8", PostgresType.MACADDR8.getKeyword(), TestUtils.getBytes(8)));

        // spatial
        listCdSimple.add(new ColumnDefinition("CPOINT", PostgresType.POINT.getKeyword(), "(1.5,2.0)"));
        listCdSimple.add(new ColumnDefinition("CLINE", PostgresType.LINE.getKeyword(), "{0.5,-0.1,1.0}"));
        listCdSimple.add(new ColumnDefinition("CLSEG", PostgresType.LSEG.getKeyword(), "[(1.2, 2.1),(4.8, 5.1)]"));
        listCdSimple.add(new ColumnDefinition("CBOX", PostgresType.BOX.getKeyword(), "((1,1),(2,2))"));
        listCdSimple.add(new ColumnDefinition("CPATH", PostgresType.PATH.getKeyword(), "[(0,0),(10,0),(10,10),(0,10)]"));
        listCdSimple.add(new ColumnDefinition("CPOLYGON", PostgresType.POLYGON.getKeyword(), "((0,0),(10,0),(10,10),(0,10))"));
        listCdSimple.add(new ColumnDefinition("CCIRCLE", PostgresType.CIRCLE.getKeyword(), "<(1.0,0.0),5.0>"));

        return listCdSimple;
    }

    public static List<TestColumnDefinition> _listCdSimple = getCdSimple();

    /* complex type : builtin int4range */
    private static List<TestColumnDefinition> getListBuiltinRange() {
        List<TestColumnDefinition> listBuiltinRange = new ArrayList<>();
        listBuiltinRange.add(new ColumnDefinition(PostgresDatabaseMetaData.sRANGE_START, PostgresType.INTEGER.getKeyword(), 473));
        listBuiltinRange.add(new ColumnDefinition(PostgresDatabaseMetaData.sRANGE_END, PostgresType.INTEGER.getKeyword(), 5435));
        listBuiltinRange.add(new ColumnDefinition(PostgresDatabaseMetaData.sRANGE_SIGNATURE, PostgresType.CHAR.getKeyword() + "(2)", "[)"));
        return listBuiltinRange;
    }

    public static List<TestColumnDefinition> _listBuiltinRange = getListBuiltinRange();

    /* complex type : year domain */
    private static List<TestColumnDefinition> getListBaseYear() {
        List<TestColumnDefinition> listBaseYear = new ArrayList<>();
        listBaseYear.add(new ColumnDefinition(getQualifiedYearType().format(), PostgresType.INTEGER.getKeyword(), 2019));
        return listBaseYear;
    }

    public static List<TestColumnDefinition> _listBaseYear = getListBaseYear();

    /* complex type : domain */
    private static List<TestColumnDefinition> getListBaseDomain() {
        List<TestColumnDefinition> listBaseDomain = new ArrayList<>();
        listBaseDomain.add(new ColumnDefinition(getQualifiedDomainType().format(), PostgresType.INTEGER.getKeyword(), 999));
        return listBaseDomain;
    }

    public static List<TestColumnDefinition> _listBaseDomain = getListBaseDomain();

    /* complex type : composite */
    public static int _iPrimaryComplex = -1;

    private static List<TestColumnDefinition> getListCompositeType() {
        List<TestColumnDefinition> listCompositeType = new ArrayList<>();
        listCompositeType.add(new ColumnDefinition("F1", PostgresType.INTEGER.getKeyword(), -25));
        listCompositeType.add(new ColumnDefinition("F2", "text", TestUtils.getString(511)));
        return listCompositeType;
    }

    public static List<TestColumnDefinition> _listCompositeType = getListCompositeType();

    /* complex type : enum */
    private static List<TestColumnDefinition> getListEnumType() {
        List<TestColumnDefinition> listEnumType = new ArrayList<>();
        listEnumType.add(new ColumnDefinition(getQualifiedEnumType().format(), "ENUM ('clubs','spades','hearts','diamonds')", "hearts"));
        return listEnumType;
    }

    public static List<TestColumnDefinition> _listEnumType = getListEnumType();

    /* complex type : string RANGE */
    private static List<TestColumnDefinition> getListRangeType() {
        List<TestColumnDefinition> listRangeType = new ArrayList<>();
        listRangeType.add(new ColumnDefinition(PostgresDatabaseMetaData.sRANGE_START, PostgresType.VARCHAR.getKeyword() + "(64)", "bstart"));
        listRangeType.add(new ColumnDefinition(PostgresDatabaseMetaData.sRANGE_END, PostgresType.VARCHAR.getKeyword() + "(64)", "cend"));
        listRangeType.add(new ColumnDefinition(PostgresDatabaseMetaData.sRANGE_SIGNATURE, PostgresType.CHAR.getKeyword() + "(2)", "(]"));
        return listRangeType;
    }

    public static List<TestColumnDefinition> _listRangeType = getListRangeType();

    /* complex type : string ARRAY */
    private static List<TestColumnDefinition> getListStringArray() {
        List<TestColumnDefinition> listStringArray = new ArrayList<>();
        listStringArray.add(new ColumnDefinition("CARRAY[1]", PostgresType.VARCHAR.getKeyword() + "(64)", "line1"));
        listStringArray.add(new ColumnDefinition("CARRAY[2]", PostgresType.VARCHAR.getKeyword() + "(64)", "line2"));
        listStringArray.add(new ColumnDefinition("CARRAY[3]", PostgresType.VARCHAR.getKeyword() + "(64)", "line3"));
        listStringArray.add(new ColumnDefinition("CARRAY[4]", PostgresType.VARCHAR.getKeyword() + "(64)", "line4"));
        return listStringArray;
    }

    public static List<TestColumnDefinition> _listStringArray = getListStringArray();

    /* complex type : double MATRIX */
    private static List<TestColumnDefinition> getListDoubleMatrix() {
        List<TestColumnDefinition> listDoubleMatrix = new ArrayList<>();
        List<TestColumnDefinition> listDoubleArray = new ArrayList<>();
        listDoubleArray.add(new ColumnDefinition("CMATRIX[1][1]", PostgresType.DOUBLE.getKeyword(), 0.1));
        listDoubleArray.add(new ColumnDefinition("CMATRIX[1][2]", PostgresType.DOUBLE.getKeyword(), 0.0));
        listDoubleArray.add(new ColumnDefinition("CMATRIX[1][3]", PostgresType.DOUBLE.getKeyword(), 0.5));
        listDoubleArray.add(new ColumnDefinition("CMATRIX[1][4]", PostgresType.DOUBLE.getKeyword(), 2.0));
        listDoubleMatrix.add(new ColumnDefinition("CMATRIX[1]", PostgresType.DOUBLE.getKeyword() + "[4]", listDoubleArray));
        listDoubleArray = new ArrayList<>();
        listDoubleArray.add(new ColumnDefinition("CMATRIX[2][1]", PostgresType.DOUBLE.getKeyword(), 10.0));
        listDoubleArray.add(new ColumnDefinition("CMATRIX[2][2]", PostgresType.DOUBLE.getKeyword(), null));
        listDoubleArray.add(new ColumnDefinition("CMATRIX[2][3]", PostgresType.DOUBLE.getKeyword(), 2.0));
        listDoubleArray.add(new ColumnDefinition("CMATRIX[2][4]", PostgresType.DOUBLE.getKeyword(), 0.5));
        listDoubleMatrix.add(new ColumnDefinition("CMATRIX[2]", PostgresType.DOUBLE.getKeyword() + "[4]", listDoubleArray));
        listDoubleArray = new ArrayList<>();
        listDoubleArray.add(new ColumnDefinition("CMATRIX[3][1]", PostgresType.DOUBLE.getKeyword(), 5.0));
        listDoubleArray.add(new ColumnDefinition("CMATRIX[3][2]", PostgresType.DOUBLE.getKeyword(), 0.0));
        listDoubleArray.add(new ColumnDefinition("CMATRIX[3][3]", PostgresType.DOUBLE.getKeyword(), 0.25));
        listDoubleArray.add(new ColumnDefinition("CMATRIX[3][4]", PostgresType.DOUBLE.getKeyword(), 1.0));
        listDoubleMatrix.add(new ColumnDefinition("CMATRIX[3]", PostgresType.DOUBLE.getKeyword() + "[4]", listDoubleArray));
        return listDoubleMatrix;
    }

    public static List<TestColumnDefinition> _listDoubleMatrix = getListDoubleMatrix();

    /* all complex types */
    private static List<TestColumnDefinition> getCdComplex() {
        List<TestColumnDefinition> listCdComplex = new ArrayList<>();
        _iPrimaryComplex = listCdComplex.size(); // next column will be primary key column
        listCdComplex.add(new ColumnDefinition("CID", PostgresType.INTEGER.getKeyword(), 987654321));
        listCdComplex.add(new ColumnDefinition("CYEAR", getQualifiedYearType().format(), _listBaseYear));
        listCdComplex.add(new ColumnDefinition("CINT_DOMAIN", getQualifiedDomainType().format(), _listBaseDomain));
        listCdComplex.add(new ColumnDefinition("CCOMPOSITE", getQualifiedCompositeType().format(), _listCompositeType));
        listCdComplex.add(new ColumnDefinition("CENUM_SUIT", getQualifiedEnumType().format(), _listEnumType));
        listCdComplex.add(new ColumnDefinition("CINT_BUILTIN", getQualifiedBuiltinRange().format(), _listBuiltinRange));
        listCdComplex.add(new ColumnDefinition("CSTRING_RANGE", getQualifiedRangeType().format(), _listRangeType));
        listCdComplex.add(new ColumnDefinition("CSTRING_ARRAY", getQualifiedArrayType().format(), _listStringArray));
        listCdComplex.add(new ColumnDefinition("CDOUBLE_MATRIX", getQualifiedMatrixType().format(), _listDoubleMatrix));
        return listCdComplex;
    }

    public static List<TestColumnDefinition> _listCdComplex = getCdComplex();


    private Connection _conn = null;
    private String _sDbUser = null;

    public TestPostgresDatabase(PostgresConnection connPostgres, String sDbUser)
            throws SQLException, IOException {
        _conn = connPostgres.unwrap(Connection.class);
        _sDbUser = sDbUser;
        _conn.setAutoCommit(false);
        drop();
        create();
    }

    private void drop() {
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
            /* terminate transaction */
            try {
                _conn.rollback();
            } catch (SQLException seRollback) {
                System.out.println("Rollback failed with " + EU.getExceptionMessage(seRollback));
            }
        }
    }

    private void deleteTables() {
        // SELECT lo_unlink(l.oid) FROM pg_largeobject_metadata l;
        try {
            LargeObjectManager lobj = ((PGConnection) _conn).getLargeObjectAPI();
            Statement stmt = _conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COID FROM " + getQualifiedSimpleTable().format());
            while (rs.next()) {
                long loid = rs.getLong("COID");
                lobj.unlink(loid);
            }
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
            /* terminate transaction */
            try {
                _conn.rollback();
            } catch (SQLException seRollback) {
                System.out.println("Rollback failed with " + EU.getExceptionMessage(seRollback));
            }
        }
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
    } /* dropTable */

    private void dropTypes() {
        dropType(getQualifiedYearType());
        dropType(getQualifiedDomainType());
        dropType(getQualifiedCompositeType());
        dropType(getQualifiedEnumType());
        dropType(getQualifiedRangeType());
    }

    private void dropType(QualifiedId qiType) {
        if (qiType.getName().equals(_sTEST_INTEGER_DOMAIN))
            executeDrop("DROP DOMAIN " + qiType.format());
        else
            executeDrop("DROP TYPE " + qiType.format());
    }

    private void dropSchema() {
        executeDrop("DROP SCHEMA " + SqlLiterals.formatId(_sTEST_SCHEMA));
    } /* dropSchema */

    private void create()
            throws SQLException, IOException {
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
        SchemaId sid = new SchemaId(null, _sTEST_SCHEMA);
        executeCreate("CREATE SCHEMA " + sid.format() + " AUTHORIZATION " + _sDbUser);
    }

    private void createTypes()
            throws SQLException {
        createType(getQualifiedYearType(), _listBaseYear);
        createType(getQualifiedDomainType(), _listBaseDomain);
        createType(getQualifiedCompositeType(), _listCompositeType);
        createType(getQualifiedEnumType(), _listEnumType);
        createType(getQualifiedRangeType(), _listRangeType);
    }

    private void createType(QualifiedId qiType, List<TestColumnDefinition> listAttributes)
            throws SQLException {
        if (qiType.getName().equals(_sTEST_INTEGER_DOMAIN) || (qiType.getName().equals(_sTEST_YEAR_DOMAIN))) {
            TestColumnDefinition cd = listAttributes.get(0);
            executeCreate("CREATE DOMAIN " + qiType.format() + " AS " + cd.getType());
        } else if (qiType.getName().equals(_sTEST_TYPE_COMP)) {
            StringBuilder sb = new StringBuilder("CREATE TYPE ");
            sb.append(qiType.format());
            sb.append(" AS (");
            for (int iAttribute = 0; iAttribute < listAttributes.size(); iAttribute++) {
                TestColumnDefinition cd = listAttributes.get(iAttribute);
                if (iAttribute > 0)
                    sb.append(",");
                sb.append("\r\n  ");
                sb.append(PostgresLiterals.formatId(cd.getName()));
                sb.append(" ");
                sb.append(cd.getType());
            }
            sb.append("\r\n)");
            executeCreate(sb.toString());
        } else if (qiType.getName().equals(_sTEST_TYPE_ENUM)) {
            TestColumnDefinition cd = listAttributes.get(0);
            executeCreate("CREATE TYPE " + qiType.format() + " AS " + cd.getType());
        } else if (qiType.getName().equals(_sTEST_TYPE_RANGE)) {
            executeCreate("CREATE TYPE " + qiType.format() + " AS RANGE (subtype=text)");
        }
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
            TestColumnDefinition cd = listCd.get(iColumn);
            if (iColumn > 0)
                sbSql.append(",\r\n  ");
            sbSql.append(cd.getName());
            sbSql.append(" ");
            if (cd.getName().equals("CINT_BUILTIN"))
                sbSql.append("int4range");
            else if (cd.getName().equals("CSTRING_ARRAY"))
                sbSql.append("text[4]");
            else if (cd.getName().equals("CDOUBLE_MATRIX"))
                sbSql.append("float8[3][4]");
            else
                sbSql.append(cd.getType());
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
            throws SQLException, IOException {
        insertTable(getQualifiedSimpleTable(), _listCdSimple);
        insertTable(getQualifiedComplexTable(), _listCdComplex);
    }

    private void insertTable(QualifiedId qiTable, List<TestColumnDefinition> listCd)
            throws SQLException, IOException {
        StringBuilder sbSql = new StringBuilder("INSERT INTO ");
        sbSql.append(qiTable.format());
        sbSql.append("\r\n(\r\n  ");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            TestColumnDefinition cd = listCd.get(iColumn);
            if (cd.getValue() != null) {
                if (iColumn > 0)
                    sbSql.append(",\r\n  ");
                sbSql.append(cd.getName());
            }
        }
        sbSql.append("\r\n)\r\nVALUES\r\n(\r\n  ");
        List<Object> listLobs = new ArrayList<>();
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            TestColumnDefinition cd = listCd.get(iColumn);
            if (cd.getValue() != null) {
                if (iColumn > 0)
                    sbSql.append(",\r\n  ");
                String sLiteral = cd.getValueLiteral();
                if (sLiteral.length() < 100000)
                    sbSql.append(sLiteral);
                else {
                    sbSql.append("?");
                    listLobs.add(cd.getValue());
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
                LargeObjectManager lobj = ((PGConnection) _conn).getLargeObjectAPI();
                long oid = lobj.createLO();
                LargeObject lo = lobj.open(oid, LargeObjectManager.WRITE);
                InputStream isBlob = new ByteArrayInputStream((byte[]) o);
                byte[] bufBlob = new byte[iBUFSIZ];
                for (int iRead = isBlob.read(bufBlob, 0, bufBlob.length); iRead != -1; iRead = isBlob.read(bufBlob, 0, bufBlob.length))
                    lo.write(bufBlob, 0, iRead);
                lo.close();
                pstmt.setLong(iLob + 1, oid);
                Statement stmt = _conn.createStatement();
                String sSql = "GRANT ALL ON LARGE OBJECT " + oid + " TO PUBLIC";
                stmt.executeUpdate(sSql);
                stmt.close();

            } else
                throw new SQLException("Invalid LOB type " + o.getClass().getName() + "!");
        }
        int iResult = pstmt.executeUpdate();
        assertSame("Insert failed!", 1, iResult);
        pstmt.close();
        _conn.commit();
    }
}
