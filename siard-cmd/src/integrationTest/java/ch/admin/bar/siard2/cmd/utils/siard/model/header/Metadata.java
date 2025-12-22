package ch.admin.bar.siard2.cmd.utils.siard.model.header;

import ch.admin.bar.siard2.cmd.utils.siard.model.utils.FolderId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.StringWrapper;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@Jacksonized
@JacksonXmlRootElement(localName = "siardArchive")
public class Metadata implements Updatable<Metadata> {
    @NonNull StringWrapper dbname;

    @NonNull
    @Builder.Default
    Set<Schema> schemas = new HashSet<>();

    @Override
    public Metadata applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new Metadata(
                updatedThis.dbname.applyUpdates(updater),
                updatedThis.schemas.stream()
                        .map(schema -> schema.applyUpdates(updater))
                        .collect(Collectors.toSet()));
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class Schema implements Updatable<Schema> {
        @NonNull Id<Schema> name;
        @NonNull FolderId folder;

        @NonNull
        @Builder.Default
        Set<Type> types = new HashSet<>();

        @NonNull
        @Builder.Default
        Set<Table> tables = new HashSet<>();

        @Override
        public Schema applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new Schema(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.folder.applyUpdates(updater),
                    updatedThis.types.stream()
                            .map(type -> type.applyUpdates(updater))
                            .collect(Collectors.toSet()),
                    updatedThis.tables.stream()
                            .map(table -> table.applyUpdates(updater))
                            .collect(Collectors.toSet())
            );
        }
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class Table implements Updatable<Table> {
        Id<Table> name;

        @Builder.Default
        List<Column> columns = new ArrayList<>();

        @NonNull
        @Builder.Default
        Optional<PrimaryKey> primaryKey = Optional.empty();

        @Builder.Default
        Set<ForeignKey> foreignKeys = new HashSet<>();

        FolderId folder;

        @NonNull
        @Builder.Default
        Optional<StringWrapper> description = Optional.empty();

        @Override
        public Table applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new Table(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.columns.stream()
                            .map(column -> column.applyUpdates(updater))
                            .collect(Collectors.toList()),
                    updatedThis.primaryKey.map(primaryKey -> primaryKey.applyUpdates(updater)),
                    updatedThis.foreignKeys.stream()
                            .map(foreignKey -> foreignKey.applyUpdates(updater))
                            .collect(Collectors.toSet()),
                    updatedThis.folder.applyUpdates(updater),
                    updatedThis.description.map(description -> description.applyUpdates(updater))
            );
        }
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class Column implements Updatable<Column> {
        Id<Column> name;

        @NonNull
        @Builder.Default
        Boolean nullable = true; // from the SIARD 2.2 spec. p.20: "No indication of <nullable> implies <nullable>true</nullable>"

        @NonNull
        @Builder.Default
        Optional<Id<Schema>> typeSchema = Optional.empty();

        @NonNull
        @Builder.Default
        Optional<Id<Type>> typeName = Optional.empty();

        @NonNull
        @Builder.Default
        Optional<Id<Type>> type = Optional.empty();

        @NonNull
        @Builder.Default
        Optional<Id<MimeType>> mimeType = Optional.empty();

        @NonNull
        @Builder.Default
        Optional<Id<Type>> typeOriginal = Optional.empty();

        @Override
        public Column applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new Column(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.nullable,
                    updatedThis.typeSchema.map(typeId -> typeId.applyUpdates(updater)),
                    updatedThis.typeName.map(typeId -> typeId.applyUpdates(updater)),
                    updatedThis.type.map(typeId -> typeId.applyUpdates(updater)),
                    updatedThis.mimeType.map(mimeType -> mimeType.applyUpdates(updater)),
                    updatedThis.typeOriginal.map(typeId -> typeId.applyUpdates(updater))
            );
        }
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class PrimaryKey implements Updatable<PrimaryKey> {
        Id<PrimaryKey> name;

        @NonNull
        @Builder.Default
        @JacksonXmlProperty(localName = "column")
        @JacksonXmlElementWrapper(useWrapping = false)
        Set<Id<Column>> columns = new HashSet<>();

        @Override
        public PrimaryKey applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new PrimaryKey(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.columns.stream()
                            .map(columnId -> columnId.applyUpdates(updater))
                            .collect(Collectors.toSet()));
        }
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class ForeignKey implements Updatable<ForeignKey> {
        Id<ForeignKey> name;
        Id<Schema> referencedSchema;
        Id<Table> referencedTable;

        @NonNull
        @Builder.Default
        @JacksonXmlProperty(localName = "reference")
        @JacksonXmlElementWrapper(useWrapping = false)
        Set<Reference> references = new HashSet<>();


        @Builder.Default
        Optional<StringWrapper> deleteAction = Optional.empty();
        @Builder.Default
        Optional<StringWrapper> updateAction = Optional.empty();

        @Override
        public ForeignKey applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new ForeignKey(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.referencedSchema.applyUpdates(updater),
                    updatedThis.referencedTable.applyUpdates(updater),
                    updatedThis.references.stream()
                            .map(reference -> reference.applyUpdates(updater))
                            .collect(Collectors.toSet()),
                    updatedThis.deleteAction.map(s -> s.applyUpdates(updater)),
                    updatedThis.updateAction.map(s -> s.applyUpdates(updater)));
        }
    }

    @Value
    @Builder
    @Jacksonized
    public static class Reference implements Updatable<Reference> {
        Id<Column> column;
        Id<Column> referenced;

        @Override
        public Reference applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new Reference(
                    updatedThis.column.applyUpdates(updater),
                    updatedThis.referenced.applyUpdates(updater));
        }
    }

    @Value
    @Builder
    @Jacksonized
    public static class Type implements Updatable<Type> {
        @NonNull Id<Type> name;
        @NonNull Category category;
        @NonNull Boolean instantiable;

        @JsonProperty("final")
        @NonNull
        Boolean finalFlag;

        public enum Category {
            distinct,
            udt
        }

        @Override
        public Type applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new Type(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.category,
                    updatedThis.instantiable,
                    updatedThis.finalFlag);
        }
    }


    @Value
    @Builder
    @Jacksonized
    public static class MimeType implements Updatable<MimeType> {
        String mimeType;

        @Override
        public MimeType applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new MimeType(
                    updatedThis.mimeType
            );
        }
    }
}
