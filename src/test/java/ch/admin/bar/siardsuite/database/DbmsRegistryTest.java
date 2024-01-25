package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siardsuite.database.model.DbmsId;
import ch.admin.bar.siardsuite.database.model.FileBasedDbms;
import ch.admin.bar.siardsuite.database.model.FileBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbms;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbmsConnectionProperties;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;

import java.io.File;
import java.util.Optional;

public class DbmsRegistryTest {

    private static final ServerBasedDbmsConnectionProperties EXAMPLE_PROPERTIES = ServerBasedDbmsConnectionProperties.builder()
            .host("db.host.org")
            .port("1234")
            .dbName("test")
            .options(Optional.of("option=value"))
            .user("")
            .password("")
            .build();

    private static final String EXPECTED_JDBC_URL_FOR_POSTGRESQL = "jdbc:postgresql://db.host.org:1234/test?option=value";
    private static final String EXPECTED_JDBC_URL_FOR_ORACLE = "jdbc:oracle:thin:@//db.host.org:1234/test?option=value";
    private static final String EXPECTED_JDBC_URL_FOR_MYSQL = "jdbc:mysql://db.host.org:1234/test?option=value";
    private static final String EXPECTED_JDBC_URL_FOR_MSSQL = "jdbc:sqlserver://db.host.org:1234;databaseName=test;option=value";
    private static final String EXPECTED_JDBC_URL_FOR_DB2 = "jdbc:db2://db.host.org:1234/test?option=value";

    private static final FileBasedDbmsConnectionProperties EXAMPLE_FILE_BASEDPROPERTIES = new FileBasedDbmsConnectionProperties(
            new File("C:/i/am/a/file.accdb".replace("/", File.separator))
    );

    private static final String EXPECTED_JDBC_URL_FOR_MSACCESS = "jdbc:access:C:/i/am/a/file.accdb".replace("/", File.separator);

    @Test
    public void decode_forPostgresql_expectDecodedProperties() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("postgresql"));

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_POSTGRESQL);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_PROPERTIES);
    }

    @Test
    public void encode_forPostgresql_expectEncodedUrl() {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("postgresql"));

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_PROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_POSTGRESQL);
    }

    @Test
    public void decode_forOracle_expectDecodedProperties() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("oracle"));

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_ORACLE);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_PROPERTIES);
    }

    @Test
    public void encode_forOracle_expectEncodedUrl() {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("oracle"));

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_PROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_ORACLE);
    }

    @Test
    public void decode_forMySql_expectDecodedProperties() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("mysql"));

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_MYSQL);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_PROPERTIES);
    }

    @Test
    public void encode_forMySql_expectEncodedUrl() {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("mysql"));

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_PROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_MYSQL);
    }

    @Test
    public void decode_forMsSql_expectDecodedProperties() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("mssql"));

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_MSSQL);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_PROPERTIES);
    }

    @Test
    public void encode_forMsSql_expectEncodedUrl() {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("mssql"));

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_PROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_MSSQL);
    }

    @Test
    public void decode_forDb2_expectDecodedProperties() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("db2"));

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_DB2);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_PROPERTIES);
    }

    @Test
    public void encode_forDb2_expectEncodedUrl() {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("db2"));

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_PROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_DB2);
    }

    @Test
    public void decode_forAccess_expectDecodedProperties() throws Exception {
        // given
        val dbms = (FileBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("access"));

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_MSACCESS);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_FILE_BASEDPROPERTIES);
    }

    @Test
    public void encode_forAccess_expectEncodedUrl() {
        // given
        val dbms = (FileBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("access"));

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_FILE_BASEDPROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_MSACCESS);
    }
}