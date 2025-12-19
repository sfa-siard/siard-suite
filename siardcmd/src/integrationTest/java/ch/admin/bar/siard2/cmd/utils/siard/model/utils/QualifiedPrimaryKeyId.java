package ch.admin.bar.siard2.cmd.utils.siard.model.utils;

import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class QualifiedPrimaryKeyId {
    @NonNull Id<Metadata.Schema> schemaId;
    @NonNull Id<Metadata.Table> tableId;
    @NonNull Id<Metadata.PrimaryKey> primaryKeyId;

    public QualifiedTableId getQualifiedTableId() {
        return QualifiedTableId.builder()
                .schemaId(schemaId)
                .tableId(tableId)
                .build();
    }

    public static class QualifiedPrimaryKeyIdBuilder {
        public QualifiedPrimaryKeyIdBuilder qualifiedTableId(final QualifiedTableId qualifiedTableId) {
            schemaId(qualifiedTableId.getSchemaId());
            tableId(qualifiedTableId.getTableId());

            return this;
        }
    }
}
