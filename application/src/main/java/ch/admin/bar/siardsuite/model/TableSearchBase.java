package ch.admin.bar.siardsuite.model;

import javafx.scene.control.TableView;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

public final class TableSearchBase {
    private final TableView<Map> tableView;
    private final LinkedHashSet<Map> rows;

    public TableSearchBase(TableView<Map> tableView, LinkedHashSet<Map> rows) {
        this.tableView = tableView;
        this.rows = rows;
    }

    public TableView<Map> tableView() {
        return tableView;
    }

    public LinkedHashSet<Map> rows() {
        return rows;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        TableSearchBase that = (TableSearchBase) obj;
        return Objects.equals(this.tableView, that.tableView) &&
                Objects.equals(this.rows, that.rows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableView, rows);
    }

    @Override
    public String toString() {
        return "TableSearchBase[" +
                "tableView=" + tableView + ", " +
                "rows=" + rows + ']';
    }
}
