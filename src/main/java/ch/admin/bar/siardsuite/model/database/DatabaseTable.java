package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTable extends DatabaseObject {

    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final String name;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    protected final String numberOfColumns;
    protected final List<DatabaseRow> rows = new ArrayList<>();
    protected final String numberOfRows;
    protected DatabaseTable(SiardArchive archive, DatabaseSchema schema, Table table) {
        this(archive, schema, table, false);
    }

    protected DatabaseTable(SiardArchive archive, DatabaseSchema schema, Table table, boolean onlyMetaData) {
        this.archive = archive;
        this.schema = schema;
        name = table.getMetaTable().getName();
        for (int i = 0; i < table.getMetaTable().getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(archive, schema, this, table.getMetaTable().getMetaColumn(i)));
        }
        numberOfColumns = String.valueOf(columns.size());
        String n = "";
        if (!onlyMetaData) {
            try {
                int i = 0;
                final RecordDispenser recordDispenser = table.openRecords();
                while (i < table.getMetaTable().getRows()) {
                    rows.add(new DatabaseRow(archive, schema, this, recordDispenser.get()));
                    i++;
                }
                n = String.valueOf(rows.size());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        numberOfRows = n;
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name, numberOfRows, columns, rows);
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        final TableColumn<Map, String> col0 = new TableColumn<>("Name");
        col0.setCellValueFactory(new MapValueFactory<>("name"));
        tableView.setItems(rows(type));
        tableView.getColumns().add(col0);
    }

    private ObservableList<Map> rows(TreeContentView type) {
        final ObservableList<Map> rows = FXCollections.observableArrayList();
        if (TreeContentView.TABLE.equals(type) || TreeContentView.COLUMNS.equals(type)) {
            for (DatabaseColumn c : columns) {
                Map<String, String> row = new HashMap<>();
                row.put("name", c.name);
                rows.add(row);
            }
        } else if (TreeContentView.ROWS.equals(type)) {
            for (DatabaseRow r : this.rows) {
                Map<String, String> row = new HashMap<>();
                row.put("name", r.name);
                rows.add(row);
            }
        }
        return rows;
    }

}
