package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siardsuite.database.model.Dbms;
import ch.admin.bar.siardsuite.database.model.FileBasedDbms;
import ch.admin.bar.siardsuite.database.model.FileBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbms;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbmsConnectionProperties;
import lombok.val;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DbmsRegistry {

    public static final Collection<Dbms<?>> DBMS = Arrays.asList(
            FileBasedDbms.builder()
                    .name("MS Access")
                    .id("access")
                    .driverClassName("ch.admin.bar.siard2.jdbc.AccessDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:access:%s",
                            config.getFile().getAbsolutePath()))
                    .jdbcConnectionStringDecoder(encoded -> {
                        val file = new File(encoded.replace("jdbc:access:", ""));
                        return new FileBasedDbmsConnectionProperties(file);
                    })
                    .exampleFile(new File("D:\\Projekte\\SIARD2\\JdbcAccess\\testfiles\\dbfile.mdb"))
                    .build(),

            ServerBasedDbms.builder()
                    .name("DB/2")
                    .id("db2")
                    .driverClassName("ch.admin.bar.siard2.jdbc.Db2Driver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:db2://%s:%s/%s%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName(),
                            config.getOptions()
                                    .map(optionsString -> "?" + optionsString)
                                    .orElse("")))
                    .jdbcConnectionStringDecoder(encoded -> {
                        val splitEncoded = encoded.split(":");
                        val splitPortAndDbNameWithOptions = splitEncoded[3].split("/", 2);
                        val splitDbNameAndOptions = splitPortAndDbNameWithOptions[1].split("\\?", 2);

                        return ServerBasedDbmsConnectionProperties.builder()
                                .host(splitEncoded[2].replace("//", ""))
                                .port(splitPortAndDbNameWithOptions[0])
                                .dbName(splitDbNameAndOptions[0])
                                .options(splitDbNameAndOptions.length > 1 ? Optional.of(splitDbNameAndOptions[1]) : Optional.empty())
                                .user("")
                                .password("")
                                .build();
                    })
                    .examplePort("50000")
                    .exampleHost("db2.exampleHost.org")
                    .exampleDbName("DB2-Database")
                    .build(),

            ServerBasedDbms.builder()
                    .name("MySQL")
                    .id("mysql")
                    .driverClassName("ch.admin.bar.siard2.jdbc.MySqlDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:mysql://%s:%s/%s%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName(),
                            config.getOptions()
                                    .map(optionsString -> "?" + optionsString)
                                    .orElse("")))
                    .jdbcConnectionStringDecoder(encoded -> {
                        val splitEncoded = encoded.split(":");
                        val splitPortAndDbNameWithOptions = splitEncoded[3].split("/", 2);
                        val splitDbNameAndOptions = splitPortAndDbNameWithOptions[1].split("\\?", 2);

                        return ServerBasedDbmsConnectionProperties.builder()
                                .host(splitEncoded[2].replace("//", ""))
                                .port(splitPortAndDbNameWithOptions[0])
                                .dbName(splitDbNameAndOptions[0])
                                .options(splitDbNameAndOptions.length > 1 ? Optional.of(splitDbNameAndOptions[1]) : Optional.empty())
                                .user("")
                                .password("")
                                .build();
                    })
                    .examplePort("3306")
                    .exampleHost("mysql.exampleHost.org")
                    .exampleDbName("MySQL-Database")
                    .build(),

            ServerBasedDbms.builder()
                    .name("Oracle")
                    .id("oracle")
                    .driverClassName("ch.admin.bar.siard2.jdbc.OracleDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:oracle:thin:@%s:%s/%s%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName(),
                            config.getOptions()
                                    .map(optionsString -> "?" + optionsString)
                                    .orElse("")))
                    .jdbcConnectionStringDecoder(encoded -> {
                        val splitEncoded = encoded.split(":");
                        val splitPortAndDbNameWithOptions = splitEncoded[4].split("/", 2);
                        val splitDbNameAndOptions = splitPortAndDbNameWithOptions[1].split("\\?", 2);

                        return ServerBasedDbmsConnectionProperties.builder()
                                .host(splitEncoded[3].replace("@", ""))
                                .port(splitPortAndDbNameWithOptions[0])
                                .dbName(splitDbNameAndOptions[0])
                                .options(splitDbNameAndOptions.length > 1 ? Optional.of(splitDbNameAndOptions[1]) : Optional.empty())
                                .user("")
                                .password("")
                                .build();
                    })
                    .examplePort("1521")
                    .exampleHost("oracle.exampleHost.org")
                    .exampleDbName("Oracle-Database")
                    .build(),

            ServerBasedDbms.builder()
                    .name("PostgreSQL")
                    .id("postgresql")
                    .driverClassName("ch.admin.bar.siard2.jdbc.PostgresDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:postgresql://%s:%s/%s%s", // example: "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true"
                            config.getHost(),
                            config.getPort(),
                            config.getDbName(),
                            config.getOptions()
                                    .map(optionsString -> "?" + optionsString)
                                    .orElse("")))
                    .jdbcConnectionStringDecoder(encoded -> {
                        val splitEncoded = encoded.split(":");
                        val splitPortAndDbNameWithOptions = splitEncoded[3].split("/", 2);
                        val host = splitEncoded[2].replace("//", "");
                        val splitDbNameAndOptions = splitPortAndDbNameWithOptions[1].split("\\?", 2);

                        return ServerBasedDbmsConnectionProperties.builder()
                                .host(host)
                                .port(splitPortAndDbNameWithOptions[0])
                                .dbName(splitDbNameAndOptions[0])
                                .options(Optional.of(splitDbNameAndOptions)
                                        .filter(strings -> strings.length > 1)
                                        .map(strings -> strings[1]))
                                .user("")
                                .password("")
                                .build();
                    })
                    .examplePort("5432")
                    .exampleHost("postgresql.exampleHost.org")
                    .exampleDbName("PostgreSQL-Database")
                    .build(),

            ServerBasedDbms.builder()
                    .name("Microsoft SQL Server")
                    .id("sqlserver")
                    .driverClassName("ch.admin.bar.siard2.jdbc.MsSqlDriver")
                    .jdbcConnectionStringEncoder(config -> String.format(
                            "jdbc:sqlserver://%s:%s;databaseName=%s%s",
                            config.getHost(),
                            config.getPort(),
                            config.getDbName(),
                            config.getOptions()
                                    .map(optionsString -> ";" + optionsString)
                                    .orElse("")))
                    .jdbcConnectionStringDecoder(encoded -> {
                        val splitEncoded = encoded.split(":");
                        val splitPortAndDbNameWithOptions = splitEncoded[3].split(";");

                        val dbName = Arrays.stream(splitPortAndDbNameWithOptions)
                                .skip(1) // contains port
                                .filter(s -> s.startsWith("databaseName="))
                                .map(s -> s.replace("databaseName=", ""))
                                .findAny()
                                .orElseThrow(() -> new IllegalArgumentException("Missing database name"));

                        val options =  Arrays.stream(splitPortAndDbNameWithOptions)
                                .skip(1) // contains port
                                .filter(s -> !s.startsWith("databaseName="))
                                .collect(Collectors.joining(";"));

                        return ServerBasedDbmsConnectionProperties.builder()
                                .host(splitEncoded[2].replace("//", ""))
                                .port(splitPortAndDbNameWithOptions[0])
                                .dbName(dbName)
                                .options(Optional.of(options))
                                .user("")
                                .password("")
                                .build();
                    })
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

    public static Predicate<String> checkJdbcUrlValidity(final Dbms<?> serverBasedDbms) {
        return nullableValue -> Optional.ofNullable(nullableValue)
                .filter(value -> {
                    if (!value.startsWith("jdbc:" + serverBasedDbms.getId())) {
                        return false;
                    }

                    try {
                        serverBasedDbms.getJdbcConnectionStringDecoder().apply(value);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .isPresent();
    }
}
