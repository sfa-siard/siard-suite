package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.ConsoleLogConsumer;
import ch.admin.bar.siard2.OracleDatasourceFactory;
import ch.admin.bar.siard2.SqlScripts;
import ch.admin.bar.siard2.TestResourcesResolver;
import lombok.Getter;
import lombok.val;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OracleDatabaseMetaData_typeOriginalPrecision {

    static OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart").withLogConsumer(new ConsoleLogConsumer())
                                                                                         .withCopyFileToContainer(MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.TEST_USER)
                                                                                                                                                                 .toPath()), "/container-entrypoint-initdb.d/00_add_user.sql")
                                                                                         .withCopyFileToContainer(MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.TYPE_ORIGINAL)
                                                                                                                                                                 .toPath()), "/container-entrypoint-initdb.d/01_create_table.sql");


    private static OracleDatabaseMetaData oracleDatabaseMetaData;

    @BeforeAll
    static void beforeAll() throws SQLException {
        db.start();
        val oracleDataSource = new OracleDatasourceFactory().create(db, "testuser", "testpassword");
        val connection = (OracleConnection) oracleDataSource.getConnection();
        connection.setAutoCommit(false);
        oracleDatabaseMetaData = (OracleDatabaseMetaData) connection.getMetaData();
    }

    @AfterAll
    static void afterAll() {
        db.stop();
    }

    @Test
    public void includes_varchar_size_in_type_original() throws SQLException {
        assertNotNull(oracleDatabaseMetaData);

        ResultSet resultSet = oracleDatabaseMetaData.getColumns("%", "TESTUSER", "VARCHARTEST", "TEXT%");

        Columns columns = new Columns(resultSet);

        assertEquals(1, columns.sizeOf("TEXT2"));
        assertEquals("VARCHAR2(1)", columns.typeOf("TEXT2"));

        assertEquals(255, columns.sizeOf("TEXT3"));
        assertEquals("VARCHAR2(255)", columns.typeOf("TEXT3"));

        assertEquals(4000, columns.sizeOf("TEXT4"));
        assertEquals("VARCHAR2(4000)", columns.typeOf("TEXT4"));
    }

    @Getter
    class Column {

        private String name;
        private long size;
        private String type;

        public Column(ResultSet resultSet) throws SQLException {
            this.name = resultSet.getString("COLUMN_NAME");
            this.size = resultSet.getLong("COLUMN_SIZE");
            this.type = resultSet.getString("TYPE_NAME");
        }
    }

    class Columns {

        private List<Column> columns;

        public Columns(ResultSet resultSet) throws SQLException {
            this.columns = new ArrayList<>();
            while (resultSet.next()) {
                columns.add(new Column(resultSet));
            }
        }

        public Column pick(String columnName) {
            return this.columns.stream()
                               .filter(c -> c.getName()
                                             .equals(columnName))
                               .findFirst()
                               .orElseThrow(() -> new IllegalArgumentException("Column \"" + columnName + "\" not found"));
        }

        public long sizeOf(String column) {
            return this.pick(column).getSize();
        }

        public String typeOf(String column) {
            return this.pick(column).getType();
        }
    }
}