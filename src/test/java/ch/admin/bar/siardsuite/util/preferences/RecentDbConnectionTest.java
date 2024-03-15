package ch.admin.bar.siardsuite.util.preferences;

import ch.admin.bar.siardsuite.service.database.DbmsRegistry;
import ch.admin.bar.siardsuite.service.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.service.database.model.FileBasedDbms;
import ch.admin.bar.siardsuite.service.database.model.FileBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.service.database.model.ServerBasedDbms;
import ch.admin.bar.siardsuite.service.database.model.ServerBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.service.preferences.RecentDbConnection;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;

import java.io.File;
import java.util.Optional;

class RecentDbConnectionTest {

    // The following names can not be changed due to backwards compatibility
    private static final String DB_2 = "DB/2";
    private static final String MY_SQL = "MySQL";
    private static final String ORACLE = "Oracle";
    private static final String POSTGRES = "PostgreSQL";
    private static final String MS_SQL = "Microsoft SQL Server";
    private static final String MS_ACCESS = "MS Access";

    private static final RecentDbConnection RECENT_DB_CONNECTION = RecentDbConnection.builder()
            .name("RECENT_DB_CONNECTION")
            .dbmsProduct("dbmsid")
            .connectionOptions("options")
            .host("host")
            .port("port")
            .dbName("dbname")
            .user("user")
            .file("file")
            .build();

    private static final ServerBasedDbmsConnectionProperties SERVER_BASED_DBMS_CONNECTION_PROPERTIES = ServerBasedDbmsConnectionProperties.builder()
            .host(RECENT_DB_CONNECTION.getHost())
            .port(RECENT_DB_CONNECTION.getPort())
            .dbName(RECENT_DB_CONNECTION.getDbName())
            .user(RECENT_DB_CONNECTION.getUser())
            .password("")
            .options(Optional.of(RECENT_DB_CONNECTION.getConnectionOptions()))
            .build();

    private static final FileBasedDbmsConnectionProperties FILE_BASED_DBMS_CONNECTION_PROPERTIES = new FileBasedDbmsConnectionProperties(new File(RECENT_DB_CONNECTION.getFile()));

    @Test
    public void mapToDbmsConnectionData_forDb2_expectConnectionData() {
        // given

        // when
        val connectionData = RECENT_DB_CONNECTION
                .toBuilder()
                .dbmsProduct(DB_2)
                .build()
                .mapToDbmsConnectionData();

        // then
        Assertions.assertThat(connectionData)
                .isEqualTo(new DbmsConnectionData(
                        (ServerBasedDbms) DbmsRegistry.findDbmsByName(DB_2),
                        SERVER_BASED_DBMS_CONNECTION_PROPERTIES
                ));
    }

    @Test
    public void mapToDbmsConnectionData_forMySql_expectConnectionData() {
        // given

        // when
        val connectionData = RECENT_DB_CONNECTION
                .toBuilder()
                .dbmsProduct(MY_SQL)
                .build()
                .mapToDbmsConnectionData();

        // then
        Assertions.assertThat(connectionData)
                .isEqualTo(new DbmsConnectionData(
                        (ServerBasedDbms) DbmsRegistry.findDbmsByName(MY_SQL),
                        SERVER_BASED_DBMS_CONNECTION_PROPERTIES
                ));
    }

    @Test
    public void mapToDbmsConnectionData_forOracle_expectConnectionData() {
        // given

        // when
        val connectionData = RECENT_DB_CONNECTION
                .toBuilder()
                .dbmsProduct(ORACLE)
                .build()
                .mapToDbmsConnectionData();

        // then
        Assertions.assertThat(connectionData)
                .isEqualTo(new DbmsConnectionData(
                        (ServerBasedDbms) DbmsRegistry.findDbmsByName(ORACLE),
                        SERVER_BASED_DBMS_CONNECTION_PROPERTIES
                ));
    }

    @Test
    public void mapToDbmsConnectionData_forPostgres_expectConnectionData() {
        // given

        // when
        val connectionData = RECENT_DB_CONNECTION
                .toBuilder()
                .dbmsProduct(POSTGRES)
                .build()
                .mapToDbmsConnectionData();

        // then
        Assertions.assertThat(connectionData)
                .isEqualTo(new DbmsConnectionData(
                        (ServerBasedDbms) DbmsRegistry.findDbmsByName(POSTGRES),
                        SERVER_BASED_DBMS_CONNECTION_PROPERTIES
                ));
    }

    @Test
    public void mapToDbmsConnectionData_forMsSql_expectConnectionData() {
        // given

        // when
        val connectionData = RECENT_DB_CONNECTION
                .toBuilder()
                .dbmsProduct(MS_SQL)
                .build()
                .mapToDbmsConnectionData();

        // then
        Assertions.assertThat(connectionData)
                .isEqualTo(new DbmsConnectionData(
                        (ServerBasedDbms) DbmsRegistry.findDbmsByName(MS_SQL),
                        SERVER_BASED_DBMS_CONNECTION_PROPERTIES
                ));
    }

    @Test
    public void mapToDbmsConnectionData_forMsAccess_expectConnectionData() {
        // given

        // when
        val connectionData = RECENT_DB_CONNECTION
                .toBuilder()
                .dbmsProduct(MS_ACCESS)
                .build()
                .mapToDbmsConnectionData();

        // then
        Assertions.assertThat(connectionData)
                .isEqualTo(new DbmsConnectionData(
                        (FileBasedDbms) DbmsRegistry.findDbmsByName(MS_ACCESS),
                        FILE_BASED_DBMS_CONNECTION_PROPERTIES
                ));
    }

}