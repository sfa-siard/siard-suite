package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.*;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTable {

    protected final DatabaseArchive archive;
    protected final DatabaseSchema schema;
    protected final String name;
    protected final ObservableList<DatabaseColumn> columns =  FXCollections.observableArrayList();
    protected TableView<DatabaseColumn> tableViewCol;
    protected final ObservableList<DatabaseRow> rows = FXCollections.observableArrayList();
    protected TableView<DatabaseRow> tableViewRow;

    protected DatabaseTable(DatabaseArchive archive, DatabaseSchema schema, Table table) {
        this(archive, schema, table, false);
    }

    protected DatabaseTable(DatabaseArchive archive, DatabaseSchema schema, Table table, boolean onlyMetaData) {
        initTableViewCol();
        this.archive = archive;
        this.schema = schema;
        name = table.getMetaTable().getName();
        for (int i = 0; i < table.getMetaTable().getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(archive, schema, this, table.getMetaTable().getMetaColumn(i)));
        }
        tableViewCol.setItems(columns);
        if (!onlyMetaData) {
            initTableViewRow();
            try {
                int i = 0;
                final RecordDispenser recordDispenser = table.openRecords();
                while (i < table.getMetaTable().getRows()) {
                    rows.add(new DatabaseRow(archive, schema, this, recordDispenser.get()));
                    i++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            tableViewRow.setItems(rows);
        }
    }

    private void initTableViewCol() {
        tableViewCol = new TableView<>();
        TableColumn<DatabaseColumn, String> col1 = new TableColumn<>();
//        col1.textProperty().bind(I18n.createStringBinding());
        col1.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableViewCol.getColumns().add(col1);
    }

    private void initTableViewRow() {
        tableViewRow = new TableView<>();
        TableColumn<DatabaseRow, String> col1 = new TableColumn<>();
//        col1.textProperty().bind(I18n.createStringBinding());
        col1.setCellValueFactory(new PropertyValueFactory<>("index"));
        TableColumn<DatabaseRow, String> col2 = new TableColumn<>();
//        col1.textProperty().bind(I18n.createStringBinding());
        col2.setCellValueFactory(new PropertyValueFactory<>("tableView"));
        tableViewRow.getColumns().add(col2);
    }

    public DatabaseTable(String name) {
        this.archive = null;
        this.schema = null;
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
