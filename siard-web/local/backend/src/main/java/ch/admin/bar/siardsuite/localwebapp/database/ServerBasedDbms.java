package ch.admin.bar.siardsuite.localwebapp.database;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/** A DBMS reached over the network, described by host/port/database properties. */
public record ServerBasedDbms(
        String name,
        String id,
        String driverClassName,
        Function<ServerBasedDbmsConnectionProperties, String> jdbcConnectionStringEncoder,
        Function<String, ServerBasedDbmsConnectionProperties> jdbcConnectionStringDecoder,
        String exampleHost,
        String examplePort,
        String exampleDbName) implements Dbms<ServerBasedDbmsConnectionProperties> {

    public ServerBasedDbms {
        requireNonNull(name, "name");
        requireNonNull(id, "id");
        requireNonNull(driverClassName, "driverClassName");
        requireNonNull(jdbcConnectionStringEncoder, "jdbcConnectionStringEncoder");
        requireNonNull(jdbcConnectionStringDecoder, "jdbcConnectionStringDecoder");
        requireNonNull(exampleHost, "exampleHost");
        requireNonNull(examplePort, "examplePort");
        requireNonNull(exampleDbName, "exampleDbName");
    }
}
