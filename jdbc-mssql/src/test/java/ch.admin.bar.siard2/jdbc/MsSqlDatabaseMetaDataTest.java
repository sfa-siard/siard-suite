package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.jdbcx.MsSqlDataSource;
import ch.admin.bar.siard2.mssql.TestMsSqlDatabase;
import ch.admin.bar.siard2.mssql.TestSqlDatabase;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.TestColumnDefinition;
import ch.enterag.utils.database.SqlTypes;
import ch.enterag.utils.jdbc.BaseDatabaseMetaDataTester;
import ch.enterag.utils.jdbc.MetadataResultSetWrapper;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import lombok.SneakyThrows;
import org.junit.*;
import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MsSqlDatabaseMetaDataTest extends BaseDatabaseMetaDataTester {
    private static final String MSSQL_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";
    private static final String SA_PASSWORD = "YourStrong!Passw0rd";

    @ClassRule
    public static MSSQLServerContainer<?> mssqlContainer = new MSSQLServerContainer<>(MSSQL_IMAGE).acceptLicense()
                                                                                                  .withPassword(SA_PASSWORD)
                                                                                                  .withUrlParam("trustServerCertificate", "true");

    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    private static String DB_CATALOG;
    private static final Pattern TYPE_PATTERN = Pattern.compile("^(.*?)(\\(\\s*((\\d+)(\\s*,\\s*(\\d+))?)\\s*\\))?$");
    private static QualifiedId GEOMETRY_TYPE;
    private static QualifiedId GEOGRAPHY_TYPE;
    private static QualifiedId HIERARCHY_TYPE;
    private MsSqlDatabaseMetaData metadata = null;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        DB_URL = mssqlContainer.getJdbcUrl();
        DB_USER = mssqlContainer.getUsername();
        DB_PASSWORD = mssqlContainer.getPassword();
        DB_CATALOG = "master";

        // Initialize QualifiedId instances after catalog is set
        GEOMETRY_TYPE = new QualifiedId(DB_CATALOG, "sys", "geometry");
        GEOGRAPHY_TYPE = new QualifiedId(DB_CATALOG, "sys", "geography");
        HIERARCHY_TYPE = new QualifiedId(DB_CATALOG, "sys", "hierarchyid");

        MsSqlDataSource dataSource = new MsSqlDataSource();
        dataSource.setUrl(DB_URL);
        dataSource.setUser(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        MsSqlConnection connection = (MsSqlConnection) dataSource.getConnection();
        /* drop and create the test databases */
        new TestSqlDatabase(connection);
        new TestMsSqlDatabase(connection);
        connection.close();
    }

    @Before
    public void setUp() throws SQLException {
        MsSqlDataSource dataSource = new MsSqlDataSource();
        dataSource.setUrl(DB_URL);
        dataSource.setUser(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        MsSqlConnection connection = (MsSqlConnection) dataSource.getConnection();
        metadata = (MsSqlDatabaseMetaData) connection.getMetaData();
        setDatabaseMetaData(metadata);
    }

    @Test
    public void testClass() {
        assertEquals("Wrong database meta data class!", MsSqlDatabaseMetaData.class, metadata.getClass());
    }

    @Test
    @Override
    @SneakyThrows
    public void testGetTypeInfo() {
        print(metadata.getTypeInfo());
    }

    @SneakyThrows
    private void testColumns(QualifiedId qualifiedId, List<TestColumnDefinition> columnDefinitions) throws SQLException {
        Map<String, TestColumnDefinition> columnDefinitionsMap = columnDefinitions.stream()
                                                                                  .collect(Collectors.toMap(TestColumnDefinition::getName, Function.identity()));

        ResultSet rs = metadata.getColumns(qualifiedId.getCatalog(), metadata.toPattern(qualifiedId.getSchema()), metadata.toPattern(qualifiedId.getName()), "%");

        if ((rs != null) && (!rs.isClosed())) {
            int position = 0;
            while (rs.next()) {
                position++;

                String catalogName = rs.getString("TABLE_CAT");
                String schemaName = rs.getString("TABLE_SCHEM");
                String tableName = rs.getString("TABLE_NAME");

                assertEquals(DB_CATALOG.toLowerCase(), catalogName.toLowerCase());
                assertEquals(qualifiedId.getSchema(), schemaName);
                assertEquals(qualifiedId.getName(), tableName);

                String columnName = rs.getString("COLUMN_NAME");
                int dataType = rs.getInt("DATA_TYPE");
                String typeName = rs.getString("TYPE_NAME");
                int columnSize = rs.getInt("COLUMN_SIZE");
                int ordinalPosition = rs.getInt("ORDINAL_POSITION");
                assertEquals(position, ordinalPosition);
                String baseTypeName = typeName;
                // If the type name contains a parenthesis, strip everything after and including the parenthesis.
                // This is to handle cases like "varchar(max)" where the type name is not just the base type name.
                int iParenIndex = typeName.indexOf('(');
                if (iParenIndex > 0) {
                    baseTypeName = typeName.substring(0, iParenIndex)
                                           .trim();
                }
                switch (baseTypeName) {
                    case "CHAR":
                        assertEquals("Invalid CHAR mapping!", Types.CHAR, dataType);
                        break;
                    case "char":
                        assertEquals("Invalid char mapping!", Types.CHAR, dataType);
                        break;
                    case "VARCHAR":
                        assertEquals("Invalid VARCHAR mapping!", Types.VARCHAR, dataType);
                        break;
                    case "varchar":
                        assertEquals("Invalid varchar mapping!", Types.VARCHAR, dataType);
                        break;
                    case "uniqueidentifier":
                        assertEquals("Invalid UUID mapping!", Types.CHAR, dataType);
                        break;
                    case "CLOB":
                        assertEquals("Invalid CLOB mapping!", Types.CLOB, dataType);
                        break;
                    case "text":
                        assertEquals("Invalid text mapping!", Types.CLOB, dataType);
                        break;
                    case "NCHAR":
                        assertEquals("Invalid NCHAR mapping!", Types.NCHAR, dataType);
                        break;
                    case "nchar":
                        assertEquals("Invalid nchar mapping!", Types.NCHAR, dataType);
                        break;
                    case "NCHAR VARYING":
                        assertEquals("Invalid NCHAR VARYING mapping!", Types.NVARCHAR, dataType);
                        break;
                    case "nvarchar":
                        assertEquals("Invalid nvarchar mapping!", Types.NVARCHAR, dataType);
                        break;
                    case "NCLOB":
                        assertEquals("Invalid NCLOB mapping!", Types.NCLOB, dataType);
                        break;
                    case "ntext":
                        assertEquals("Invalid ntext mapping!", Types.NCLOB, dataType);
                        break;
                    case "XML":
                        assertEquals("Invalid XML mapping!", Types.SQLXML, dataType);
                        break;
                    case "xml":
                        assertEquals("Invalid xml mapping!", Types.SQLXML, dataType);
                        break;
                    case "BINARY":
                        assertEquals("Invalid BINARY mapping!", Types.BINARY, dataType);
                        break;
                    case "binary":
                        assertEquals("Invalid binary mapping!", Types.BINARY, dataType);
                        break;
                    case "timestamp":
                        assertEquals("Invalid timestamp mapping!", Types.BINARY, dataType);
                        break;
                    case "VARBINARY":
                        assertEquals("Invalid VARBINARY mapping!", Types.VARBINARY, dataType);
                        break;
                    case "varbinary":
                        assertEquals("Invalid varbinary mapping!", Types.VARBINARY, dataType);
                        break;
                    case "BLOB":
                        assertEquals("Invalid BLOB mapping!", Types.BLOB, dataType);
                        break;
                    case "image":
                        assertEquals("Invalid image mapping!", Types.BLOB, dataType);
                        break;
                    case "tinyint":
                        assertEquals("Invalid tinyint mapping!", Types.SMALLINT, dataType);
                        break;
                    case "SMALLINT":
                        assertEquals("Invalid SMALLINT mapping!", Types.SMALLINT, dataType);
                        break;
                    case "smallint":
                        assertEquals("Invalid smallint mapping!", Types.SMALLINT, dataType);
                        break;
                    case "INTEGER":
                        assertEquals("Invalid INTEGER mapping!", Types.INTEGER, dataType);
                        break;
                    case "int":
                        assertEquals("Invalid INTEGER mapping!", Types.INTEGER, dataType);
                        break;
                    case "BIGINT":
                        assertEquals("Invalid BIGINT mapping!", Types.BIGINT, dataType);
                        break;
                    case "bigint":
                        assertEquals("Invalid bigint mapping!", Types.BIGINT, dataType);
                        break;
                    case "DECIMAL":
                        assertEquals("Invalid DECIMAL mapping!", Types.DECIMAL, dataType);
                        break;
                    case "decimal":
                        assertEquals("Invalid decimal mapping!", Types.DECIMAL, dataType);
                        break;
                    case "NUMERIC":
                        assertEquals("Invalid NUMERIC mapping!", Types.NUMERIC, dataType);
                        break;
                    case "numeric":
                        assertEquals("Invalid numeric mapping!", Types.NUMERIC, dataType);
                        break;
                    case "smallmoney":
                        assertEquals("Invalid smallmoney mapping!", Types.DECIMAL, dataType);
                        break;
                    case "money":
                        assertEquals("Invalid money mapping!", Types.DECIMAL, dataType);
                        break;
                    case "REAL":
                        assertEquals("Invalid REAL mapping!", Types.REAL, dataType);
                        break;
                    case "real":
                        assertEquals("Invalid real mapping!", Types.REAL, dataType);
                        break;
                    case "DOUBLE":
                        assertEquals("Invalid DOUBLE mapping!", Types.DOUBLE, dataType);
                        break;
                    case "float":
                        assertEquals("Invalid float mapping!", Types.DOUBLE, dataType);
                        break;
                    case "BOOLEAN":
                        assertEquals("Invalid BOOLEAN mapping!", Types.BOOLEAN, dataType);
                        break;
                    case "bit":
                        assertEquals("Invalid bit mapping!", Types.BOOLEAN, dataType);
                        break;
                    case "DATE":
                        assertEquals("Invalid DATE mapping!", Types.DATE, dataType);
                        break;
                    case "date":
                        assertEquals("Invalid date mapping!", Types.DATE, dataType);
                        break;
                    case "TIME":
                        assertEquals("Invalid TIME mapping!", Types.TIME, dataType);
                        break;
                    case "time":
                        assertEquals("Invalid time mapping!", Types.TIME, dataType);
                        break;
                    case "TIMESTAMP":
                        assertEquals("Invalid TIMESTAMP mapping!", Types.TIMESTAMP, dataType);
                        break;
                    case "smalldatetime":
                        assertEquals("Invalid smalldatetime mapping!", Types.TIMESTAMP, dataType);
                        break;
                    case "datetime":
                        assertEquals("Invalid datetime mapping!", Types.TIMESTAMP, dataType);
                        break;
                    case "datetime2":
                        assertEquals("Invalid datetime2 mapping!", Types.TIMESTAMP, dataType);
                        break;
                    case "datetimeoffset":
                        assertEquals("Invalid datetimeoffset mapping!", Types.VARCHAR, dataType);
                        break;
                    case "sql_variant":
                        assertEquals("Invalid sql_variant mapping!", Types.VARBINARY, dataType);
                        break;
                    default:
                        QualifiedId id = new QualifiedId(typeName);
                        if (id.getCatalog() == null) id.setCatalog(catalogName);
                        if (id.getSchema() == null) id.setSchema(schemaName);
                        if (id.equals(GEOMETRY_TYPE) || id.equals(GEOGRAPHY_TYPE))
                            assertEquals("Invalid geo type mapping!", Types.VARCHAR, dataType);
                        else if (id.equals(HIERARCHY_TYPE)) {
                            assertEquals("Invalid hierarchyid type mapping!", Types.VARCHAR, dataType);
                            assertTrue("Invalid length of hierarchyid type mapping!", columnSize < 4000);
                        } else assertEquals("Invalid UDT mapping!", Types.DISTINCT, dataType);
                        break;
                }
                TestColumnDefinition tcd = columnDefinitionsMap.get(columnName);
                String type = tcd.getType();
                if (!type.startsWith("INTERVAL")) {
                    // parse type
                    Matcher matcher = TYPE_PATTERN.matcher(type);
                    if (matcher.matches()) {
                        /* compare column size with explicit precision */
                        String precision = matcher.group(4);
                        if (precision != null) {
                            int iPrecision = Integer.parseInt(precision);
                            if ((dataType == Types.DOUBLE) || (dataType == Types.REAL) || (dataType == Types.TIMESTAMP) || (dataType == Types.TIME)) {
                                assertTrue("Explicit precision too large!", (iPrecision <= columnSize));
                                columnSize = iPrecision;
                            } else if (typeName.startsWith("datetimeoffset")) iPrecision = 64;
                            assertEquals("Explicit precision does not match!", iPrecision, columnSize);
                        }
                    }
                }
            }
        } else fail("Invalid column meta data result set!");
    }

    @Test
    public void testColumnsMsSqlSimple() throws SQLException {
        testColumns(TestMsSqlDatabase.getQualifiedSimpleTable(), TestMsSqlDatabase._listCdSimple);
    }

    @Test
    public void testColumnsMsSqlComplex() throws SQLException {
        testColumns(TestMsSqlDatabase.getQualifiedComplexTable(), TestMsSqlDatabase._listCdComplex);
    }

    @Test
    public void testColumnsSqlSimple() throws SQLException {
        testColumns(TestSqlDatabase.getQualifiedSimpleTable(), TestSqlDatabase._listCdSimple);
    }

    @Test
    public void testColumnsSqlComplex() throws SQLException {
        testColumns(TestSqlDatabase.getQualifiedComplexTable(), TestSqlDatabase._listCdComplex);
    }

    @Test
    public void testGetUDTsMsSqlDistinct() throws SQLException {
        testGetUDTs(TestMsSqlDatabase.getQualifiedDistinctType(), Types.DISTINCT, Types.INTEGER);
    }

    @Test
    public void testGetUDTsSqlDistinct() throws SQLException {
        testGetUDTs(TestSqlDatabase.getQualifiedDistinctType(), Types.DISTINCT, Types.NVARCHAR);
    }

    @SneakyThrows
    @Test
    @Override
    public void testSupportsResultSetType() {
        // types defined in java.sql.ResultSet
        assertTrue(metadata.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY));
        assertTrue(metadata.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE));
        assertTrue(metadata.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE));

        // SQL Server specific result set types
        assertTrue(metadata.supportsResultSetType(SQLServerResultSet.TYPE_SS_DIRECT_FORWARD_ONLY));
        assertTrue(metadata.supportsResultSetType(SQLServerResultSet.TYPE_SS_SERVER_CURSOR_FORWARD_ONLY));
        assertTrue(metadata.supportsResultSetType(SQLServerResultSet.TYPE_SS_SCROLL_DYNAMIC));
    }

    @SneakyThrows
    @Test
    @Override
    @Ignore
    public void testSupportsResultSetConcurrency() {
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            String sSqlType = SqlTypes.getTypeName(iType);
            println(sSqlType + " (READ_ONLY): " + metadata.supportsResultSetConcurrency(iType, ResultSet.CONCUR_READ_ONLY));
            println(sSqlType + " (UPDATABLE): " + metadata.supportsResultSetConcurrency(iType, ResultSet.CONCUR_UPDATABLE));
        }
    }

    @SneakyThrows
    @Test
    @Override
    @Ignore
    public void testOwnUpdatesAreVisible() {
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            println(SqlTypes.getTypeName(iType) + ": " + metadata.ownUpdatesAreVisible(iType));
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
                println(SqlTypes.getTypeName(iType) + ": " + metadata.ownDeletesAreVisible(iType));
            } catch (SQLException se) {
                System.out.println(EU.getExceptionMessage(se));
            }
        }
    }

    @SneakyThrows
    @Test
    @Override
    @Ignore
    public void testOwnInsertsAreVisible() {
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            println(SqlTypes.getTypeName(iType) + ": " + metadata.ownInsertsAreVisible(iType));
        }
    }

    @SneakyThrows
    @Test
    @Override
    @Ignore
    public void testOthersUpdatesAreVisible() {
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            println(SqlTypes.getTypeName(iType) + ": " + metadata.othersUpdatesAreVisible(iType));
        }
    }

    @SneakyThrows
    @Test
    @Override
    @Ignore
    public void testOthersDeletesAreVisible() {
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            println(SqlTypes.getTypeName(iType) + ": " + metadata.othersDeletesAreVisible(iType));
        }
    }

    @SneakyThrows
    @Test
    @Override
    @Ignore
    public void testOthersInsertsAreVisible() {
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            println(SqlTypes.getTypeName(iType) + ": " + metadata.othersInsertsAreVisible(iType));
        }
    }

    @SneakyThrows
    @Test
    @Override
    @Ignore
    public void testUpdatesAreDetected() {
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            println(SqlTypes.getTypeName(iType) + ": " + metadata.updatesAreDetected(iType));
        }
    }

    @SneakyThrows
    @Test
    @Override
    @Ignore
    public void testDeletesAreDetected() {
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            println(SqlTypes.getTypeName(iType) + ": " + metadata.deletesAreDetected(iType));
        }
    }

    @SneakyThrows
    @Test
    @Override
    @Ignore
    public void testInsertsAreDetected() {
        List<Integer> listTypes = SqlTypes.getAllTypes();
        for (Integer listType : listTypes) {
            int iType = listType;
            println(SqlTypes.getTypeName(iType) + ": " + metadata.insertsAreDetected(iType));
        }
    }

    @SneakyThrows
    @Test
    @Override
    public void testGetTableTypes() {
        print(metadata.getTableTypes());
    }

    @SneakyThrows
    @Test
    @Override
    public void testGetProcedures() {
        print(metadata.getProcedures(null, TestSqlDatabase._sTEST_SCHEMA, "%"));
    }

    @SneakyThrows
    @Test
    @Override
    public void testGetProcedureColumns() {
        print(metadata.getProcedureColumns(null, TestSqlDatabase._sTEST_SCHEMA, "%", "%"));
    }

    @SneakyThrows
    @Test
    @Override
    public void testGetTables() {
        ResultSet resultSet = metadata.getTables(null, TestSqlDatabase._sTEST_SCHEMA, "%", new String[]{"TABLE"});
        verifyTable(resultSet, "master", "TESTSQLSCHEMA", "TSQLCOMPLEX", "TABLE");
        verifyTable(resultSet, "master", "TESTSQLSCHEMA", "TSQLSIMPLE", "TABLE");
        assertFalse(resultSet.next());
    }


    @Test
    public void testGetViews() throws SQLException {
        ResultSet resultSet = metadata.getTables("master", TestSqlDatabase._sTEST_SCHEMA, "%", new String[]{"VIEW"});
        verifyTable(resultSet, "master", "TESTSQLSCHEMA", "VSQLSIMPLE", "VIEW");
        assertFalse(resultSet.next());
    }

    @Test
    public void shouldGetAllTypesOfTables() throws SQLException {
        ResultSet resultSet = metadata.getTables(null, TestSqlDatabase._sTEST_SCHEMA, "%", null);
        verifyTable(resultSet, "master", "TESTSQLSCHEMA", "TSQLCOMPLEX", "TABLE");
        verifyTable(resultSet, "master", "TESTSQLSCHEMA", "TSQLSIMPLE", "TABLE");
        verifyTable(resultSet, "master", "TESTSQLSCHEMA", "VSQLSIMPLE", "VIEW");
        assertFalse(resultSet.next());
    }

    @Test
    public void shouldGetNoResultForUnknownCatalogName() throws SQLException {
        ResultSet resultSet = metadata.getTables("unknown", TestSqlDatabase._sTEST_SCHEMA, "%", null);
        assertFalse(resultSet.next());
    }

    private void verifyTable(ResultSet resultSet, String tableCat, String tableSchema, String tableName, String tableType) throws SQLException {
        resultSet.next();
        assertEquals(tableCat, resultSet.getString("TABLE_CAT"));
        assertEquals(tableSchema, resultSet.getString("TABLE_SCHEM"));
        assertEquals(tableName, resultSet.getString("TABLE_NAME"));
        assertEquals(tableType, resultSet.getString("TABLE_TYPE"));
    }

    @SneakyThrows
    @Test
    @Override
    public void testGetUDTs() {
        print(metadata.getUDTs(null, "%", "%", null));
    }

    @SneakyThrows
    @Test
    @Override
    public void testGetAttributes() {
        print(metadata.getAttributes(null, TestSqlDatabase._sTEST_SCHEMA, "%", "%"));
    }

    @SneakyThrows
    @Test
    @Override
    public void testGetImportedKeys() {
        print(metadata.getImportedKeys(null, TestSqlDatabase._sTEST_SCHEMA, "%"));
    }

    @SneakyThrows
    @Test
    @Override
    public void testGetExportedKeys() {
        print(metadata.getExportedKeys(null, TestSqlDatabase._sTEST_SCHEMA, "%"));
    }

    @SneakyThrows
    @Test
    @Override
    public void testGetCrossReference() {
        print(metadata.getCrossReference(null, TestSqlDatabase._sTEST_SCHEMA, "%", null, TestSqlDatabase._sTEST_SCHEMA, "%"));
    }

    @SneakyThrows
    @Override
    @Test
    public void testGetPseudoColumns() {
        print(metadata.getPseudoColumns(null, null, "%", "%"));
    }

    private void testGetUDTs(QualifiedId qualifiedId, Integer expectedDataType, Integer expectedBaseType) throws SQLException {
        ResultSet rs = metadata.getUDTs(qualifiedId.getCatalog(), metadata.toPattern(qualifiedId.getSchema()), metadata.toPattern(qualifiedId.getName()), new int[]{Types.STRUCT, Types.DISTINCT});

        boolean foundRow = false;
        while (rs.next()) {
            foundRow = true;

            MetadataResultSetWrapper wrapper = MetadataResultSetWrapper.forType(rs);
            QualifiedId actual = wrapper.toQualifiedId();
            assertEquals("Unexpected catalog", DB_CATALOG.toLowerCase(), actual.getCatalog()
                                                                               .toLowerCase());
            assertEquals("Unexpected schema", qualifiedId.getSchema(), actual.getSchema());
            assertEquals("Unexpected type", qualifiedId.getName(), actual.getName());

            String className = rs.getString("CLASSNAME");
            int dataType = rs.getInt("DATA_TYPE");
            int baseType = rs.getInt("BASE_TYPE");

            assertNull("CLASS_NAME should be null for DISTINCT types", className);
            if (expectedDataType != null) {
                assertEquals("Unexpected DATA_TYPE", expectedDataType.intValue(), dataType);
            }
            if (expectedBaseType != null) {
                assertEquals("Unexpected BASE_TYPE", expectedBaseType.intValue(), baseType);
            }
        }
        rs.close();
        assertTrue("Expected to find at least one UDT row", foundRow);
    }
}
