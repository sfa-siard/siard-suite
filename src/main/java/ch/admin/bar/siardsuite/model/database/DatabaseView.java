package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaView;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.util.I18n;
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

import java.util.*;
import java.util.stream.Collectors;

public class DatabaseView extends DatabaseObject implements WithColumns {
    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final MetaView view;
    protected final boolean onlyMetaData;
    protected final String name;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    protected final String numberOfColumns;
    protected final List<DatabaseRow> rows = new ArrayList<>();
    protected final String numberOfRows;
    protected int loadBatchSize = 50;
    protected int lastRowLoadedIndex = -1;
    protected final TreeContentView treeContentView = TreeContentView.TABLE;

    protected DatabaseView(SiardArchive archive, DatabaseSchema schema, MetaView view) {
        this(archive, schema, view, false);
    }

    protected DatabaseView(SiardArchive archive, DatabaseSchema schema, MetaView view, boolean onlyMetaData) {
        this.archive = archive;
        this.schema = schema;
        this.view = view;
        this.onlyMetaData = onlyMetaData;
        name = view.getName();

        for (int i = 0; i < view.getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(archive, schema, this, view.getMetaColumn(i)));
        }
        numberOfColumns = String.valueOf(columns.size());
        numberOfRows = String.valueOf(view.getRows());
    }

    @Override
    public String name() {
        return name;
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
