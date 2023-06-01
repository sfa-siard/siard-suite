package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siardsuite.component.SiardLabelContainer;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.facades.Cardinality;
import ch.admin.bar.siardsuite.util.MetaSearch;
import ch.admin.bar.siardsuite.util.MetaSearchTerm;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;
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
    protected String cardinality;
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
        try {
            cardinality = new Cardinality(column.getCardinality()).format();
        } catch (IOException e) {
            cardinality = "";
        }
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

    protected TreeSet<MetaSearchHit> aggregatedMetaSearch(String s) {
        return new MetaSearch(new MetaSearchTerm(s))
                .check(name, "name")
                .check(lobFolder, "lobFolder")
                .check(mimeType, "mimeType")
                .check(type, "type")
                .check(userDefinedTypeSchema, "userDefinedTypeSchema")
                .check(userDefinedTypeName, "userDefinedTypeName")
                .check(originalType, "originalType")
                .check(defaultValue, "defaultValue")
                .check(description, description)
                .hits("Schema " + schema.name + ", Table " + table.name() + ", Column " + name, this, treeContentView);
    }

    @Override
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
        return cardinality;
    }
}
