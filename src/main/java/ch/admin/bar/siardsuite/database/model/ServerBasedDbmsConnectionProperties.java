package ch.admin.bar.siardsuite.database.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

import java.util.Optional;

@Value
@Builder(toBuilder = true)
public class ServerBasedDbmsConnectionProperties implements DbmsConnectionProperties<ServerBasedDbmsConnectionProperties> {
    @NonNull String host;
    @NonNull String port;
    @NonNull String dbName;
    @NonNull String user;

    @NonNull
    @ToString.Exclude
    String password;

    @Builder.Default
    @NonNull
    Optional<String> options = Optional.empty();
}
