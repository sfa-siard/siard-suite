package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.component.SiardTableView;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTypes extends DatabaseObject {

    private List<DatabaseType> types;

    public DatabaseTypes(List<DatabaseType> types) {
        this.types = types;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView == null || type == null) return;
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

    @Override
    protected void populate(VBox vBox, TreeContentView type) {
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
