package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siardsuite.component.SiardLabelContainer;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class DatabaseColumn extends DatabaseObject {

    protected final SiardArchive archive;
    protected final DatabaseSchema schema;
    protected final WithColumns table;
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

    protected DatabaseColumn(SiardArchive archive, DatabaseSchema schema, WithColumns table, MetaColumn column) {
        this.archive = archive;
        this.schema = schema;
        this.table = table;
        this.column = column;
        this.index = String.valueOf(column.getPosition());
        name = column.getName();
        String type = null;
        try {
            type = column.getType();
        } catch (IOException ignored) {
        }
        this.type = type;
        lobFolder = column.getLobFolder() != null ? column.getLobFolder().toString() : "";
        mimeType = column.getMimeType();
        userDefinedTypeSchema = column.getTypeSchema();
        userDefinedTypeName = column.getTypeName();
        originalType = column.getTypeOriginal();
        isNullable = String.valueOf(column.isNullable());
        defaultValue = column.getDefaultValue();
        String cardinality = null;
        try {
            cardinality = String.valueOf(column.getCardinality());
        } catch (IOException ignored) {
        }
        this.cardinality = cardinality;
        description = column.getDescription();
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name);
    }

    @Override
    protected void populate(TableView tableView, TreeContentView type) {
    }

    @Override
    protected void populate(VBox vBox, TreeContentView type) {
        new SiardLabelContainer(vBox).withLabel(name, "name")
                                     .withLabel(index, "index")
                                     .withLabel(lobFolder, "lobFolder")
                                     .withLabel(mimeType, "mimeType")
                                     .withLabel(this.type, "type")
                                     .withLabel(userDefinedTypeSchema, "userDefinedTypeSchema")
                                     .withLabel(userDefinedTypeName, "userDefinedTypeName")
                                     .withLabel(originalType, "originalType")
                                     .withLabel(isNullable, "isNullable")
                                     .withLabel(defaultValue, "defaultValue")
                                     .withLabel(cardinality, "cardinality")
                                     .withLabel(description, "description");

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
            metaSearchHits.add(new MetaSearchHit("Schema " + schema.name + ", Table " + table.name() + ", Column " + name,
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

    public String name() {
        return this.name;
    }

    public String index() {
        return index;
    }

    public String type() {
        return type;
    }

    public String cardinality() {
        if (cardinality.equals("-1")) return "";
        return cardinality;
    }
}
