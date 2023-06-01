package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siardsuite.component.SiardLabelContainer;
import ch.admin.bar.siardsuite.component.SiardTableView;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseType extends DatabaseObject {

    private final String name;
    private final String category;
    private final boolean instantiable;
    private final boolean isFinal;
    private final String base;
    private final String description;

    private final List<DatabaseAttribute> databaseAttributes;

    public DatabaseType(String name, String category, boolean instantiable, boolean isFinal, String base,
                        String description, List<DatabaseAttribute> metaAttributes) {
        this.name = name;
        this.category = category;
        this.instantiable = instantiable;
        this.isFinal = isFinal;
        this.base = base;
        this.description = description;
        this.databaseAttributes = metaAttributes;
    }

    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visit(name, category, instantiable, isFinal, base, description);
    }

    public String name() {
        return name;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {

    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView == null || type == null) return;
        new SiardTableView(tableView).withColumn(TYPE_ATTRIBUTE_NAME, NAME)
                                     .withColumn(TYPE_ATTRIBUTE_TYPE, TYPE)
                                     .withColumn(TYPE_ATTRIBUTE_CARDINALITY, CARDINALITY)
                                     .withItems(items());
    }

    private ObservableList<Map> items() {
        return FXCollections.observableArrayList(this.databaseAttributes.stream().map(databaseAttribute -> {
            Map<String, String> item = new HashMap<>();
            item.put(NAME, databaseAttribute.name());
            item.put(TYPE, databaseAttribute.type());
            item.put(CARDINALITY, databaseAttribute.cardinality());
            return item;
        }).collect(Collectors.toList()));
    }

    @Override
    protected void populate(VBox container, TreeContentView type) {
        new SiardLabelContainer(container)
                .withLabel(name, "name")
                .withLabel(category, "typeCategory")
                .withLabel(String.valueOf(instantiable), "isInstantiable")
                .withLabel(String.valueOf(isFinal), "isFinal")
                .withLabel(base, "baseType")
                .withLabel(description, "description");

        for (Node node : container.getChildren()) {
            node.getStyleClass().add("table-container-label-small");
        }
    }

    public int numberOfAttributes() {
        return this.databaseAttributes.size();
    }

    public List<DatabaseAttribute> attributes() {
        return databaseAttributes;
    }

    private static final String NAME = "name";
    private static final String TYPE_ATTRIBUTE_NAME = "type.attribute.name";
    private static final String TYPE_ATTRIBUTE_TYPE = "type.attribute.type";
    private static final String TYPE = "type";
    private static final String TYPE_ATTRIBUTE_CARDINALITY = "type.attribute.cardinality";
    private static final String CARDINALITY = "cardinality";
}
