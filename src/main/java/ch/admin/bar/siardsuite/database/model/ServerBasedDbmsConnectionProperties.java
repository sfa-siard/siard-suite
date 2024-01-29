package ch.admin.bar.siardsuite.database.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
@Builder(toBuilder = true)
public class ServerBasedDbmsConnectionProperties implements DbmsConnectionProperties<ServerBasedDbmsConnectionProperties> {
    @NonNull String host;
    @NonNull String port;
    @NonNull String dbName;
    @NonNull String user;
    @NonNull String password;

    @Builder.Default
    @NonNull
    Optional<String> options = Optional.empty();
}
