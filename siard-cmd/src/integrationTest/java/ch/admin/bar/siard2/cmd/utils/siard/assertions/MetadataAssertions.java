package ch.admin.bar.siard2.cmd.utils.siard.assertions;

import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.assertj.core.api.Assertions;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MetadataAssertions {
    @Builder(buildMethodName = "assertEqual")
    public MetadataAssertions(
            @NonNull Metadata expected,
            @NonNull Metadata actual

    ) {
        // columns
        Assertions
                .assertThat(extractQualifiedColumns(actual))
                .as("Columns of SIARD archives are not equal")
                .isEqualTo(extractQualifiedColumns(expected));

        // primary keys
        Assertions
                .assertThat(extractQualifiedPrimaryKeys(actual))
                .as("Primary keys of SIARD archives are not equal")
                .isEqualTo(extractQualifiedPrimaryKeys(expected));

        // foreign keys
        Assertions
                .assertThat(extractQualifiedForeignKeys(actual))
                .as("Foreign keys of SIARD archives are not equal")
                .isEqualTo(extractQualifiedForeignKeys(expected));

        Assertions.assertThat(actual)
                .as("SIARD archives are not equal")
                .isEqualTo(expected);
    }

    private static List<Qualifier<Metadata.Column>> extractQualifiedColumns(final Metadata siardMetadata) {
        return siardMetadata.getSchemas().stream()
                .flatMap(schema -> schema.getTables().stream()
                        .flatMap(table -> table.getColumns().stream()
                                .map(column -> new Qualifier<>(
                                        String.format("%s.%s.%s",
                                                table.getName(),
                                                schema.getName(),
                                                column.getName()),
                                        column))))
                .sorted(Comparator.comparing(Qualifier::getQualifier))
                .collect(Collectors.toList());
    }

    private static List<Qualifier<Metadata.PrimaryKey>> extractQualifiedPrimaryKeys(final Metadata siardMetadata) {
        return siardMetadata.getSchemas().stream()
                .flatMap(schema -> schema.getTables().stream()
                        .flatMap(table -> stream(table.getPrimaryKey())
                                .map(primaryKey -> new Qualifier<>(
                                        String.format("%s.%s.%s->%s",
                                                table.getName(),
                                                schema.getName(),
                                                primaryKey.getName(),
                                                primaryKey.getColumns().stream()
                                                        .map(columnId -> columnId.getValue().getValue())
                                                        .sorted()
                                                        .collect(Collectors.joining(";"))),
                                        primaryKey))))
                .sorted(Comparator.comparing(Qualifier::getQualifier))
                .collect(Collectors.toList());
    }

    private static List<Qualifier<Metadata.ForeignKey>> extractQualifiedForeignKeys(final Metadata siardMetadata) {
        return siardMetadata.getSchemas().stream()
                .flatMap(schema -> schema.getTables().stream()
                        .flatMap(table -> table.getForeignKeys().stream()
                                .map(foreignKey -> new Qualifier<>(
                                        String.format("%s.%s.%s",
                                                table.getName(),
                                                schema.getName(),
                                                foreignKey.getName()),
                                        foreignKey
                                ))))
                .sorted(Comparator.comparing(Qualifier::getQualifier))
                .collect(Collectors.toList());
    }

    private static <T> Stream<T> stream(final Optional<T> optional) {
        return optional
                .map(Stream::of)
                .orElseGet(Stream::empty);
    }

    @Value
    private static class Qualifier<T> {
        String qualifier;
        T value;
    }
}
