package ch.admin.bar.siardsuite.service.preferences;

import ch.admin.bar.siardsuite.service.database.DbmsRegistry;
import ch.admin.bar.siardsuite.service.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.service.database.model.FileBasedDbms;
import ch.admin.bar.siardsuite.service.database.model.FileBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.service.database.model.ServerBasedDbms;
import ch.admin.bar.siardsuite.service.database.model.ServerBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.io.File;
import java.util.Optional;

@Value
@Builder(toBuilder = true)
public class RecentDbConnection {
    @NonNull String name;

    @NonNull String dbmsProduct;
    @NonNull String connectionOptions;

    @NonNull String host;
    @NonNull String port;
    @NonNull String dbName;
    @NonNull String user;

    @NonNull String file;

    public DbmsConnectionData mapToDbmsConnectionData() {
        val dbms = DbmsRegistry.findDbmsByName(dbmsProduct);

        return OptionalHelper.firstPresent(
                        () -> CastHelper.tryCast(dbms, ServerBasedDbms.class)
                                .map(serverBasedDbms -> new DbmsConnectionData(
                                        serverBasedDbms,
                                        ServerBasedDbmsConnectionProperties.builder()
                                                .host(host)
                                                .port(port)
                                                .dbName(dbName)
                                                .options(Optional.of(connectionOptions)
                                                        .filter(s -> !s.isEmpty()))
                                                .user(user)
                                                .password("")
                                                .build()
                                )),
                        () -> CastHelper.tryCast(dbms, FileBasedDbms.class)
                                .map(fileBasedDbms -> new DbmsConnectionData(
                                        fileBasedDbms,
                                        new FileBasedDbmsConnectionProperties(new File(file))
                                )))
                .orElseThrow(() -> new IllegalStateException(String.format(
                        "Failed to map stored connection %s to valid connection data",
                        this)));
    }

    public static RecentDbConnection from(
            final DbmsConnectionData connectionData,
            final String name
    ) {

        val serverBasedProp = CastHelper.tryCast(
                connectionData.getProperties(),
                ServerBasedDbmsConnectionProperties.class);

        val fileBasedProp = CastHelper.tryCast(
                connectionData.getProperties(),
                FileBasedDbmsConnectionProperties.class);

        return RecentDbConnection.builder()
                .name(name)
                .dbmsProduct(connectionData.getDbms().getName())
                .connectionOptions(serverBasedProp.flatMap(ServerBasedDbmsConnectionProperties::getOptions).orElse(""))
                .host(serverBasedProp.map(ServerBasedDbmsConnectionProperties::getHost).orElse(""))
                .port(serverBasedProp.map(ServerBasedDbmsConnectionProperties::getPort).orElse(""))
                .dbName(serverBasedProp.map(ServerBasedDbmsConnectionProperties::getDbName).orElse(""))
                .user(serverBasedProp.map(ServerBasedDbmsConnectionProperties::getUser).orElse(""))

                .file(fileBasedProp.map(prop -> prop.getFile().getAbsolutePath()).orElse(""))

                .build();
    }
}
