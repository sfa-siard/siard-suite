package ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.TableRecord;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.model.database.DatabaseColumn;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.ui.component.rendering.model.*;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.Converter;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.ListAssembler;
import ch.admin.bar.siardsuite.util.FileHelper;
import ch.admin.bar.siardsuite.util.OS;
import ch.enterag.utils.BU;
import ch.enterag.utils.mime.MimeTypes;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                                Converter.longToString(DatabaseTable::getNumberOfRows)))
                        .property(RenderableLazyLoadingTable.<DatabaseTable, TableRecordWrapper>builder()
                                .dataExtractor(databaseTable -> new TableRecordDataSource(table.getTable()))
                                .properties(tableProperties)
                                .build())
                        .build())
                .build();
    }

    public static class TableRecordWrapper {
        @Getter
        private final TableRecord tableRecord;
        private final Map<String, Cell> cellsByName;

        public TableRecordWrapper(@NonNull TableRecord tableRecord) {
            this.tableRecord = tableRecord;

            val cells = new ListAssembler<>(
                    Converter.catchExceptions(tableRecord::getCells),
                    Converter.catchExceptions(tableRecord::getCell)
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
    public static class TableRecordDataSource implements LazyLoadingDataSource<TableRecordWrapper> {
        private final Table table;

        @SneakyThrows
        @Override
        public List<TableRecordWrapper> load(int startIndex, int nrOfItems) {
            val tableRecordDispenser = table.openTableRecords();
            tableRecordDispenser.skip(startIndex);

            final List<TableRecordWrapper> collected = new ArrayList<>();
            for (int x = 0; x < nrOfItems; x++) {
                val record = tableRecordDispenser.get();

                if (record == null) {
                    break;
                }

                collected.add(new TableRecordWrapper(record));
            }

            return collected;
        }

        @Override
        public long findIndexOf(TableRecordWrapper item) {
            return item.getTableRecord().getRecord();
        }

        @Override
        public long getNumberOfItems() {
            return table.getMetaTable().getRows();
        }
    }

    private static Optional<TableColumnProperty.CellClickedListener<TableRecordWrapper>> createCellClickListener(final DatabaseColumn column) {
        try {
            val type = column.getColumn().getPreType();
            val clickListenerSupported = type == Types.BINARY || type == Types.VARBINARY || type == Types.BLOB;

            if (!clickListenerSupported) {
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
                Tika tika = new Tika();
                String mimeType = tika.detect(cell.getBytes());
                String extension = "." + MimeTypes.getExtension(mimeType);
                Path tempFilePath = FileHelper.createTempFile(extension, cell.getBytes());
                OS.openFile(String.valueOf(tempFilePath));
            } else {
                OS.openFile(absoluteLobFolder + cell.getFilename());
            }
        });
    }
}
