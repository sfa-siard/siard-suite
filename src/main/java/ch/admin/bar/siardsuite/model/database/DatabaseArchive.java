package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveVisitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DatabaseArchive {

    private String archiveName;
    private boolean onlyMetaData = false;
    private static final Map<String, List<String>> dbTypes = Map.of(
            "MS Access", List.of("access", ""),
            "DB/2", List.of("db2", "50000"),
            "MySQL", List.of("mysql", "3306"),
            "Oracle", List.of("oracle", "1521"),
            "PostgreSQL", List.of("postgresql", "5432"),
            "Microsoft SQL Server", List.of("sqlserver", "1433"));
    protected final ObservableList<DatabaseSchema> schemas = FXCollections.observableArrayList();
    protected TableView<DatabaseSchema> tableView;
    private DatabaseArchiveMetaData metaData;

    public DatabaseArchive() {
    }

    public DatabaseArchive(String archiveName, Archive archive) {
        this(archiveName, archive, false);
    }

    public DatabaseArchive(String archiveName, Archive archive, boolean onlyMetaData) {
        initTableView();
        this.onlyMetaData = onlyMetaData;
        this.archiveName = archiveName;
        metaData = new DatabaseArchiveMetaData(archive.getMetaData());
        for (int i = 0; i < archive.getSchemas(); i++) {
            schemas.add(new DatabaseSchema(this, archive.getSchema(i), onlyMetaData));
        }
        tableView.setItems(schemas);
        System.out.println("archive created");
    }

    public void addArchiveMetaData(String databaseDescription, String databaseOwner, String dataOriginTimespan,
                                   String archiverName, String archiverContact, File targetArchive) {
        this.metaData = new DatabaseArchiveMetaData(databaseDescription, databaseOwner, dataOriginTimespan,
                archiverName, archiverContact, targetArchive);
    }

    public void shareProperties(DatabaseArchiveVisitor visitor) {
        visitor.visit(archiveName, onlyMetaData);
    }

    public void shareObject(DatabaseArchiveVisitor visitor) {
        visitor.visit(this);
    }

    public void shareProperties(DatabaseArchiveMetaDataVisitor visitor) {
        if (metaData != null) {
            metaData.shareProperties(visitor);
        }
    }

    public void shareObject(DatabaseArchiveMetaDataVisitor visitor) {
        if (metaData != null) {
            metaData.shareObject(visitor);
        }
    }

    private void initTableView() {
        tableView = new TableView<>();
        TableColumn<DatabaseSchema, String> col1 = new TableColumn<>();
//        col1.textProperty().bind(I18n.createStringBinding());
        col1.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<DatabaseSchema, String> col2 = new TableColumn<>();
//        col2.textProperty().bind(I18n.createStringBinding());
        col2.setCellValueFactory(new PropertyValueFactory<>("tableView"));
        tableView.getColumns().add(col1);
        tableView.getColumns().add(col2);
    }

    public TableView<DatabaseSchema> tableView() {
        return tableView;
    }

    public TableView<DatabaseTable> tableView(DatabaseSchema schema) {
        TableView<DatabaseTable> v = new TableView<>();
        Optional<DatabaseSchema> o = schemas.stream().filter(s -> s.equals(schema)).findFirst();
        if (o.isPresent()) {
            v = o.get().tableView;
        }
        return v;
    }

    public TableView<DatabaseColumn> tableViewCol(DatabaseTable table) {
        TableView<DatabaseColumn> v = new TableView<>();
        Optional<DatabaseTable> o = schemas.stream().flatMap(s -> s.tables.stream()).filter(t -> t.equals(table)).findFirst();
        if (o.isPresent()) {
            v = o.get().tableViewCol;
        }
        return v;
    }

    public TableView<DatabaseRow> tableViewRow(DatabaseTable table) {
        TableView<DatabaseRow> v = new TableView<>();
        Optional<DatabaseTable> o = schemas.stream().flatMap(s -> s.tables.stream()).filter(t -> t.equals(table)).findFirst();
        if (o.isPresent()) {
            v = o.get().tableViewRow;
        }
        return v;
    }

    public String name(DatabaseSchema schema) {
        String n = "";
        Optional<DatabaseSchema> o = schemas.stream().filter(s -> s.equals(schema)).findFirst();
        if (o.isPresent()) {
            n = o.get().name;
        }
        return n;
    }

    public String name(DatabaseTable table) {
        String n = "";
        Optional<DatabaseTable> o = schemas.stream().flatMap(s -> s.tables.stream()).filter(t -> t.equals(table)).findFirst();
        if (o.isPresent()) {
            n = o.get().name;
        }
        return n;
    }

    public String name(DatabaseColumn column) {
        String n = "";
        Optional<DatabaseColumn> o = schemas.stream().flatMap(s -> s.tables.stream().flatMap(t -> t.columns.stream())).filter(c -> c.equals(column)).findFirst();
        if (o.isPresent()) {
            n = o.get().name;
        }
        return n;
    }

}
