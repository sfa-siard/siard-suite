package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseSchema extends DatabaseObject {

    protected final SiardArchive archive;
    protected final Schema schema;
    protected final boolean onlyMetaData;
    protected final String name;
    protected final String description;
    protected List<DatabaseTable> tables;
    protected List<DatabaseView> views;
    protected final List<DatabaseType> types = new ArrayList<>();

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
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visitSchema(name, description, tables, views);
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        if (tableView != null && type != null) {
            final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col1 = new TableColumn<>();
            final TableColumn<Map, StringProperty> col2 = new TableColumn<>();
            col0.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.row"));
            col1.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.tableName"));
            col2.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.numberOfColumns"));
            col0.setCellValueFactory(new MapValueFactory<>("index"));
            col1.setCellValueFactory(new MapValueFactory<>("name"));
            col2.setCellValueFactory(new MapValueFactory<>("numberOfColumns"));
            tableView.getColumns().add(col0);
            tableView.getColumns().add(col1);
            tableView.getColumns().add(col2);
            if (!onlyMetaData) {
                final TableColumn<Map, StringProperty> col3 = new TableColumn<>();
                col3.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.numberOfRows"));
                col3.setCellValueFactory(new MapValueFactory<>("numberOfRows"));
                tableView.getColumns().add(col3);
            }
            tableView.setItems(items());
        }
    }

    private ObservableList<Map> items() {
        final ObservableList<Map> items = FXCollections.observableArrayList();
        for (DatabaseTable table : tables) {
            Map<String, String> item = new HashMap<>();
            item.put("index", String.valueOf(tables.indexOf(table) + 1));
            item.put("name", table.name);
            item.put("numberOfColumns", table.numberOfColumns);
            item.put("numberOfRows", table.numberOfRows);
            items.add(item);
        }
        return items;
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
