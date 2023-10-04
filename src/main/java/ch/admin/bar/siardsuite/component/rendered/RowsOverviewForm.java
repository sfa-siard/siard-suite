package ch.admin.bar.siardsuite.component.rendered;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.database.DatabaseCell;
import ch.admin.bar.siardsuite.model.database.DatabaseRow;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.util.I18nKey;
import lombok.val;

import java.util.Objects;
import java.util.stream.Collectors;

public class RowsOverviewForm {
    public static RenderableForm<DatabaseTable> create(final DatabaseTable table) {
        val tableProperties = table.getColumns().stream()
                .map(column -> new ReadOnlyStringProperty<DatabaseRow>(
                        I18nKey.of(column.getName()),
                        row -> {
                            val matchingCell = row.getCells().stream()
                                    .filter(cell -> Objects.equals(cell.name(), row.name()))
                                    .findAny();

                            return matchingCell.map(DatabaseCell::getValue)
                                    .orElse("");
                        }))
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
                        .property(RenderableTable.<DatabaseTable, DatabaseRow>builder()
                                .dataExtractor(databaseTable -> databaseTable.getRows())
                                .properties(tableProperties)
                                .build())
                        .build())
                .build();
    }
}
