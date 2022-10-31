package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.*;

public class DatabaseSchema extends DatabaseObject {

    protected final SiardArchive archive;
    protected final String name;
    protected final String description;
    protected final List<DatabaseTable> tables = new ArrayList<>();

    protected DatabaseSchema(SiardArchive archive, Schema schema) {
        this(archive, schema, false);
    }

    protected DatabaseSchema(SiardArchive archive, Schema schema, boolean onlyMetaData) {
        this.archive = archive;
        name = schema.getMetaSchema().getName();
        description = schema.getMetaSchema().getDescription();
        for (int i = 0; i < schema.getTables(); i++) {
            tables.add(new DatabaseTable(archive, this, schema.getTable(i), onlyMetaData));
        }
    }

    protected void shareProperties(SiardArchiveVisitor visitor) {
        visitor.visit(name, description, tables);
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        final TableColumn<Map, StringProperty> col0 = new TableColumn<>();
        final TableColumn<Map, StringProperty> col1 = new TableColumn<>();
        final TableColumn<Map, StringProperty> col2 = new TableColumn<>();
        final TableColumn<Map, StringProperty> col3 = new TableColumn<>();
        col0.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.row"));
        col1.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.tableName"));
        col2.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.numberOfColumns"));
        col3.textProperty().bind(I18n.createStringBinding("tableContainer.table.header.numberOfRows"));
        col0.setMinWidth(125);
        col1.setMinWidth(250);
        col2.setMinWidth(125);
        col3.setMinWidth(125);
        col0.setStyle("-fx-alignment: CENTER_RIGHT");
        col1.setStyle("-fx-alignment: CENTER_LEFT");
        col2.setStyle("-fx-alignment: CENTER_RIGHT");
        col3.setStyle("-fx-alignment: CENTER_RIGHT");
        col0.setCellValueFactory(new MapValueFactory<>("index"));
        col1.setCellValueFactory(new MapValueFactory<>("name"));
        col2.setCellValueFactory(new MapValueFactory<>("numberOfColumns"));
        col3.setCellValueFactory(new MapValueFactory<>("numberOfRows"));
        tableView.getColumns().add(col0);
        tableView.getColumns().add(col1);
        tableView.getColumns().add(col2);
        tableView.getColumns().add(col3);
        tableView.setItems(rows());
        tableView.setMaxWidth(590);
    }

    private ObservableList<Map> rows() {
        final ObservableList<Map> rows = FXCollections.observableArrayList();
        for (DatabaseTable t : tables) {
            Map<String, String> row = new HashMap<>();
            row.put("index", String.valueOf(tables.indexOf(t) + 1));
            row.put("name", t.name);
            row.put("numberOfColumns", t.numberOfColumns);
            row.put("numberOfRows", t.numberOfRows);
            rows.add(row);
        }
        return rows;
    }

}
