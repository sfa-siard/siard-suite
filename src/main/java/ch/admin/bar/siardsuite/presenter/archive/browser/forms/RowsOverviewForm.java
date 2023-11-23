package ch.admin.bar.siardsuite.presenter.archive.browser.forms;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.Record;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.Value;
import ch.admin.bar.siard2.api.primary.CellImpl;
import ch.admin.bar.siardsuite.component.rendering.model.*;
import ch.admin.bar.siardsuite.model.database.DatabaseColumn;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.model.facades.PreTypeFacade;
import ch.admin.bar.siardsuite.presenter.archive.browser.forms.utils.ListAssembler;
import ch.admin.bar.siardsuite.util.FileHelper;
import ch.admin.bar.siardsuite.util.OS;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.enterag.sqlparser.Interval;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.utils.BU;
import ch.enterag.utils.DU;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ch.admin.bar.siardsuite.presenter.archive.browser.forms.utils.Converter.catchExceptions;
import static ch.admin.bar.siardsuite.presenter.archive.browser.forms.utils.Converter.longToString;

@Slf4j
public class RowsOverviewForm {

    private static final I18nKey LABEL_TABLE = I18nKey.of("tableContainer.labelTable");
    private static final I18nKey LABEL_NUMBER_OF_ROWS = I18nKey.of("tableContainer.labelNumberOfRows");

    public static RenderableForm<DatabaseTable> create(@NonNull final DatabaseTable table) {
        val tableProperties = table.getColumns().stream()
                .map(column -> new TableColumnProperty<>(
                        DisplayableText.of(column.getName()),
                        row -> row.findCellValue(column.getName()),
                        createCellClickListener(column)))
                .collect(Collectors.toList());


        return RenderableForm.<DatabaseTable>builder()
                .dataSupplier(() -> table)
                .group(RenderableFormGroup.<DatabaseTable>builder()
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_TABLE,
                                DatabaseTable::getName))
                        .property(new ReadOnlyStringProperty<>(
                                LABEL_NUMBER_OF_ROWS,
                                longToString(DatabaseTable::getNumberOfRows)))
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
                    catchExceptions(record::getCells),
                    catchExceptions(record::getCell)
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
            return extractText(cell);
        }

        private String extractText(final Cell cell) {
            if (cell == null || cell.isNull()) {
                return "";
            }
            try {
                switch (cell.getMetaValue().getPreType()) {
                    case Types.BINARY:
                    case Types.VARBINARY:
                    case Types.BLOB:
                        val bytes = cell.getBytes();

                        if (bytes.length == 0) {
                            return "";
                        }

                        if (bytes.length < 16) {
                            return "0x" + BU.toHex(cell.getBytes());
                        }

                        return "0x" + BU.toHex(cell.getBytes()).substring(0, 16) + "...";

                    default:
                        return cell.getString();
                }
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

        @Override
        public long getNumberOfItems() {
            return table.getMetaTable().getRows();
        }
    }


    private static Optional<TableColumnProperty.CellClickedListener<RecordWrapper>> createCellClickListener(final DatabaseColumn column) {
        try {
            if (!new PreTypeFacade(column.getColumn().getPreType()).isBlob()) {
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("Can not read pre-type of column {}. Message: {}", column.getName(), e.getMessage());
            return Optional.empty();
        }

        return Optional.of((property, value) -> {
            val absoluteLobFolder = column.getColumn().getAbsoluteLobFolder();
            val cell = value.findCell(column.getName());

            if (absoluteLobFolder == null) {
                val cellImpl = (CellImpl) cell;
                Path tempFilePath = FileHelper.createTempFile(cellImpl.getLobFilename(), cell.getBytes());
                OS.openFile(String.valueOf(tempFilePath));
            } else {
                OS.openFile(absoluteLobFolder + cell.getFilename());
            }
        });
    }
}
