package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaView;
import ch.admin.bar.siardsuite.component.SiardTableView;
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

    protected final MetaView metaView;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    private DatabaseSchema schema;

    public DatabaseView(SiardArchive archive, DatabaseSchema schema, MetaView metaView) {
        this.metaView = metaView;
        this.schema = schema;

        for (int i = 0; i < metaView.getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(archive, schema, this, metaView.getMetaColumn(i)));
        }
    }

    @Override
    public String name() {
        return metaView.getName();
    }

    public String getNumberOfColumns() {
        return String.valueOf(columns.size());
    }

    public String getNumberOfRows() {
        return String.valueOf(metaView.getRows());
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name(), getNumberOfRows(), columns, new ArrayList<>());
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView == null) return;
        if (!TreeContentView.COLUMNS.equals(type) && !TreeContentView.VIEW.equals(type)) return;
        new SiardTableView(tableView).withColumn(TABLE_CONTAINER_TABLE_HEADER_POSITION, POSITION)
                .withColumn(TABLE_CONTAINER_TABLE_HEADER_COLUMN_NAME, NAME)
                .withColumn(TABLE_CONTAINER_TABLE_HEADER_COLUMN_TYPE, TYPE)
                .withColumn(TABLE_CONTAINER_TABLE_HEADER_CARDINALITY, CARDINALITY)
                .withItems(colItems());
    }

    @Override
    protected void populate(VBox vbox, TreeContentView type) {
    }

    private ObservableList<Map> colItems() {
        return FXCollections.observableArrayList(columns.stream()
                .map(DatabaseView::createItem)
                .collect(Collectors.toList()));
    }

    private static Map<String, String> createItem(DatabaseColumn column) {
        Map<String, String> item = new HashMap<>();
        item.put(POSITION, column.index());
        item.put(NAME, column.name());
        item.put(TYPE, column.type());
        item.put(CARDINALITY, column.cardinality());
        return item;
    }


    private TreeSet<MetaSearchHit> metaSearch(String s) {
        TreeSet<MetaSearchHit> hits = new TreeSet<>();
        final List<String> nodeIds = new ArrayList<>();
        if (contains(this.name(), s)) {
            nodeIds.add("name");
        }
        if (nodeIds.size() > 0) {
            List<MetaSearchHit> metaSearchHits = new ArrayList<>();
            metaSearchHits.add(new MetaSearchHit("Schema " + schema.name + ", View " + metaView.getName(),
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

    private static final String TABLE_CONTAINER_TABLE_HEADER_POSITION = "tableContainer.table.header.position";
    private static final String TABLE_CONTAINER_TABLE_HEADER_COLUMN_NAME = "tableContainer.table.header.columnName";
    private static final String TABLE_CONTAINER_TABLE_HEADER_COLUMN_TYPE = "tableContainer.table.header.columnType";
    private static final String TABLE_CONTAINER_TABLE_HEADER_CARDINALITY = "tableContainer.view.header.cardinality";
    private static final String POSITION = "index";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String CARDINALITY = "cardinality";
}
