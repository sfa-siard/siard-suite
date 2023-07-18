package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.RecordDispenser;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.component.SiardTableView;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.facades.PreTypeFacade;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseTable extends DatabaseObject implements WithColumns {

    public static final String TABLE_CONTAINER_TABLE_HEADER_ROW = "tableContainer.table.header.row";
    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final Table table;
    protected final boolean onlyMetaData;
    public final String name;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    public final String numberOfColumns;
    protected final List<DatabaseRow> rows = new ArrayList<>();
    public final String numberOfRows;
    protected int loadBatchSize = 50;
    protected int lastRowLoadedIndex = -1;
    protected final TreeContentView treeContentView = TreeContentView.TABLE;

    public DatabaseTable(SiardArchive archive, DatabaseSchema schema, Table table) {
        this(archive, schema, table, false);
    }

    public DatabaseTable(SiardArchive archive, DatabaseSchema schema, Table table, boolean onlyMetaData) {
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
    public void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView == null) return;

        if (TreeContentView.TABLE.equals(type) || TreeContentView.COLUMNS.equals(type)) {
            new SiardTableView(tableView).withColumn(TABLE_CONTAINER_TABLE_HEADER_POSITION, INDEX)
                                         .withColumn(TABLE_CONTAINER_TABLE_HEADER_COLUMN_NAME, NAME)
                                         .withColumn(TABLE_CONTAINER_TABLE_HEADER_COLUMN_TYPE, TYPE)
                                         .withItems(colItems());
        }
        if (TreeContentView.ROWS.equals(type)) {
            lastRowLoadedIndex = -1;
            final List<TableRow<Map>> rows = new ArrayList<>();
            final Callback<TableView<Map>, TableRow<Map>> rowFactory = o -> {
                TableRow<Map> row = new TableRow<>();
                rows.add(row);
                return row;
            };
            tableView.setRowFactory(rowFactory);

            SiardTableView siardTableView = new SiardTableView(tableView).withColumn(TABLE_CONTAINER_TABLE_HEADER_ROW,
                                                                                     INDEX);
            for (DatabaseColumn column : columns) {
                siardTableView.withColumn(column.name, column.index);
            }
            try {
                final RecordDispenser recordDispenser = table.openRecords();
                loadRecords(recordDispenser);
                tableView.setItems(rowItems());
                EventHandler<MouseEvent> cellClickedHandler = event -> {
                    TablePosition tablePosition = tableView.getSelectionModel().getSelectedCells().get(0);
                    if (tablePosition.getColumn() == 0) return;
                    int column = tablePosition.getColumn() - 1; // since we added an index column...
                    int row = tablePosition.getRow();

                    DatabaseRow databaseRow = this.rows.get(row);
                    DatabaseCell databaseCell = databaseRow.cells.get(column);
                    try {
                        boolean isBlob = new PreTypeFacade(databaseCell.cell.getMetaColumn().getPreType()).isBlob();
                        if (isBlob) {
                            System.out.println("this is a blob... try to open it in native application");
                            System.out.println("the mime type is..." + databaseCell.cell.getMetaColumn().getMimeType());
                            URI absoluteLobFolder = databaseCell.cell.getMetaColumn()
                                                                     .getAbsoluteLobFolder();
                            System.out.println("its saved (externally)" + absoluteLobFolder);


                            HostServicesDelegate hostServices = HostServicesFactory.getInstance(new SiardApplication()); // TODO: this seems to be really silly
                            hostServices.showDocument(absoluteLobFolder.toString() + "/" + databaseCell.cell.getFilename());

                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(databaseCell.value);


                };
                tableView.setOnMouseClicked(cellClickedHandler);
                tableView.setOnScroll(event -> loadItems(recordDispenser, tableView, rows));
                tableView.addEventHandler(SiardEvent.EXPAND_DATABASE_TABLE,
                                          event -> expand(recordDispenser, tableView));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void populate(VBox vbox, TreeContentView type) {
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

    private ObservableList<Map> rowItems() {
        return FXCollections.observableArrayList(rows.stream().map(row -> {
            Map<String, String> item = new HashMap<>();
            item.put(INDEX, String.valueOf(Integer.parseInt(row.index) + 1));
            row.cells.forEach(cell -> item.put(cell.index, cell.value));
            return item;
        }).collect(Collectors.toList()));
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
        if (onlyMetaData) return;
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
        this.table.getParentSchema()
                  .getParentArchive()
                  .getSchema(this.schema.name)
                  .getTable(this.name)
                  .exportAsHtml(outPutStream, lobFolder);
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

    @Override
    public String name() {
        return name;
    }

    public List<DatabaseColumn> columns() {
        return this.columns;
    }

    public String numberOfRows() {
        return this.numberOfRows;
    }

    private static final String TABLE_CONTAINER_TABLE_HEADER_POSITION = "tableContainer.table.header.position";
    private static final String TABLE_CONTAINER_TABLE_HEADER_COLUMN_NAME = "tableContainer.table.header.columnName";
    private static final String TABLE_CONTAINER_TABLE_HEADER_COLUMN_TYPE = "tableContainer.table.header.columnType";
    private static final String INDEX = "index";
    private static final String NAME = "name";
    private static final String TYPE = "type";
}
