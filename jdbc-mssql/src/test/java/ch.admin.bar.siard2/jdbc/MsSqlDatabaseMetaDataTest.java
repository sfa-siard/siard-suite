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
import org.junit.jupiter.api.*;
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

import static org.junit.jupiter.api.Assertions.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Assertions;

@Testcontainers
public class MsSqlDatabaseMetaDataTest extends BaseDatabaseMetaDataTester {
    private static final String MSSQL_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";
    private static final String SA_PASSWORD = "YourStrong!Passw0rd";

    @Container
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

    @BeforeAll
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

    @BeforeEach
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
        assertEquals(MsSqlDatabaseMetaData.class, metadata.getClass(), "Wrong database meta data class!");
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
                        assertEquals(Types.CHAR, dataType, "Invalid CHAR mapping!");
                        break;
                    case "char":
                        assertEquals(Types.CHAR, dataType, "Invalid char mapping!");
                        break;
                    case "VARCHAR":
                        assertEquals(Types.VARCHAR, dataType, "Invalid VARCHAR mapping!");
                        break;
                    case "varchar":
                        assertEquals(Types.VARCHAR, dataType, "Invalid varchar mapping!");
                        break;
                    case "uniqueidentifier":
                        assertEquals(Types.CHAR, dataType, "Invalid UUID mapping!");
                        break;
                    case "CLOB":
                        assertEquals(Types.CLOB, dataType, "Invalid CLOB mapping!");
                        break;
                    case "text":
                        assertEquals(Types.CLOB, dataType, "Invalid text mapping!");
                        break;
                    case "NCHAR":
                        assertEquals(Types.NCHAR, dataType, "Invalid NCHAR mapping!");
                        break;
                    case "nchar":
                        assertEquals(Types.NCHAR, dataType, "Invalid nchar mapping!");
                        break;
                    case "NCHAR VARYING":
                        assertEquals(Types.NVARCHAR, dataType, "Invalid NCHAR VARYING mapping!");
                        break;
                    case "nvarchar":
                        assertEquals(Types.NVARCHAR, dataType, "Invalid nvarchar mapping!");
                        break;
                    case "NCLOB":
                        assertEquals(Types.NCLOB, dataType, "Invalid NCLOB mapping!");
                        break;
                    case "ntext":
                        assertEquals(Types.NCLOB, dataType, "Invalid ntext mapping!");
                        break;
                    case "XML":
                        assertEquals(Types.SQLXML, dataType, "Invalid XML mapping!");
                        break;
                    case "xml":
                        assertEquals(Types.SQLXML, dataType, "Invalid xml mapping!");
                        break;
                    case "BINARY":
                        assertEquals(Types.BINARY, dataType, "Invalid BINARY mapping!");
                        break;
                    case "binary":
                        assertEquals(Types.BINARY, dataType, "Invalid binary mapping!");
                        break;
                    case "timestamp":
                        assertEquals(Types.BINARY, dataType, "Invalid timestamp mapping!");
                        break;
                    case "VARBINARY":
                        assertEquals(Types.VARBINARY, dataType, "Invalid VARBINARY mapping!");
                        break;
                    case "varbinary":
                        assertEquals(Types.VARBINARY, dataType, "Invalid varbinary mapping!");
                        break;
                    case "BLOB":
                        assertEquals(Types.BLOB, dataType, "Invalid BLOB mapping!");
                        break;
                    case "image":
                        assertEquals(Types.BLOB, dataType, "Invalid image mapping!");
                        break;
                    case "tinyint":
                        assertEquals(Types.SMALLINT, dataType, "Invalid tinyint mapping!");
                        break;
                    case "SMALLINT":
                        assertEquals(Types.SMALLINT, dataType, "Invalid SMALLINT mapping!");
                        break;
                    case "smallint":
                        assertEquals(Types.SMALLINT, dataType, "Invalid smallint mapping!");
                        break;
                    case "INTEGER":
                        assertEquals(Types.INTEGER, dataType, "Invalid INTEGER mapping!");
                        break;
                    case "int":
                        assertEquals(Types.INTEGER, dataType, "Invalid INTEGER mapping!");
                        break;
                    case "BIGINT":
                        assertEquals(Types.BIGINT, dataType, "Invalid BIGINT mapping!");
                        break;
                    case "bigint":
                        assertEquals(Types.BIGINT, dataType, "Invalid bigint mapping!");
                        break;
                    case "DECIMAL":
                        assertEquals(Types.DECIMAL, dataType, "Invalid DECIMAL mapping!");
                        break;
                    case "decimal":
                        assertEquals(Types.DECIMAL, dataType, "Invalid decimal mapping!");
                        break;
                    case "NUMERIC":
                        assertEquals(Types.NUMERIC, dataType, "Invalid NUMERIC mapping!");
                        break;
                    case "numeric":
                        assertEquals(Types.NUMERIC, dataType, "Invalid numeric mapping!");
                        break;
                    case "smallmoney":
                        assertEquals(Types.DECIMAL, dataType, "Invalid smallmoney mapping!");
                        break;
                    case "money":
                        assertEquals(Types.DECIMAL, dataType, "Invalid money mapping!");
                        break;
                    case "REAL":
                        assertEquals(Types.REAL, dataType, "Invalid REAL mapping!");
                        break;
                    case "real":
                        assertEquals(Types.REAL, dataType, "Invalid real mapping!");
                        break;
                    case "DOUBLE":
                        assertEquals(Types.DOUBLE, dataType, "Invalid DOUBLE mapping!");
                        break;
                    case "float":
                        assertEquals(Types.DOUBLE, dataType, "Invalid float mapping!");
                        break;
                    case "BOOLEAN":
                        assertEquals(Types.BOOLEAN, dataType, "Invalid BOOLEAN mapping!");
                        break;
                    case "bit":
                        assertEquals(Types.BOOLEAN, dataType, "Invalid bit mapping!");
                        break;
                    case "DATE":
                        assertEquals(Types.DATE, dataType, "Invalid DATE mapping!");
                        break;
                    case "date":
                        assertEquals(Types.DATE, dataType, "Invalid date mapping!");
                        break;
                    case "TIME":
                        assertEquals(Types.TIME, dataType, "Invalid TIME mapping!");
                        break;
                    case "time":
                        assertEquals(Types.TIME, dataType, "Invalid time mapping!");
                        break;
                    case "TIMESTAMP":
                        assertEquals(Types.TIMESTAMP, dataType, "Invalid TIMESTAMP mapping!");
                        break;
                    case "smalldatetime":
                        assertEquals(Types.TIMESTAMP, dataType, "Invalid smalldatetime mapping!");
                        break;
                    case "datetime":
                        assertEquals(Types.TIMESTAMP, dataType, "Invalid datetime mapping!");
                        break;
                    case "datetime2":
                        assertEquals(Types.TIMESTAMP, dataType, "Invalid datetime2 mapping!");
                        break;
                    case "datetimeoffset":
                        assertEquals(Types.VARCHAR, dataType, "Invalid datetimeoffset mapping!");
                        break;
                    case "sql_variant":
                        assertEquals(Types.VARBINARY, dataType, "Invalid sql_variant mapping!");
                        break;
                    default:
                        QualifiedId id = new QualifiedId(typeName);
                        if (id.getCatalog() == null) id.setCatalog(catalogName);
                        if (id.getSchema() == null) id.setSchema(schemaName);
                        if (id.equals(GEOMETRY_TYPE) || id.equals(GEOGRAPHY_TYPE))
                            assertEquals(Types.VARCHAR, dataType, "Invalid geo type mapping!");
                        else if (id.equals(HIERARCHY_TYPE)) {
                            assertEquals(Types.VARCHAR, dataType, "Invalid hierarchyid type mapping!");
                            assertTrue(columnSize < 4000, "Invalid length of hierarchyid type mapping!");
                        } else assertEquals(Types.DISTINCT, dataType, "Invalid UDT mapping!");
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
                                assertTrue((iPrecision <= columnSize), "Explicit precision too large!");
                                columnSize = iPrecision;
                            } else if (typeName.startsWith("datetimeoffset")) iPrecision = 64;
                            assertEquals(iPrecision, columnSize, "Explicit precision does not match!");
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
    @Disabled
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
    @Disabled
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
    @Disabled
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
    @Disabled
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
    @Disabled
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
    @Disabled
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
    @Disabled
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
    @Disabled
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
    @Disabled
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
            assertEquals(DB_CATALOG.toLowerCase(), actual.getCatalog()
                                                                               .toLowerCase(), "Unexpected catalog");
            assertEquals(qualifiedId.getSchema(), actual.getSchema(), "Unexpected schema");
            assertEquals(qualifiedId.getName(), actual.getName(), "Unexpected type");

            String className = rs.getString("CLASSNAME");
            int dataType = rs.getInt("DATA_TYPE");
            int baseType = rs.getInt("BASE_TYPE");

            assertNull(className, "CLASS_NAME should be null for DISTINCT types");
            if (expectedDataType != null) {
                assertEquals(expectedDataType.intValue(), dataType, "Unexpected DATA_TYPE");
            }
            if (expectedBaseType != null) {
                assertEquals(expectedBaseType.intValue(), baseType, "Unexpected BASE_TYPE");
            }
        }
        rs.close();
        assertTrue(foundRow, "Expected to find at least one UDT row");
    }
}
