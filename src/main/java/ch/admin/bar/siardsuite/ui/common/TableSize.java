package ch.admin.bar.siardsuite.ui.common;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class TableSize {

    TableView<?> table;

    public TableSize(TableView<?> table) {
        this.table = table;
    }

    public void resize() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().forEach((column) -> column.setPrefWidth(calculateMaxWidth(column) + 20.0d)
        );
    }

    private double calculateMaxWidth(TableColumn<?, ?> column) {
        double max = width(column.getText());
        for (int i = 0; i < table.getItems().size(); i++) {
            if (column.getCellData(i) != null) {
                double calcwidth = width(column.getCellData(i)
                                               .toString());
                if (calcwidth > max) {
                    max = calcwidth;
                }
            }
        }
        return max;
    }

    private static double width(String content) {
        Text t = new Text(content);
        return t.getLayoutBounds().getWidth();
    }
}
