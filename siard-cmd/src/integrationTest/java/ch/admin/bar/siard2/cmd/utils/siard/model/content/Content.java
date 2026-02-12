package ch.admin.bar.siard2.cmd.utils.siard.model.content;

import ch.admin.bar.siard2.cmd.utils.siard.model.utils.FolderId;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class Content implements Updatable<Content> {

    @NonNull List<Table> tables;

    @Override
    public Content applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new Content(
                updatedThis.tables.stream()
                        .map(table -> table.applyUpdates(updater))
                        .collect(Collectors.toList()));
    }

    public Table findTable(final FolderId schemaFolder, final FolderId tableFolder) {
        return tables.stream()
                .filter(table -> table.getSchemaFolder().equals(schemaFolder) && table.getTableFolder().equals(tableFolder))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No table in folders %s.%s found", schemaFolder.getValue(), tableFolder.getValue())));
    }

    @Value
    @Builder
    public static class Table implements Updatable<Table> {
        FolderId schemaFolder;
        FolderId tableFolder;

        TableContent tableContent;

        @Override
        public Table applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new Table(
                    updatedThis.schemaFolder.applyUpdates(updater),
                    updatedThis.tableFolder.applyUpdates(updater),
                    updatedThis.tableContent.applyUpdates(updater));
        }
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    @JacksonXmlRootElement(localName = "table")
    public static class TableContent implements Updatable<TableContent> {

        @NonNull
        @Singular
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "row")
        List<TableRow> rows;

        @Override
        public TableContent applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new TableContent(updatedThis.rows.stream()
                    .map(tableRow -> tableRow.applyUpdates(updater))
                    .collect(Collectors.toList()));
        }
    }

    @Data
    @Builder(toBuilder = true)
    @JsonDeserialize(using = ContentReader.Deserializer.class)
    public static class TableRow implements Updatable<TableRow> {

        @NonNull
        @Singular
        List<TableCell> cells;

        @Override
        public TableRow applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new TableRow(
                    updatedThis.cells.stream()
                            .map(tableCell -> tableCell.applyUpdates(updater))
                            .collect(Collectors.toList()));
        }
    }

    @Value
    @Builder(toBuilder = true)
    public static class TableCell implements Updatable<TableCell> {
        int columnNumber;
        String value;

        @Override
        public TableCell applyUpdates(Updater updater) {
            return updater.applyUpdate(this);
        }
    }
}
