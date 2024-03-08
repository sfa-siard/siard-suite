package ch.admin.bar.siardsuite.presenter.archive.model;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.io.File;
import java.net.URI;

@Value
@Builder
public class UserDefinedMetadata {
        String dbName;
        String description;
        String owner;
        String dataOriginTimespan;
        String archiverName;
        String archiverContact;
        URI lobFolder;

        @NonNull File saveAt;
        @NonNull Boolean exportViewsAsTables;

        @NonNull DbmsConnectionData dbmsConnectionData;
}
