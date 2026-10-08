package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.jdbcx.MySqlDataSource;
import ch.admin.bar.siard2.mysql.TestMySqlDatabase;
import ch.admin.bar.siard2.mysql.TestSqlDatabase;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.jdbc.BaseDatabaseMetaDataTester;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.MountableFile;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlDatabaseMetaDataTester extends BaseDatabaseMetaDataTester {
    private static final MySQLContainer<?> _mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testschema")
            .withUsername("testuser")
            .withPassword("testpwd")
            .withCopyFileToContainer(MountableFile.forClasspathResource("zzz-test-overrides.cnf"), "/etc/mysql/conf.d/zzz-test-overrides.cnf");

    private static String _sDB_URL;
    private static String _sDB_USER;
    private static String _sDB_PASSWORD;
    private static String _sDB_CATALOG;
    private static String _sDBA_USER;
    private static String _sDBA_PASSWORD;
    private static Pattern _patTYPE = Pattern.compile("^(.*?)(\\(\\s*((\\d+)(\\s*,\\s*(\\d+))?)\\s*\\))?$");

    private MySqlDatabaseMetaData _dmdMySql = null;

    @BeforeAll
    public static void setUpClass() {
        try {
            _mysql.start();
            _sDB_CATALOG = _mysql.getDatabaseName();
            _sDB_URL = MySqlDriver.getUrl(_mysql.getHost() + ":" + _mysql.getFirstMappedPort() + "/" + _sDB_CATALOG, true);
            _sDB_USER = _mysql.getUsername();
            _sDB_PASSWORD = _mysql.getPassword();
            _sDBA_USER = "root";
            _sDBA_PASSWORD = _mysql.getPassword();
            MySqlDataSource dsMySql = new MySqlDataSource();
            dsMySql.setUrl(_sDB_URL);
            dsMySql.setUser(_sDBA_USER);
            dsMySql.setPassword(_sDBA_PASSWORD);
            MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
            connMySql.setAutoCommit(false);
            TestMySqlDatabase.grantSchemaUser(connMySql,
                                              "mysql", _sDB_USER); // needed for some meta data!
            /* drop and create the test databases */
            new TestMySqlDatabase(connMySql);
            TestMySqlDatabase.grantSchemaUser(connMySql,
                                              TestMySqlDatabase._sTEST_SCHEMA, _sDB_USER);
            new TestSqlDatabase(connMySql);
            TestMySqlDatabase.grantSchemaUser(connMySql,
                                              TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
            connMySql.commit();
            connMySql.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @AfterAll
    public static void tearDownClass() {
        try {
            MySqlDataSource dsMySql = new MySqlDataSource();
            dsMySql.setUrl(_sDB_URL);
            dsMySql.setUser(_sDBA_USER);
            dsMySql.setPassword(_sDBA_PASSWORD);
            MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
            connMySql.setAutoCommit(false);
            TestMySqlDatabase.revokeSchemaUser(connMySql,
                                               "mysql", _sDB_USER);
            connMySql.commit();
            connMySql.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        } finally {
            _mysql.stop();
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        try {
            MySqlDataSource dsMySql = new MySqlDataSource();
            dsMySql.setUrl(_sDB_URL);
            dsMySql.setUser(_sDB_USER);
            dsMySql.setPassword(_sDB_PASSWORD);
            MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
            connMySql.setAutoCommit(false);
            _dmdMySql = (MySqlDatabaseMetaData) connMySql.getMetaData();
            setDatabaseMetaData(_dmdMySql);
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testClass() {
        assertEquals(MySqlDatabaseMetaData.class, _dmdMySql.getClass(), "Wrong result set meta class!");
    }

    @Test
    @Override
    public void testGetTypeInfo() {
        enter();
        try {
            print(_dmdMySql.getTypeInfo());
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    private void testColumns(QualifiedId qiTable, List<TestColumnDefinition> listCd) {
        try {
            Map<String, TestColumnDefinition> mapCd = new HashMap<String, TestColumnDefinition>();
            for (int iColumn = 0; iColumn < listCd.size(); iColumn++) {
                TestColumnDefinition tcd = listCd.get(iColumn);
                mapCd.put(tcd.getName(), tcd);
            }
            ResultSet rs = _dmdMySql.getColumns(
                    qiTable.getCatalog(),
                    _dmdMySql.toPattern(qiTable.getSchema()),
                    _dmdMySql.toPattern(qiTable.getName()),
                    "%");
            if ((rs != null) && (!rs.isClosed())) {
                while (rs.next()) {
                    String sCatalogName = rs.getString("TABLE_CAT");
                    String sSchemaName = rs.getString("TABLE_SCHEM");
                    String sTableName = rs.getString("TABLE_NAME");
                    if (sCatalogName != null) {
                        if (!_sDB_CATALOG.equalsIgnoreCase(sCatalogName))
                            fail("Unexpected catalog: " + sCatalogName);
                    }
                    if (!qiTable.getSchema()
                                .equals(sSchemaName.toUpperCase()))
                        fail("Unexpected schema: " + sSchemaName);
                    if (!qiTable.getName()
                                .equals(sTableName.toUpperCase()))
                        fail("Unexpected table: " + sTableName);
                    String sColumnName = rs.getString("COLUMN_NAME");
                    int iDataType = rs.getInt("DATA_TYPE");
                    String sTypeName = rs.getString("TYPE_NAME");
                    long lColumnSize = rs.getLong("COLUMN_SIZE");
                    // int iDecimalDigits = rs.getInt("DECIMAL_DIGITS");

                    // Extract base type name
                    String sBaseTypeName = sTypeName.toLowerCase();
                    int iParenIndex = sBaseTypeName.indexOf('(');
                    if (iParenIndex > 0) {
                        sBaseTypeName = sBaseTypeName.substring(0, iParenIndex);
                    }

                    switch (sBaseTypeName) {
                        case "char":
                            assertEquals(Types.CHAR, iDataType, "Invalid char mapping!");
                            break;
                        case "varchar":
                            assertEquals(Types.VARCHAR, iDataType, "Invalid varchar mapping!");
                            break;
                        case "text":
                            assertEquals(Types.CLOB, iDataType, "Invalid text mapping!");
                            break;
                        case "tinytext":
                            assertEquals(Types.VARCHAR, iDataType, "Invalid tinytext mapping!");
                            break;
                        case "mediumtext":
                            assertEquals(Types.CLOB, iDataType, "Invalid mediumtext mapping!");
                            break;
                        case "longtext":
                            assertEquals(Types.CLOB, iDataType, "Invalid longtext mapping!");
                            break;
                        case "binary":
                            assertEquals(Types.BINARY, iDataType, "Invalid binary mapping!");
                            break;
                        case "varbinary":
                            assertEquals(Types.VARBINARY, iDataType, "Invalid varbinary mapping!");
                            break;
                        case "blob":
                            assertEquals(Types.BLOB, iDataType, "Invalid blob mapping!");
                            break;
                        case "tinyblob":
                            assertEquals(Types.VARBINARY, iDataType, "Invalid tinyblob mapping!");
                            break;
                        case "mediumblob":
                            assertEquals(Types.BLOB, iDataType, "Invalid mediumblob mapping!");
                            break;
                        case "longblob":
                            assertEquals(Types.BLOB, iDataType, "Invalid longblob mapping!");
                            break;
                        case "int":
                            assertEquals(Types.INTEGER, iDataType, "Invalid int mapping!");
                            break;
                        case "int unsigned":
                            assertEquals(Types.BIGINT, iDataType, "Invalid int unsigned mapping!");
                            break;
                        case "tinyint":
                            assertEquals(Types.SMALLINT, iDataType, "Invalid tinyint mapping!");
                            break;
                        case "tinyint unsigned":
                            assertEquals(Types.SMALLINT, iDataType, "Invalid tinyint unsigned mapping!");
                            break;
                        case "smallint":
                            assertEquals(Types.SMALLINT, iDataType, "Invalid smallint mapping!");
                            break;
                        case "smallint unsigned":
                            assertEquals(Types.INTEGER, iDataType, "Invalid smallint unsigned mapping!");
                            break;
                        case "mediumint":
                            assertEquals(Types.INTEGER, iDataType, "Invalid mediumint mapping!");
                            break;
                        case "mediumint unsigned":
                            assertEquals(Types.BIGINT, iDataType, "Invalid mediumint unsigned mapping!");
                            break;
                        case "bigint":
                            assertEquals(Types.BIGINT, iDataType, "Invalid bigint mapping!");
                            break;
                        case "bigint unsigned":
                            assertEquals(Types.BIGINT, iDataType, "Invalid bigint unsigned mapping!");
                            break;
                        case "decimal":
                            assertEquals(Types.DECIMAL, iDataType, "Invalid decimal mapping!");
                            break;
                        case "numeric":
                            assertEquals(Types.DECIMAL, iDataType, "Invalid numeric mapping!");
                            break;
                        case "real":
                            assertEquals(Types.REAL, iDataType, "Invalid real mapping!");
                            break;
                        case "float":
                            assertEquals(Types.FLOAT, iDataType, "Invalid float mapping!");
                            break;
                        case "double":
                            assertEquals(Types.DOUBLE, iDataType, "Invalid double mapping!");
                            break;
                        case "bit":
                            if (sTypeName.equalsIgnoreCase("bit(1)")) {
                                assertEquals(Types.BOOLEAN, iDataType, "Invalid bit mapping!");
                            } else {
                                assertEquals(Types.BINARY, iDataType, "Invalid multibit mapping!");
                            }
                            break;
                        case "bool":
                            assertEquals(Types.BOOLEAN, iDataType, "Invalid bool mapping!");
                            break;
                        case "date":
                            assertEquals(Types.DATE, iDataType, "Invalid date mapping!");
                            break;
                        case "time":
                            assertEquals(Types.TIME, iDataType, "Invalid time mapping!");
                            break;
                        case "timestamp":
                            assertEquals(Types.TIMESTAMP, iDataType, "Invalid timestamp mapping!");
                            break;
                        case "datetime":
                            assertEquals(Types.TIMESTAMP, iDataType, "Invalid datetime mapping!");
                            break;
                        case "year":
                            assertEquals(Types.SMALLINT, iDataType, "Invalid year mapping!");
                            break;
                        case "geometry":
                            assertEquals(Types.CLOB, iDataType, "Invalid geometry mapping!");
                            break;
                        case "point":
                            assertEquals(Types.CLOB, iDataType, "Invalid point mapping!");
                            break;
                        case "linestring":
                            assertEquals(Types.CLOB, iDataType, "Invalid linestring mapping!");
                            break;
                        case "polygon":
                            assertEquals(Types.CLOB, iDataType, "Invalid polygon mapping!");
                            break;
                        case "multipoint":
                            assertEquals(Types.CLOB, iDataType, "Invalid multipoint mapping!");
                            break;
                        case "multilinestring":
                            assertEquals(Types.CLOB, iDataType, "Invalid multilinestring mapping!");
                            break;
                        case "multipolygon":
                            assertEquals(Types.CLOB, iDataType, "Invalid multipolygon mapping!");
                            break;
                        case "geometrycollection":
                            assertEquals(Types.CLOB, iDataType, "Invalid geometrycollection mapping!");
                            break;
                        // new with mysql 8.0
                        case "geomcollection":
                            assertEquals(Types.CLOB, iDataType, "Invalid geometrycollection mapping!");
                            break;
                        case "enum":
                            assertEquals(Types.VARCHAR, iDataType, "Invalid enum mapping!");
                            break;
                        case "set":
                            assertEquals(Types.VARCHAR, iDataType, "Invalid set mapping!");
                            break;
                        default:
                            fail("Invalid type " + sTypeName + "!");
                            break;
                    }
                    TestColumnDefinition tcd = mapCd.get(sColumnName);
                    String sType = tcd.getType();
                    if (!sType.startsWith("INTERVAL")) {
                        // parse type
                        Matcher matcher = _patTYPE.matcher(sBaseTypeName);
                        if (matcher.matches()) {
                            /* compare column size with explicit precision */
                            String sPrecision = matcher.group(4);
                            if (sPrecision != null) {
                                int iPrecision = Integer.parseInt(sPrecision);
                                if ((iDataType == Types.DOUBLE) ||
                                        (iDataType == Types.FLOAT) ||
                                        (iDataType == Types.REAL) ||
                                        (iDataType == Types.CLOB) ||
                                        (iDataType == Types.BLOB) ||
                                        (iDataType == Types.TIMESTAMP) ||
                                        (iDataType == Types.TIME) ||
                                        (sTypeName.startsWith("datetimeoffset"))) {
                                    assertTrue((iPrecision <= lColumnSize), "Explicit precision too large!");
                                    lColumnSize = iPrecision;
                                }
                                assertEquals(iPrecision, lColumnSize, "Explicit precision does not match!");
                            }
                        }
                    }
                }
            } else
                fail("Invalid column meta data result set!");
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testColumnsMySqlSimple() {
        enter();
        testColumns(TestMySqlDatabase.getQualifiedSimpleTable(), TestMySqlDatabase._listCdSimple);
    }

    @Test
    public void testColumnsMySqlComplex() {
        enter();
        testColumns(TestMySqlDatabase.getQualifiedComplexTable(), TestMySqlDatabase._listCdComplex);
    }

    @Test
    public void testColumnsSqlSimple() {
        enter();
        testColumns(TestSqlDatabase.getQualifiedSimpleTable(), TestSqlDatabase._listCdSimple);
    }

    @Test
    public void testColumnsSqlComplex() {
        enter();
        testColumns(TestSqlDatabase.getQualifiedComplexTable(), TestSqlDatabase._listCdComplex);
    }

    @Test
    @Override
    public void testGetTableTypes() {
        enter();
        try {
            print(_dmdMySql.getTableTypes());
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetProcedures() {
        enter();
        try {
            print(_dmdMySql.getProcedures(null, TestSqlDatabase._sTEST_SCHEMA, "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetProcedureColumns() {
        enter();
        try {
            print(_dmdMySql.getProcedureColumns(null, TestSqlDatabase._sTEST_SCHEMA, "%", "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetTables() {
        enter();
        try {
            print(_dmdMySql.getTables(null, TestSqlDatabase._sTEST_SCHEMA, "%", new String[]{"TABLE"}));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetViews() {
        enter();
        try {
            print(_dmdMySql.getTables(null, TestSqlDatabase._sTEST_SCHEMA, "%", new String[]{"VIEW"}));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetUDTs() {
        enter();
        try {
            print(_dmdMySql.getUDTs(null, "%", "%", null));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetAttributes() {
        enter();
        try {
            print(_dmdMySql.getAttributes(null, TestSqlDatabase._sTEST_SCHEMA, "%", "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetPrimaryKeys() {
        enter();
        QualifiedId qiTable = TestMySqlDatabase.getQualifiedSimpleTable();
        try {
            print(_dmdMySql.getPrimaryKeys(qiTable.getCatalog(), qiTable.getSchema(), qiTable.getName()));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetImportedKeys() {
        enter();
        QualifiedId qiTable = TestSqlDatabase.getQualifiedComplexTable();
        try {
            print(_dmdMySql.getImportedKeys(qiTable.getCatalog(), qiTable.getSchema(), qiTable.getName()));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetExportedKeys() {
        enter();
        QualifiedId qiTable = TestSqlDatabase.getQualifiedSimpleTable();
        try {
            print(_dmdMySql.getExportedKeys(qiTable.getCatalog(), qiTable.getSchema(), qiTable.getName()));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetCrossReference() {
        enter();
        try {
            print(_dmdMySql.getCrossReference(null, TestSqlDatabase._sTEST_SCHEMA, "%", null, TestSqlDatabase._sTEST_SCHEMA, "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetColumnPrivileges() {
        enter();
        try {
            print(_dmdMySql.getColumnPrivileges(null, null, "%", "%"));
        }
        /* do not fail: MySql throws exception because of insufficient privileges of testuser */ catch (
                SQLException se) {
            System.err.println(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetVersionColumns() {
        enter();
        QualifiedId qiTable = TestSqlDatabase.getQualifiedSimpleTable();
        try {
            print(_dmdMySql.getVersionColumns(
                    qiTable.getCatalog(),
                    qiTable.getSchema(),
                    qiTable.getName()));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetBestRowIdentifier() {
        enter();
        QualifiedId qiTable = TestSqlDatabase.getQualifiedSimpleTable();
        try {
            print(_dmdMySql.getBestRowIdentifier(
                    qiTable.getCatalog(),
                    qiTable.getSchema(),
                    qiTable.getName(),
                    DatabaseMetaData.bestRowUnknown, true));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }
    /***
     @Test public void testNullable()
     {
     /* this is from a bug report, so we need another database
      * This may be necessary but doesn't change things:
      * grant all on mysql.* to 'asuser'@'localhost'

     try
     {
     MySqlDataSource dsMySql = new MySqlDataSource();
     String sCatalog = "archivespace";
     dsMySql.setUrl(MySqlDriver.getUrl(_cp.getHost() + ":" + _cp.getPort()+"/"+sCatalog));
     dsMySql.setUser("asuser");
     dsMySql.setPassword("aspwd");
     MySqlConnection connMySql = (MySqlConnection) dsMySql.getConnection();
     connMySql.setAutoCommit(false);
     _dmdMySql = (MySqlDatabaseMetaData) connMySql.getMetaData();
     setDatabaseMetaData(_dmdMySql);
     /* now to test ...
     ResultSet rs = getDatabaseMetaData().getColumns(null, sCatalog, "resource", "ead_id");
     if (rs.next())
     {
     String sNullable = rs.getString("IS_NULLABLE");
     assertEquals("YES", sNullable, "Wrong ISO nullability!");
     int iNullable = rs.getInt("NULLABLE");
     assertEquals(DatabaseMetaData.columnNullable, iNullable, "Wrong nullability!");
     }
     else
     fail("Column ead_id in table resource in catalog archivespace not found!");
     }
     catch(SQLException se) { fail(EU.getExceptionMessage(se)); }

     }
     ***/
}
