package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.model.TreeContentView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseSchema extends DatabaseObject {

    protected final SiardArchive archive;
    protected final String name;
    protected final List<DatabaseTable> tables = new ArrayList<>();

    protected DatabaseSchema(SiardArchive archive, Schema schema) {
        this(archive, schema, false);
    }

    protected DatabaseSchema(SiardArchive archive, Schema schema, boolean onlyMetaData) {
        this.archive = archive;
        name = schema.getMetaSchema().getName();
        for (int i = 0; i < schema.getTables(); i++) {
            tables.add(new DatabaseTable(archive, this, schema.getTable(i), onlyMetaData));
        }
    }

    @Override
    protected void populate(TableView<Map> tableView, TreeContentView type) {
        final TableColumn<Map, String> col0 = new TableColumn<>("Name");
        col0.setCellValueFactory(new MapValueFactory<>("name"));
        tableView.setItems(rows());
        tableView.getColumns().add(col0);
    }

    private ObservableList<Map> rows() {
        final ObservableList<Map> rows = FXCollections.observableArrayList();
        for (DatabaseTable t : tables) {
            Map<String, String> row = new HashMap<>();
            row.put("name", t.name);
            rows.add(row);
        }
        return rows;
    }

}
