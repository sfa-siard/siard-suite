package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.access.Conversions;
import ch.admin.bar.siard2.access.TestAccessDatabase;
import ch.admin.bar.siard2.access.TestSqlDatabase;
import ch.admin.bar.siard2.jdbcx.AccessDataSource;
import ch.enterag.sqlparser.Interval;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.FU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.base.TestUtils;
import ch.enterag.utils.jdbc.BaseResultSetTester;
import ch.enterag.utils.lang.Execute;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.Duration;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AccessResultSetTester extends BaseResultSetTester {
    private static final String _sNativeQuerySimple = getTableQuery(TestAccessDatabase.getQualifiedSimpleTable(), TestAccessDatabase._listCdSimple);
    private static final String _sNativeQueryComplex = getTableQuery(TestAccessDatabase.getQualifiedComplexTable(), TestAccessDatabase._listCdComplex);
    private static final String _sSqlQuerySimple = getTableQuery(TestSqlDatabase.getQualifiedSimpleTable(), TestSqlDatabase._listCdSimple);
    private static final File fileTEST_EMPTY_DATABASE = new File("src/test/resources/testfiles/testempty.accdb");
    private static final File fileTEST_ACCESS_SOURCE = new File("src/test/resources/testfiles/testaccess.accdb");
    private static final File fileTEST_ACCESS_DATABASE = new File("src/test/resources/tmp/testaccess.accdb");
    private static final File fileTEST_SQL_DATABASE = new File("src/test/resources/tmp/testsql.accdb");
    private static final String sUSER = "Admin";
    private static final String sPASSWORD = "";
    public static List<TestColumnDefinition> _listCdSimple = getListCdSimple();

    private static String getTableQuery(QualifiedId qiTable, List<TestColumnDefinition> listCd) {
        StringBuilder sbSql = new StringBuilder("SELECT\r\n  ");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = listCd.get(iColumn);
            sbSql.append(SqlLiterals.formatId(tcd.getName()));
        }
        sbSql.append("\r\nFROM ");
        sbSql.append(qiTable.format());
        return sbSql.toString();
    }

    @SuppressWarnings("deprecation")
    private static List<TestColumnDefinition> getListCdSimple() {
        List<TestColumnDefinition> listCdSimple = new ArrayList<>();
        listCdSimple.add(new TestColumnDefinition("CCHAR_5", "CHAR(5)", "wxyZ"));
        listCdSimple.add(new TestColumnDefinition("CVARCHAR_255", "VARCHAR(255)", TestUtils.getString(92)));
        listCdSimple.add(new TestColumnDefinition("CCLOB_2M", "CLOB(2M)", TestUtils.getString(1000000)));
        listCdSimple.add(new TestColumnDefinition("CNCHAR_5", "NCHAR(5)", "Auää"));
        listCdSimple.add(new TestColumnDefinition("CNVARCHAR_127", "NCHAR VARYING(127)", TestUtils.getNString(53)));
        listCdSimple.add(new TestColumnDefinition("CNCLOB_1M", "NCLOB(1M)", TestUtils.getNString(500000)));
        listCdSimple.add(new TestColumnDefinition("CXML", "XML", "<a>foöäpwkfèégopàèwerkgv fviodsjv jdsjd idsjidsjsiudojiou operkv &lt; and &amp; ifjeifj</a>"));
        listCdSimple.add(new TestColumnDefinition("CBINARY_5", "BINARY(5)", new byte[]{5, -4, 3, -2}));
        listCdSimple.add(new TestColumnDefinition("CVARBINARY_255", "VARBINARY(255)", TestUtils.getBytes(76)));
        listCdSimple.add(new TestColumnDefinition("CBLOB", "BLOB", TestUtils.getBytes(500000)));
        listCdSimple.add(new TestColumnDefinition("CNUMERIC_28", "NUMERIC(28)", BigInteger.valueOf(987654321098765432L)));
        listCdSimple.add(new TestColumnDefinition("CDECIMAL_15_5", "DECIMAL(15,5)", new BigDecimal(BigInteger.valueOf(9876543210987L), 5)));
        listCdSimple.add(new TestColumnDefinition("CSMALLINT", "SMALLINT", (short) 23000));
        listCdSimple.add(new TestColumnDefinition("CINTEGER", "INTEGER", 987654321));
        listCdSimple.add(new TestColumnDefinition("CBIGINT", "BIGINT", -987654321098765432L));
        listCdSimple.add(new TestColumnDefinition("CFLOAT_10", "FLOAT(10)", (float) Math.PI));
        listCdSimple.add(new TestColumnDefinition("CREAL", "REAL", (float) Math.E));
        listCdSimple.add(new TestColumnDefinition("CDOUBLE", "DOUBLE PRECISION", Math.PI));
        listCdSimple.add(new TestColumnDefinition("CBOOLEAN", "BOOLEAN", Boolean.FALSE));
        listCdSimple.add(new TestColumnDefinition("CDATE", "DATE", new Date(2016 - 1900, 12, 2)));
        listCdSimple.add(new TestColumnDefinition("CTIME", "TIME", new Time(14, 24, 12)));
        listCdSimple.add(new TestColumnDefinition("CTIMESTAMP", "TIMESTAMP(3)", new Timestamp(2016 - 1900, 12, 2, 14, 24, 12, 987000000)));
        listCdSimple.add(new TestColumnDefinition("CINTERVAL_YEAR_2_MONTH", "INTERVAL YEAR(2) TO MONTH", new Interval(1, 3, 6)));
        listCdSimple.add(new TestColumnDefinition("CINTERVAL_DAY_2_SECONDS_6", "INTERVAL DAY(2) TO SECOND(6)", new Interval(1, 0, 17, 54, 23, 123456000L)));
        return listCdSimple;
    }

    @BeforeAll
    public static void setUpClass() throws SQLException, IOException {
        FU.copy(fileTEST_EMPTY_DATABASE, fileTEST_ACCESS_DATABASE);
        /* The JDBC-ODBC bridge could still be used until JAVA 1.8 using
         * an extract from the JAVA 7 run-time library and the JdbcOdbc.dll.
         * Now that is blocked by the split packages prohibition.
         * So we use the test database originally created under JAVA 1.8.
         * If we ever want more controlled features in the test database
         * we shall be in trouble ... (have to use JAVA 1.7 or 1.8!)
         */
        if (Execute.isOsWindows() && Execute.isJavaVersionLessThan("9"))
            new TestAccessDatabase(fileTEST_ACCESS_DATABASE);
        else FU.copy(fileTEST_ACCESS_SOURCE, fileTEST_ACCESS_DATABASE);
        FU.copy(fileTEST_EMPTY_DATABASE, fileTEST_SQL_DATABASE);
        AccessDataSource dsAccess = new AccessDataSource();
        dsAccess.setDatabaseName(fileTEST_SQL_DATABASE.getAbsolutePath());
        dsAccess.setDescription("SQL data base");
        dsAccess.setReadOnly(false);
        dsAccess.setUser(sUSER);
        dsAccess.setPassword(sPASSWORD);
        AccessConnection connAccess = (AccessConnection) dsAccess.getConnection();
        new TestSqlDatabase(connAccess);
        connAccess.close();
    }

    private TestColumnDefinition findColumnDefinition(List<TestColumnDefinition> listCd, String sName) {
        TestColumnDefinition tcd = null;
        for (TestColumnDefinition tcdTry : listCd) {
            if (sName.equals(tcdTry.getName())) tcd = tcdTry;
        }
        return tcd;
    }

    private void openResultSet(boolean bSql, String sQuery, boolean bWithNext) throws SQLException {
        tearDown();
        AccessDataSource dsAccess = new AccessDataSource();
        if (bSql) {
            dsAccess.setDatabaseName(fileTEST_SQL_DATABASE.getAbsolutePath());
            dsAccess.setDescription("SQL data base");
        } else {
            dsAccess.setDatabaseName(fileTEST_ACCESS_DATABASE.getAbsolutePath());
            dsAccess.setDescription("Access data base");
        }
        dsAccess.setReadOnly(false);
        dsAccess.setUser(sUSER);
        dsAccess.setPassword(sPASSWORD);
        AccessConnection connAccess = (AccessConnection) dsAccess.getConnection();
        connAccess.setAutoCommit(false);
        Statement stmt = connAccess.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery(sQuery);
        setResultSet(rs);
        if (bWithNext) rs.next();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        openResultSet(true, _sSqlQuerySimple, true);
    }

    @Test
    public void testClass() {
        assertEquals(AccessResultSet.class, getResultSet().getClass(), "Wrong result set class!");
    }

    @SneakyThrows
    @Test
    public void testFindColumn() {
        getResultSet().findColumn(TestSqlDatabase._listCdSimple.get(0)
                                                               .getName());
    }

    @SneakyThrows
    @Test
    public void testWasNull() {
        getResultSet().getObject(1);
        getResultSet().wasNull();
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetString() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CVARCHAR_255");
        String s = getResultSet().getString(tcd.getName());
        assertEquals(tcd.getValue(), s, "Invalid String!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetNString() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CNVARCHAR_127");
        String s = getResultSet().getNString(tcd.getName());
        assertEquals(tcd.getValue(), s, "Invalid String!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetClob() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CCLOB_2M");
        Clob clob = getResultSet().getClob(tcd.getName());
        String s = clob.getSubString(1L, (int) clob.length());
        assertEquals(tcd.getValue(), s, "Invalid Clob!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetNClob() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CNCLOB_1M");
        NClob nclob = getResultSet().getNClob(tcd.getName());
        String s = nclob.getSubString(1L, (int) nclob.length());
        assertEquals(tcd.getValue(), s, "Invalid NClob!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetSqlXml() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CXML");
        SQLXML sqlxml = getResultSet().getSQLXML(tcd.getName());
        String s = sqlxml.getString();
        assertEquals(tcd.getValue(), s, "Invalid SQLXML!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetBytes() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CVARBINARY_255");
        byte[] buf = getResultSet().getBytes(tcd.getName());
        assertArrayEquals((byte[]) tcd.getValue(), buf, "Invalid byte array!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetBlob() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CBLOB");
        Blob blob = getResultSet().getBlob(tcd.getName());
        byte[] buf = blob.getBytes(1L, (int) blob.length());
        assertArrayEquals((byte[]) tcd.getValue(), buf, "Invalid Blob!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetBigDecimal() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CDECIMAL_15_5");
        BigDecimal bd = getResultSet().getBigDecimal(tcd.getName());
        assertEquals(tcd.getValue(), bd, "Invalid BigDecimal!");
    }

    @SneakyThrows
    @Override
    @Test
    @SuppressWarnings("deprecation")
    public void testGetBigDecimal_Int() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CDECIMAL_15_5");
        BigDecimal bd = getResultSet().getBigDecimal(tcd.getName(), 3);
        assertEquals(((BigDecimal) tcd.getValue()).setScale(3, RoundingMode.DOWN), bd, "Invalid BigDecimal!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetByte() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CSMALLINT");
        byte by = getResultSet().getByte(tcd.getName());
        assertEquals(((Short) tcd.getValue()).byteValue(), by, "Invalid byte!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetShort() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CSMALLINT");
        short sh = getResultSet().getShort(tcd.getName());
        assertEquals(((Short) tcd.getValue()).shortValue(), sh, "Invalid short!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetInt() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CINTEGER");
        int i = getResultSet().getInt(tcd.getName());
        assertEquals(((Integer) tcd.getValue()).intValue(), i, "Invalid int!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetLong() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CBIGINT");
        long l = getResultSet().getLong(tcd.getName());
        assertEquals(((Long) tcd.getValue()).longValue(), l, "Invalid long!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetFloat() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CREAL");
        float f = getResultSet().getFloat(tcd.getName());
        assertEquals(tcd.getValue(), f, "Invalid float!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetDouble() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CDOUBLE");
        double d = getResultSet().getDouble(tcd.getName());
        assertEquals(tcd.getValue(), d, "Invalid double!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetBoolean() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CBOOLEAN");
        boolean b = getResultSet().getBoolean(tcd.getName());
        assertEquals((Boolean) tcd.getValue(), b, "Invalid boolean!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetDate() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CDATE");
        Date date = getResultSet().getDate(tcd.getName());
        assertEquals(tcd.getValue(), date, "Invalid Date!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetDate_Calendar() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CDATE");
        Calendar cal = new GregorianCalendar();
        Date date = getResultSet().getDate(tcd.getName(), cal);
        assertEquals(tcd.getValue(), date, "Invalid Date!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetTime() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CTIME");
        Time time = getResultSet().getTime(tcd.getName());
        assertEquals(tcd.getValue(), time, "Invalid Time!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetTime_Calendar() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CTIME");
        Calendar cal = new GregorianCalendar();
        Time time = getResultSet().getTime(tcd.getName(), cal);
        assertEquals(tcd.getValue(), time, "Invalid Time!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetTimestamp() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CTIMESTAMP");
        Timestamp ts = getResultSet().getTimestamp(tcd.getName());
        assertEquals(tcd.getValue(), ts, "Invalid Timestamp!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetTimestamp_Calendar() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CTIMESTAMP");
        Calendar cal = new GregorianCalendar();
        Timestamp ts = getResultSet().getTimestamp(tcd.getName(), cal);
        assertEquals(tcd.getValue(), ts, "Invalid Timestamp!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetDuration() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CINTERVAL_DAY_2_SECONDS_6");
        Duration duration = getBaseResultSet().getDuration(tcd.getName());
        assertEquals(((Interval) tcd.getValue()).toDuration(), duration, "Invalid Duration!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetAsciiStream() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CVARCHAR_255");
        InputStream is = getResultSet().getAsciiStream(tcd.getName());
        byte[] buf = new byte[((String) tcd.getValue()).length()];
        is.read(buf);
        if (is.read() != -1) fail("Invalid length of ASCII stream!");
        is.close();
        String s = new String(buf);
        assertEquals(tcd.getValue(), s, "Invalid String!");
    }

    @SneakyThrows
    @Override
    @Test
    @SuppressWarnings("deprecation")
    public void testGetUnicodeStream() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CNVARCHAR_127");
        Reader rdr = new InputStreamReader(getResultSet().getUnicodeStream(tcd.getName()), StandardCharsets.UTF_16);
        char[] cbuf = new char[((String) tcd.getValue()).length()];
        rdr.read(cbuf);
        if (rdr.read() != -1) fail("Invalid length of character stream!");
        rdr.close();
        String s = new String(cbuf);
        assertEquals(tcd.getValue(), s, "Invalid String!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetCharacterStream() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CVARCHAR_255");
        Reader rdr = getResultSet().getCharacterStream(tcd.getName());
        char[] cbuf = new char[((String) tcd.getValue()).length()];
        rdr.read(cbuf);
        if (rdr.read() != -1) fail("Invalid length of character stream!");
        rdr.close();
        String s = new String(cbuf);
        assertEquals(tcd.getValue(), s, "Invalid String!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetNCharacterStream() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CNVARCHAR_127");
        Reader rdr = getResultSet().getNCharacterStream(tcd.getName());
        char[] cbuf = new char[((String) tcd.getValue()).length()];
        rdr.read(cbuf);
        if (rdr.read() != -1) fail("Invalid length of character stream!");
        rdr.close();
        String s = new String(cbuf);
        assertEquals(tcd.getValue(), s, "Invalid String!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetBinaryStream() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CVARBINARY_255");
        InputStream is = getResultSet().getBinaryStream(tcd.getName());
        byte[] buf = new byte[((byte[]) tcd.getValue()).length];
        is.read(buf);
        if (is.read() != -1) fail("Invalid length of binary stream!");
        is.close();
        assertArrayEquals((byte[]) tcd.getValue(), buf, "Invalid byte array!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetArray() {
        openResultSet(false, _sNativeQueryComplex, true);
        TestColumnDefinition tcd = findColumnDefinition(TestAccessDatabase._listCdComplex, "COLLOOKUP");
        Array array = getResultSet().getArray(tcd.getName());
        assertEquals(((String[]) tcd.getValue()).length, ((Object[]) array.getArray()).length, "Invalid array length!");
        assertArrayEquals((Object[]) tcd.getValue(), (Object[]) array.getArray(), "Invalid array!");
    }

    @Override
    @Test
    public void testGetRef() {
    }

    @Override
    @Test
    public void testGetRowId() {
    }

    @Override
    @Test
    public void testGetUrl() throws MalformedURLException, SQLException {
    }

    @Test
    public void testUpdateUrl() throws MalformedURLException, SQLException {
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetObject() {
        for (int i = 0; i < TestSqlDatabase._listCdSimple.size(); i++) {
            TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(i);
            Object o = getResultSet().getObject(tcd.getName());
            // assertEquals(tcd.getValue(), o, "Invalid Object!");
            System.out.println(tcd.getName() + "\t" + o.getClass()
                                                       .getName());
        }
        /***
         TestColumnDefinition tcd = findColumnDefinition(
         TestSqlDatabase._listCdSimple,"CDATE");
         Object o = getResultSet().getObject(tcd.getName());
         assertEquals(tcd.getValue(), o, "Invalid Date!");
         ***/
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetObject_Class() {
        TestColumnDefinition tcd = findColumnDefinition(TestSqlDatabase._listCdSimple, "CDATE");
        assertThrows(ClassCastException.class, () -> {
            Date date = getResultSet().getObject(tcd.getName(), Date.class);
        });
    }

    @Override
    @Test
    public void testGetObject_Map() {
    }

    @Test
    public void testGetObjectSqlSimple() throws SQLException {
        for (int iColumn = 0; iColumn < TestSqlDatabase._listCdSimple.size(); iColumn++) {
            TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(iColumn);
            Object o = getResultSet().getObject(tcd.getName());
            if (tcd.getName()
                   .equals("CCHAR_5") || tcd.getName()
                                            .equals("CNCHAR_5")) {
                if (o instanceof String) {
                    String s = (String) o;
                    s = s.substring(0, ((String) tcd.getValue()).length());
                    assertEquals(tcd.getValue(), s, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type String expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CVARCHAR_255") || tcd.getName()
                                                        .equals("CNVARCHAR_127")) {
                if (o instanceof String) {
                    String s = (String) o;
                    assertEquals(tcd.getValue(), s, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type String expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CCLOB_2M") || tcd.getName()
                                                    .equals("CNCLOB_1M") || tcd.getName()
                                                                               .equals("CXML")) {
                if (o instanceof Clob) {
                    Clob clob = (Clob) o;
                    assertEquals(tcd.getValue(), clob.getSubString(1L, (int) clob.length()), "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Clob expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CBINARY_5")) {
                if (o instanceof byte[]) {
                    byte[] buf = (byte[]) o;
                    buf = Arrays.copyOf(buf, ((byte[]) tcd.getValue()).length);
                    assertArrayEquals((byte[]) tcd.getValue(), buf, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type byte[] expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CVARBINARY_255")) {
                if (o instanceof byte[]) {
                    byte[] buf = (byte[]) o;
                    assertArrayEquals((byte[]) tcd.getValue(), buf, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type byte[] expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CBLOB")) {
                if (o instanceof Blob) {
                    Blob blob = (Blob) o;
                    assertArrayEquals((byte[]) tcd.getValue(), blob.getBytes(1L, (int) blob.length()), "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Blob expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CDECIMAL_15_5") || tcd.getName()
                                                         .equals("CNUMERIC_28")) {
                if (o instanceof BigDecimal) {
                    BigDecimal bd = (BigDecimal) o;
                    Object oExpected = tcd.getValue();
                    if (oExpected instanceof BigDecimal) {
                        BigDecimal bdExpected = (BigDecimal) o;
                        assertEquals(bdExpected, bd, "Invalid value for " + tcd.getType() + "!");
                    } else if (oExpected instanceof BigInteger) {
                        BigInteger biExpected = (BigInteger) oExpected;
                        BigInteger bi = bd.toBigInteger();
                        assertEquals(biExpected, bi, "Invalid value for " + tcd.getType() + "!");
                    }
                } else fail("Type BigDecimal expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CSMALLINT")) {
                if (o instanceof Short) {
                    Short sh = (Short) o;
                    assertEquals(tcd.getValue(), sh, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Short expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CINTEGER")) {
                if (o instanceof Integer) {
                    Integer i = (Integer) o;
                    assertEquals(tcd.getValue(), i, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Integer expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CBIGINT")) {
                if (o instanceof BigDecimal) {
                    BigDecimal bd = (BigDecimal) o;
                    assertEquals(tcd.getValue(), bd.longValue(), "Invalid value for " + tcd.getType() + "!");
                } else fail("Type BigDecimal expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CREAL")) {
                if (o instanceof Float) {
                    Float f = (Float) o;
                    assertEquals(tcd.getValue(), f, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Float expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CDOUBLE") || tcd.getName()
                                                   .equals("CFLOAT_10")) {
                if (o instanceof Double) {
                    Double d = (Double) o;
                    assertEquals(tcd.getValue(), d, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Double expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CBOOLEAN")) {
                if (o instanceof Short) {
                    Short sh = (Short) o;
                    assertEquals(tcd.getValue(), sh != 0, "Invalid value for " + tcd.getType() + "!");
                }
            } else if (tcd.getName()
                          .equals("CDATE")) {
                if (o instanceof Timestamp) {
                    Timestamp ts = (Timestamp) o;
                    Date date = new Date(ts.getTime());
                    assertEquals(tcd.getValue(), date, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Timestamp expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CTIME")) {
                if (o instanceof Timestamp) {
                    Timestamp ts = (Timestamp) o;
                    Time time = new Time(ts.getTime());
                    assertEquals(tcd.getValue(), time, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Timestamp expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CTIMESTAMP")) {
                if (o instanceof Timestamp) {
                    Timestamp ts = (Timestamp) o;
                    assertEquals(tcd.getValue(), ts, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Timestamp expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CINTERVAL_YEAR_2_MONTH") || tcd.getName()
                                                                  .equals("CINTERVAL_DAY_2_SECONDS_6")) {
                if (o instanceof BigDecimal) {
                    Duration duration = Conversions.getDuration(o);
                    assertEquals(((Interval) tcd.getValue()).toDuration(), duration, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Duration expected for " + tcd.getType() + "!");
            } else fail("Unexpected column: " + tcd.getName() + "!");
        }
    }

    @SneakyThrows
    @Test
    public void testGetObjectNativeSimple() {
        openResultSet(false, _sNativeQuerySimple, true);
        for (int iColumn = 0; iColumn < TestAccessDatabase._listCdSimple.size(); iColumn++) {
            TestColumnDefinition tcd = TestAccessDatabase._listCdSimple.get(iColumn);
            Object o = getResultSet().getObject(tcd.getName());
            if (tcd.getName()
                   .equals("CCOUNTER") || tcd.getName()
                                             .equals("CINTEGER")) {
                if (o instanceof Integer) {
                    Integer i = (Integer) o;
                    assertEquals(tcd.getValue(), i, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Integer expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CBYTE") || tcd.getName()
                                                 .equals("CSMALLINT")) {
                if (o instanceof Short) {
                    Short sh = (Short) o;
                    Short shExpected = null;
                    if (tcd.getValue() instanceof Byte) {
                        Byte by = (Byte) tcd.getValue();
                        shExpected = by.shortValue();
                    } else shExpected = (Short) tcd.getValue();
                    assertEquals(shExpected, sh, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Short expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CDECIMAL_10_5") || tcd.getName()
                                                         .equals("CNUMERIC_18") || tcd.getName()
                                                                                      .equals("CCURRENCY")) {
                if (o instanceof BigDecimal) {
                    BigDecimal bdExpected = null;
                    if (tcd.getValue() instanceof BigInteger)
                        bdExpected = new BigDecimal((BigInteger) tcd.getValue());
                    else bdExpected = (BigDecimal) tcd.getValue();
                    BigDecimal bd = (BigDecimal) o;
                    assertEquals(bdExpected, bd, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type BigDecimal expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CREAL")) {
                if (o instanceof Float) {
                    Float f = (Float) o;
                    assertEquals(tcd.getValue(), f, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Float expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CDOUBLE")) {
                if (o instanceof Double) {
                    Double d = (Double) o;
                    assertEquals(tcd.getValue(), d, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Double expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CDATETIME")) {
                if (o instanceof Timestamp) {
                    Timestamp ts = (Timestamp) o;
                    assertEquals(tcd.getValue(), ts, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Timestamp expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CCHAR_254")) {
                if (o instanceof String) {
                    String s = (String) o;
                    String sExpected = (String) tcd.getValue();
                    s = s.substring(0, sExpected.length());
                    assertEquals(sExpected, s, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type String expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CVARCHAR_254")) {
                if (o instanceof String) {
                    String s = (String) o;
                    assertEquals(tcd.getValue(), s, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type String expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CLONGCHAR")) {
                if (o instanceof Clob) {
                    Clob clob = (Clob) o;
                    assertEquals(tcd.getValue(), clob.getSubString(1L, (int) clob.length()), "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Clob expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CBINARY")) {
                if (o instanceof byte[]) {
                    byte[] buf = (byte[]) o;
                    byte[] bufExpected = (byte[]) tcd.getValue();
                    buf = Arrays.copyOf(buf, bufExpected.length);
                    assertArrayEquals(bufExpected, buf, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type byte[] expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CVARBINARY")) {
                if (o instanceof byte[]) {
                    byte[] buf = (byte[]) o;
                    assertArrayEquals((byte[]) tcd.getValue(), buf, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type byte[] expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CLONGBINARY")) {
                if (o instanceof Blob) {
                    Blob blob = (Blob) o;
                    assertArrayEquals((byte[]) tcd.getValue(), blob.getBytes(1L, (int) blob.length()), "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Blob expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CGUID")) {
                if (o instanceof String) {
                    String s = (String) o;
                    UUID uuid = UUID.fromString(s.substring(1, s.length() - 1));
                    assertEquals(tcd.getValue(), uuid, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type String expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("CBIT")) {
                if (o instanceof Boolean) {
                    Boolean b = (Boolean) o;
                    assertEquals(tcd.getValue(), b, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Boolean expected for " + tcd.getType() + "!");
            }
        }
    }

    @Test
    public void testGetObjectNativeComplex() throws SQLException, IOException {
        openResultSet(false, _sNativeQueryComplex, true);
        for (int iColumn = 0; iColumn < TestAccessDatabase._listCdComplex.size(); iColumn++) {
            TestColumnDefinition tcd = TestAccessDatabase._listCdComplex.get(iColumn);
            Object o = getResultSet().getObject(tcd.getName());
            if (tcd.getName()
                   .equals("id") || tcd.getName()
                                       .equals("COLLONG")) {
                if (o instanceof Integer) {
                    Integer i = (Integer) o;
                    assertEquals(tcd.getValue(), i, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Integer expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLINT") || tcd.getName()
                                                  .equals("COLBYTE")) {
                if (o instanceof Short) {
                    Short sh = (Short) o;
                    Short shExpected = null;
                    if (tcd.getValue() instanceof Byte) {
                        Byte by = (Byte) tcd.getValue();
                        shExpected = by.shortValue();
                    } else shExpected = (Short) tcd.getValue();
                    assertEquals(shExpected, sh, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Short expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLDECIMAL") || tcd.getName()
                                                      .equals("COLMONEY")) {
                if (o instanceof BigDecimal) {
                    BigDecimal bd = (BigDecimal) o;
                    assertEquals(tcd.getValue(), bd, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type BigDecimal expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLFLOAT")) {
                if (o instanceof Float) {
                    Float f = (Float) o;
                    assertEquals(tcd.getValue(), f, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Float expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLDOUBLE")) {
                if (o instanceof Double) {
                    Double d = (Double) o;
                    assertEquals(tcd.getValue(), d, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Double expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLDATE")) {
                if (o instanceof Timestamp) {
                    Timestamp ts = (Timestamp) o;
                    Date date = new Date(ts.getTime());
                    assertEquals(tcd.getValue(), date, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Timestamp expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLTIME")) {
                if (o instanceof Timestamp) {
                    Timestamp ts = (Timestamp) o;
                    // the timestamp is negative, because it is based on 1899-12-30!
                    Time time = new Time(ts.getTime() % (24 * 60 * 60 * 1000) + (24 * 60 * 60 * 1000));
                    assertEquals(tcd.getValue(), time, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Timestamp expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLDATETIME")) {
                if (o instanceof Timestamp) {
                    Timestamp ts = (Timestamp) o;
                    assertEquals(tcd.getValue(), ts, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Timestamp expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLTEXT")) {
                if (o instanceof String) {
                    String s = (String) o;
                    assertEquals(tcd.getValue(), s, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type String expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLMEMO") || tcd.getName()
                                                   .equals("COLRICHTEXT")) {
                if (o instanceof Clob) {
                    Clob clob = (Clob) o;
                    assertEquals(tcd.getValue(), clob.getSubString(1L, (int) clob.length()), "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Clob expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLBOOLEAN")) {
                if (o instanceof Boolean) {
                    Boolean b = (Boolean) o;
                    assertEquals(tcd.getValue(), b, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Boolean expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLLOOKUP")) {
                if (o instanceof Array) {
                    Array array = (Array) o;
                    Object[] ao = (Object[]) array.getArray();
                    assertArrayEquals((Object[]) tcd.getValue(), ao, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Array expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLATTACH")) {
                if (o instanceof Array) {
                    Array array = (Array) o;
                    Object[] ao = (Object[]) array.getArray();
                    assertEquals(1, ao.length, "Invalid length for " + tcd.getType() + "!");
                    Blob blob = (Blob) ao[0];
                    byte[] buf = blob.getBytes(1L, (int) blob.length());
                    byte[] bufExpected = new byte[buf.length];
                    File file = (File) tcd.getValue();
                    FileInputStream fis = new FileInputStream(file);
                    int iRead = fis.read(bufExpected);
                    assertEquals(buf.length, iRead, "Attachment value is too long!");
                    assertEquals(-1, fis.read(), "Attachment value is too short!");
                    fis.close();
                    assertArrayEquals(bufExpected, buf, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Array expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLOLE")) {
                if (o instanceof Blob) {
                    Blob blob = (Blob) o;
                    byte[] buf = blob.getBytes(1L, (int) blob.length());
                    byte[] bufExpected = new byte[buf.length];
                    File file = (File) tcd.getValue();
                    FileInputStream fis = new FileInputStream(file);
                    int iRead = fis.read(bufExpected);
                    assertEquals(buf.length, iRead, "OLE value is too long!");
                    assertEquals(-1, fis.read(), "OLE value is too short!");
                    fis.close();
                    assertArrayEquals(bufExpected, buf, "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Blob expected for " + tcd.getType() + "!");
            } else if (tcd.getName()
                          .equals("COLLINK")) {
                if (o instanceof Clob) {
                    Clob clob = (Clob) o;
                    assertEquals("#" + tcd.getValue() + "#", clob.getSubString(1L, (int) clob.length()), "Invalid value for " + tcd.getType() + "!");
                } else fail("Type Clob expected for " + tcd.getType() + "!");
            }
        }
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateNull() {
        getResultSet().updateNull(TestSqlDatabase._listCdSimple.get(0)
                                                               .getName());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateString() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARCHAR_255");
        getResultSet().updateString(tcd.getName(), (String) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateNString() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CNVARCHAR_127");
        getResultSet().updateNString(tcd.getName(), (String) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateClob() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CCLOB_2M");
        Clob clob = getResultSet().getStatement()
                                  .getConnection()
                                  .createClob();

        clob.setString(1L, (String) tcd.getValue());
        getResultSet().updateClob(tcd.getName(), clob);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateClob_Reader() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CCLOB_2M");
        Reader rdr = new StringReader((String) tcd.getValue());
        getResultSet().updateClob(tcd.getName(), rdr);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateClob_Reader_Long() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CCLOB_2M");
        Reader rdr = new StringReader((String) tcd.getValue());
        getResultSet().updateClob(tcd.getName(), rdr, ((String) tcd.getValue()).length());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateNClob() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CNCLOB_1M");
        NClob nclob = getResultSet().getStatement()
                                    .getConnection()
                                    .createNClob();
        nclob.setString(1L, (String) tcd.getValue());
        getResultSet().updateNClob(tcd.getName(), nclob);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateNClob_Reader() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CNCLOB_1M");
        Reader rdr = new StringReader((String) tcd.getValue());
        getResultSet().updateNClob(tcd.getName(), rdr);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateNClob_Reader_Long() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CNCLOB_1M");
        Reader rdr = new StringReader((String) tcd.getValue());
        getResultSet().updateNClob(tcd.getName(), rdr, ((String) tcd.getValue()).length());
    }


    @SneakyThrows
    @Override
    @Test
    public void testUpdateSqlXml() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CXML");
        SQLXML sqlxml = getResultSet().getStatement()
                                      .getConnection()
                                      .createSQLXML();
        sqlxml.setString((String) tcd.getValue());
        getResultSet().updateSQLXML(tcd.getName(), sqlxml);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateBytes() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARBINARY_255");
        getResultSet().updateBytes(tcd.getName(), (byte[]) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateBlob() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CBLOB");
        Blob blob = getResultSet().getStatement()
                                  .getConnection()
                                  .createBlob();
        blob.setBytes(1, (byte[]) tcd.getValue());
        getResultSet().updateBlob(tcd.getName(), blob);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateBlob_InputStream() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CBLOB");
        InputStream is = new ByteArrayInputStream((byte[]) tcd.getValue());
        getResultSet().updateBlob(tcd.getName(), is);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateBlob_InputStream_Long() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CBLOB");
        InputStream is = new ByteArrayInputStream((byte[]) tcd.getValue());
        getResultSet().updateBlob(tcd.getName(), is, ((byte[]) tcd.getValue()).length);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateBigDecimal() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CDECIMAL_15_5");
        getResultSet().updateBigDecimal(tcd.getName(), (BigDecimal) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateByte() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CBOOLEAN");
        getResultSet().updateByte(tcd.getName(), (Boolean) tcd.getValue() ? (byte) 1 : (byte) 0);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateShort() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CSMALLINT");
        getResultSet().updateShort(tcd.getName(), (Short) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateInt() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CINTEGER");
        getResultSet().updateInt(tcd.getName(), (Integer) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateLong() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CBIGINT");
        getResultSet().updateLong(tcd.getName(), (Long) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateFloat() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CREAL");
        getResultSet().updateFloat(tcd.getName(), (Float) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateDouble() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CDOUBLE");
        getResultSet().updateDouble(tcd.getName(), (Double) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateBoolean() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CBOOLEAN");
        getResultSet().updateBoolean(tcd.getName(), (Boolean) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateDate() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CDATE");
        getResultSet().updateDate(tcd.getName(), (Date) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateTime() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CTIME");
        getResultSet().updateTime(tcd.getName(), (Time) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateTimestamp() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CTIMESTAMP");
        getResultSet().updateTimestamp(tcd.getName(), (Timestamp) tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateDuration() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CINTERVAL_YEAR_2_MONTH");
        getBaseResultSet().updateDuration(tcd.getName(), ((Interval) tcd.getValue()).toDuration());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateAsciiStream() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARCHAR_255");
        InputStream is = new ByteArrayInputStream(((String) tcd.getValue()).getBytes());
        getResultSet().updateAsciiStream(tcd.getName(), is);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateAsciiStream_Int() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARCHAR_255");
        InputStream is = new ByteArrayInputStream(((String) tcd.getValue()).getBytes());
        getResultSet().updateAsciiStream(tcd.getName(), is, ((String) tcd.getValue()).length());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateAsciiStream_Long() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARCHAR_255");
        InputStream is = new ByteArrayInputStream(((String) tcd.getValue()).getBytes());
        getResultSet().updateAsciiStream(tcd.getName(), is, (long) ((String) tcd.getValue()).length());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateCharacterStream() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARCHAR_255");
        Reader rdr = new StringReader((String) tcd.getValue());
        getResultSet().updateCharacterStream(tcd.getName(), rdr);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateCharacterStream_Int() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARCHAR_255");
        Reader rdr = new StringReader((String) tcd.getValue());
        getResultSet().updateCharacterStream(tcd.getName(), rdr, ((String) tcd.getValue()).length());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateCharacterStream_Long() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARCHAR_255");
        Reader rdr = new StringReader((String) tcd.getValue());
        getResultSet().updateCharacterStream(tcd.getName(), rdr, (long) ((String) tcd.getValue()).length());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateNCharacterStream() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CNVARCHAR_127");
        Reader rdr = new StringReader((String) tcd.getValue());
        getResultSet().updateCharacterStream(tcd.getName(), rdr);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateNCharacterStream_Int() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CNVARCHAR_127");
        Reader rdr = new StringReader((String) tcd.getValue());
        getResultSet().updateCharacterStream(tcd.getName(), rdr, ((String) tcd.getValue()).length());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateNCharacterStream_Long() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CNVARCHAR_127");
        Reader rdr = new StringReader((String) tcd.getValue());
        getResultSet().updateCharacterStream(tcd.getName(), rdr, (long) ((String) tcd.getValue()).length());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateBinaryStream() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARBINARY_255");
        InputStream is = new ByteArrayInputStream((byte[]) tcd.getValue());
        getResultSet().updateBinaryStream(tcd.getName(), is);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateBinaryStream_Int() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARBINARY_255");
        InputStream is = new ByteArrayInputStream((byte[]) tcd.getValue());
        getResultSet().updateBinaryStream(tcd.getName(), is, ((byte[]) tcd.getValue()).length);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateBinaryStream_Long() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CVARBINARY_255");
        InputStream is = new ByteArrayInputStream((byte[]) tcd.getValue());
        getResultSet().updateBinaryStream(tcd.getName(), is, (long) ((byte[]) tcd.getValue()).length);
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateArray() {
        openResultSet(false, _sNativeQueryComplex, true);
        TestColumnDefinition tcd = findColumnDefinition(TestAccessDatabase._listCdComplex, "COLLOOKUP");
        Object[] ao = (Object[]) tcd.getValue();
        Array array = new AccessArray("VARCHAR(128)", ao);
        getResultSet().updateArray(tcd.getName(), array);
    }

    @Override
    @Test
    public void testUpdateRef() {
        // no REFs in DB/2 tables
    }

    @Override
    @Test
    public void testUpdateRowId() {
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateObject() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CDATE");
        getResultSet().updateObject(tcd.getName(), tcd.getValue());
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateObject_Int() {
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CDECIMAL_15_5");
        getResultSet().updateObject(tcd.getName(), tcd.getValue(), 3);
    }

    @SneakyThrows
    @Override
    @Test
    public void testInsertRow() {
        /* if result were empty, then one could not execute next() */
        openResultSet(true, _sSqlQuerySimple, false);
        TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(TestSqlDatabase._iPrimarySimple);
        getResultSet().moveToInsertRow();
        // primary key must not be null
        getResultSet().updateInt(tcd.getName(), 2);
        getResultSet().insertRow();
        // restore the database
        tearDown();
        setUpClass();
    }

    @SneakyThrows
    @Override
    @Test
    public void testUpdateRow() {
        getResultSet().updateRow();
        // restore the database
        tearDown();
        setUpClass();
    }

    @SneakyThrows
    @Override
    @Test
    public void testDeleteRow() {
        getResultSet().deleteRow();
        // restore the database
        tearDown();
        setUpClass();
    }

    @Override
    @Test
    public void testRefreshRow() throws SQLException {
        enter();
        getResultSet().refreshRow();
    }

    @Test
    public void testInsertRowSqlSimple() throws IOException, SQLException {
        enter();
        getResultSet().moveToInsertRow();
        /* insert new values */
        TestColumnDefinition tcd = findColumnDefinition(_listCdSimple, "CCHAR_5");
        getResultSet().updateString(tcd.getName(), (String) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CVARCHAR_255");
        getResultSet().updateString(tcd.getName(), (String) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CCLOB_2M");
        Clob clob = getResultSet().getStatement()
                                  .getConnection()
                                  .createClob();
        clob.setString(1L, (String) tcd.getValue());
        getResultSet().updateClob(tcd.getName(), clob);
        tcd = findColumnDefinition(_listCdSimple, "CNCHAR_5");
        getResultSet().updateString(tcd.getName(), (String) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CNVARCHAR_127");
        getResultSet().updateString(tcd.getName(), (String) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CNCLOB_1M");
        NClob nclob = getResultSet().getStatement()
                                    .getConnection()
                                    .createNClob();
        nclob.setString(1L, (String) tcd.getValue());
        getResultSet().updateNClob(tcd.getName(), nclob);
        tcd = findColumnDefinition(_listCdSimple, "CXML");
        SQLXML sqlxml = getResultSet().getStatement()
                                      .getConnection()
                                      .createSQLXML();
        sqlxml.setString((String) tcd.getValue());
        getResultSet().updateSQLXML(tcd.getName(), sqlxml);
        tcd = findColumnDefinition(_listCdSimple, "CBINARY_5");
        getResultSet().updateBytes(tcd.getName(), (byte[]) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CVARBINARY_255");
        getResultSet().updateBytes(tcd.getName(), (byte[]) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CBLOB");
        Blob blob = getResultSet().getStatement()
                                  .getConnection()
                                  .createBlob();
        blob.setBytes(1L, (byte[]) tcd.getValue());
        getResultSet().updateBlob(tcd.getName(), blob);
        tcd = findColumnDefinition(_listCdSimple, "CNUMERIC_28");
        getResultSet().updateBigDecimal(tcd.getName(), new BigDecimal((BigInteger) tcd.getValue()));
        tcd = findColumnDefinition(_listCdSimple, "CDECIMAL_15_5");
        getResultSet().updateBigDecimal(tcd.getName(), (BigDecimal) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CSMALLINT");
        getResultSet().updateShort(tcd.getName(), (Short) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CINTEGER");
        getResultSet().updateInt(tcd.getName(), (Integer) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CBIGINT");
        getResultSet().updateLong(tcd.getName(), (Long) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CFLOAT_10");
        getResultSet().updateFloat(tcd.getName(), (Float) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CREAL");
        getResultSet().updateFloat(tcd.getName(), (Float) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CDOUBLE");
        getResultSet().updateDouble(tcd.getName(), (Double) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CBOOLEAN");
        getResultSet().updateBoolean(tcd.getName(), (Boolean) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CDATE");
        getResultSet().updateDate(tcd.getName(), (Date) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CTIME");
        getResultSet().updateTime(tcd.getName(), (Time) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CTIMESTAMP");
        getResultSet().updateTimestamp(tcd.getName(), (Timestamp) tcd.getValue());
        tcd = findColumnDefinition(_listCdSimple, "CINTERVAL_YEAR_2_MONTH");
        getBaseResultSet().updateDuration(tcd.getName(), ((Interval) tcd.getValue()).toDuration());
        tcd = findColumnDefinition(_listCdSimple, "CINTERVAL_DAY_2_SECONDS_6");
        getBaseResultSet().updateDuration(tcd.getName(), ((Interval) tcd.getValue()).toDuration());

        /* insert */
        getResultSet().insertRow();
        getResultSet().moveToCurrentRow();
        /* close and reopen result set */
        openResultSet(true, _sSqlQuerySimple, true);
        tcd = findColumnDefinition(_listCdSimple, "CINTEGER");
        /* find inserted row */
        while ((getResultSet().getInt(tcd.getName()) != (Integer) tcd.getValue()) && getResultSet().next()) {
        }
        tcd = findColumnDefinition(_listCdSimple, "CCHAR_5");
        assertEquals(tcd.getValue(), (getResultSet().getString(tcd.getName())).substring(0, ((String) tcd.getValue()).length()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CVARCHAR_255");
        assertEquals(tcd.getValue(), getResultSet().getString(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CCLOB_2M");
        clob = getResultSet().getClob(tcd.getName());
        assertEquals(tcd.getValue(), clob.getSubString(1L, (int) clob.length()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CNCHAR_5");
        assertEquals(tcd.getValue(), (getResultSet().getString(tcd.getName())).substring(0, ((String) tcd.getValue()).length()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CNVARCHAR_127");
        assertEquals(tcd.getValue(), getResultSet().getString(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CNCLOB_1M");
        nclob = getResultSet().getNClob(tcd.getName());
        assertEquals(tcd.getValue(), nclob.getSubString(1L, (int) nclob.length()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CXML");
        sqlxml = getResultSet().getSQLXML(tcd.getName());
        assertEquals(tcd.getValue(), sqlxml.getString(), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CBINARY_5");
        assertArrayEquals((byte[]) tcd.getValue(), Arrays.copyOf(getResultSet().getBytes(tcd.getName()), ((byte[]) tcd.getValue()).length), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CVARBINARY_255");
        assertArrayEquals((byte[]) tcd.getValue(), getResultSet().getBytes(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CBLOB");
        blob = getResultSet().getBlob(tcd.getName());
        assertArrayEquals((byte[]) tcd.getValue(), blob.getBytes(1L, (int) blob.length()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CNUMERIC_28");
        assertEquals(tcd.getValue(), getResultSet().getBigDecimal(tcd.getName())
                                                                                              .toBigInteger(), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CDECIMAL_15_5");
        assertEquals(tcd.getValue(), getResultSet().getBigDecimal(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CSMALLINT");
        assertEquals(((Short) tcd.getValue()).shortValue(), getResultSet().getShort(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CINTEGER");
        assertEquals(((Integer) tcd.getValue()).intValue(), getResultSet().getInt(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CBIGINT");
        assertEquals(((Long) tcd.getValue()).longValue(), getResultSet().getLong(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CFLOAT_10");
        assertEquals(tcd.getValue(), getResultSet().getFloat(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CREAL");
        assertEquals(tcd.getValue(), getResultSet().getFloat(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CDOUBLE");
        assertEquals(tcd.getValue(), getResultSet().getDouble(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CBOOLEAN");
        assertEquals(tcd.getValue(), getResultSet().getBoolean(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CDATE");
        assertEquals(tcd.getValue(), getResultSet().getDate(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CTIME");
        assertEquals(tcd.getValue(), getResultSet().getTime(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CTIMESTAMP");
        assertEquals(tcd.getValue(), getResultSet().getTimestamp(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CINTERVAL_YEAR_2_MONTH");
        assertEquals(((Interval) tcd.getValue()).toDuration(), getBaseResultSet().getDuration(tcd.getName()), "Insert of " + tcd.getType() + " failed!");
        tcd = findColumnDefinition(_listCdSimple, "CINTERVAL_DAY_2_SECONDS_6");
        Date dateZero = new Date(0L);
        assertEquals(((Interval) tcd.getValue()).toDuration()
                                                                                           .getTimeInMillis(dateZero) / 1000, getBaseResultSet().getDuration(tcd.getName())
                                                                                                                                                .getTimeInMillis(dateZero) / 1000, "Insert of " + tcd.getType() + " failed!");

        // restore the database
        tearDown();
        setUpClass();
    }

    @Override
    @Test
    public void testMoveToInsertRow() throws SQLException {
        enter();
        getResultSet().moveToInsertRow();
    }
}
