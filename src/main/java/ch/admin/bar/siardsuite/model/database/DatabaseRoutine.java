package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaRoutine;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseRoutine extends DatabaseObject implements WithColumns {
    protected final String name;
    private final SiardArchive archive;
    private final DatabaseSchema schema;
    private final boolean onlyMetaData;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    protected final String numberOfColumns;
    protected final List<DatabaseRow> rows = new ArrayList<>();
    protected final String numberOfRows;
    private final MetaRoutine metaRoutine;
    protected int lastRowLoadedIndex = -1;

    protected DatabaseRoutine(SiardArchive archive, DatabaseSchema schema, MetaRoutine metaRoutine) {
        this(archive, schema, metaRoutine, false);
    }

    protected DatabaseRoutine(SiardArchive archive, DatabaseSchema schema, MetaRoutine metaRoutine,
                              boolean onlyMetaData) {
        this.archive = archive;
        this.schema = schema;
        this.metaRoutine = metaRoutine;
        this.onlyMetaData = onlyMetaData;

        name = metaRoutine.getName();
        for (int i = 0; i < metaRoutine.getMetaParameters(); i++) {
            //  columns.add(new DatabaseRoutine(archive, schema, this, metaRoutine.getMetaParameter(i)));
        }
        numberOfColumns = String.valueOf(columns.size());
        numberOfRows = String.valueOf(metaRoutine.getMetaParameters());
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name, numberOfRows, columns, rows);
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView == null) return;
        if (TreeContentView.TABLE.equals(type) || TreeContentView.COLUMNS.equals(type)) {
            final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col1 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col2 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col3 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col4 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col5 = new TableColumn<>();
            col0.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.position"));
            col1.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.routineName"));
            col2.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.specificRoutineName"));
            col2.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.characteristic"));
            col2.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.returnType"));
            col2.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.parameters"));
            col0.setCellValueFactory(new MapValueFactory<>("index"));
            col1.setCellValueFactory(new MapValueFactory<>("routineName"));
            col2.setCellValueFactory(new MapValueFactory<>("specificRoutineName"));
            col2.setCellValueFactory(new MapValueFactory<>("characteristic"));
            col2.setCellValueFactory(new MapValueFactory<>("returnType"));
            col2.setCellValueFactory(new MapValueFactory<>("parameters"));

            tableView.getColumns().add(col0);
            tableView.getColumns().add(col1);
            tableView.getColumns().add(col2);
            tableView.getColumns().add(col3);
            tableView.getColumns().add(col4);
            tableView.getColumns().add(col5);
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
            // TODO: specific for routines....
            for (DatabaseColumn column : columns) {
                col = new TableColumn<>();
                col.setText(column.name);
                col.setCellValueFactory(new MapValueFactory<>(column.index));
                tableView.getColumns().add(col);
            }
        }
    }

    private ObservableList<Map> colItems() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (DatabaseColumn column : columns) {
            Map<String, String> item = new HashMap<>();
            item.put("index", column.index);
            item.put("routineName", column.name);
            item.put("specificRoutineName", column.type);
         /*   item.put("characteristic"));
            item.put("returnType"));
            item.put("parameters"));*/
            items.add(item);
        }
        return items;
    }

    @Override
    protected void populate(VBox vBox, TreeContentView type) {

    }

    @Override
    public String name() {
        return name;
    }
}
