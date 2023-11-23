package ch.admin.bar.siardsuite.component.rendering.table;

import ch.admin.bar.siardsuite.component.rendering.model.TableColumnProperty;
import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import lombok.Value;
import lombok.val;

public class TableColumnFactory {

    private static final I18nKey READ_VALUE_ERROR = I18nKey.of("table.readValue.error");

    public static <I> TableColumn<I, ?> column(final TableColumnProperty<I> columnProperty) {
        val column = new TableColumn<I, StringWrapper>(columnProperty.getTitle().getText());

        column.setSortable(false); // Not sortable because of lazy loading
        column.setCellFactory(param -> new TableCell<I, StringWrapper>() {
            @Override
            protected void updateItem(StringWrapper item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                    setText(null);
                } else {
                    setText(item.getValue());
                    switch (item.getStyling()) {
                        case ERROR:
                            setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
                            break;
                        case NORMAL:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        column.setCellValueFactory(cellData -> {
            try {
                val value = columnProperty.getValueExtractor().extract(cellData.getValue());
                return new SimpleObjectProperty<>(new StringWrapper(value, CellStyling.NORMAL));
            } catch (Exception ex) {
                val translatableErrorMessage = TranslatableText.of(READ_VALUE_ERROR);
                val property = new SimpleObjectProperty<>(new StringWrapper(translatableErrorMessage.getText(), CellStyling.ERROR));
                translatableErrorMessage.bindable()
                        .addListener((observable, oldValue, newValue) -> property.setValue(new StringWrapper(newValue, CellStyling.ERROR)));

                return property;
            }
        });

        return column;
    }

    @Value
    private static class StringWrapper {
        String value;
        CellStyling styling;

        @Override
        public String toString() {
            return value;
        }
    }

    private enum CellStyling {
        NORMAL,
        ERROR
    }
}
