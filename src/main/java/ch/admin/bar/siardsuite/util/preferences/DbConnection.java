package ch.admin.bar.siardsuite.util.preferences;

import ch.admin.bar.siardsuite.database.DbmsRegistry;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.database.model.DbmsId;
import ch.admin.bar.siardsuite.database.model.FileBasedDbms;
import ch.admin.bar.siardsuite.database.model.FileBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbms;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbmsConnectionProperties;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.io.File;
import java.util.Optional;

@Value
@Builder
public class DbConnection {
    @NonNull String name;

    @NonNull DbmsId dbmsProduct;
    @NonNull String jdbcUrl;

    @NonNull String host;
    @NonNull String port;
    @NonNull String dbName;
    @NonNull String user;

    @NonNull String file;

    public Optional<DbmsConnectionData> tryMapToDbmsConnectionData() {
        val dbms = DbmsRegistry.findDbmsById(dbmsProduct);

        return OptionalHelper.firstPresent(
                () -> CastHelper.tryCast(dbms, ServerBasedDbms.class)
                        .map(serverBasedDbms -> new DbmsConnectionData(
                                serverBasedDbms,
                                ServerBasedDbmsConnectionProperties.builder()
                                        .host(host)
                                        .port(port)
                                        .dbName(dbName)
                                        .user(user)
                                        .password("")
                                        .build()
                        )),
                () -> CastHelper.tryCast(dbms, FileBasedDbms.class)
                        .map(fileBasedDbms -> new DbmsConnectionData(
                                fileBasedDbms,
                                new FileBasedDbmsConnectionProperties(new File(file))
                        ))
        );
    }

    public static DbConnection from(
            final DbmsConnectionData connectionData,
            final String name,
            final String jdbcUrl
    ) {

        val serverBasedProp = CastHelper.tryCast(
                connectionData.getProperties(),
                ServerBasedDbmsConnectionProperties.class);

        val fileBasedProp = CastHelper.tryCast(
                connectionData.getProperties(),
                FileBasedDbmsConnectionProperties.class);

        return DbConnection.builder()
                .name(name)
                .dbmsProduct(connectionData.getDbms().getId())
                .jdbcUrl(jdbcUrl)

                .host(serverBasedProp.map(ServerBasedDbmsConnectionProperties::getHost).orElse(""))
                .port(serverBasedProp.map(ServerBasedDbmsConnectionProperties::getPort).orElse(""))
                .dbName(serverBasedProp.map(ServerBasedDbmsConnectionProperties::getDbName).orElse(""))
                .user(serverBasedProp.map(ServerBasedDbmsConnectionProperties::getUser).orElse(""))

                .file(fileBasedProp.map(prop -> prop.getFile().getAbsolutePath()).orElse(""))

                .build();
    }
}
