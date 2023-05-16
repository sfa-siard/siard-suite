package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.*;
import ch.admin.bar.siardsuite.presenter.tree.TreePresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PreviewPresenter extends StepperPresenter implements SiardArchiveVisitor {

    private final StringProperty archiveName = new SimpleStringProperty();
    private boolean onlyMetaData = false;
    private List<DatabaseSchema> schemas = new ArrayList<>();
    private String schemaName = "";
    private List<DatabaseTable> tables = new ArrayList<>();
    private List<DatabaseView> views = new ArrayList<>();
    private String tableName = "";
    private List<DatabaseColumn> columns = new ArrayList<>();
    private String columnName = "";
    private String numberOfRows = "";
    private List<User> users;
    private List<Privilige> priviliges;
    private List<DatabaseType> types;

    protected final Node db = new ImageView(new Image(String.valueOf(SiardApplication.class.getResource(
            "icons/server.png")), 16.0, 16.0, true, false));
    @FXML
    protected TreeView<TreeAttributeWrapper> treeView;
    @FXML
    protected MFXButton tableSearchButton;
    @FXML
    protected MFXButton metaSearchButton;
    @FXML
    protected AnchorPane tableContainerContent;
    @FXML
    public Label titleTableContainer;
    @FXML
    public StackPane rightTableBox;


    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        model.setCurrentPreviewPresenter(this);
        this.tableSearchButton.setVisible(false);
        I18n.bind(this.metaSearchButton.textProperty(), "tableContainer.metaSearchButton");
        I18n.bind(this.tableSearchButton.textProperty(), "tableContainer.tableSearchButton");

        setListeners();
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
    }

    protected void initTreeView() {
        model.provideDatabaseArchiveObject(this);
        model.provideDatabaseArchiveProperties(this);

        final TreeItem<TreeAttributeWrapper> rootItem = new TreeItem<>(new TreeAttributeWrapper(archiveName.get(),
                                                                                                TreeContentView.ROOT,
                                                                                                null), db);

        rootItem.getChildren().add(createSchemasItem());
        rootItem.setExpanded(true);
        addIfNotEmpty(rootItem, users, create("archive.tree.view.node.users",
                                              TreeContentView.USERS,
                                              new Users(users),
                                              users));
        addIfNotEmpty(rootItem, priviliges, create("archive.tree.view.node.priviliges",
                                                   TreeContentView.PRIVILIGES,
                                                   new Priviliges(priviliges),
                                                   priviliges));
        treeView.setRoot(rootItem);

        updateTableContainerContent(rootItem.getValue());
    }

    private void addIfNotEmpty(TreeItem<TreeAttributeWrapper> rootItem, List<? extends Object> items,
                               TreeItem<TreeAttributeWrapper> item) {
        if (items.size() == 0) return;
        rootItem.getChildren().add(item);
    }

    private TreeItem<TreeAttributeWrapper> create(String nodeLabel, TreeContentView view, DatabaseObject dbObject,
                                                  Collection elements) {
        final TreeItem<TreeAttributeWrapper> item = new TreeItem<>();
        item.valueProperty()
            .bind(I18n.createTreeAtributeWrapperBinding(nodeLabel,
                                                        view,
                                                        dbObject,
                                                        elements.size()));
        return item;
    }

    private TreeItem<TreeAttributeWrapper> createSchemasItem() {
        final TreeItem<TreeAttributeWrapper> schemasItem = new TreeItem<>();
        schemasItem.valueProperty()
                   .bind(I18n.createTreeAtributeWrapperBinding("archive.tree.view.node.schemas",
                                                               TreeContentView.SCHEMAS,
                                                               null,
                                                               schemas.size()));
        TreeItem<TreeAttributeWrapper> schemaItem;
        schemasItem.setExpanded(true);

        for (DatabaseSchema schema : schemas) {
            model.provideDatabaseArchiveProperties(this, schema);

            schemaItem = new TreeItem<>(new TreeAttributeWrapper(schemaName, TreeContentView.SCHEMA, schema));
            schemaItem.setExpanded(true);


            addIfNotEmpty(schemaItem, types, create("archive.tree.view.node.types",
                                                    TreeContentView.TYPES,
                                                    new DatabaseTypes(types),
                                                    types));
            TreeItem<TreeAttributeWrapper> tablesItem = createTablesItem(schema);
            tablesItem.setExpanded(true);
            addIfNotEmpty(schemaItem, tables, tablesItem);
            addIfNotEmpty(schemaItem, views, createViewsItem(schema));
            schemasItem.getChildren().add(schemaItem);
        }
        return schemasItem;
    }

    private TreeItem<TreeAttributeWrapper> createViewsItem(DatabaseSchema schema) {
        TreeItem<TreeAttributeWrapper> rowsItem;
        TreeItem<TreeAttributeWrapper> columnsItem;
        TreeItem<TreeAttributeWrapper> viewsItem;
        TreeItem<TreeAttributeWrapper> viewItem;
        TreeItem<TreeAttributeWrapper> columnItem;
        viewsItem = new TreeItem<>();

        viewsItem.valueProperty()
                 .bind(I18n.createTreeAtributeWrapperBinding("archive.tree.view.node.views", TreeContentView.VIEWS,
                                                             schema, views.size()));

        for (DatabaseView view : views) {
            model.provideDatabaseArchiveProperties(this, view);
            viewItem = new TreeItem<>(new TreeAttributeWrapper(view.name(), TreeContentView.VIEW, view));

            columnsItem = new TreeItem<>();
            columnsItem.valueProperty()
                       .bind(I18n.createTreeAtributeWrapperBinding("archive.tree.view.node.columns",
                                                                   TreeContentView.COLUMNS,
                                                                   view,
                                                                   columns.size()));


            for (DatabaseColumn column : columns) {
                model.provideDatabaseArchiveProperties(this, column);

                columnItem = new TreeItem<>(new TreeAttributeWrapper(columnName, TreeContentView.COLUMN, column));
                columnsItem.getChildren().add(columnItem);
            }

            viewItem.getChildren().add(columnsItem);
            viewsItem.getChildren().add(viewItem);
        }
        return viewsItem;
    }

    private TreeItem<TreeAttributeWrapper> createTablesItem(DatabaseSchema schema) {
        TreeItem<TreeAttributeWrapper> rowsItem;
        TreeItem<TreeAttributeWrapper> columnsItem;
        TreeItem<TreeAttributeWrapper> tablesItem;
        TreeItem<TreeAttributeWrapper> tableItem;
        TreeItem<TreeAttributeWrapper> columnItem;
        tablesItem = new TreeItem<>();
        tablesItem.valueProperty()
                  .bind(I18n.createTreeAtributeWrapperBinding("archive.tree.view.node.tables", TreeContentView.TABLES,
                                                              schema, tables.size()));

        for (DatabaseTable table : tables) {
            model.provideDatabaseArchiveProperties(this, table);

            tableItem = new TreeItem<>(new TreeAttributeWrapper(tableName, TreeContentView.TABLE, table));


            columnsItem = new TreeItem<>();
            columnsItem.valueProperty()
                       .bind(I18n.createTreeAtributeWrapperBinding("archive.tree.view.node.columns",
                                                                   TreeContentView.COLUMNS,
                                                                   table,
                                                                   columns.size()));

            for (DatabaseColumn column : columns) {
                model.provideDatabaseArchiveProperties(this, column);

                columnItem = new TreeItem<>(new TreeAttributeWrapper(columnName, TreeContentView.COLUMN, column));
                columnsItem.getChildren().add(columnItem);
            }

            if (!onlyMetaData) {
                rowsItem = new TreeItem<>();
                rowsItem.valueProperty()
                        .bind(I18n.createTreeAtributeWrapperBinding("archive.tree.view.node.rows",
                                                                    TreeContentView.ROWS,
                                                                    table,
                                                                    numberOfRows));

                tableItem.getChildren().add(rowsItem);
            }

            tableItem.getChildren().add(columnsItem);
            tablesItem.getChildren().add(tableItem);
        }
        return tablesItem;
    }

    protected void setListeners() {
        MultipleSelectionModel<TreeItem<TreeAttributeWrapper>> selection = treeView.getSelectionModel();
        selection.selectedItemProperty()
                 .addListener(((observable, oldValue, newValue) -> updateTableContainerContent(newValue.getValue())));
        tableSearchButton.setOnAction(event -> {
            if (model.getCurrentTableSearchButton() != null && tableSearchButton.equals(model.getCurrentTableSearchButton()
                                                                                             .button()) && model.getCurrentTableSearchButton()
                                                                                                                .active()) {
                model.setCurrentTableSearchButton(tableSearchButton, false);
                tableSearchButton.setStyle("-fx-font-weight: normal;");
                model.getCurrentTableSearchBase()
                     .tableView()
                     .setItems(FXCollections.observableArrayList(model.getCurrentTableSearchBase().rows()));
            } else {
                model.setCurrentTableSearchButton(tableSearchButton, false);
                stage.openDialog(View.SEARCH_TABLE_DIALOG);
            }
        });
        metaSearchButton.setOnAction(event -> stage.openDialog(View.SEARCH_METADATA_DIALOG));
    }

    protected void updateTableContainerContent(TreeAttributeWrapper wrapper) {
        try {
            FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(wrapper.getType().getViewName()));
            Node container = loader.load();
            tableContainerContent.getChildren().setAll(container);
            loader.<TreePresenter>getController().init(this.controller, model, this.stage, wrapper);
            tableSearchButton.setVisible(wrapper.getType().getHasTableSearch());
            I18n.bind(this.titleTableContainer.textProperty(), wrapper.getType().getViewTitle());
            tableContainerContent.prefWidthProperty().bind(rightTableBox.widthProperty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AnchorPane getTableContainerContent() {
        return tableContainerContent;
    }

    @Override
    public void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas, List<User> users,
                      List<Privilige> priviliges) {
        this.archiveName.setValue(archiveName);
        this.onlyMetaData = onlyMetaData;
        this.schemas = schemas;
        this.users = users;
        this.priviliges = priviliges;
    }

    @Override
    public void visitSchema(String schemaName, String schemaDescription, List<DatabaseTable> tables,
                            List<DatabaseView> views, List<DatabaseType> types) {
        this.schemaName = schemaName;
        this.tables = tables;
        this.views = views;
        this.types = types;
    }

    @Override
    public void visit(String tableName, String numberOfRows, List<DatabaseColumn> columns, List<DatabaseRow> rows) {
        this.tableName = tableName;
        this.columns = columns;
        this.numberOfRows = numberOfRows;
    }

    @Override
    public void visit(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public void visit(SiardArchive archive) {
    }

}
