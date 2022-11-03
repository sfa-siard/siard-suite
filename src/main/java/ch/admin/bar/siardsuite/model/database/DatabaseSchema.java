package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseSchema extends DatabaseObject {

    protected final SiardArchive siardArchive;
    protected final Archive archive;
    protected final boolean onlyMetaData;
    protected final String name;
    protected final String description;
    protected final List<DatabaseTable> tables = new ArrayList<>();

    protected DatabaseSchema(SiardArchive siardArchive, Archive archive, Schema schema, boolean onlyMetaData) {
        this.siardArchive = siardArchive;
        this.archive = archive;
        this.onlyMetaData = onlyMetaData;
        name = schema.getMetaSchema().getName();
        description = schema.getMetaSchema().getDescription();
        for (int i = 0; i < schema.getTables(); i++) {
            tables.add(new DatabaseTable(siardArchive, archive, this, schema.getTable(i), onlyMetaData));
        }
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name, description, tables);
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
            col0.setMinWidth(125);
            col1.setMinWidth(285);
            col2.setMinWidth(125);
            col0.setStyle("-fx-alignment: CENTER_RIGHT");
            col1.setStyle("-fx-alignment: CENTER_LEFT");
            col2.setStyle("-fx-alignment: CENTER_RIGHT");
            col0.setCellValueFactory(new MapValueFactory<>("index"));
            col1.setCellValueFactory(new MapValueFactory<>("name"));
            col2.setCellValueFactory(new MapValueFactory<>("numberOfColumns"));
            tableView.getColumns().add(col0);
            tableView.getColumns().add(col1);
            tableView.getColumns().add(col2);
            if (!onlyMetaData) {
                final TableColumn<Map, StringProperty> col3 = new TableColumn<>();
                col3.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.numberOfRows"));
                col3.setMinWidth(125);
                col3.setStyle("-fx-alignment: CENTER_RIGHT");
                col3.setCellValueFactory(new MapValueFactory<>("numberOfRows"));
                tableView.getColumns().add(col3);
            }
            tableView.setItems(items());
            tableView.setMinWidth(590);
            tableView.setMaxWidth(590);
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
}
