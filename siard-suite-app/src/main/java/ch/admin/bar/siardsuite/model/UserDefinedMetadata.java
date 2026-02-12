package ch.admin.bar.siardsuite.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.io.File;
import java.net.URI;
import java.util.Optional;

@Value
@Builder
public class UserDefinedMetadata {
    @NonNull String dbName;
    @NonNull Optional<String> description;
    @NonNull String owner;
    @NonNull String dataOriginTimespan;
    @NonNull Optional<String> archiverName;
    @NonNull Optional<String> archiverContact;
    @NonNull Optional<URI> lobFolder;

    @NonNull File saveAt;
    @NonNull Boolean exportViewsAsTables;
}
