package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.component.Icon;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.*;
import ch.admin.bar.siardsuite.presenter.tree.DetailsPresenter;
import ch.admin.bar.siardsuite.presenter.tree.TreeItemFactory;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Presentes an archive - either when archiving a database (always only metadata) or when a SIARD Archive file was opened to browse the archive content
 */
public class ArchivePresenter extends StepperPresenter implements SiardArchiveVisitor {

    private final StringProperty archiveName = new SimpleStringProperty();
    private boolean onlyMetaData = false;
    private String schemaName = "";
    private List<DatabaseSchema> schemas = new ArrayList<>();
    private List<DatabaseTable> tables = new ArrayList<>();
    private List<DatabaseView> views = new ArrayList<>();
    private String tableName = "";
    private List<DatabaseColumn> columns = new ArrayList<>();
    private String columnName = "";
    private String numberOfRows = "";
    private List<User> users;
    private List<Privilige> priviliges;
    private List<DatabaseType> types;
    private List<Routine> routines = new ArrayList<>();

    protected final Node db = new ImageView(Icon.db);
    @FXML
    protected TreeView<TreeAttributeWrapper> treeView;
    @FXML
    protected MFXButton tableSearchButton;
    @FXML
    protected MFXButton metaSearchButton;
    @FXML
    protected AnchorPane contentPane;
    @FXML
    public Label titleTableContainer;
    @FXML
    public StackPane rightTableBox;


    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;
        initTreeView();
        setListeners();
        model.setCurrentPreviewPresenter(this);
        this.tableSearchButton.setVisible(false);
        I18n.bind(this.metaSearchButton.textProperty(), "tableContainer.metaSearchButton");
        I18n.bind(this.tableSearchButton.textProperty(), "tableContainer.tableSearchButton");
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
    }

    protected void initTreeView() {
        model.provideDatabaseArchiveProperties(this);

        final TreeItem<TreeAttributeWrapper> rootItem = createRootItem();
        rootItem.getChildren().add(createSchemasItem());

        addIfNotEmpty(rootItem, TreeItemFactory.create("archive.tree.view.node.users",
                                                       TreeContentView.USERS,
                                                       new Users(users),
                                                       users));
        addIfNotEmpty(rootItem, TreeItemFactory.create("archive.tree.view.node.priviliges",
                                                       TreeContentView.PRIVILIGES,
                                                       new Priviliges(priviliges),
                                                       priviliges));
        treeView.setRoot(rootItem);

        refreshContentPane(rootItem.getValue());
    }


    private TreeItem<TreeAttributeWrapper> createSchemasItem() {
        final TreeItem<TreeAttributeWrapper> schemasItem = TreeItemFactory.create("archive.tree.view.node.schemas",
                                                                                  TreeContentView.SCHEMAS,
                                                                                  null,
                                                                                  schemas);
        schemasItem.setExpanded(true);

        schemas.forEach(schema -> {
            model.provideDatabaseArchiveProperties(this, schema);
            schemasItem.getChildren().add(createSchemaItem(schema));
        });

        return schemasItem;
    }

    private TreeItem<TreeAttributeWrapper> createSchemaItem(DatabaseSchema schema) {
        TreeItem<TreeAttributeWrapper> schemaItem = new TreeItem<>(new TreeAttributeWrapper(schemaName,
                                                                                            TreeContentView.SCHEMA,
                                                                                            schema));
        schemaItem.setExpanded(true);


        addIfNotEmpty(schemaItem, TreeItemFactory.create("archive.tree.view.node.types",
                                                         TreeContentView.TYPES,
                                                         new DatabaseTypes(types),
                                                         types));

        addIfNotEmpty(schemaItem, createTablesItem(schema));
        addIfNotEmpty(schemaItem, createRoutinesItem(schema));
        addIfNotEmpty(schemaItem, createViewsItem(schema));
        return schemaItem;
    }

    private TreeItem<TreeAttributeWrapper> createRoutinesItem(DatabaseSchema schema) {
        TreeItem<TreeAttributeWrapper> rowsItem;
        TreeItem<TreeAttributeWrapper> parametersItem;
        TreeItem<TreeAttributeWrapper> routinesItem;
        TreeItem<TreeAttributeWrapper> routineItem;
        TreeItem<TreeAttributeWrapper> columnItem;

        routinesItem = TreeItemFactory.create("archive.tree.view.node.routines",
                                              TreeContentView.ROUTINES,
                                              schema,
                                              routines);

        for (Routine routine : routines) {
            model.provideDatabaseArchiveProperties(this, routine);
            routineItem = new TreeItem<>(new TreeAttributeWrapper(routine.name(), TreeContentView.ROUTINE, routine));

            List<MetaParameter> parameters = routine.parameters();
            parametersItem = TreeItemFactory.create("archive.tree.view.node.parameters",
                                                    TreeContentView.COLUMNS,
                                                    // TODO: maybe create treeContentView.PARAMETERS?
                                                    routine,
                                                    parameters);

            parametersItem.getChildren().addAll(parameters.stream().map(metaParameter ->
                                                                                new TreeItem<>(new TreeAttributeWrapper(
                                                                                        metaParameter.getName(),
                                                                                        TreeContentView.PARAMETER,
                                                                                        new DatabaseParameter(
                                                                                                metaParameter)))
            ).collect(Collectors.toList()));
            routineItem.getChildren().add(parametersItem);
            routinesItem.getChildren().add(routineItem);
        }
        return routinesItem;
    }

    private TreeItem<TreeAttributeWrapper> createViewsItem(DatabaseSchema schema) {
        TreeItem<TreeAttributeWrapper> viewsItem;
        TreeItem<TreeAttributeWrapper> viewItem;

        TreeItem<TreeAttributeWrapper> columnsItem;
        TreeItem<TreeAttributeWrapper> columnItem;
        viewsItem = TreeItemFactory.create("archive.tree.view.node.views", TreeContentView.VIEWS,
                                           schema, views);

        for (DatabaseView view : views) {
            model.provideDatabaseArchiveProperties(this, view);
            viewItem = new TreeItem<>(new TreeAttributeWrapper(view.name(), TreeContentView.VIEW, view));

            columnsItem = TreeItemFactory.create("archive.tree.view.node.columns",
                                                 TreeContentView.COLUMNS,
                                                 view,
                                                 columns);


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
        tablesItem = TreeItemFactory.create("archive.tree.view.node.tables", TreeContentView.TABLES,
                                            schema, tables);

        for (DatabaseTable table : tables) {
            model.provideDatabaseArchiveProperties(this, table);

            tableItem = new TreeItem<>(new TreeAttributeWrapper(tableName, TreeContentView.TABLE, table));


            columnsItem = TreeItemFactory.create("archive.tree.view.node.columns",
                                                 TreeContentView.COLUMNS,
                                                 table,
                                                 columns);

            for (DatabaseColumn column : columns) {
                model.provideDatabaseArchiveProperties(this, column);

                columnItem = new TreeItem<>(new TreeAttributeWrapper(columnName, TreeContentView.COLUMN, column));
                columnsItem.getChildren().add(columnItem);
            }

            if (!onlyMetaData) {
                rowsItem = TreeItemFactory.create("archive.tree.view.node.rows",
                                                  TreeContentView.ROWS,
                                                  table,
                                                  numberOfRows);

                tableItem.getChildren().add(rowsItem);
            }

            tableItem.getChildren().add(columnsItem);
            tablesItem.getChildren().add(tableItem);
        }
        tablesItem.setExpanded(true);
        return tablesItem;
    }

    private TreeItem<TreeAttributeWrapper> createRootItem() {
        final TreeItem<TreeAttributeWrapper> rootItem = new TreeItem<>(new TreeAttributeWrapper(archiveName.get(),
                                                                                                TreeContentView.ROOT,
                                                                                                null), db);

        rootItem.setExpanded(true);
        return rootItem;
    }

    private void addIfNotEmpty(TreeItem<TreeAttributeWrapper> rootItem,
                               TreeItem<TreeAttributeWrapper> item) {
        if (item.getChildren().size() == 0) return;
        rootItem.getChildren().add(item);
    }

    protected void setListeners() {
        MultipleSelectionModel<TreeItem<TreeAttributeWrapper>> selection = treeView.getSelectionModel();
        selection.selectedItemProperty()
                 .addListener(((observable, oldValue, newValue) -> refreshContentPane(newValue.getValue())));
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

    // Refresh the content view based on the selected item in the tree (e.g. tables, users...)
    protected void refreshContentPane(TreeAttributeWrapper wrapper) {
        try {
            FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(wrapper.getType().getViewName()));
            Node container = loader.load();
            contentPane.getChildren().setAll(container);
            loader.<DetailsPresenter>getController().init(this.controller, model, this.stage, wrapper);
            tableSearchButton.setVisible(wrapper.getType().getHasTableSearch());
            I18n.bind(this.titleTableContainer.textProperty(), wrapper.getType().getViewTitle());
            contentPane.prefWidthProperty().bind(rightTableBox.widthProperty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AnchorPane getContentPane() {
        return contentPane;
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
                            List<DatabaseView> views, List<DatabaseType> types, List<Routine> routines) {
        this.schemaName = schemaName;
        this.tables = tables;
        this.views = views;
        this.types = types;
        this.routines = routines;
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

}
