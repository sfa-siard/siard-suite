package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.access.TestAccessDatabase;
import ch.admin.bar.siard2.access.TestSqlDatabase;
import ch.admin.bar.siard2.jdbcx.AccessDataSource;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.FU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.database.SqlTypes;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;
import ch.enterag.utils.jdbc.BaseDatabaseMetaDataTester;
import ch.enterag.utils.lang.Execute;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class AccessDatabaseMetaDataTester extends BaseDatabaseMetaDataTester {
    private static final File fileTEST_EMPTY_DATABASE = new File("src/test/resources/testfiles/testempty.accdb");
    private static final File fileTEST_ACCESS_SOURCE = new File("src/test/resources/testfiles/testaccess.accdb");
    private static final File fileTEST_ACCESS_DATABASE = new File("src/test/resources/tmp/testaccess.accdb");
    private static final File fileTEST_SQL_DATABASE = new File("src/test/resources/tmp/testsql.accdb");
    private static final String sUSER = "Admin";
    private static final String sPASSWORD = "";
    private static final Pattern _patTYPE = Pattern.compile("^(.*?)(\\(\\s*((\\d+)(\\s*,\\s*(\\d+))?)\\s*\\))?$");

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

    private void setUp(boolean bSql) throws SQLException {
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
        setDatabaseMetaData(connAccess.getMetaData());
    }

    @BeforeEach
    public void setUp() throws SQLException {
        setUp(true);
    }

    @Test
    public void testClass() {
        assertEquals(AccessDatabaseMetaData.class, getDatabaseMetaData().getClass(), "Wrong database meta data class!");
    }

    @Test
    public void testMatches() throws SQLException {
        BaseDatabaseMetaData bdmd = (BaseDatabaseMetaData) getDatabaseMetaData();
        assertTrue(AccessDatabaseMetaData.matches(bdmd.toPattern("ZLA_LAND"), "ZLA_LAND"), "Underscore in name fails!");
        assertTrue(AccessDatabaseMetaData.matches(bdmd.toPattern("ZLA%readme"), "ZLA%readme"), "Percent in name fails!");
        assertTrue(AccessDatabaseMetaData.matches(bdmd.toPattern("ZLA.LAND"), "ZLA.LAND"), "Other special character in name fails!");
        assertTrue(AccessDatabaseMetaData.matches("%", "ZLA_readme"), "Percent does not match all!");
        assertTrue(AccessDatabaseMetaData.matches("ZLA_readme", "ZLAPreadme"), "Underscore does not match single letter!");
        assertTrue(AccessDatabaseMetaData.matches("ZLA_readme", "ZLA.readme"), "Underscore does not match special character!");
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetTypeInfo() {
        print(getDatabaseMetaData().getTypeInfo());
    }

    /***
     @Test public void testBug456GetColumns()
     {
     try
     {
     tearDown();
     FU.copy(new File("D:\\Projekte\\SIARD2\\Bugs\\456\\spatz\\spatz.accdb"), fileTEST_ACCESS_DATABASE);
     setUp(false);
     print(getDatabaseMetaData().getColumns(null, "Admin", "Abfrage1", "%"));
     }
     catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
     catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
     }
     ***/

    @Test
    public void testGetColumnsSqlSimple() throws SQLException {
        Map<String, TestColumnDefinition> mapCd = new HashMap<String, TestColumnDefinition>();
        for (int iColumn = 0; iColumn < TestSqlDatabase._listCdSimple.size(); iColumn++) {
            TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(iColumn);
            mapCd.put(tcd.getName(), tcd);
        }
        QualifiedId qiSimple = TestSqlDatabase.getQualifiedSimpleTable();
        ResultSet rs = getDatabaseMetaData().getColumns(qiSimple.getCatalog(), qiSimple.getSchema(), qiSimple.getName(), "%");
        while (rs.next()) {
            String sColumnName = rs.getString("COLUMN_NAME");
            int iDataType = rs.getInt("DATA_TYPE");
            String sTypeName = rs.getString("TYPE_NAME");
            int iColumnSize = rs.getInt("COLUMN_SIZE");
            switch (sTypeName) {
                case "BYTE":
                    assertEquals(Types.SMALLINT, iDataType, "Invalid BYTE mapping!");
                    break;
                case "INT":
                    assertEquals(Types.SMALLINT, iDataType, "Invalid INT mapping!");
                    break;
                case "LONG":
                    assertEquals(Types.INTEGER, iDataType, "Invalid LONG mapping!");
                    break;
                case "MONEY":
                    assertEquals(Types.DECIMAL, iDataType, "Invalid MONEY mapping!");
                    break;
                case "NUMERIC":
                    assertEquals(Types.NUMERIC, iDataType, "Invalid NUMERIC mapping!");
                    break;
                case "FLOAT":
                    assertEquals(Types.REAL, iDataType, "Invalid FLOAT mapping!");
                    break;
                case "DOUBLE":
                    assertEquals(Types.DOUBLE, iDataType, "Invalid DOUBLE mapping!");
                    break;
                case "SHORT_DATE_TIME":
                    assertEquals(Types.TIMESTAMP, iDataType, "Invalid SHORT_DATE_TIME mapping!");
                    break;
                case "TEXT":
                    assertEquals(Types.VARCHAR, iDataType, "Invalid TEXT mapping!");
                    break;
                case "MEMO":
                    assertEquals(Types.CLOB, iDataType, "Invalid MEMO mapping!");
                    break;
                case "BINARY":
                    assertEquals(Types.BINARY, iDataType, "Invalid BINARY mapping!");
                    break;
                case "GUID":
                    assertEquals(Types.BINARY, iDataType, "Invalid GUID mapping!");
                    break;
                case "OLE":
                    assertEquals(Types.BLOB, iDataType, "Invalid OLE mapping!");
                    break;
                case "BOOLEAN":
                    assertEquals(Types.BOOLEAN, iDataType, "Invalid BIT mapping!");
                    break;
                default:
                    fail("Unexpected type name " + sTypeName + "!");
            }
            TestColumnDefinition tcd = mapCd.get(sColumnName);
            String sType = tcd.getType();
            // parse type
            if (!sType.startsWith("INTERVAL")) {
                Matcher matcher = _patTYPE.matcher(sType);
                if (matcher.matches()) {
                    /* compare column size with explicit precision */
                    String sPrecision = matcher.group(4);
                    if (sPrecision != null) {
                        int iPrecision = Integer.parseInt(sPrecision);
                        if (iDataType == Types.TIMESTAMP) iPrecision = iColumnSize;
                        if ((iDataType == Types.DOUBLE) || (iDataType == Types.FLOAT) || (iDataType == Types.REAL))
                            iPrecision = iColumnSize; // the explicit number of bits is irrelevant, the size is always 8 bytes
                        assertEquals(iPrecision, iColumnSize, "Explicit precision does not match!");
                    }
                }
            }
        }
        rs.close();
        print(getDatabaseMetaData().getColumns(qiSimple.getCatalog(), qiSimple.getSchema(), qiSimple.getName(), "%"));
    }

    @Test
    public void testGetColumnsAccessSimple() throws SQLException {
        tearDown();
        setUp(false);
        Map<String, TestColumnDefinition> mapCd = new HashMap<String, TestColumnDefinition>();
        for (int iColumn = 0; iColumn < TestAccessDatabase._listCdSimple.size(); iColumn++) {
            TestColumnDefinition tcd = TestAccessDatabase._listCdSimple.get(iColumn);
            mapCd.put(tcd.getName(), tcd);
        }
        QualifiedId qiSimple = TestAccessDatabase.getQualifiedSimpleTable();
        ResultSet rs = getDatabaseMetaData().getColumns(qiSimple.getCatalog(), qiSimple.getSchema(), qiSimple.getName(), "%");
        while (rs.next()) {
            String sColumnName = rs.getString("COLUMN_NAME");
            int iDataType = rs.getInt("DATA_TYPE");
            String sTypeName = rs.getString("TYPE_NAME");
            int iColumnSize = rs.getInt("COLUMN_SIZE");
            switch (sTypeName) {
                case "BYTE":
                    assertEquals(Types.SMALLINT, iDataType, "Invalid BYTE mapping!");
                    break;
                case "INT":
                    assertEquals(Types.SMALLINT, iDataType, "Invalid INT mapping!");
                    break;
                case "LONG":
                    assertEquals(Types.INTEGER, iDataType, "Invalid LONG mapping!");
                    break;
                case "MONEY":
                    assertEquals(Types.DECIMAL, iDataType, "Invalid MONEY mapping!");
                    break;
                case "NUMERIC":
                    assertEquals(Types.NUMERIC, iDataType, "Invalid NUMERIC mapping!");
                    break;
                case "FLOAT":
                    assertEquals(Types.REAL, iDataType, "Invalid FLOAT mapping!");
                    break;
                case "DOUBLE":
                    assertEquals(Types.DOUBLE, iDataType, "Invalid DOUBLE mapping!");
                    break;
                case "SHORT_DATE_TIME":
                    assertEquals(Types.TIMESTAMP, iDataType, "Invalid SHORT_DATE_TIME mapping!");
                    break;
                case "TEXT":
                    assertEquals(Types.VARCHAR, iDataType, "Invalid TEXT mapping!");
                    break;
                case "MEMO":
                    assertEquals(Types.CLOB, iDataType, "Invalid MEMO mapping!");
                    break;
                case "BINARY":
                    assertEquals(Types.BINARY, iDataType, "Invalid BINARY mapping!");
                    break;
                case "GUID":
                    assertEquals(Types.CHAR, iDataType, "Invalid GUID mapping!");
                    break;
                case "OLE":
                    assertEquals(Types.BLOB, iDataType, "Invalid OLE mapping!");
                    break;
                case "BOOLEAN":
                    assertEquals(Types.BOOLEAN, iDataType, "Invalid BIT mapping!");
                    break;
                default:
                    fail("Unexpected type name " + sTypeName + "!");
            }
            TestColumnDefinition tcd = mapCd.get(sColumnName);
            String sType = tcd.getType();
            if (sType.equals("GUID")) assertEquals(38, iColumnSize, "Invalid length for GUID string!");
            else {
                // parse type
                Matcher matcher = _patTYPE.matcher(sType);
                if (matcher.matches()) {
                    /* compare column size with explicit precision */
                    String sPrecision = matcher.group(4);
                    if (sPrecision != null) {
                        int iPrecision = Integer.parseInt(sPrecision);
                        if (iDataType == Types.TIMESTAMP) iPrecision = iColumnSize;
                        if ((iDataType == Types.DOUBLE) || (iDataType == Types.FLOAT) || (iDataType == Types.REAL))
                            iPrecision = iColumnSize; // the explicit number of bits is irrelevant, the size is always 8 bytes
                        assertEquals(iPrecision, iColumnSize, "Explicit precision does not match!");
                    }
                }
            }
        }
        rs.close();
        print(getDatabaseMetaData().getColumns(qiSimple.getCatalog(), qiSimple.getSchema(), qiSimple.getName(), "%"));
    }

    @Test
    public void testGetColumnsAccessComplex() throws SQLException {
        tearDown();
        setUp(false);
        Map<String, TestColumnDefinition> mapCd = new HashMap<String, TestColumnDefinition>();
        for (int iColumn = 0; iColumn < TestAccessDatabase._listCdComplex.size(); iColumn++) {
            TestColumnDefinition tcd = TestAccessDatabase._listCdComplex.get(iColumn);
            mapCd.put(tcd.getName(), tcd);
        }
        QualifiedId qiComplex = TestAccessDatabase.getQualifiedComplexTable();
        ResultSet rs = getDatabaseMetaData().getColumns(qiComplex.getCatalog(), qiComplex.getSchema(), qiComplex.getName(), "%");
        while (rs.next()) {
            String sColumnName = rs.getString("COLUMN_NAME");
            int iDataType = rs.getInt("DATA_TYPE");
            String sTypeName = rs.getString("TYPE_NAME");
            int iColumnSize = rs.getInt("COLUMN_SIZE");
            switch (sTypeName) {
                case "BYTE":
                    assertEquals(Types.SMALLINT, iDataType, "Invalid BYTE mapping!");
                    break;
                case "INT":
                    assertEquals(Types.SMALLINT, iDataType, "Invalid INT mapping!");
                    break;
                case "LONG":
                    assertEquals(Types.INTEGER, iDataType, "Invalid LONG mapping!");
                    break;
                case "MONEY":
                    assertEquals(Types.DECIMAL, iDataType, "Invalid MONEY mapping!");
                    break;
                case "NUMERIC":
                    assertEquals(Types.NUMERIC, iDataType, "Invalid NUMERIC mapping!");
                    break;
                case "FLOAT":
                    assertEquals(Types.REAL, iDataType, "Invalid FLOAT mapping!");
                    break;
                case "DOUBLE":
                    assertEquals(Types.DOUBLE, iDataType, "Invalid DOUBLE mapping!");
                    break;
                case "SHORT_DATE_TIME":
                    assertEquals(Types.TIMESTAMP, iDataType, "Invalid SHORT_DATE_TIME mapping!");
                    break;
                case "TEXT":
                    assertEquals(Types.VARCHAR, iDataType, "Invalid TEXT mapping!");
                    break;
                case "MEMO":
                    assertEquals(Types.CLOB, iDataType, "Invalid MEMO mapping!");
                    break;
                case "BINARY":
                    assertEquals(Types.BINARY, iDataType, "Invalid BINARY mapping!");
                    break;
                case "GUID":
                    assertEquals(Types.BINARY, iDataType, "Invalid GUID mapping!");
                    break;
                case "OLE":
                    assertEquals(Types.BLOB, iDataType, "Invalid OLE mapping!");
                    break;
                case "BOOLEAN":
                    assertEquals(Types.BOOLEAN, iDataType, "Invalid BIT mapping!");
                    break;
                case "VARCHAR(2) ARRAY[4]":
                    assertEquals(Types.ARRAY, iDataType, "Invalid multivalued ARRAY mapping!");
                    break;
                case "BLOB ARRAY[127]":
                    assertEquals(Types.ARRAY, iDataType, "Invalid attachment ARRAY mapping!");
                    break;
                default:
                    fail("Unexpected type name " + sTypeName + "!");
            }
            TestColumnDefinition tcd = mapCd.get(sColumnName);
            String sType = tcd.getType();
            // parse type
            Matcher matcher = _patTYPE.matcher(sType);
            if (matcher.matches()) {
                /* compare column size with explicit precision */
                String sPrecision = matcher.group(4);
                if (sPrecision != null) {
                    if (iDataType == Types.TIMESTAMP) iColumnSize = iColumnSize - 20;
                    int iPrecision = Integer.parseInt(sPrecision);
                    assertEquals(iPrecision, iColumnSize, "Explicit precision does not match!");
                }
            }
        }
        rs.close();
        print(getDatabaseMetaData().getColumns(qiComplex.getCatalog(), qiComplex.getSchema(), qiComplex.getName(), "%"));
    }

    @Test
    public void testGetColumnsViewSimple() throws SQLException {
        tearDown();
        setUp(false);
        QualifiedId qiView = TestAccessDatabase.getQualifiedSimpleView();
        ResultSet rs = getDatabaseMetaData().getColumns(qiView.getCatalog(), qiView.getSchema(), qiView.getName(), "%");
        while (rs.next()) {
            String sColumnName = rs.getString("COLUMN_NAME");
            int iDataType = rs.getInt("DATA_TYPE");
            String sTypeName = rs.getString("TYPE_NAME");
            System.out.println(sColumnName + ": " + iDataType + " (" + SqlTypes.getTypeName(iDataType) + ") " + sTypeName);
        }
        rs.close();
    }

    @Test
    public void testGetColumnsViewComplex() throws SQLException {
        tearDown();
        setUp(false);
        QualifiedId qiView = TestAccessDatabase.getQualifiedComplexView();
        ResultSet rs = getDatabaseMetaData().getColumns(qiView.getCatalog(), qiView.getSchema(), qiView.getName(), "%");
        while (rs.next()) {
            String sColumnName = rs.getString("COLUMN_NAME");
            int iDataType = rs.getInt("DATA_TYPE");
            String sTypeName = rs.getString("TYPE_NAME");
            System.out.println(sColumnName + ": " + iDataType + " (" + SqlTypes.getTypeName(iDataType) + ") " + sTypeName);
        }
        rs.close();
    }

    @SneakyThrows
    @Test
    public void testGetIndexInfo() {
        ResultSet rs = getDatabaseMetaData().getIndexInfo(null, null, "TABLETEST", true, false);
        while (rs.next()) {
            boolean bNonUnique = rs.getBoolean("NON_UNIQUE");
            if (!bNonUnique) {
                String sIndexName = rs.getString("INDEX_NAME");
                int iIndexType = rs.getInt("TYPE");
                String sIndexType = null;
                switch (iIndexType) {
                    case DatabaseMetaData.tableIndexClustered:
                        sIndexType = "tableIndexClustered";
                        break;
                    case DatabaseMetaData.tableIndexHashed:
                        sIndexType = "tableIndexHashed";
                        break;
                    case DatabaseMetaData.tableIndexOther:
                        sIndexType = "tableIndexOther";
                        break;
                    case DatabaseMetaData.tableIndexStatistic:
                        sIndexType = "tableIndexStatistic";
                        break;
                }
                int iOrdinalPosition = rs.getInt("ORDINAL_POSITION");
                String sColumnName = rs.getString("COLUMN_NAME");
                System.out.println(sIndexName + ": " + sIndexType + ", " + iOrdinalPosition + " " + sColumnName);
            } else System.err.println("Unexpected non-unique index found!!!");
        }
        rs.close();
    }

    @SneakyThrows
    @Test
    @Disabled("uses files not available anymore")
    public void testGetImportedKeys() {
        File fileBugDatabase = new File("..\\Bugs\\445\\Empty.accdb");
        File fileAccessDatabase = new File("logs\\Empty.accdb");
        FU.copy(fileBugDatabase, fileAccessDatabase);
        AccessDataSource dsAccess = new AccessDataSource();
        dsAccess.setDatabaseName(fileAccessDatabase.getAbsolutePath());
        dsAccess.setDescription("Bug 445 data base");
        dsAccess.setReadOnly(false);
        dsAccess.setUser(sUSER);
        dsAccess.setPassword(sPASSWORD);
        AccessConnection connAccess = (AccessConnection) dsAccess.getConnection();
        connAccess.setAutoCommit(false);
        AccessDatabaseMetaData dmdAccess = (AccessDatabaseMetaData) connAccess.getMetaData();
        ResultSet rs = dmdAccess.getImportedKeys(null, "Admin", "Main");
        rs.close();
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetTables() {
        AccessDatabaseMetaData dmdAccess = (AccessDatabaseMetaData) getDatabaseMetaData();
        print(dmdAccess.getTables(null, null, "%", new String[]{"TABLE"}));
    }

    @SneakyThrows
    @Test
    public void testGetViews() {
        AccessDatabaseMetaData dmdAccess = (AccessDatabaseMetaData) getDatabaseMetaData();
        print(dmdAccess.getTables(null, "%", "%", new String[]{"VIEW"}));
    }

}