package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siardsuite.model.facades.MetaDataFacade;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
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

        @SneakyThrows
        public void writeTo(final MetaData metaData) {
                metaData.setDbName(dbName);
                metaData.setDescription(description);
                metaData.setDataOwner(owner);
                metaData.setDataOriginTimespan(dataOriginTimespan);
                metaData.setArchiver(archiverName);
                metaData.setArchiverContact(archiverContact);

                new MetaDataFacade(metaData).setLobFolder(lobFolder);
        }
}
