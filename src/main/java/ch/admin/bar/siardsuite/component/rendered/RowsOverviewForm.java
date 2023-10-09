package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.Record;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siardsuite.component.rendered.utils.Converter;
import ch.admin.bar.siardsuite.component.rendered.utils.ListAssembler;
import ch.admin.bar.siardsuite.component.rendering.model.LazyLoadingDataSource;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableLazyLoadingTable;
import ch.admin.bar.siardsuite.model.database.DatabaseCell;
import ch.admin.bar.siardsuite.model.database.DatabaseRow;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.model.facades.PreTypeFacade;
import ch.admin.bar.siardsuite.util.I18nKey;
import ch.enterag.utils.BU;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class RowsOverviewForm {
    public static RenderableForm<DatabaseTable> create(final DatabaseTable table) {
        val tableProperties = table.getColumns().stream()
                .map(column -> new ReadOnlyStringProperty<RecordWrapper>(
                        I18nKey.of(column.getName()),
                        row -> row.findCellValue(column.name())))
                .collect(Collectors.toList());


        return RenderableForm.<DatabaseTable>builder()
                .dataExtractor(controller -> table)
                .group(RenderableFormGroup.<DatabaseTable>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("tableContainer.labelTable"),
                                DatabaseTable::name))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("tableContainer.labelNumberOfRows"),
                                DatabaseTable::getNumberOfRows))
                        .property(RenderableLazyLoadingTable.<DatabaseTable, RecordWrapper>builder()
                                .dataExtractor(databaseTable -> new RecordDataSource(table.getTable()))
                                .properties(tableProperties)
                                .build())
                        .build())
                .build();
    }

    public static class RecordWrapper {
        @Getter
        private final Record record;
        private final Map<String, Cell> cellsByName;

        @SneakyThrows // TODO Remove
        public RecordWrapper(@NonNull Record record) {
            this.record = record;

            val nrOfCells = record.getCells();

            val cells = new ListAssembler<>(
                    Converter.catchExceptions(() -> (int)record.getCells(), 0),
                    Converter.catchExceptions(record::getCell, null) // TODO remove "null"
            ).assemble();

            this.cellsByName = cells.stream()
                    .collect(Collectors.toMap(cell -> cell.getMetaColumn().getName(), cell -> cell));
        }

        public Cell findCell(final String name) {
            return Optional.ofNullable(cellsByName.get(name))
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("No cell with name %s found", name)));
        }

        private String findCellValue(final String name) {
            val cell = findCell(name);

            try {
                if (new PreTypeFacade(cell.getMetaColumn().getPreType()).isBlob()) {
                    return "0x" + BU.toHex(cell.getBytes()).substring(0, 16) + "...";
                }
                return cell.getString();
            } catch (IOException e) {
                return "ERROR"; // TODO
            }
        }
    }

    @Value
    public static class RecordDataSource implements LazyLoadingDataSource<RecordWrapper> {
        private final Table table;
        private final long nrOfRows;

        public RecordDataSource(Table table) {
            this.table = table;
            this.nrOfRows = table.getMetaTable().getRows();
        }

        @Override
        public long getNrOfEntries() {
            return nrOfRows;
        }

        @SneakyThrows // TODO temp
        @Override
        public List<RecordWrapper> load(int startIndex, int nrOfItems) {
            val recordDispenser = table.openRecords(); // TODO: not always open new dispenser

            if (startIndex != 0) {
                recordDispenser.skip(startIndex - 1);
            }

            final List<RecordWrapper> collected = new ArrayList<>();
            for (int x = 0; x < nrOfItems; x++) {
                val record = recordDispenser.get();

                if (record == null) {
                    break;
                }

                collected.add(new RecordWrapper(record));
            }

            return collected;
        }

        @Override
        public long findIndexOf(RecordWrapper item) {
            return item.getRecord().getRecord();
        }
    }
}
