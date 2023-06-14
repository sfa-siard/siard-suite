package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siardsuite.component.SiardLabelContainer;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.Map;

public class DatabaseAttribute extends DatabaseObject {

    private final MetaAttribute metaAttribute;

    public DatabaseAttribute(MetaAttribute metaAttribute) {
        this.metaAttribute = metaAttribute;
    }

    @Override
    protected void shareProperties(SiardArchiveVisitor visitor) {

    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {

    }

    @Override
    protected void populate(VBox container, TreeContentView type) {
        new SiardLabelContainer(container).withLabel(metaAttribute.getName(), "name")
                                          .withLabel(String.valueOf(metaAttribute.getPosition()), "position")
                                          .withLabel(metaAttribute.getType(), "SQL_TYPE")
                                          .withLabel(metaAttribute.getTypeSchema(), "UDT_SCHEMA")
                                          .withLabel(metaAttribute.getTypeName(), "TYPE_NAME")
                                          .withLabel(metaAttribute.getTypeOriginal(), "TYPE_ORIGINAL")
                                          .withLabel(String.valueOf(metaAttribute.isNullable()), "NULLABLE")
                                          .withLabel(metaAttribute.getDefaultValue(), "DEFAULT_VALUE")
                                          .withLabel(String.valueOf(metaAttribute.getCardinality()), "cardinality")
                                          .withLabel(
                                                  metaAttribute.getDescription(), "description"
                                          );
        for (Node node : container.getChildren()) {
            node.getStyleClass().add("table-container-label-small");
        }
    }

    public String name() {
        return this.metaAttribute.getName();
    }

    public String type() {
        return this.metaAttribute.getType();
    }

    public String cardinality() {
        return String.valueOf(this.metaAttribute.getCardinality());
    }
}
