package ch.admin.bar.siardsuite.database;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DbmsRegistry {
    public static final BasicAuthCredentials DUMMY_CREDENTIALS = BasicAuthCredentials.builder()
            .user("")
            .password("")
            .build();

    public static final Collection<Dbms<?>> DBMS = Arrays.asList(
            Dbms.<FileBasedDbms>builder()
                    .name("MS Access")
                    .driverClassName("ch.admin.bar.siard2.jdbc.AccessDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:access:%s",
                            config.getFile().getAbsolutePath()))
                    .exampleConfiguration(new FileBasedDbms(new File("D:\\Projekte\\SIARD2\\JdbcAccess\\testfiles\\dbfile.mdb")))
                    .build(),

            Dbms.<ServerBasedDbms>builder()
                    .name("DB/2")
                    .driverClassName("ch.admin.bar.siard2.jdbc.Db2Driver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:db2://%s:%s/%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName()))
                    .exampleConfiguration(ServerBasedDbms.builder()
                            .port(50000)
                            .host("db2.host.org")
                            .dbName("DB2-Database")
                            .credentials(DUMMY_CREDENTIALS)
                            .build())
                    .build(),

            Dbms.<ServerBasedDbms>builder()
                    .name("MySQL")
                    .driverClassName("ch.admin.bar.siard2.jdbc.MySqlDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:mysql://%s:%s/%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName()))
                    .exampleConfiguration(ServerBasedDbms.builder()
                            .port(3306)
                            .host("mysql.host.org")
                            .dbName("MySQL-Database")
                            .credentials(DUMMY_CREDENTIALS)
                            .build())
                    .build(),

            Dbms.<ServerBasedDbms>builder()
                    .name("Oracle")
                    .driverClassName("ch.admin.bar.siard2.jdbc.OracleDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:oracle:thin:@%s:%s/%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName()))
                    .exampleConfiguration(ServerBasedDbms.builder()
                            .port(1521)
                            .host("oracle.host.org")
                            .dbName("Oracle-Database")
                            .credentials(DUMMY_CREDENTIALS)
                            .build())
                    .build(),

            Dbms.<ServerBasedDbms>builder()
                    .name("PostgreSQL")
                    .driverClassName("ch.admin.bar.siard2.jdbc.PostgresDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:postgresql://%s:%s/%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName()))
                    .exampleConfiguration(ServerBasedDbms.builder()
                            .port(5432)
                            .host("postgresql.host.org")
                            .dbName("PostgreSQL-Database")
                            .credentials(DUMMY_CREDENTIALS)
                            .build())
                    .build(),

            Dbms.<ServerBasedDbms>builder()
                    .name("Microsoft SQL Server")
                    .driverClassName("ch.admin.bar.siard2.jdbc.MsSqlDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:sqlserver://%s:%s;databaseName=%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName()))
                    .exampleConfiguration(ServerBasedDbms.builder()
                            .port(1433)
                            .host("mssql.host.org")
                            .dbName("MS-SQL-Database")
                            .credentials(DUMMY_CREDENTIALS)
                            .build())
                    .build()
    );

    public static Set<String> getSupportedDbms() {
        return DBMS.stream()
                .map(Dbms::getName)
                .collect(Collectors.toSet());
    }

    public static Dbms<?> findDbmsByName(final String name) {
        return DBMS.stream()
                .filter(dbms -> dbms.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No DBMS with name " + name + " available"));
    }

    @Value
    @Builder
    public static class Dbms<T> {
        @NonNull String name;
        @NonNull String driverClassName;
        @NonNull Function<T, String> jdbcConnectionStringEncoder;

        @NonNull T exampleConfiguration;
    }

    @Value
    @Builder
    public static class BasicAuthCredentials {
        @NonNull String user;
        @NonNull String password;
    }

    @Value
    @Builder
    public static class ServerBasedDbms {
        @NonNull String host;
        @NonNull Integer port;
        @NonNull String dbName;
        @NonNull BasicAuthCredentials credentials;

        public Connection createConnection(final Function<ServerBasedDbms, String> jdbcConnectionStringEncoder) throws SQLException {
            val jdbcConnectionString = jdbcConnectionStringEncoder.apply(this);
            return DriverManager.getConnection(
                    jdbcConnectionString,
                    credentials.getUser(),
                    credentials.getPassword());
        }
    }

    @Value
    public static class FileBasedDbms {
        @NonNull File file;

        public Connection createConnection(final Function<FileBasedDbms, String> jdbcConnectionStringEncoder) throws SQLException {
            val jdbcConnectionString = jdbcConnectionStringEncoder.apply(this);
            return DriverManager.getConnection(jdbcConnectionString);
        }
    }
}
