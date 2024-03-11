package ch.admin.bar.siardsuite.presenter.archive.model;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;

@Value
@Builder
public class UserDefinedMetadata {
        @NonNull String dbName;
        @Nullable String description;
        @NonNull String owner;
        @NonNull String dataOriginTimespan;
        @Nullable String archiverName;
        @Nullable String archiverContact;
        @Nullable URI lobFolder;

        @NonNull File saveAt;
        @NonNull Boolean exportViewsAsTables;

        @NonNull DbmsConnectionData dbmsConnectionData;
}
