package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siard2.api.MetaRoutine;
import ch.admin.bar.siardsuite.component.SiardTableView;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.facades.MetaParameterFacade;
import ch.admin.bar.siardsuite.model.facades.MetaRoutineFacade;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Routine extends DatabaseObject implements WithColumns {

    private final MetaRoutine metaRoutine;

    public Routine(MetaRoutine metaRoutine) {
        this.metaRoutine = metaRoutine;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(metaRoutine.getName(),
                      String.valueOf(metaRoutine.getMetaParameters()),
                      new ArrayList<>(),
                      new ArrayList<>());
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView == null) return;
        if (!TreeContentView.COLUMNS.equals(type) && !TreeContentView.ROUTINE.equals(type)) return;
        new SiardTableView(tableView)
                .withColumn(TABLE_CONTAINER_TABLE_HEADER_POSITION, POSITION)
                .withColumn(TABLE_CONTAINER_PARAMETER_HEADER_PARAMETER_NAME, PARAMETER_NAME)
                .withColumn(TABLE_CONTAINER_PARAMETER_HEADER_PARAMETER_MODE, PARAMETER_MODE)
                .withColumn(TABLE_CONTAINER_PARAMETER_HEADER_PARAMETER_TYPE, PARAMETER_TYPE)
                .withColumn(TABLE_CONTAINER_PARAMETER_HEADER_CARDINALITY, CARDINALITY).withItems(colItems());
    }

    private ObservableList<Map> colItems() {
        return FXCollections.observableArrayList(new MetaRoutineFacade(metaRoutine).parameters()
                                                                                   .map(metaParameter -> createItem(
                                                                                           metaParameter)

                                                                                   ).collect(Collectors.toList()));
    }

    private static Map<String, String> createItem(MetaParameter metaParameter) {
        Map<String, String> item = new HashMap<>();
        item.put(POSITION, String.valueOf(metaParameter.getPosition()));
        item.put(PARAMETER_NAME, metaParameter.getName());
        item.put(PARAMETER_MODE, metaParameter.getMode());
        item.put(PARAMETER_TYPE, metaParameter.getType());
        item.put(CARDINALITY,
                 new MetaParameterFacade(metaParameter).formattedCardinality());
        return item;
    }


    @Override
    protected void populate(VBox vBox, TreeContentView type) {
    }

    @Override
    public String name() {
        return metaRoutine.getName();
    }

    public String characteristics() {
        return metaRoutine.getCharacteristic();
    }

    public String specificName() {
        return metaRoutine.getSpecificName();
    }

    public String returnType() {
        return metaRoutine.getSpecificName();
    }

    public String numberOfParameters() {
        return String.valueOf(metaRoutine.getMetaParameters());
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
