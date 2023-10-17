package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.facades.MetaSchemaFacade;
import ch.admin.bar.siardsuite.presenter.tree.RoutinesTableViewPopulatorStrategy;
import ch.admin.bar.siardsuite.presenter.tree.TableViewPopulatorStrategy;
import ch.admin.bar.siardsuite.presenter.tree.TablesTableViewPopulatorStrategy;
import ch.admin.bar.siardsuite.presenter.tree.TypesTableViewPopulatorStrategy;
import ch.admin.bar.siardsuite.presenter.tree.ViewsTableViewPopulatorStrategy;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseSchema extends DatabaseObject {

    protected final SiardArchive siardArchive;
    protected final Schema schema;
    protected final boolean onlyMetaData;
    protected final String name;

    @Setter
    @Getter
    protected String description;

    @Getter
    protected List<DatabaseTable> tables;

    @Getter
    protected List<DatabaseView> views;

    @Getter
    protected List<DatabaseType> types;

    @Getter
    protected List<Routine> routines;

    protected DatabaseSchema(SiardArchive siardArchive, Schema schema, boolean onlyMetaData) {
        this.siardArchive = siardArchive;
        this.schema = schema;
        this.onlyMetaData = onlyMetaData;

        MetaSchemaFacade metaSchemaFacade = new MetaSchemaFacade(schema);
        name = metaSchemaFacade.name();
        description = metaSchemaFacade.description();

        this.tables = metaSchemaFacade.tables(siardArchive, this, onlyMetaData);
        this.views = metaSchemaFacade.views(siardArchive, this);
        this.routines = metaSchemaFacade.routines(siardArchive, this);

        this.types = metaSchemaFacade.types();
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visitSchema(name, description, tables, views, types, routines);
    }

    @Override
    public void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView == null || type == null) return;
        TableViewPopulatorStrategy strategy = getStrategy(type);
        if (strategy == null) return;
        strategy.populate(tableView, onlyMetaData);
    }

    public void write() {
        val schema = siardArchive.getArchive()
                .getSchema(name);
        schema.getMetaSchema().setDescription(description);
    }

    private TableViewPopulatorStrategy getStrategy(TreeContentView type) {
        if (type.equals(TreeContentView.TABLES) || type.equals(TreeContentView.SCHEMA))
            return new TablesTableViewPopulatorStrategy(tables);
        if (type.equals(TreeContentView.VIEWS)) return new ViewsTableViewPopulatorStrategy(views);
        if (type.equals(TreeContentView.ROUTINES)) return new RoutinesTableViewPopulatorStrategy(routines);
        if (type.equals(TreeContentView.TYPES)) return new TypesTableViewPopulatorStrategy(types);
        return null;
    }


    public void export(List<String> tablesToExport, File directory) {
        this.tables.stream()
                .filter(databaseTable -> tablesToExport.contains(databaseTable.name))
                .forEach(databaseTable -> {
                    try {
                        databaseTable.export(directory);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
    }

    public void populate(CheckBoxTreeItem<String> schemaItem) {
        List<CheckBoxTreeItem<String>> checkBoxTreeItems = this.tables.stream()
                .map(table -> new CheckBoxTreeItem<>(table.name))
                .collect(Collectors.toList());
        schemaItem.getChildren().setAll(checkBoxTreeItems);
    }

    @Override
    public void populate(VBox vbox, TreeContentView type) {
    }

    public String name() {
        return name;
    }

    public List<DatabaseType> types() {
        return this.types;
    }

    public List<Routine> routines() {
        return this.routines;
    }

    public List<DatabaseView> views() {
        return this.views;
    }

    public List<DatabaseTable> tables() {
        return this.tables;
    }
}
