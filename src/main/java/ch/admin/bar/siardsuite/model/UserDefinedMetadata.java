package ch.admin.bar.siardsuite.model;

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
}
