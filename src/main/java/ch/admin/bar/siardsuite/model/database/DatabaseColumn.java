package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.*;

public class DatabaseColumn extends DatabaseObject {

    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final DatabaseTable table;
    protected final MetaColumn column;
    protected final String index;
    protected final String name;
    protected final String type;
    protected final String lobFolder;
    protected final String mimeType;
    protected final String userDefinedTypeSchema;
    protected final String userDefinedTypeName;
    protected final String originalType;
    protected final String isNullable;
    protected final String defaultValue;
    protected final String cardinality;
    protected final String description;

    protected final TreeContentView treeContentView = TreeContentView.COLUMN;

    protected DatabaseColumn(SiardArchive archive, DatabaseSchema schema, DatabaseTable table, MetaColumn column) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        this.column = column;
        this.index = String.valueOf(column.getPosition());
        name = column.getName();
        try {
            type = column.getType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lobFolder = column.getLobFolder() != null ? column.getLobFolder().toString() : "";
        mimeType = column.getMimeType();
        userDefinedTypeSchema = column.getTypeSchema();
        userDefinedTypeName = column.getTypeName();
        originalType = column.getTypeOriginal();
        isNullable = String.valueOf(column.isNullable());
        defaultValue = column.getDefaultValue();
        try {
            cardinality = String.valueOf(column.getCardinality());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        description = column.getDescription();
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name);
    }

    @Override
    protected void populate(TableView tableView, TreeContentView type) {}

    @Override
    protected void populate(VBox vBox, TreeContentView type) {
        List<Label> labels = new ArrayList<>();
        Label label = new Label(name != null && !name.isEmpty() ? name : "-");
        label.setId("name");
        labels.add(label);
        label = new Label(index != null && !index.isEmpty() ? index : "-");
        label.setId("index");
        labels.add(label);
        label = new Label(lobFolder != null && !lobFolder.isEmpty() ? lobFolder : "-");
        label.setId("lobFolder");
        labels.add(label);
        label = new Label(mimeType != null && !mimeType.isEmpty() ? mimeType : "-");
        label.setId("mimeType");
        labels.add(label);
        label = new Label(this.type != null && !this.type.isEmpty() ? this.type : "-");
        label.setId("type");
        labels.add(label);
        label = new Label(userDefinedTypeSchema != null && !userDefinedTypeSchema.isEmpty() ? userDefinedTypeSchema : "-");
        label.setId("userDefinedTypeSchema");
        labels.add(label);
        label = new Label(userDefinedTypeName != null && !userDefinedTypeName.isEmpty() ? userDefinedTypeName : "-");
        label.setId("userDefinedTypeName");
        labels.add(label);
        label = new Label(originalType != null && !originalType.isEmpty() ? originalType : "-");
        label.setId("originalType");
        labels.add(label);
        label = new Label(isNullable != null && !isNullable.isEmpty() ? isNullable : "-");
        label.setId("isNullable");
        labels.add(label);
        label = new Label(defaultValue != null && !defaultValue.isEmpty() ? defaultValue : "-");
        label.setId("defaultValue");
        labels.add(label);
        label = new Label(cardinality != null && !cardinality.isEmpty() ? cardinality : "-");
        label.setId("cardinality");
        labels.add(label);
        label = new Label(description != null && !description.isEmpty() ? description : "-");
        label.setId("description");
        labels.add(label);

        vBox.getChildren().addAll(labels);
        for (Node node : vBox.getChildren()) {
            node.getStyleClass().add("table-container-label-small");
        }
    }

    private TreeSet<MetaSearchHit> metaSearch(String s) {
        TreeSet<MetaSearchHit> hits = new TreeSet<>();
        final List<String> nodeIds = new ArrayList<>();
        if (contains(name, s)) {
            nodeIds.add("name");
        }
        if (contains(lobFolder, s)) {
            nodeIds.add("lobFolder");
        }
        if (contains(mimeType, s)) {
            nodeIds.add("mimeType");
        }
        if (contains(type, s)) {
            nodeIds.add("type");
        }
        if (contains(userDefinedTypeSchema, s)) {
            nodeIds.add("userDefinedTypeSchema");
        }
        if (contains(userDefinedTypeName, s)) {
            nodeIds.add("userDefinedTypeName");
        }
        if (contains(originalType, s)) {
            nodeIds.add("originalType");
        }
        if (contains(defaultValue, s)) {
            nodeIds.add("defaultValue");
        }
        if (contains(description, s)) {
            nodeIds.add("description");
        }
        if (nodeIds.size() > 0) {
            List<MetaSearchHit> metaSearchHits = new ArrayList<>();
            metaSearchHits.add(new MetaSearchHit("Schema " + schema.name + ", Table " + table.name + ", Column " + name,
                                                 this,
                                                 treeContentView,
                                                 nodeIds));
            hits = new TreeSet<>(
                    metaSearchHits);
        }
        return hits;
    }

    protected TreeSet<MetaSearchHit> aggregatedMetaSearch(String s) {
        return metaSearch(s);
    }

}
