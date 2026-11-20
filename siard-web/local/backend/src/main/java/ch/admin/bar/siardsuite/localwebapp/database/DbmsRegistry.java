package ch.admin.bar.siardsuite.localwebapp.database;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The DBMS the archive workflow supports, with their JDBC URL encoders and decoders.
 * Ported from {@code siard-suite-app}'s {@code DbmsRegistry}, which is frozen; the two copies may
 * drift until the model moves to {@code backend-core} (design D1).
 */
public final class DbmsRegistry {

    public static final List<Dbms<?>> DBMS = List.of(
            new FileBasedDbms(
                    "MS Access",
                    "access",
                    "ch.admin.bar.siard2.jdbc.AccessDriver",
                    properties -> "jdbc:access:" + properties.file().toAbsolutePath(),
                    encoded -> new FileBasedDbmsConnectionProperties(
                            Path.of(encoded.substring("jdbc:access:".length()))),
                    Path.of("D:/Projekte/SIARD2/JdbcAccess/testfiles/dbfile.mdb")),

            new ServerBasedDbms(
                    "DB/2",
                    "db2",
                    "ch.admin.bar.siard2.jdbc.Db2Driver",
                    properties -> String.format(
                            "jdbc:db2://%s:%s/%s%s",
                            properties.host(),
                            properties.port(),
                            properties.dbName(),
                            properties.options()
                                    .map(options -> "?" + options)
                                    .orElse("")),
                    DbmsRegistry::decodeHostPortDatabaseUrl,
                    "db2.exampleHost.org",
                    "50000",
                    "DB2-Database"),

            new ServerBasedDbms(
                    "MySQL",
                    "mysql",
                    "ch.admin.bar.siard2.jdbc.MySqlDriver",
                    properties -> String.format(
                            "jdbc:mysql://%s:%s/%s%s",
                            properties.host(),
                            properties.port(),
                            properties.dbName(),
                            properties.options()
                                    .map(options -> "?" + options)
                                    .orElse("")),
                    DbmsRegistry::decodeHostPortDatabaseUrl,
                    "mysql.exampleHost.org",
                    "3306",
                    "MySQL-Database"),

            new ServerBasedDbms(
                    "Oracle",
                    "oracle",
                    "ch.admin.bar.siard2.jdbc.OracleDriver",
                    properties -> String.format(
                            "jdbc:oracle:thin:@%s:%s/%s%s",
                            properties.host(),
                            properties.port(),
                            properties.dbName(),
                            properties.options()
                                    .map(options -> "?" + options)
                                    .orElse("")),
                    encoded -> {
                        String[] splitEncoded = encoded.split(":");
                        String[] splitPortAndDbNameWithOptions = splitEncoded[4].split("/", 2);
                        String[] splitDbNameAndOptions = splitPortAndDbNameWithOptions[1].split("\\?", 2);

                        return new ServerBasedDbmsConnectionProperties(
                                splitEncoded[3].replace("@", ""),
                                splitPortAndDbNameWithOptions[0],
                                splitDbNameAndOptions[0],
                                "",
                                "%",
                                "",
                                optionsOf(splitDbNameAndOptions));
                    },
                    "oracle.exampleHost.org",
                    "1521",
                    "Oracle-Database"),

            new ServerBasedDbms(
                    "PostgreSQL",
                    "postgresql",
                    "ch.admin.bar.siard2.jdbc.PostgresDriver",
                    properties -> String.format(
                            // example: "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true"
                            "jdbc:postgresql://%s:%s/%s%s",
                            properties.host(),
                            properties.port(),
                            properties.dbName(),
                            properties.options()
                                    .map(options -> "?" + options)
                                    .orElse("")),
                    DbmsRegistry::decodeHostPortDatabaseUrl,
                    "postgresql.exampleHost.org",
                    "5432",
                    "PostgreSQL-Database"),

            new ServerBasedDbms(
                    "Microsoft SQL Server",
                    "sqlserver",
                    "ch.admin.bar.siard2.jdbc.MsSqlDriver",
                    properties -> String.format(
                            "jdbc:sqlserver://%s:%s;databaseName=%s%s",
                            properties.host(),
                            properties.port(),
                            properties.dbName(),
                            properties.options()
                                    .map(options -> ";" + options)
                                    .orElse("")),
                    encoded -> {
                        String[] splitEncoded = encoded.split(":");
                        String[] splitPortAndDbNameWithOptions = splitEncoded[3].split(";");

                        String dbName = Arrays.stream(splitPortAndDbNameWithOptions)
                                .skip(1) // contains port
                                .filter(part -> part.startsWith("databaseName="))
                                .map(part -> part.substring("databaseName=".length()))
                                .findAny()
                                .orElseThrow(() -> new IllegalArgumentException("Missing database name"));

                        String options = Arrays.stream(splitPortAndDbNameWithOptions)
                                .skip(1) // contains port
                                .filter(part -> !part.startsWith("databaseName="))
                                .collect(Collectors.joining(";"));

                        return new ServerBasedDbmsConnectionProperties(
                                splitEncoded[2].replace("//", ""),
                                splitPortAndDbNameWithOptions[0],
                                dbName,
                                "",
                                "%",
                                "",
                                Optional.of(options).filter(value -> !value.isEmpty()));
                    },
                    "mssql.exampleHost.org",
                    "1433",
                    "MS-SQL-Database"));

    private DbmsRegistry() {
    }

    /** Decoder shared by the {@code jdbc:<id>://host:port/db?options} DBMS (DB/2, MySQL, PostgreSQL). */
    private static ServerBasedDbmsConnectionProperties decodeHostPortDatabaseUrl(String encoded) {
        String[] splitEncoded = encoded.split(":");
        String[] splitPortAndDbNameWithOptions = splitEncoded[3].split("/", 2);
        String[] splitDbNameAndOptions = splitPortAndDbNameWithOptions[1].split("\\?", 2);

        return new ServerBasedDbmsConnectionProperties(
                splitEncoded[2].replace("//", ""),
                splitPortAndDbNameWithOptions[0],
                splitDbNameAndOptions[0],
                "",
                "%",
                "",
                optionsOf(splitDbNameAndOptions));
    }

    private static Optional<String> optionsOf(String[] splitDbNameAndOptions) {
        return splitDbNameAndOptions.length > 1
                ? Optional.of(splitDbNameAndOptions[1])
                : Optional.empty();
    }

    /** The server-based entries, in registry order; file-based DBMS are excluded. */
    public static List<ServerBasedDbms> serverBased() {
        return DBMS.stream()
                .filter(ServerBasedDbms.class::isInstance)
                .map(ServerBasedDbms.class::cast)
                .toList();
    }

    public static Set<String> supportedNames() {
        return DBMS.stream()
                .map(Dbms::name)
                .collect(Collectors.toSet());
    }

    public static Optional<Dbms<?>> findById(String id) {
        return DBMS.stream()
                .filter(dbms -> dbms.id().equals(id))
                .findAny();
    }

    public static Optional<Dbms<?>> findByName(String name) {
        return DBMS.stream()
                .filter(dbms -> dbms.name().equals(name))
                .findAny();
    }

    /** Whether the value is a JDBC URL of the given DBMS that its decoder accepts. */
    public static Predicate<String> checkJdbcUrlValidity(Dbms<?> dbms) {
        return value -> Optional.ofNullable(value)
                .filter(candidate -> candidate.startsWith("jdbc:" + dbms.id()))
                .map(candidate -> {
                    try {
                        dbms.jdbcConnectionStringDecoder().apply(candidate);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .orElse(false);
    }
}
