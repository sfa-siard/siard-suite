package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaParameter;
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
import java.util.stream.Collectors;

public class Routine extends DatabaseObject implements WithColumns {


    protected final String name;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    protected final String numberOfColumns;
    protected final List<DatabaseRow> rows = new ArrayList<>();
    protected final String numberOfRows;
    private final MetaRoutine metaRoutine;
    private final String characterisics;
    private final String specificName;
    private final String returnType;
    private final String numberOfParameters;

    protected Routine(SiardArchive archive, DatabaseSchema schema, MetaRoutine metaRoutine) {
        this(archive, schema, metaRoutine, false);
    }

    protected Routine(SiardArchive archive, DatabaseSchema schema, MetaRoutine metaRoutine,
                      boolean onlyMetaData) {
        this.metaRoutine = metaRoutine;

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
        if (tableView == null) return;
        if (TreeContentView.COLUMNS.equals(type) || TreeContentView.ROUTINE.equals(type)) {
            tableView.getColumns().add(createTableColumn(TABLE_CONTAINER_TABLE_HEADER_POSITION, POSITION));
            tableView.getColumns()
                     .add(createTableColumn(TABLE_CONTAINER_PARAMETER_HEADER_PARAMETER_NAME, PARAMETER_NAME));
            tableView.getColumns()
                     .add(createTableColumn(TABLE_CONTAINER_PARAMETER_HEADER_PARAMETER_MODE, PARAMETER_MODE));
            tableView.getColumns()
                     .add(createTableColumn(TABLE_CONTAINER_PARAMETER_HEADER_PARAMETER_TYPE, PARAMETER_TYPE));
            tableView.getColumns().add(createTableColumn(TABLE_CONTAINER_PARAMETER_HEADER_CARDINALITY, CARDINALITY));
            tableView.setItems(colItems());
        }
    }

    private TableColumn<Map, ?> createTableColumn(String label, String key) {
        final TableColumn<Map, StringProperty> column = new TableColumn<>();
        I18n.bind(column.textProperty(), label);
        column.setCellValueFactory(new MapValueFactory<>(key));
        return column;
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

    public List<MetaParameter> parameters() {
        return new MetaRoutineFacade(metaRoutine).parameters().collect(Collectors.toList());
    }

    private static final String TABLE_CONTAINER_TABLE_HEADER_POSITION = "tableContainer.table.header.position";
    private static final String TABLE_CONTAINER_PARAMETER_HEADER_PARAMETER_NAME = "tableContainer.parameter.header.parameterName";
    private static final String TABLE_CONTAINER_PARAMETER_HEADER_PARAMETER_MODE = "tableContainer.parameter.header.parameterMode";
    private static final String TABLE_CONTAINER_PARAMETER_HEADER_PARAMETER_TYPE = "tableContainer.parameter.header.parameterType";
    private static final String TABLE_CONTAINER_PARAMETER_HEADER_CARDINALITY = "tableContainer.parameter.header.cardinality";
    private static final String POSITION = "position";
    private static final String PARAMETER_NAME = "parameterName";
    private static final String PARAMETER_MODE = "parameterMode";
    private static final String PARAMETER_TYPE = "parameterType";
    private static final String CARDINALITY = "cardinality";
}
