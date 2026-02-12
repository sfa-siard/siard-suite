package ch.admin.bar.siard2.cmd.utils.siard.utils;

import ch.admin.bar.siard2.cmd.utils.siard.model.SiardArchive;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.Content;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.FolderId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContentExplorer {

    private final Set<TableDataMapping> tableDataMappings;
    private final Set<ColumnMapping> columnMappings;

    public ContentExplorer(final SiardArchive siardArchive) {
        this.tableDataMappings = findMappings(siardArchive);
        columnMappings = extractColumnMappings(siardArchive);
    }

    public Set<QualifiedTableId> getAllIds() {
        return tableDataMappings.stream()
                .map(TableDataMapping::getTableId)
                .collect(Collectors.toSet());
    }

    public Optional<Content.Table> tryFindTable(final QualifiedTableId tableId) {
        return tableDataMappings.stream()
                .filter(tableDataMapping -> tableDataMapping.getTableId().equals(tableId))
                .findAny()
                .map(TableDataMapping::getTable);
    }

    public Content.Table findTable(final QualifiedTableId tableId) {
        return tryFindTable(tableId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "No table with id %s found. Available tables are: %s",
                        tableId,
                        getAllIds().stream()
                                .map(QualifiedTableId::toString)
                                .collect(Collectors.joining("\n - "))
                )));
    }

    public List<Content.TableCell> findCells(final QualifiedColumnId columnId) {
        val table = findTable(columnId.getQualifiedTableId());
        val columnMapping = findColumnMapping(columnId);

        return table.getTableContent().getRows().stream()
                .flatMap(tableRow -> tableRow.getCells().stream()
                        .filter(tableCell -> tableCell.getColumnNumber() == columnMapping.getColumnNumber()))
                .collect(Collectors.toList());
    }

    public String findCellValue(final QualifiedColumnId columnId, final int rowIndex) {
        val cells = findCells(columnId);

        if (rowIndex >= cells.size()) {
            throw new IllegalArgumentException(String.format(
                    "Just %d rows available for column %s (searched row: %d)",
                    cells.size(),
                    columnId,
                    rowIndex
            ));
        }

        return cells.get(rowIndex).getValue();
    }

    private ColumnMapping findColumnMapping(final QualifiedColumnId columnId) {
        val matchingMappings = columnMappings.stream()
                .filter(columnMapping -> columnMapping.getColumnId().equals(columnId))
                .collect(Collectors.toList());

        if (matchingMappings.size() != 1) {
            throw new IllegalStateException(String.format(
                    "No or ambiguous mappings found for column %s. Found mappings are: %s",
                    columnId,
                    matchingMappings.stream()
                            .map(ColumnMapping::toString)
                            .collect(Collectors.joining(";"))
            ));
        }

        return matchingMappings.get(0);
    }

    private static Set<TableDataMapping> findMappings(final SiardArchive siardArchive) {
        return siardArchive.getSiardMetadata().getSchemas().stream()
                .flatMap(schemaMetaData -> schemaMetaData.getTables().stream()
                        .map(tableMetaData -> TableDataMapping.builder()
                                .tableId(QualifiedTableId.builder()
                                        .schemaId(schemaMetaData.getName())
                                        .tableId(tableMetaData.getName())
                                        .build())
                                .schemaFolder(schemaMetaData.getFolder())
                                .tableFolder(tableMetaData.getFolder())
                                .table(siardArchive.getSiardContent().findTable(schemaMetaData.getFolder(), tableMetaData.getFolder()))
                                .build()))
                .collect(Collectors.toSet());
    }

    private static Set<ColumnMapping> extractColumnMappings(final SiardArchive siardArchive) {
        return siardArchive.getSiardMetadata().getSchemas().stream()
                .flatMap(schemaMetaData -> schemaMetaData.getTables().stream()
                        .flatMap(tableMetaData -> streamWithIndex(tableMetaData.getColumns())
                                .map(columnIndexedEntry -> new ColumnMapping(
                                        QualifiedColumnId.builder()
                                                .schemaId(schemaMetaData.getName())
                                                .tableId(tableMetaData.getName())
                                                .columnId(columnIndexedEntry.getEntry().getName())
                                                .build(),
                                        columnIndexedEntry.getIndex() + 1 // 1 based index, see SIARD spec
                                ))))
                .collect(Collectors.toSet());
    }

    private static <T> Stream<IndexedEntry<T>> streamWithIndex(final List<T> list) {
        val tempList = new ArrayList<IndexedEntry<T>>(list.size());

        for (int counter = 0; counter < list.size(); counter++) {
            val entry = list.get(counter);
            tempList.add(new IndexedEntry<>(counter, entry));
        }

        return tempList.stream();
    }

    @Value
    private static class IndexedEntry<T> {
        int index;
        T entry;
    }

    @Value
    @Builder
    private static class TableDataMapping {
        @NonNull QualifiedTableId tableId;
        @NonNull FolderId schemaFolder; // FIXME: Not really needed
        @NonNull FolderId tableFolder; // FIXME: Not really needed
        @NonNull Content.Table table;
    }

    @Value
    @Builder
    private static class ColumnMapping {
        @NonNull QualifiedColumnId columnId;
        int columnNumber;
    }
}
