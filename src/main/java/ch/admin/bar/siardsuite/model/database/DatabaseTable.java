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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTable extends DatabaseObject {

    protected final SiardArchive siardArchive;
    protected final Archive archive;
    protected final DatabaseSchema schema;
    protected final String name;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    protected final String numberOfColumns;
    protected final List<DatabaseRow> rows = new ArrayList<>();
    protected final String numberOfRows;
    protected DatabaseTable(SiardArchive siardArchive, Archive archive, DatabaseSchema schema, Table table) {
        this(siardArchive, archive, schema, table, false);
    }

    protected DatabaseTable(SiardArchive siardArchive, Archive archive, DatabaseSchema schema, Table table, boolean onlyMetaData) {
        this.siardArchive = siardArchive;
        this.schema = schema;
        this.archive = archive;
        name = table.getMetaTable().getName();
        for (int i = 0; i < table.getMetaTable().getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(siardArchive, archive, schema, this, table.getMetaTable().getMetaColumn(i)));
        }
        numberOfColumns = String.valueOf(columns.size());
        String n = "";
        if (!onlyMetaData) {
            try {
                int i = 0;
                final RecordDispenser recordDispenser = table.openRecords();
                while (i < table.getMetaTable().getRows()) {
                    rows.add(new DatabaseRow(siardArchive, archive, schema, this, recordDispenser.get()));
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
        if (tableView != null) {
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
            } else if (TreeContentView.ROWS.equals(type)) {
                final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
                col0.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.row"));
                col0.setMinWidth(125);
                col0.setStyle("-fx-alignment: CENTER_RIGHT");
                col0.setCellValueFactory(new MapValueFactory<>("index"));
                tableView.getColumns().add(col0);
                TableColumn<Map, StringProperty> col;
                for (DatabaseColumn column : columns) {
                    col = new TableColumn<>();
                    col.setText(column.name);
                    col.setMinWidth(125);
                    col.setStyle("-fx-alignment: CENTER_LEFT");
                    col.setCellValueFactory(new MapValueFactory<>(column.index));
                    tableView.getColumns().add(col);
                }
                tableView.setItems(rowItems());
            }
            tableView.setMinWidth(590);
            tableView.setMaxWidth(590);
        }
    }

    private ObservableList<Map> colItems() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (DatabaseColumn column : columns) {
            Map<String, String> item = new HashMap<>();
            item.put("index", String.valueOf(columns.indexOf(column) + 1));
            item.put("name", column.name);
            item.put("type", column.type);
            items.add(item);
        }
        return items;
    }

    private ObservableList<Map> rowItems() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (DatabaseRow row : rows) {
            Map<String, String> item = new HashMap<>();
            item.put("index", String.valueOf(rows.indexOf(row) + 1));
            for (DatabaseCell cell : row.cells) {
                item.put(cell.index, cell.value);
            }
            items.add(item);
        }
        return items;
    }

    protected void export(File directory) throws IOException {
        File destination = new File(directory.getAbsolutePath(), this.name + ".html");
        File lobFolder = new File(directory, "lobs/"); //TODO: was taken from the user properties in the original GUI
        OutputStream outPutStream = new FileOutputStream(destination);
        this.archive.getSchema(this.schema.name).getTable(this.name).exportAsHtml(outPutStream, lobFolder);
        outPutStream.close();
    }
}
