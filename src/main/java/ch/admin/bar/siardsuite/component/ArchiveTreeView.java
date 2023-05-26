package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.*;
import ch.admin.bar.siardsuite.presenter.Priviliges;
import ch.admin.bar.siardsuite.presenter.tree.TreeItemFactory;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArchiveTreeView implements SiardArchiveVisitor {

    private final StringProperty archiveName = new SimpleStringProperty();
    private final SiardArchive siardArchive;
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


    private final TreeView<TreeAttributeWrapper> treeView;
    private final Model model; // TODO: get rid of it asap

    public ArchiveTreeView(SiardArchive siardArchive, TreeView<TreeAttributeWrapper> treeView, Model model) {
        this.siardArchive = siardArchive;
        this.siardArchive.accept(archiveName -> this.archiveName.setValue(archiveName));
        this.treeView = treeView;
        this.model = model;
    }

    public void init() {
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
                                                                                                null),
                                                                       new ImageView(Icon.db));


        rootItem.setExpanded(true);
        return rootItem;
    }

    private void addIfNotEmpty(TreeItem<TreeAttributeWrapper> rootItem,
                               TreeItem<TreeAttributeWrapper> item) {
        if (item.getChildren().size() == 0) return;
        rootItem.getChildren().add(item);
    }

    @Override
    public void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas, List<User> users,
                      List<Privilige> priviliges) {
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

    public TreeItem<TreeAttributeWrapper> rootItem() {
        return this.treeView.getRoot();
    }
}
