package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaView;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

public class DatabaseView extends DatabaseObject implements WithColumns {
    public static final String TABLE_CONTAINER_TABLE_HEADER_POSITION = "tableContainer.table.header.position";
    public static final String TABLE_CONTAINER_TABLE_HEADER_COLUMN_NAME = "tableContainer.table.header.columnName";
    public static final String TABLE_CONTAINER_TABLE_HEADER_COLUMN_TYPE = "tableContainer.table.header.columnType";
    public static final String POSITION = "index";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    protected final MetaView view;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    private DatabaseSchema schema;

    protected DatabaseView(SiardArchive archive, DatabaseSchema schema, MetaView view) {
        this.view = view;
        this.schema = schema;

        for (int i = 0; i < view.getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(archive, schema, this, view.getMetaColumn(i)));
        }
    }

    @Override
    public String name() {
        return view.getName();
    }

    public String getNumberOfColumns() {
        return String.valueOf(columns.size());
    }

    public String getNumberOfRows() {
        return String.valueOf(view.getRows());
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name(), getNumberOfRows(), columns, new ArrayList<>());
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView == null) return;
        if (!TreeContentView.COLUMNS.equals(type) && !TreeContentView.VIEW.equals(type)) return;
        tableView.getColumns().add(createTableColumn(TABLE_CONTAINER_TABLE_HEADER_POSITION, POSITION));
        tableView.getColumns().add(createTableColumn(TABLE_CONTAINER_TABLE_HEADER_COLUMN_NAME, NAME));
        tableView.getColumns().add(createTableColumn(TABLE_CONTAINER_TABLE_HEADER_COLUMN_TYPE, TYPE));
        tableView.setItems(colItems());
    }

    @Override
    protected void populate(VBox vbox, TreeContentView type) {
    }

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


    private TreeSet<MetaSearchHit> metaSearch(String s) {
        TreeSet<MetaSearchHit> hits = new TreeSet<>();
        final List<String> nodeIds = new ArrayList<>();
        if (contains(this.name(), s)) {
            nodeIds.add("name");
        }
        if (nodeIds.size() > 0) {
            List<MetaSearchHit> metaSearchHits = new ArrayList<>();
            metaSearchHits.add(new MetaSearchHit("Schema " + schema.name + ", View " + view.getName(),
                                                 this,
                                                 TreeContentView.VIEW,
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

    public List<DatabaseColumn> columns() {
        return this.columns;
    }
}
