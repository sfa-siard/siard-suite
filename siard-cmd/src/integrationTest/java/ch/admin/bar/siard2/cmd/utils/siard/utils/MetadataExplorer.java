package ch.admin.bar.siard2.cmd.utils.siard.utils;

import ch.admin.bar.siard2.cmd.utils.siard.model.SiardArchive;
import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedForeignKeyId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTypeId;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MetadataExplorer {

    private final SiardArchive siardArchive;

    public Optional<Metadata.Schema> tryFindBySchemaId(final Id<Metadata.Schema> id) {
        return siardArchive.getSiardMetadata().getSchemas().stream()
                .filter(schema -> schema.getName().equals(id))
                .findAny();
    }

    public Metadata.Table findByTableId(final QualifiedTableId qualifiedTableId) {
        return tryFindByTableId(qualifiedTableId)
                .orElseThrow(() -> new IllegalArgumentException("No table with id " + qualifiedTableId + " found"));
    }

    public Optional<Metadata.Table> tryFindByTableId(final QualifiedTableId qualifiedId) {
        return tryFindBySchemaId(qualifiedId.getSchemaId())
                .flatMap(schema -> schema.getTables().stream()
                        .filter(table -> table.getName().equals(qualifiedId.getTableId()))
                        .findAny());
    }

    public Metadata.Column findByColumnId(final QualifiedColumnId qualifiedColumnId) {
        return tryFindByColumnId(qualifiedColumnId)
                .orElseThrow(() -> new IllegalArgumentException("No column with id " + qualifiedColumnId + " found"));
    }

    public Optional<Metadata.Column> tryFindByColumnId(final QualifiedColumnId qualifiedColumnId) {
        return tryFindByTableId(qualifiedColumnId.getQualifiedTableId())
                .flatMap(table -> table.getColumns().stream()
                        .filter(column -> column.getName().equals(qualifiedColumnId.getColumnId()))
                        .findAny());
    }

    public Metadata.Type findByTypeId(final QualifiedTypeId qualifiedId) {
        return tryFindByTypeId(qualifiedId)
                .orElseThrow(() -> new IllegalArgumentException("No type with id " + qualifiedId + " found"));
    }

    public Optional<Metadata.Type> tryFindByTypeId(final QualifiedTypeId qualifiedId) {
        return tryFindBySchemaId(qualifiedId.getSchemaId())
                .flatMap(schema -> schema.getTypes().stream()
                        .filter(type -> type.getName().equals(qualifiedId.getTypeId()))
                        .findAny());
    }

    public Optional<Metadata.PrimaryKey> tryFindPrimaryKey(final QualifiedTableId tableId) {
        return tryFindByTableId(tableId)
                .flatMap(Metadata.Table::getPrimaryKey);
    }

    public Metadata.ForeignKey findForeignKey(final QualifiedForeignKeyId foreignKeyId) {
        return tryFindForeignKey(foreignKeyId)
                .orElseThrow(() -> new IllegalArgumentException("No foreign key with id " + foreignKeyId + " found"));
    }

    public Optional<Metadata.ForeignKey> tryFindForeignKey(final QualifiedForeignKeyId foreignKeyId) {
        return tryFindByTableId(foreignKeyId.getQualifiedTableId())
                .flatMap(table -> table.getForeignKeys().stream()
                        .filter(foreignKey -> foreignKey.getName().equals(foreignKeyId.getForeignKeyId()))
                        .findAny());
    }
}
