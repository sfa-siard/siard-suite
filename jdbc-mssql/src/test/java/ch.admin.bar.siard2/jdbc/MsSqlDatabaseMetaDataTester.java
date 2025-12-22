package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import java.text.ParseException;
import java.util.*;
import java.util.regex.*;

import static org.junit.Assert.*;

import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.database.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.mssql.*;

public class MsSqlDatabaseMetaDataTester extends BaseDatabaseMetaDataTester {
    private static final ConnectionProperties _cp = new ConnectionProperties();
    private static final String _sDB_URL = MsSqlDriver.getUrl(_cp.getHost() + ":" + _cp.getPort() + ";databaseName=" + _cp.getCatalog());
    private static final String _sDB_USER = _cp.getUser();
    private static final String _sDB_PASSWORD = _cp.getPassword();
    private static final String _sDB_CATALOG = _cp.getCatalog();
    private static Pattern _patTYPE = Pattern.compile("^(.*?)(\\(\\s*((\\d+)(\\s*,\\s*(\\d+))?)\\s*\\))?$");
    private static QualifiedId _qiGeometryType = new QualifiedId(_sDB_CATALOG, "sys", "geometry");
    private static QualifiedId _qiGeographyType = new QualifiedId(_sDB_CATALOG, "sys", "geography");
    private static QualifiedId _qiHierarchyIdType = new QualifiedId(_sDB_CATALOG, "sys", "hierarchyid");
    private MsSqlDatabaseMetaData _dmdMsSql = null;

