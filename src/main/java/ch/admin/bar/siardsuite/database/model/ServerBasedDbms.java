package ch.admin.bar.siardsuite.database.model;

import ch.admin.bar.siardsuite.util.ThrowingFunction;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

@Value
@Builder
public class ServerBasedDbms implements Dbms<ServerBasedDbmsConnectionProperties> {
    @NonNull String name;
    @NonNull String id;
    @NonNull String driverClassName;
    @NonNull Function<ServerBasedDbmsConnectionProperties, String> jdbcConnectionStringEncoder;
    @NonNull ThrowingFunction<String, ServerBasedDbmsConnectionProperties> jdbcConnectionStringDecoder;

    @NonNull String exampleHost;
    @NonNull String examplePort;
    @NonNull String exampleDbName;
}
