package ch.admin.bar.siardsuite.database;

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

    // The following names can not be changed due to backwards compatibility
    private static final String DB_2 = "DB/2";
    private static final String MY_SQL = "MySQL";
    private static final String ORACLE = "Oracle";
    private static final String POSTGRES = "PostgreSQL";
    private static final String MS_SQL = "Microsoft SQL Server";
    private static final String MS_ACCESS = "MS Access";

    private static final ServerBasedDbmsConnectionProperties EXAMPLE_PROPERTIES = ServerBasedDbmsConnectionProperties.builder()
            .host("db.host.org")
            .port("1234")
            .dbName("test")
            .options(Optional.of("option=value"))
            .user("")
            .password("")
            .build();

    private static final String EXPECTED_JDBC_URL_FOR_POSTGRESQL = "jdbc:postgresql://db.host.org:1234/test?option=value";
    private static final String EXPECTED_JDBC_URL_FOR_ORACLE = "jdbc:oracle:thin:@db.host.org:1234/test?option=value";
    private static final String EXPECTED_JDBC_URL_FOR_MYSQL = "jdbc:mysql://db.host.org:1234/test?option=value";
    private static final String EXPECTED_JDBC_URL_FOR_MSSQL = "jdbc:sqlserver://db.host.org:1234;databaseName=test;option=value";
    private static final String EXPECTED_JDBC_URL_FOR_DB2 = "jdbc:db2://db.host.org:1234/test?option=value";

    private static final FileBasedDbmsConnectionProperties EXAMPLE_FILE_BASEDPROPERTIES = new FileBasedDbmsConnectionProperties(
            new File("/i/am/a/file.accdb".replace("/", File.separator))
    );

    private static final String EXPECTED_JDBC_URL_FOR_MSACCESS = "jdbc:access:/i/am/a/file.accdb".replace("/", File.separator);

    @Test
    public void decode_forPostgresql_expectDecodedProperties() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsByName(POSTGRES);

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_POSTGRESQL);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_PROPERTIES);
    }

    @Test
    public void encode_forPostgresql_expectEncodedUrl() {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsByName(POSTGRES);

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_PROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_POSTGRESQL);
    }

    @Test
    public void decode_forOracle_expectDecodedProperties() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsByName(ORACLE);

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_ORACLE);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_PROPERTIES);
    }

    @Test
    public void encode_forOracle_expectEncodedUrl() {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsByName(ORACLE);

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_PROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_ORACLE);
    }

    @Test
    public void decode_forMySql_expectDecodedProperties() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsByName(MY_SQL);

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_MYSQL);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_PROPERTIES);
    }

    @Test
    public void encode_forMySql_expectEncodedUrl() {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsByName(MY_SQL);

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_PROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_MYSQL);
    }

    @Test
    public void decode_forMsSql_expectDecodedProperties() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsByName(MS_SQL);

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_MSSQL);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_PROPERTIES);
    }

    @Test
    public void encode_forMsSql_expectEncodedUrl() {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsByName(MS_SQL);

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_PROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_MSSQL);
    }

    @Test
    public void decode_forDb2_expectDecodedProperties() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsByName(DB_2);

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_DB2);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_PROPERTIES);
    }

    @Test
    public void encode_forDb2_expectEncodedUrl() {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsByName(DB_2);

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_PROPERTIES);

        // then
        Assertions.assertThat(url).isEqualTo(EXPECTED_JDBC_URL_FOR_DB2);
    }

    @Test
    public void decode_forAccess_expectDecodedProperties() throws Exception {
        // given
        val dbms = (FileBasedDbms) DbmsRegistry.findDbmsByName(MS_ACCESS);

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply(EXPECTED_JDBC_URL_FOR_MSACCESS);

        // then
        Assertions.assertThat(properties)
                .isEqualTo(EXAMPLE_FILE_BASEDPROPERTIES);
    }

    @Test
    public void encode_forAccess_expectEncodedUrl() {
        // given
        val dbms = (FileBasedDbms) DbmsRegistry.findDbmsByName(MS_ACCESS);

        // when
        val url = dbms.getJdbcConnectionStringEncoder().apply(EXAMPLE_FILE_BASEDPROPERTIES);

        // then
        Assertions.assertThat(url.replace("C:", "")) // absolut path -> contains "C:"-prefix on windows-systems
                .isEqualTo(EXPECTED_JDBC_URL_FOR_MSACCESS);
    }
}