package ch.admin.bar.siard2.access;

import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.base.TestUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class TestAccessDatabase {
    public static final String _sTEST_SCHEMA = null;
    private static final String sATTACHMENT = "src/test/resources/testfiles/Sämmtliche_Werke.pdf";
    private static final String sOLE_OBJECT = "src/test/resources/testfiles/zitrone.png";
    private static final String sHYPERLINK = "https://www.enterag.ch/";
    private static final String _sTEST_TABLE_SIMPLE = "TACCESSSIMPLE";
    private static final String _sTEST_TABLE_COMPLEX = "TABLETEST";
    private static final String _sTEST_VIEW_SIMPLE = "VACCESSSIMPLE";
    private static final String _sTEST_VIEW_COMPLEX = "VIEWTEST";
    public static int _iPrimarySimple = -1;
    public static int _iForeignSimple = -1;
    public static List<TestColumnDefinition> _listCdSimple = getListCdSimple();
    public static int _iPrimaryComplex = -1;
    public static List<TestColumnDefinition> _listCdComplex = getListCdComplex();

    static {
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("ODBC needed for creating test Access database! User JDK 1.7!");
        }
    }

    private Connection _conn = null;

    public TestAccessDatabase(File file) throws SQLException {

        String sUrl = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};" + "DBQ=" + file.getAbsolutePath() + ";" + "ExtendedAnsiSQL=1";
        _conn = DriverManager.getConnection(sUrl, "Admin", "");
        /*** Array can be created, but we prefer not to try this through ODBC ...
         Array array = _conn.createArrayOf("TYPEARRAY", asTypeInfoColumn);
         ***/
        /*** blob, clob and sqlxml fail:
         Clob clob = _conn.createClob();
         clob.setString(1l, "Blabla");
         SQLXML sqlxml = _conn.createSQLXML();
         sqlxml.setString("<p>Blabla</p>");
         ***/
        DatabaseMetaData dmd = _conn.getMetaData();
        System.out.println("Database: " + dmd.getDatabaseProductName() + " " + dmd.getDatabaseProductVersion());
        System.out.println("Driver: " + dmd.getDriverName() + " " + dmd.getDriverVersion());
    /* Type info
     * See also https://docs.microsoft.com/en-us/sql/odbc/microsoft/microsoft-access-data-types
    String[] asTypeInfoColumn = new String[] {
      "TYPE_NAME",
      "DATA_TYPE",
      "PRECISION",
      "FIXED_PREC_SCALE",
      "LOCAL_TYPE_NAME",
      "MINIMUM_SCALE",
      "MAXIMUM_SCALE"
    };
    for (int i = 0; i < asTypeInfoColumn.length; i++)
    {
      if (i > 0)
        System.out.print("\t");
      System.out.print(asTypeInfoColumn[i]);
    }
    System.out.println();
    ResultSet rsTypes = dmd.getTypeInfo();
    while (rsTypes.next())
    {
      for (int i = 0; i < asTypeInfoColumn.length; i++)
      {
        if (i > 0)
          System.out.print("\t");
        if ((i == 0) || (i == 4))
          System.out.print(rsTypes.getString(asTypeInfoColumn[i]));
        else if (i == 3)
          System.out.print(String.valueOf(rsTypes.getBoolean(asTypeInfoColumn[i])));
        else
          System.out.print(String.valueOf(rsTypes.getInt(asTypeInfoColumn[i])));
      }
      System.out.println();
    }
    rsTypes.close();
     */
        drop();
        create();
        _conn.close();
    }

    public static QualifiedId getQualifiedSimpleTable() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_TABLE_SIMPLE);
    }

    public static QualifiedId getQualifiedComplexTable() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_TABLE_COMPLEX);
    }

    public static QualifiedId getQualifiedSimpleView() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_VIEW_SIMPLE);
    }

    public static QualifiedId getQualifiedComplexView() {
        return new QualifiedId(null, _sTEST_SCHEMA, _sTEST_VIEW_COMPLEX);
    }

    @SuppressWarnings("deprecation")
    private static List<TestColumnDefinition> getListCdSimple() {
        List<TestColumnDefinition> listCdSimple = new ArrayList<>();
        _iPrimarySimple = listCdSimple.size(); // next column will be primary key column
        listCdSimple.add(new ColumnDefinition("CCOUNTER", "COUNTER", 1));
        listCdSimple.add(new ColumnDefinition("CBYTE", "BYTE", (byte) -100));
        listCdSimple.add(new ColumnDefinition("CSMALLINT", "SMALLINT", (short) -32000));
        _iForeignSimple = listCdSimple.size(); // next column will be foreign key column referencing primary key of complex table
        listCdSimple.add(new ColumnDefinition("CINTEGER", "INTEGER", 1));
        listCdSimple.add(new ColumnDefinition("CDECIMAL_10_5", "DECIMAL(10,5)", new BigDecimal(BigInteger.valueOf(3141592653L), 5)));
        listCdSimple.add(new ColumnDefinition("CNUMERIC_18", "NUMERIC(18)", BigInteger.valueOf(123456789012345678L)));
        listCdSimple.add(new ColumnDefinition("CCURRENCY", "CURRENCY", BigDecimal.valueOf(12.3456)));
        listCdSimple.add(new ColumnDefinition("CREAL", "REAL", (float) Math.PI));
        listCdSimple.add(new ColumnDefinition("CDOUBLE", "DOUBLE", Math.E));
        listCdSimple.add(new ColumnDefinition("CDATETIME", "DATETIME", new Timestamp(2017 - 1900, 3, 8, 17, 30, 43, 0)));
        listCdSimple.add(new ColumnDefinition("CCHAR_254", "CHAR(254)", TestUtils.getString(250)));
        listCdSimple.add(new ColumnDefinition("CVARCHAR_254", "VARCHAR(254)", TestUtils.getString(250)));
        listCdSimple.add(new ColumnDefinition("CLONGCHAR", "LONGCHAR", TestUtils.getString(1000000)));
        listCdSimple.add(new ColumnDefinition("CBINARY", "BINARY", TestUtils.getBytes(500)));
        listCdSimple.add(new ColumnDefinition("CGUID", "GUID", UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d")));
        listCdSimple.add(new ColumnDefinition("CVARBINARY", "VARBINARY", TestUtils.getBytes(500)));
        listCdSimple.add(new ColumnDefinition("CLONGBINARY", "LONGBINARY", TestUtils.getBytes(2000000)));
        listCdSimple.add(new ColumnDefinition("CBIT", "BIT", Boolean.TRUE));
        return listCdSimple;
    }

    @SuppressWarnings("deprecation")
    private static List<TestColumnDefinition> getListCdComplex() {
        List<TestColumnDefinition> listCdComplex = new ArrayList<>();
        _iPrimaryComplex = listCdComplex.size(); // next column will be primary key column
        listCdComplex.add(new ColumnDefinition("id", "INTEGER", 1));
        listCdComplex.add(new ColumnDefinition("COLTEXT", "TEXT", "text"));
        listCdComplex.add(new ColumnDefinition("COLMEMO", "MEMO", "memo"));
        listCdComplex.add(new ColumnDefinition("COLLONG", "LONG", 100000));
        listCdComplex.add(new ColumnDefinition("COLINT", "INT", (short) 10000));
        listCdComplex.add(new ColumnDefinition("COLBYTE", "BYTE", (byte) 100));
        listCdComplex.add(new ColumnDefinition("COLDECIMAL", "DECIMAL", BigDecimal.valueOf(1234567890L)));
        listCdComplex.add(new ColumnDefinition("COLDOUBLE", "DOUBLE", 12.3456789012));
        listCdComplex.add(new ColumnDefinition("COLFLOAT", "FLOAT", 98.76543F));
        listCdComplex.add(new ColumnDefinition("COLDATETIME", "DATETIME", new Timestamp(2017 - 1900, 2, 8, 11, 38, 0, 0)));
        listCdComplex.add(new ColumnDefinition("COLDATE", "DATE", new Date(2017 - 1900, 2, 8))); // month is 0-based!
        listCdComplex.add(new ColumnDefinition("COLTIME", "TIME", new Time(11, 38, 25)));
        listCdComplex.add(new ColumnDefinition("COLMONEY", "MONEY", BigDecimal.valueOf(123400, 4)));
        listCdComplex.add(new ColumnDefinition("COLBOOLEAN", "BIT", Boolean.TRUE));
        listCdComplex.add(new ColumnDefinition("COLLOOKUP", "LOOKUP", new String[]{"DE", "FR"}));
        listCdComplex.add(new ColumnDefinition("COLRICHTEXT", "RICHTEXT", "<div>RICH <strong>really</strong>?</div>"));
        listCdComplex.add(new ColumnDefinition("COLATTACH", "ATTACHMENT", new File(sATTACHMENT)));
        listCdComplex.add(new ColumnDefinition("COLOLE", "OLE_OBJECT", new File(sOLE_OBJECT)));
        listCdComplex.add(new ColumnDefinition("COLLINK", "HYPERLINK", sHYPERLINK));
        return listCdComplex;
    }

    private void drop() {
        dropViews();
        dropTables();
    }

    private void dropViews() {
        dropView(getQualifiedSimpleView());
    }

    private void dropView(QualifiedId qiView) {
        String sSql = "DROP VIEW " + qiView.format();
        try {
            Statement stmt = _conn.createStatement();
            stmt.executeUpdate(sSql);
            stmt.close();
            _conn.commit();
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
        }
    }

    private void dropTables() {
        dropTable(getQualifiedSimpleTable());
    }

    private void dropTable(QualifiedId qiTable) {
        String sSql = "DROP TABLE " + qiTable.format();
        try {
            Statement stmt = _conn.createStatement();
            stmt.executeUpdate(sSql);
            stmt.close();
            _conn.commit();
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
        }
    }

    private void create() throws SQLException {
        createTables();
        createViews();
        insertTables();
    }

    private List<TestColumnDefinition> getSingle(TestColumnDefinition cd) {
        List<TestColumnDefinition> listSingle = Collections.singletonList(cd);
        return listSingle;
    }

    private void createTables() throws SQLException {
        createTable(getQualifiedSimpleTable(), _listCdSimple, getSingle(_listCdSimple.get(_iPrimarySimple)), getSingle(_listCdSimple.get(_iForeignSimple)), getQualifiedComplexTable(), getSingle(_listCdComplex.get(_iPrimaryComplex)));
    }

    private void createTable(QualifiedId qiTable, List<TestColumnDefinition> listCd, List<TestColumnDefinition> listCdPrimary, List<TestColumnDefinition> listCdForeign, QualifiedId qiTableReferenced, List<TestColumnDefinition> listCdReferenced) throws SQLException {
        StringBuilder sbSql = new StringBuilder("CREATE TABLE ");
        sbSql.append(qiTable.format());
        sbSql.append("\r\n(\r\n  ");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = listCd.get(iColumn);
            sbSql.append(tcd.getName());
            sbSql.append(" ");
            sbSql.append(tcd.getType());
        }
        if (listCdPrimary != null) {
            sbSql.append(",\r\n  ");
            sbSql.append("CONSTRAINT " + "PK" + qiTable.getName() + " PRIMARY KEY(");
            for (TestColumnDefinition testColumnDefinition : listCdPrimary)
                sbSql.append(testColumnDefinition
                        .getName());
            sbSql.append(")");
        }
        if (listCdForeign != null) {
            sbSql.append(",\r\n  ");
            sbSql.append("CONSTRAINT " + "FK" + qiTable.getName() + " FOREIGN KEY(");
            for (TestColumnDefinition testColumnDefinition : listCdForeign)
                sbSql.append(testColumnDefinition
                        .getName());
            sbSql.append(") REFERENCES " + qiTableReferenced.format() + "(");
            for (int iReferenced = 0; iReferenced < listCdForeign.size(); iReferenced++)
                sbSql.append(listCdReferenced.get(iReferenced)
                        .getName());
            sbSql.append(")");
        }
        sbSql.append("\r\n)");
        Statement stmt = _conn.createStatement();
        stmt.executeUpdate(sbSql.toString());
        stmt.close();
        _conn.commit();
    }

    private void createViews() throws SQLException {
        createView(getQualifiedSimpleView(), _listCdSimple, getQualifiedSimpleTable());
    }

    private void createView(QualifiedId qiView, List<TestColumnDefinition> listCd, QualifiedId qiTable) throws SQLException {
        StringBuilder sbSql = new StringBuilder("CREATE VIEW ");
        sbSql.append(qiView.format());
        sbSql.append(" AS\r\nSELECT\r\n  ");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            sbSql.append(listCd.get(iColumn)
                    .getName());
        }
        sbSql.append("\r\nFROM ");
        sbSql.append(qiTable.format());
        Statement stmt = _conn.createStatement();
        stmt.executeUpdate(sbSql.toString());
        stmt.close();
        _conn.commit();
    }

    private void insertTables() throws SQLException {
        insertTable(getQualifiedSimpleTable(), _listCdSimple);
    }

    private void insertTable(QualifiedId qiTable, List<TestColumnDefinition> listCd) throws SQLException {
        StringBuilder sbSql = new StringBuilder("INSERT INTO ");
        sbSql.append(qiTable.format());
        sbSql.append("\r\n(\r\n  ");
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = listCd.get(iColumn);
            sbSql.append(tcd.getName());
        }
        sbSql.append("\r\n)\r\nVALUES\r\n(\r\n  ");
        Map<Integer, Object> mapParameters = new HashMap<>();
        int iParameter = 1;
        for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
            if (iColumn > 0) sbSql.append(",\r\n  ");
            TestColumnDefinition tcd = listCd.get(iColumn);
            String sLiteral = tcd.getValueLiteral();
            if (sLiteral.length() < 1000) sbSql.append(sLiteral);
            else {
                sbSql.append("?");
                mapParameters.put(iParameter, tcd.getValue());
                iParameter++;
            }
        }
        sbSql.append("\r\n)");
        PreparedStatement pstmt = _conn.prepareStatement(sbSql.toString());
        for (iParameter = 1; iParameter <= mapParameters.size(); iParameter++) {
            Object o = mapParameters.get(iParameter);
            if (o instanceof byte[]) {
                byte[] buf = (byte[]) o;
                InputStream isBlob = new ByteArrayInputStream(buf);
                pstmt.setBinaryStream(iParameter, isBlob, buf.length);
            } else if (o instanceof String) {
                String s = (String) o;
                Reader rdrClob = new StringReader(s);
                pstmt.setCharacterStream(iParameter, rdrClob, s.length());
            }
        }
        pstmt.executeUpdate();
        pstmt.close();
        _conn.commit();
    }

    /*
     * We cannot create a database with all needed features programmatically
     * because Jackcess does not support creating everything you can read.
     *
     * Therefore we use ODBC (32 bit!) to create the Access database.
     * This still does not support all features but it is better than nothing.
     */
    private static class ColumnDefinition extends TestColumnDefinition {
        public ColumnDefinition(String sName, String sType, Object oValue) {
            super(sName, sType, oValue);
        }

        @Override
        public String getValueLiteral() {
            String sValueLiteral = super.getValueLiteral();
            if (getValue() instanceof Timestamp) {
                sValueLiteral = sValueLiteral.substring("TIMESTAMP".length());
                int i = sValueLiteral.indexOf('.');
                if (i >= 0) sValueLiteral = sValueLiteral.substring(0, i) + "'";
            } else if (getValue() instanceof byte[])
                sValueLiteral = "0x" + sValueLiteral.substring(2, sValueLiteral.length() - 3);
            else if (getValue() instanceof Byte) sValueLiteral = ((Byte) getValue()).toString();
            else if (getValue() instanceof Boolean) {
                boolean b = (Boolean) getValue();
                if (b) sValueLiteral = "1";
                else sValueLiteral = "0";
            } else if (getValue() instanceof UUID)
                sValueLiteral = SqlLiterals.formatStringLiteral(getValue().toString());
            return sValueLiteral;
        }
    }
}
