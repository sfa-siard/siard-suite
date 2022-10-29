package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DatabaseSchema {

    protected final DatabaseArchive archive;
    protected final String name;
    protected final ObservableList<DatabaseTable> tables = FXCollections.observableArrayList();
    protected TableView<DatabaseTable> tableView;

    protected DatabaseSchema(DatabaseArchive archive, Schema schema) {
        this(archive, schema, false);
    }

    protected DatabaseSchema(DatabaseArchive archive, Schema schema, boolean onlyMetaData) {
        initTableView();
        this.archive = archive;
        name = schema.getMetaSchema().getName();
        for (int i = 0; i < schema.getTables(); i++) {
            tables.add(new DatabaseTable(archive, this, schema.getTable(i), onlyMetaData));
        }
        tableView.setItems(tables);
    }

    private void initTableView() {
        tableView = new TableView<>();
        TableColumn<DatabaseTable, String> col1 = new TableColumn<>();
//        col1.textProperty().bind(I18n.createStringBinding());
        col1.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<DatabaseTable, String> col2 = new TableColumn<>();
//        col2.textProperty().bind(I18n.createStringBinding());
        col2.setCellValueFactory(new PropertyValueFactory<>("tableViewCol"));
        TableColumn<DatabaseTable, String> col3 = new TableColumn<>();
//        col2.textProperty().bind(I18n.createStringBinding());
        col3.setCellValueFactory(new PropertyValueFactory<>("tableViewRow"));
        tableView.getColumns().add(col1);
        tableView.getColumns().add(col2);
        tableView.getColumns().add(col3);
    }

}