    @BeforeClass
    public static void setUpClass() {
        try {
            MsSqlDataSource dsMsSql = new MsSqlDataSource();
            dsMsSql.setUrl(_sDB_URL);
            dsMsSql.setUser(_sDB_USER);
            dsMsSql.setPassword(_sDB_PASSWORD);
            MsSqlConnection connMsSql = (MsSqlConnection) dsMsSql.getConnection();
            /* drop and create the test databases */
            new TestSqlDatabase(connMsSql);
            new TestMsSqlDatabase(connMsSql);
            connMsSql.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Before
    public void setUp() {
        try {
            MsSqlDataSource dsMsSql = new MsSqlDataSource();
            dsMsSql.setUrl(_sDB_URL);
            dsMsSql.setUser(_sDB_USER);
            dsMsSql.setPassword(_sDB_PASSWORD);
            MsSqlConnection connMsSql = (MsSqlConnection) dsMsSql.getConnection();
            _dmdMsSql = (MsSqlDatabaseMetaData) connMsSql.getMetaData();
            setDatabaseMetaData(_dmdMsSql);
        } catch (SQLException se) {
            fail(se.getClass().getName() + ": " + se.getMessage());
        }
    }

    @Test
    public void testClass() {
        assertEquals("Wrong database meta data class!", MsSqlDatabaseMetaData.class, _dmdMsSql.getClass());
    }

    @Override
    @Test
    public void testGetTypeInfo() {
        enter();
        try {
            print(_dmdMsSql.getTypeInfo());
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    private void testColumns(QualifiedId qiTable, List<TestColumnDefinition> listCd) {
        try {
            Map<String, TestColumnDefinition> mapCd = new HashMap<>();
            for (TestColumnDefinition tcd : listCd) {
                mapCd.put(tcd.getName(), tcd);
            }
            ResultSet rs = _dmdMsSql.getColumns(
                    qiTable.getCatalog(),
                    _dmdMsSql.toPattern(qiTable.getSchema()),
                    _dmdMsSql.toPattern(qiTable.getName()),
                    "%");
            if ((rs != null) && (!rs.isClosed())) {
                int iPosition = 0;
                while (rs.next()) {
                    String sCatalogName = rs.getString("TABLE_CAT");
                    String sSchemaName = rs.getString("TABLE_SCHEM");
                    String sTableName = rs.getString("TABLE_NAME");
                    if (sCatalogName != null) {
                        if (!_sDB_CATALOG.equalsIgnoreCase(sCatalogName))
                            fail("Unexpected catalog: " + sCatalogName);
                    }
                    if (!qiTable.getSchema().equals(sSchemaName))
                        fail("Unexpected schema: " + sSchemaName);
                    if (!qiTable.getName().equals(sTableName))
                        fail("Unexpected table: " + sTableName);
                    String sColumnName = rs.getString("COLUMN_NAME");
                    int iDataType = rs.getInt("DATA_TYPE");
                    String sTypeName = rs.getString("TYPE_NAME");
                    int iColumnSize = rs.getInt("COLUMN_SIZE");
                    // int iDecimalDigits = rs.getInt("DECIMAL_DIGITS");
                    iPosition++;
                    int iOrdinalPosition = rs.getInt("ORDINAL_POSITION");
                    assertEquals("Invalid position encountered!", iPosition, iOrdinalPosition);
                    String sBaseTypeName = sTypeName;
                    int iParenIndex = sTypeName.indexOf('(');
                    if (iParenIndex > 0) {
                        sBaseTypeName = sTypeName.substring(0, iParenIndex).trim();
                    }
                    switch (sBaseTypeName) {
                        case "CHAR":
                            assertEquals("Invalid CHAR mapping!", Types.CHAR, iDataType);
                            break;
                        case "char":
                            assertEquals("Invalid char mapping!", Types.CHAR, iDataType);
                            break;
                        case "VARCHAR":
                            assertEquals("Invalid VARCHAR mapping!", Types.VARCHAR, iDataType);
                            break;
                        case "varchar":
                            assertEquals("Invalid varchar mapping!", Types.VARCHAR, iDataType);
                            break;
                        case "uniqueidentifier":
                            assertEquals("Invalid UUID mapping!", Types.CHAR, iDataType);
                            break;
                        case "CLOB":
                            assertEquals("Invalid CLOB mapping!", Types.CLOB, iDataType);
                            break;
                        case "text":
                            assertEquals("Invalid text mapping!", Types.CLOB, iDataType);
                            break;
                        case "NCHAR":
                            assertEquals("Invalid NCHAR mapping!", Types.NCHAR, iDataType);
                            break;
                        case "nchar":
                            assertEquals("Invalid nchar mapping!", Types.NCHAR, iDataType);
                            break;
                        case "NCHAR VARYING":
                            assertEquals("Invalid NCHAR VARYING mapping!", Types.NVARCHAR, iDataType);
                            break;
                        case "nvarchar":
                            assertEquals("Invalid nvarchar mapping!", Types.NVARCHAR, iDataType);
                            break;
                        case "NCLOB":
                            assertEquals("Invalid NCLOB mapping!", Types.NCLOB, iDataType);
                            break;
                        case "ntext":
                            assertEquals("Invalid ntext mapping!", Types.NCLOB, iDataType);
                            break;
                        case "XML":
                            assertEquals("Invalid XML mapping!", Types.SQLXML, iDataType);
                            break;
                        case "xml":
                            assertEquals("Invalid xml mapping!", Types.SQLXML, iDataType);
                            break;
                        case "BINARY":
                            assertEquals("Invalid BINARY mapping!", Types.BINARY, iDataType);
                            break;
                        case "binary":
                            assertEquals("Invalid binary mapping!", Types.BINARY, iDataType);
                            break;
                        case "timestamp":
                            assertEquals("Invalid timestamp mapping!", Types.BINARY, iDataType);
                            break;
                        case "VARBINARY":
                            assertEquals("Invalid VARBINARY mapping!", Types.VARBINARY, iDataType);
                            break;
                        case "varbinary":
                            assertEquals("Invalid varbinary mapping!", Types.VARBINARY, iDataType);
                            break;
                        case "BLOB":
                            assertEquals("Invalid BLOB mapping!", Types.BLOB, iDataType);
                            break;
                        case "image":
                            assertEquals("Invalid image mapping!", Types.BLOB, iDataType);
                            break;
                        case "tinyint":
                            assertEquals("Invalid tinyint mapping!", Types.SMALLINT, iDataType);
                            break;
                        case "SMALLINT":
                            assertEquals("Invalid SMALLINT mapping!", Types.SMALLINT, iDataType);
                            break;
                        case "smallint":
                            assertEquals("Invalid smallint mapping!", Types.SMALLINT, iDataType);
                            break;
                        case "INTEGER":
                            assertEquals("Invalid INTEGER mapping!", Types.INTEGER, iDataType);
                            break;
                        case "int":
                            assertEquals("Invalid INTEGER mapping!", Types.INTEGER, iDataType);
                            break;
                        case "BIGINT":
                            assertEquals("Invalid BIGINT mapping!", Types.BIGINT, iDataType);
                            break;
                        case "bigint":
                            assertEquals("Invalid bigint mapping!", Types.BIGINT, iDataType);
                            break;
                        case "DECIMAL":
                            assertEquals("Invalid DECIMAL mapping!", Types.DECIMAL, iDataType);
                            break;
                        case "decimal":
                            assertEquals("Invalid decimal mapping!", Types.DECIMAL, iDataType);
                            break;
                        case "NUMERIC":
                            assertEquals("Invalid NUMERIC mapping!", Types.NUMERIC, iDataType);
                            break;
                        case "numeric":
                            assertEquals("Invalid numeric mapping!", Types.NUMERIC, iDataType);
                            break;
                        case "smallmoney":
                            assertEquals("Invalid smallmoney mapping!", Types.DECIMAL, iDataType);
                            break;
                        case "money":
                            assertEquals("Invalid money mapping!", Types.DECIMAL, iDataType);
                            break;
                        case "REAL":
                            assertEquals("Invalid REAL mapping!", Types.REAL, iDataType);
                            break;
                        case "real":
                            assertEquals("Invalid real mapping!", Types.REAL, iDataType);
                            break;
                        case "DOUBLE":
                            assertEquals("Invalid DOUBLE mapping!", Types.DOUBLE, iDataType);
                            break;
                        case "float":
                            assertEquals("Invalid float mapping!", Types.DOUBLE, iDataType);
                            break;
                        case "BOOLEAN":
                            assertEquals("Invalid BOOLEAN mapping!", Types.BOOLEAN, iDataType);
                            break;
                        case "bit":
                            assertEquals("Invalid bit mapping!", Types.BOOLEAN, iDataType);
                            break;
                        case "DATE":
                            assertEquals("Invalid DATE mapping!", Types.DATE, iDataType);
                            break;
                        case "date":
                            assertEquals("Invalid date mapping!", Types.DATE, iDataType);
                            break;
                        case "TIME":
                            assertEquals("Invalid TIME mapping!", Types.TIME, iDataType);
                            break;
                        case "time":
                            assertEquals("Invalid time mapping!", Types.TIME, iDataType);
                            break;
                        case "TIMESTAMP":
                            assertEquals("Invalid TIMESTAMP mapping!", Types.TIMESTAMP, iDataType);
                            break;
                        case "smalldatetime":
                            assertEquals("Invalid smalldatetime mapping!", Types.TIMESTAMP, iDataType);
                            break;
                        case "datetime":
                            assertEquals("Invalid datetime mapping!", Types.TIMESTAMP, iDataType);
                            break;
                        case "datetime2":
                            assertEquals("Invalid datetime2 mapping!", Types.TIMESTAMP, iDataType);
                            break;
                        case "datetimeoffset":
                            assertEquals("Invalid datetimeoffset mapping!", Types.VARCHAR, iDataType);
                            break;
                        case "sql_variant":
                            assertEquals("Invalid sql_variant mapping!", Types.VARBINARY, iDataType);
                            break;
                        default:
                            try {
                                QualifiedId qiType = new QualifiedId(sTypeName);
                                if (qiType.getCatalog() == null)
                                    qiType.setCatalog(sCatalogName);
                                if (qiType.getSchema() == null)
                                    qiType.setSchema(sSchemaName);
                                if (qiType.equals(_qiGeometryType) ||
                                        qiType.equals(_qiGeographyType))
                                    assertEquals("Invalid geo type mapping!", iDataType, Types.VARCHAR);
                                else if (qiType.equals(_qiHierarchyIdType)) {
                                    assertEquals("Invalid hierarchyid type mapping!", iDataType, Types.VARCHAR);
                                    assertTrue("Invalid length of hierarchyid type mapping!", iColumnSize < 4000);
                                } else
                                    assertEquals("Invalid UDT mapping!", iDataType, Types.DISTINCT);
                            } catch (ParseException pe) {
                                throw new SQLException("Type \"" + sTypeName + "\" could not be parsed!", pe);
                            }
                            break;
                    }
                    TestColumnDefinition tcd = mapCd.get(sColumnName);
                    String sType = tcd.getType();
                    if (!sType.startsWith("INTERVAL")) {
                        // parse type
                        Matcher matcher = _patTYPE.matcher(sType);
                        if (matcher.matches()) {
                            /* compare column size with explicit precision */
                            String sPrecision = matcher.group(4);
                            if (sPrecision != null) {
                                int iPrecision = Integer.parseInt(sPrecision);
                                if ((iDataType == Types.DOUBLE) ||
                                        (iDataType == Types.REAL) ||
                                        (iDataType == Types.TIMESTAMP) ||
                                        (iDataType == Types.TIME)) {
                                    assertTrue("Explicit precision too large!", (iPrecision <= iColumnSize));
                                    iColumnSize = iPrecision;
                                } else if (sTypeName.startsWith("datetimeoffset"))
                                    iPrecision = 64;
                                assertEquals("Explicit precision does not match!", iPrecision, iColumnSize);
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
    public void testColumnsMsSqlSimple() {
        enter();
        testColumns(TestMsSqlDatabase.getQualifiedSimpleTable(), TestMsSqlDatabase._listCdSimple);
    }

    @Test
    public void testColumnsMsSqlComplex() {
        enter();
        testColumns(TestMsSqlDatabase.getQualifiedComplexTable(), TestMsSqlDatabase._listCdComplex);
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

    private void testGetUDTs(QualifiedId qiType) {
        try {
            ResultSet rs = _dmdMsSql.getUDTs(
                    qiType.getCatalog(),
                    _dmdMsSql.toPattern(qiType.getSchema()),
                    _dmdMsSql.toPattern(qiType.getName()),
                    new int[]{Types.STRUCT, Types.DISTINCT});
      /* Microsoft misspelled CLASS_NAME as CLASSNAME. We do not fix this yet.
      ResultSetMetaData rsmd = rs.getMetaData();
      for (int i = 0; i  < rsmd.getColumnCount(); i++)
        System.out.println(String.valueOf(i+1)+": "+rsmd.getColumnLabel(i+1)+", "+rsmd.getColumnName(i+1));
      */
            while (rs.next()) {
                String sCatalogName = rs.getString("TYPE_CAT");
                String sSchemaName = rs.getString("TYPE_SCHEM");
                String sTypeName = rs.getString("TYPE_NAME");
                if (sCatalogName != null) {
                    if (!_sDB_CATALOG.equalsIgnoreCase(sCatalogName))
                        fail("Unexpected catalog: " + sCatalogName);
                }
                if (!qiType.getSchema().equals(sSchemaName))
                    fail("Unexpected schema: " + sSchemaName);
                if (!qiType.getName().equals(sTypeName))
                    fail("Unexpected type: " + sTypeName);
                //String sClassName = rs.getString("CLASS_NAME");
                String sClassName = rs.getString(4);
                int iDataType = rs.getInt("DATA_TYPE");
                int iBaseType = rs.getInt("BASE_TYPE");
                /* base type is mapped! Its precision and its scale have to be retrieved from getColumns() of a column with this type name! */
                System.out.println(qiType.format() + "\t" + sClassName + "\t" + iDataType + " (" + SqlTypes.getTypeName(iDataType) + ")\t" + iBaseType + " (" + SqlTypes.getTypeName(iBaseType) + ")");
            }
            rs.close();
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    public void testGetUDTsMsSqlDistinct() {
        enter();
        testGetUDTs(TestMsSqlDatabase.getQualifiedDistinctType());
    }

    @Test
    public void testGetUDTsSqlDistinct() {
        enter();
        testGetUDTs(TestSqlDatabase.getQualifiedDistinctType());
    }


    @Test
    @Override
    public void testSupportsResultSetType() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            String sSqlType = SqlTypes.getTypeName(iType);
            try {
                println(sSqlType + ": " + _dmdMsSql.supportsResultSetType(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testSupportsResultSetConcurrency() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            String sSqlType = SqlTypes.getTypeName(iType);
            try {
                println(sSqlType + " (READ_ONLY): " + _dmdMsSql.supportsResultSetConcurrency(iType, ResultSet.CONCUR_READ_ONLY));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
            try {
                println(sSqlType + " (UPDATABLE): " + _dmdMsSql.supportsResultSetConcurrency(iType, ResultSet.CONCUR_UPDATABLE));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testOwnUpdatesAreVisible() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            try {
                println(SqlTypes.getTypeName(iType) + ": " + _dmdMsSql.ownUpdatesAreVisible(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testOwnDeletesAreVisible() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            try {
                println(SqlTypes.getTypeName(iType) + ": " + _dmdMsSql.ownDeletesAreVisible(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testOwnInsertsAreVisible() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            try {
                println(SqlTypes.getTypeName(iType) + ": " + _dmdMsSql.ownInsertsAreVisible(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testOthersUpdatesAreVisible() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            try {
                println(SqlTypes.getTypeName(iType) + ": " + _dmdMsSql.othersUpdatesAreVisible(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testOthersDeletesAreVisible() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            try {
                println(SqlTypes.getTypeName(iType) + ": " + _dmdMsSql.othersDeletesAreVisible(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testOthersInsertsAreVisible() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            try {
                println(SqlTypes.getTypeName(iType) + ": " + _dmdMsSql.othersInsertsAreVisible(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testUpdatesAreDetected() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            try {
                println(SqlTypes.getTypeName(iType) + ": " + _dmdMsSql.updatesAreDetected(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testDeletesAreDetected() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            try {
                println(SqlTypes.getTypeName(iType) + ": " + _dmdMsSql.deletesAreDetected(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testInsertsAreDetected() {
        enter();
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            try {
                println(SqlTypes.getTypeName(iType) + ": " + _dmdMsSql.insertsAreDetected(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @Test
    @Override
    public void testGetTableTypes() {
        enter();
        try {
            print(_dmdMsSql.getTableTypes());
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetProcedures() {
        enter();
        try {
            print(_dmdMsSql.getProcedures(null, TestSqlDatabase._sTEST_SCHEMA, "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetProcedureColumns() {
        enter();
        try {
            print(_dmdMsSql.getProcedureColumns(null, TestSqlDatabase._sTEST_SCHEMA, "%", "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetTables() {
        try {
            ResultSet resultSet = _dmdMsSql.getTables(null, TestSqlDatabase._sTEST_SCHEMA, "%", new String[]{"TABLE"});
            verifyTable(resultSet, "testdb", "TESTSQLSCHEMA", "TSQLCOMPLEX", "TABLE");
            verifyTable(resultSet, "testdb", "TESTSQLSCHEMA", "TSQLSIMPLE", "TABLE");
            assertFalse(resultSet.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testGetViews() throws SQLException {
        ResultSet resultSet = _dmdMsSql.getTables("testdb", TestSqlDatabase._sTEST_SCHEMA, "%", new String[]{"VIEW"});
        verifyTable(resultSet, "testdb", "TESTSQLSCHEMA", "VSQLSIMPLE", "VIEW");
        assertFalse(resultSet.next());
    }

    @Test
    public void shouldGetAllTypesOfTables() throws SQLException {
        ResultSet resultSet = _dmdMsSql.getTables(null, TestSqlDatabase._sTEST_SCHEMA, "%", null);
        verifyTable(resultSet, "testdb", "TESTSQLSCHEMA", "TSQLCOMPLEX", "TABLE");
        verifyTable(resultSet, "testdb", "TESTSQLSCHEMA", "TSQLSIMPLE", "TABLE");
        verifyTable(resultSet, "testdb", "TESTSQLSCHEMA", "VSQLSIMPLE", "VIEW");
        assertFalse(resultSet.next());
    }

    @Test
    public void shouldGetNoResultForUnknownCatalogName() throws SQLException {
        ResultSet resultSet = _dmdMsSql.getTables("unknown", TestSqlDatabase._sTEST_SCHEMA, "%", null);
        assertFalse(resultSet.next());
    }

    private void verifyTable(ResultSet resultSet, String tableCat, String tableSchema, String tableName, String tableType) throws SQLException {
        resultSet.next();
        assertEquals(tableCat, resultSet.getString("TABLE_CAT"));
        assertEquals(tableSchema, resultSet.getString("TABLE_SCHEM"));
        assertEquals(tableName, resultSet.getString("TABLE_NAME"));
        assertEquals(tableType, resultSet.getString("TABLE_TYPE"));
    }

    @Test
    @Override
    public void testGetUDTs() {
        enter();
        try {
            print(_dmdMsSql.getUDTs(null, "%", "%", null));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetAttributes() {
        enter();
        try {
            print(_dmdMsSql.getAttributes(null, TestSqlDatabase._sTEST_SCHEMA, "%", "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetImportedKeys() {
        enter();
        try {
            print(_dmdMsSql.getImportedKeys(null, TestSqlDatabase._sTEST_SCHEMA, "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetExportedKeys() {
        enter();
        try {
            print(_dmdMsSql.getExportedKeys(null, TestSqlDatabase._sTEST_SCHEMA, "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Test
    @Override
    public void testGetCrossReference() {
        enter();
        try {
            print(_dmdMsSql.getCrossReference(null, TestSqlDatabase._sTEST_SCHEMA, "%", null, TestSqlDatabase._sTEST_SCHEMA, "%"));
        } catch (SQLException se) {
            fail(EU.getExceptionMessage(se));
        }
    }

    @Override
    @Test
    public void testGetPseudoColumns() {
        enter();
        try {
            print(_dmdMsSql.getPseudoColumns(null, null, "%", "%"));
        } catch (SQLFeatureNotSupportedException sfnse) {
            System.out.println(EU.getExceptionMessage(sfnse));
        } catch (SQLException se) {
            System.out.println(EU.getExceptionMessage(se));
        } // normal users are not authorized ...
    }
}
