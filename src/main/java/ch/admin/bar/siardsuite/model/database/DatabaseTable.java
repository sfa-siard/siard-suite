package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.beans.property.StringProperty;
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
        if (TreeContentView.TABLE.equals(type) || TreeContentView.COLUMNS.equals(type)) {
            final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col1 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col2 = new TableColumn<>();
            col0.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.position"));
            col1.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.columnName"));
            col2.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.columnType"));
            col0.setMinWidth(125);
            col1.setMinWidth(250);
            col2.setMinWidth(250);
            col0.setStyle("-fx-alignment: CENTER_RIGHT");
            col1.setStyle("-fx-alignment: CENTER_LEFT");
            col2.setStyle("-fx-alignment: CENTER_LEFT");
            col0.setCellValueFactory(new MapValueFactory<>("index"));
            col1.setCellValueFactory(new MapValueFactory<>("name"));
            col2.setCellValueFactory(new MapValueFactory<>("type"));
            tableView.getColumns().add(col0);
            tableView.getColumns().add(col1);
            tableView.getColumns().add(col2);
            tableView.setItems(colItems());
            tableView.setMaxWidth(590);
        } else if (TreeContentView.ROWS.equals(type)) {

        }
    }

    private ObservableList<Map> colItems() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (DatabaseColumn c : columns) {
            Map<String, String> item = new HashMap<>();
            item.put("index", String.valueOf(columns.indexOf(c) + 1));
            item.put("name", c.name);
            item.put("type", c.type);
            items.add(item);
        }
        return items;
    }

    private ObservableList<Map> rowItems() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (DatabaseRow r : rows) {
            Map<String, String> item = new HashMap<>();
            item.put("name", r.name);
            items.add(item);
        }
        return items;
    }

}
