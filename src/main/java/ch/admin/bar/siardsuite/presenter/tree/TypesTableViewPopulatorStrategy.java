package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.component.SiardTableView;
import ch.admin.bar.siardsuite.model.database.DatabaseType;
import ch.admin.bar.siardsuite.model.database.TypeVisitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypesTableViewPopulatorStrategy implements TableViewPopulatorStrategy {

    private final List<DatabaseType> types;

    public TypesTableViewPopulatorStrategy(List<DatabaseType> types) {
        this.types = types;
    }

    @Override
    public void populate(TableView tableView, boolean onlyMetaData) {
        new SiardTableView(tableView)
                .withColumn(TABLE_CONTAINER_TYPES_HEADER_NAME, NAME)
                .withColumn(TABLE_CONTAINER_TYPES_HEADER_CATEGORY, CATEGORY)
                .withColumn(TABLE_CONTAINER_TYPES_HEADER_INSTANTIABLE, INSTANTIABLE)
                .withColumn(TABLE_CONTAINER_TYPES_HEADER_FINAL, FINAL)
                .withColumn(TABLE_CONTAINER_TYPES_HEADER_BASE, BASE)
                .withColumn(TABLE_CONTAINER_TYPES_HEADER_DESCRIPTION, DESCRIPTION)
                .withItems(items());
    }

    private ObservableList<Map> items() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        MapTypesVisitor visitor = new MapTypesVisitor();
        for (DatabaseType type : types) {
            items.add(type.accept(visitor));
        }
        return items;
    }

    private class MapTypesVisitor implements TypeVisitor<Map<String, String>> {

        @Override
        public Map<String, String> visit(String name, String category, boolean instantiable, boolean isFinal,
                                         String base,
                                         String description) {

            Map<String, String> item = new HashMap<>();

            item.put(NAME, name);
            item.put(CATEGORY, category);
            item.put(INSTANTIABLE, String.valueOf(instantiable));
            item.put(FINAL, String.valueOf(isFinal));
            item.put(BASE, base);
            item.put(DESCRIPTION, description);
            return item;
        }
    }

    private static final String TABLE_CONTAINER_TYPES_HEADER_NAME = "tableContainer.types.header.name";
    private static final String TABLE_CONTAINER_TYPES_HEADER_CATEGORY = "tableContainer.types.header.category";
    private static final String TABLE_CONTAINER_TYPES_HEADER_INSTANTIABLE = "tableContainer.types.header.instantiable";
    private static final String TABLE_CONTAINER_TYPES_HEADER_FINAL = "tableContainer.types.header.final";
    private static final String TABLE_CONTAINER_TYPES_HEADER_BASE = "tableContainer.types.header.base";
    private static final String TABLE_CONTAINER_TYPES_HEADER_DESCRIPTION = "tableContainer.types.header.description";
    private static final String NAME = "name";
    private static final String CATEGORY = "category";
    private static final String INSTANTIABLE = "instantiable";
    private static final String FINAL = "final";
    private static final String BASE = "base";
    private static final String DESCRIPTION = "description";
}
