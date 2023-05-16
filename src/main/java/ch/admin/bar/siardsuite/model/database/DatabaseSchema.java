package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.presenter.tree.TablesTableViewPopulatorStrategy;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DatabaseSchema extends DatabaseObject {

    protected final SiardArchive archive;
    protected final Schema schema;
    protected final boolean onlyMetaData;
    protected final String name;
    protected final String description;
    protected List<DatabaseTable> tables;
    protected List<DatabaseView> views;
    protected List<DatabaseType> types;
    protected List<Routine> routines;

    protected final TreeContentView treeContentView = TreeContentView.SCHEMA;

    protected DatabaseSchema(SiardArchive archive, Schema schema, boolean onlyMetaData) {
        this.archive = archive;
        this.schema = schema;
        this.onlyMetaData = onlyMetaData;

        MetaSchemaFacade metaSchemaFacade = new MetaSchemaFacade(schema);
        name = metaSchemaFacade.name();
        description = metaSchemaFacade.description();

        this.tables = metaSchemaFacade.tables(archive, this, onlyMetaData);
        this.views = metaSchemaFacade.views(archive, this);
        this.routines = metaSchemaFacade.routines(archive, this);

        this.types = metaSchemaFacade.types();
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visitSchema(name, description, tables, views, types, routines);
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView != null && type != null) {
            if (type.equals(TreeContentView.TABLES)) {
                new TablesTableViewPopulatorStrategy().populate(tableView, tables, onlyMetaData);
            }
        }
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
    protected void populate(VBox vbox, TreeContentView type) {
    }

    private TreeSet<MetaSearchHit> metaSearch(String s) {
        TreeSet<MetaSearchHit> hits = new TreeSet<>();
        final List<String> nodeIds = new ArrayList<>();
        if (contains(name, s)) {
            nodeIds.add("name");
        }
        if (contains(description, s)) {
            nodeIds.add("description");
        }
        if (nodeIds.size() > 0) {
            List<MetaSearchHit> metaSearchHits = new ArrayList<>();
            metaSearchHits.add(new MetaSearchHit("Schema " + name, this, treeContentView, nodeIds));
            hits = new TreeSet<>(
                    metaSearchHits);
        }
        return hits;
    }

    protected TreeSet<MetaSearchHit> aggregatedMetaSearch(String s) {
        final TreeSet<MetaSearchHit> hits = metaSearch(s);
        hits.addAll(tables.stream()
                          .flatMap(table -> table.aggregatedMetaSearch(s).stream())
                          .collect(Collectors.toList()));
        return hits;
    }

}
