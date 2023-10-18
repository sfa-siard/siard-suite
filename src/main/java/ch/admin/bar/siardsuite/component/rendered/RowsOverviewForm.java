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
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.model.facades.PreTypeFacade;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.enterag.utils.BU;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RowsOverviewForm {

    private static final I18nKey LABEL_TABLE = I18nKey.of("tableContainer.labelTable");
    private static final I18nKey LABEL_NUMBER_OF_ROWS = I18nKey.of("tableContainer.labelNumberOfRows");

    public static RenderableForm<DatabaseTable> create(@NonNull final DatabaseTable table) {
        val tableProperties = table.getColumns().stream()
                .map(column -> new ReadOnlyStringProperty<RecordWrapper>(
                        DisplayableText.of(column.getName()),
                        row -> row.findCellValue(column.getName())))
                .collect(Collectors.toList());


        return RenderableForm.<DatabaseTable>builder()
                .dataSupplier(() -> table)
                .group(RenderableFormGroup.<DatabaseTable>builder()
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_TABLE,
                                DatabaseTable::name))
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_NUMBER_OF_ROWS,
                                Converter.longToString(DatabaseTable::getNumberOfRows)))
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

        public RecordWrapper(@NonNull Record record) {
            this.record = record;

            val cells = new ListAssembler<>(
                    Converter.catchExceptions(record::getCells).get(),
                    Converter.catchExceptions(record::getCell)
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
                    val bytes = cell.getBytes();

                    if (bytes.length == 0) {
                        return "";
                    }

                    if (bytes.length < 16) {
                        return "0x" + BU.toHex(cell.getBytes());
                    }

                    return "0x" + BU.toHex(cell.getBytes()).substring(0, 16) + "...";
                }
                return cell.getString();
            } catch (IOException e) {
                return "";
            }
        }
    }

    @RequiredArgsConstructor
    public static class RecordDataSource implements LazyLoadingDataSource<RecordWrapper> {
        private final Table table;

        @SneakyThrows
        @Override
        public List<RecordWrapper> load(int startIndex, int nrOfItems) {
            val recordDispenser = table.openRecords();
            recordDispenser.skip(startIndex);

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
