package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.database.model.DbmsId;
import ch.admin.bar.siardsuite.database.model.FileBasedDbms;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbms;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class DbmsRegistry {

    public static final Collection<Dbms<?>> DBMS = Arrays.asList(
            FileBasedDbms.builder()
                    .name("MS Access")
                    .id(DbmsId.of("access"))
                    .driverClassName("ch.admin.bar.siard2.jdbc.AccessDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:access:%s",
                            config.getFile().getAbsolutePath()))
                    .exampleFile(new File("D:\\Projekte\\SIARD2\\JdbcAccess\\testfiles\\dbfile.mdb"))
                    .build(),

            ServerBasedDbms.builder()
                    .name("DB/2")
                    .id(DbmsId.of("db2"))
                    .driverClassName("ch.admin.bar.siard2.jdbc.Db2Driver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:db2://%s:%s/%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName()))
                    .examplePort("50000")
                    .exampleHost("db2.exampleHost.org")
                    .exampleDbName("DB2-Database")
                    .build(),

            ServerBasedDbms.builder()
                    .name("MySQL")
                    .id(DbmsId.of("mysql"))
                    .driverClassName("ch.admin.bar.siard2.jdbc.MySqlDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:mysql://%s:%s/%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName()))
                    .examplePort("3306")
                    .exampleHost("mysql.exampleHost.org")
                    .exampleDbName("MySQL-Database")
                    .build(),

            ServerBasedDbms.builder()
                    .name("Oracle")
                    .id(DbmsId.of("oracle"))
                    .driverClassName("ch.admin.bar.siard2.jdbc.OracleDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:oracle:thin:@%s:%s/%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName()))
                    .examplePort("1521")
                    .exampleHost("oracle.exampleHost.org")
                    .exampleDbName("Oracle-Database")
                    .build(),

            ServerBasedDbms.builder()
                    .name("PostgreSQL")
                    .id(DbmsId.of("postgresql"))
                    .driverClassName("ch.admin.bar.siard2.jdbc.PostgresDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:postgresql://%s:%s/%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName()))
                    .examplePort("5432")
                    .exampleHost("postgresql.exampleHost.org")
                    .exampleDbName("PostgreSQL-Database")
                    .build(),

            ServerBasedDbms.builder()
                    .name("Microsoft SQL Server")
                    .id(DbmsId.of("mssql"))
                    .driverClassName("ch.admin.bar.siard2.jdbc.MsSqlDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:sqlserver://%s:%s;databaseName=%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName()))
                    .examplePort("1433")
                    .exampleHost("mssql.exampleHost.org")
                    .exampleDbName("MS-SQL-Database")
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

    public static Dbms<?> findDbmsById(final DbmsId id) {
        return DBMS.stream()
                .filter(dbms -> dbms.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No DBMS with id " + id + " available"));
    }
}
