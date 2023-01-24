package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseTable extends DatabaseObject {

    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final Table table;
    protected final boolean onlyMetaData;
    protected final String name;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    protected final String numberOfColumns;
    protected final List<DatabaseRow> rows = new ArrayList<>();
    protected final String numberOfRows;
    protected int loadBatchSize = 50;
    protected int lastRowLoadedIndex = -1;
    protected final TreeContentView treeContentView = TreeContentView.TABLE;

    protected DatabaseTable(SiardArchive archive, DatabaseSchema schema, Table table) {
        this(archive, schema, table, false);
    }

    protected DatabaseTable(SiardArchive archive, DatabaseSchema schema, Table table, boolean onlyMetaData) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        this.onlyMetaData = onlyMetaData;
        name = table.getMetaTable().getName();
        for (int i = 0; i < table.getMetaTable().getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(archive, schema, this, table.getMetaTable().getMetaColumn(i)));
        }
        numberOfColumns = String.valueOf(columns.size());
        numberOfRows = String.valueOf(table.getMetaTable().getRows());
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
                col0.setCellValueFactory(new MapValueFactory<>("index"));
                col1.setCellValueFactory(new MapValueFactory<>("name"));
                col2.setCellValueFactory(new MapValueFactory<>("type"));
                tableView.getColumns().add(col0);
                tableView.getColumns().add(col1);
                tableView.getColumns().add(col2);
                tableView.setItems(colItems());
            } else if (TreeContentView.ROWS.equals(type)) {
                lastRowLoadedIndex = -1;
                final List<TableRow<Map>> rows = new ArrayList<>();
                final Callback<TableView<Map>, TableRow<Map>> rowFactory = o -> {
                    TableRow<Map> row = new TableRow<>();
                    rows.add(row);
                    return row;
                };
                tableView.setRowFactory(rowFactory);
                final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
                col0.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.row"));
                col0.setCellValueFactory(new MapValueFactory<>("index"));
                tableView.getColumns().add(col0);
                TableColumn<Map, StringProperty> col;
                for (DatabaseColumn column : columns) {
                    col = new TableColumn<>();
                    col.setText(column.name);
                    col.setCellValueFactory(new MapValueFactory<>(column.index));
                    tableView.getColumns().add(col);
                }
                try {
                    final RecordDispenser recordDispenser = table.openRecords();
                    loadRecords(recordDispenser);
                    tableView.setItems(rowItems());
                    tableView.setOnScroll(event -> loadItems(recordDispenser, tableView, rows));
                    tableView.addEventHandler(SiardEvent.EXPAND_DATABASE_TABLE, event -> expand(recordDispenser, tableView));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    protected void populate(VBox vbox, TreeContentView type) {}

    private ObservableList<Map> colItems() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (DatabaseColumn column : columns) {
            Map<String, String> item = new HashMap<>();
            item.put("index", column.index);
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
            item.put("index", String.valueOf(Integer.parseInt(row.index) + 1));
            for (DatabaseCell cell : row.cells) {
                item.put(cell.index, cell.value);
            }
            items.add(item);
        }
        return items;
    }

    private void loadItems(RecordDispenser recordDispenser, TableView<Map> tableView, List<TableRow<Map>> rows) {
        boolean reload = false;
        for (TableRow<Map> row : rows) {
            if (lastRowLoadedIndex <= row.getIndex()) {
                reload = true;
            }
        }
        if (reload) {
            loadRecords(recordDispenser);
            tableView.getItems().addAll(rowItems());
        }
    }

    private void loadRecords(RecordDispenser recordDispenser) {
        rows.clear();
        if (!onlyMetaData) {
            try {
                final long numberOfRows = table.getMetaTable().getRows();
                int j = 0;
                while (j < numberOfRows && j < loadBatchSize) {
                    if (recordDispenser.getPosition() < numberOfRows) {
                        rows.add(new DatabaseRow(archive, schema, this, recordDispenser.get()));
                    }
                    j++;
                    lastRowLoadedIndex++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void expand(RecordDispenser recordDispenser, TableView<Map> tableView) {
        final int loadBatchSize = this.loadBatchSize;
        final long numberOfRows = table.getMetaTable().getRows();
        this.loadBatchSize = (int) numberOfRows / 50;
        while (recordDispenser.getPosition() < numberOfRows) {
            loadRecords(recordDispenser);
            tableView.getItems().addAll(rowItems());
            System.out.println(numberOfRows - recordDispenser.getPosition() + " rows remaining");
        }
        this.loadBatchSize = loadBatchSize;
    }

    protected void export(File directory) throws IOException {
        File destination = new File(directory.getAbsolutePath(), this.name + ".html");
        File lobFolder = new File(directory, "lobs/"); //TODO: was taken from the user properties in the original GUI
        OutputStream outPutStream = new FileOutputStream(destination);
        this.table.getParentSchema().getParentArchive().getSchema(this.schema.name).getTable(this.name).exportAsHtml(outPutStream, lobFolder);
        outPutStream.close();
    }

    private TreeSet<MetaSearchHit> metaSearch(String s) {
        TreeSet<MetaSearchHit> hits = new TreeSet<>();
        final List<String> nodeIds = new ArrayList<>();
        if (contains(name, s)) {
            nodeIds.add("name");
        }
        if (nodeIds.size() > 0) {
            List<MetaSearchHit> metaSearchHits = new ArrayList<>();
            metaSearchHits.add(new MetaSearchHit("Schema " + schema.name + ", Table " + name,
                                                 this,
                                                 treeContentView,
                                                 nodeIds));
            hits = new TreeSet<>(
                    metaSearchHits);
        }
        return hits;
    }

    protected TreeSet<MetaSearchHit> aggregatedMetaSearch(String s) {
        final TreeSet<MetaSearchHit> hits = metaSearch(s);
        hits.addAll(columns.stream().flatMap(col -> col.aggregatedMetaSearch(s).stream()).collect(Collectors.toList()));
        return hits;
    }

}
