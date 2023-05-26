package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaRoutine;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.facades.MetaParameterFacade;
import ch.admin.bar.siardsuite.model.facades.MetaRoutineFacade;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Routine extends DatabaseObject implements WithColumns {
    protected final String name;
    private final SiardArchive archive;
    private final DatabaseSchema schema;
    private final boolean onlyMetaData;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    protected final String numberOfColumns;
    protected final List<DatabaseRow> rows = new ArrayList<>();
    protected final String numberOfRows;
    private final MetaRoutine metaRoutine;
    private final String characterisics;
    private final String specificName;
    private final String returnType;
    private final String numberOfParameters;
    protected int lastRowLoadedIndex = -1;

    protected Routine(SiardArchive archive, DatabaseSchema schema, MetaRoutine metaRoutine) {
        this(archive, schema, metaRoutine, false);
    }

    protected Routine(SiardArchive archive, DatabaseSchema schema, MetaRoutine metaRoutine,
                      boolean onlyMetaData) {
        this.archive = archive;
        this.schema = schema;
        this.metaRoutine = metaRoutine;
        this.onlyMetaData = onlyMetaData;

        name = metaRoutine.getName();
        characterisics = metaRoutine.getCharacteristic();
        specificName = metaRoutine.getSpecificName();
        returnType = metaRoutine.getReturnType();
        numberOfParameters = String.valueOf(metaRoutine.getMetaParameters());
        for (int i = 0; i < metaRoutine.getMetaParameters(); i++) {
            //  columns.add(new DatabaseRoutine(archive, schema, this, metaRoutine.getMetaParameter(i)));
        }

        // TODO: dont need these:
        numberOfColumns = String.valueOf(columns.size());
        numberOfRows = String.valueOf(metaRoutine.getMetaParameters());
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name, numberOfRows, columns, rows);
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        // Seems to be the same as in RoutinesTableViewPopulator? or is it just similar?
        if (tableView == null) return;
        if (TreeContentView.COLUMNS.equals(type) || TreeContentView.ROUTINE.equals(type)) {
            final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col1 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col2 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col3 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col4 = new TableColumn<>();
            col0.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.position"));
            col1.textProperty().bind(I18n.createStringBinding("tableContainer.parameter.header.parameterName"));
            col2.textProperty().bind(I18n.createStringBinding("tableContainer.parameter.header.parameterMode"));
            col3.textProperty().bind(I18n.createStringBinding("tableContainer.parameter.header.parameterType"));
            col4.textProperty().bind(I18n.createStringBinding("tableContainer.parameter.header.cardinality"));
            col0.setCellValueFactory(new MapValueFactory<>("position"));
            col1.setCellValueFactory(new MapValueFactory<>("parameterName"));
            col2.setCellValueFactory(new MapValueFactory<>("parameterMode"));
            col3.setCellValueFactory(new MapValueFactory<>("parameterType"));
            col4.setCellValueFactory(new MapValueFactory<>("cardinality"));

            tableView.getColumns().add(col0);
            tableView.getColumns().add(col1);
            tableView.getColumns().add(col2);
            tableView.getColumns().add(col3);
            tableView.getColumns().add(col4);
            tableView.setItems(colItems());

        } /*else if (TreeContentView.ROWS.equals(type)) {
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
        }*/
    }

    private ObservableList<Map> colItems() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        new MetaRoutineFacade(metaRoutine).parameters()
                                          .forEach(metaParameter -> {
                                                       Map<String, String> item = new HashMap<>();
                                                       item.put("position", String.valueOf(metaParameter.getPosition()));
                                                       item.put("parameterName", metaParameter.getName());
                                                       item.put("parameterMode", metaParameter.getMode());
                                                       item.put("parameterType", metaParameter.getType());
                                                       item.put("cardinality",
                                                                new MetaParameterFacade(metaParameter).formattedCardinality());
                                                       items.add(item);
                                                   }

                                          );
        return items;

    }


    @Override
    protected void populate(VBox vBox, TreeContentView type) {
    }

    @Override
    public String name() {
        return name;
    }

    public String characteristics() {
        return characterisics;
    }

    public String specificName() {
        return specificName;
    }

    public String returnType() {
        return returnType;
    }

    public String numberOfParameters() {
        return numberOfParameters;
    }
}
