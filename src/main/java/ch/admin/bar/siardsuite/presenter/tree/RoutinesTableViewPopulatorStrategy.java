package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.component.SiardTableView;
import ch.admin.bar.siardsuite.model.database.Routine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutinesTableViewPopulatorStrategy implements TableViewPopulatorStrategy<Routine> {

    private final List<Routine> routines;

    public RoutinesTableViewPopulatorStrategy(List<Routine> routines) {
        this.routines = routines;
    }

    @Override
    public void populate(TableView<Map> tableView, boolean onlyMetaData) {
        new SiardTableView(tableView).withColumn(TABLE_CONTAINER_TABLE_HEADER_ROW, INDEX)
                                     .withColumn(TABLE_CONTAINER_ROUTINE_HEADER_NAME, NAME)
                                     .withColumn(TABLE_CONTAINER_ROUTINE_HEADER_SPECIFIC_NAME, SPECIFIC_NAME)
                                     .withColumn(TABLE_CONTAINER_ROUTINE_HEADER_CHARACTERISTICS, CHARACTERISTICS)
                                     .withColumn(TABLE_CONTAINER_ROUTINE_HEADER_RETURN_TYPE, RETURN_TYPE)
                                     .withColumn(TABLE_CONTAINER_ROUTINE_HEADER_NUMBER_OF_PARAMETERS,
                                                 NUMBER_OF_PARAMETERS)
                                     .withItems(items(routines));
    }

    private ObservableList<Map> items(List<Routine> routines) {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (Routine routine : routines) {
            Map<String, String> item = new HashMap<>();
            item.put(INDEX, String.valueOf(routines.indexOf(routine) + 1));
            item.put(NAME, routine.name());
            item.put(SPECIFIC_NAME, routine.specificName());
            item.put(CHARACTERISTICS, routine.characteristics());
            item.put(RETURN_TYPE, routine.returnType());
            item.put(NUMBER_OF_PARAMETERS, routine.numberOfParameters());
            items.add(item);
        }
        return items;
    }

    private static final String TABLE_CONTAINER_TABLE_HEADER_ROW = "tableContainer.table.header.row";
    private static final String TABLE_CONTAINER_ROUTINE_HEADER_NAME = "tableContainer.routine.header.name";
    private static final String TABLE_CONTAINER_ROUTINE_HEADER_SPECIFIC_NAME = "tableContainer.routine.header.specificName";
    private static final String TABLE_CONTAINER_ROUTINE_HEADER_CHARACTERISTICS = "tableContainer.routine.header.characteristics";
    private static final String TABLE_CONTAINER_ROUTINE_HEADER_RETURN_TYPE = "tableContainer.routine.header.returnType";
    private static final String TABLE_CONTAINER_ROUTINE_HEADER_NUMBER_OF_PARAMETERS = "tableContainer.routine.header.numberOfParameters";
    private static final String INDEX = "index";
    private static final String NAME = "name";
    private static final String SPECIFIC_NAME = "specificName";
    private static final String CHARACTERISTICS = "characteristics";
    private static final String RETURN_TYPE = "returnType";
    private static final String NUMBER_OF_PARAMETERS = "numberOfParameters";
}
