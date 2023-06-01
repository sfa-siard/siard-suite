package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.*;
import ch.admin.bar.siardsuite.presenter.Priviliges;
import ch.admin.bar.siardsuite.presenter.tree.TreeItemFactory;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.stream.Collectors;

public class ArchiveBrowserView {

    private final SiardArchive siardArchive;
    private final TreeView<TreeAttributeWrapper> treeView;

    public ArchiveBrowserView(SiardArchive siardArchive, TreeView<TreeAttributeWrapper> treeView) {
        this.siardArchive = siardArchive;
        this.treeView = treeView;
    }

    public void init() {
        final TreeItem<TreeAttributeWrapper> rootItem = createRootItem();
        addSchemas(rootItem);
        addUsers(rootItem);
        addPriviliges(rootItem);
        treeView.setRoot(rootItem);
    }

    private void addSchemas(TreeItem<TreeAttributeWrapper> rootItem) {
        List<DatabaseSchema> schemas = this.siardArchive.schemas();
        final TreeItem<TreeAttributeWrapper> schemasItem = TreeItemFactory.create("archive.tree.view.node.schemas",
                                                                                  TreeContentView.SCHEMAS,
                                                                                  null,
                                                                                  schemas);
        schemasItem.setExpanded(true);

        schemas.forEach(schema -> {
            addSchema(schemasItem, schema);
        });

        rootItem.getChildren().add(schemasItem);
    }

    private void addSchema(TreeItem<TreeAttributeWrapper> schemasItem, DatabaseSchema schema) {
        TreeItem<TreeAttributeWrapper> schemaItem = new TreeItem<>(new TreeAttributeWrapper(schema.name(),
                                                                                            TreeContentView.SCHEMA,
                                                                                            schema));
        schemaItem.setExpanded(true);


        addTypes(schemaItem, schema);

        addIfNotEmpty(schemaItem, createTablesItem(schema));
        addIfNotEmpty(schemaItem, createRoutinesItem(schema));
        addIfNotEmpty(schemaItem, createViewsItem(schema));
        schemasItem.getChildren().add(schemaItem);
    }

    private void addTypes(TreeItem<TreeAttributeWrapper> schemaItem, DatabaseSchema schema) {
        List<DatabaseType> types = schema.types();
        if (types.size() == 0) return;
        TreeItem<TreeAttributeWrapper> typesItem = TreeItemFactory.create("archive.tree.view.node.types",
                                                                          TreeContentView.TYPES,
                                                                          new DatabaseTypes(types),
                                                                          types);

        types.forEach(type ->
                      {
                          TreeItem<TreeAttributeWrapper> typeItem = TreeItemFactory.create(type.name(),
                                                                                           TreeContentView.TYPE,
                                                                                           type,

                                                                                           String.valueOf(type.numberOfAttributes()));
                          /*typeItem.getChildren()
                                  .add(TreeItemFactory.create("attributes",
                                                              TreeContentView.ATTRIBUTES,
                                                              String.valueOf(type.numberOfAttributes())));*/
                          typesItem.getChildren()
                                   .add(typeItem);
                      });
        schemaItem.getChildren().add(typesItem);
    }

    private void addPriviliges(TreeItem<TreeAttributeWrapper> rootItem) {
        List<Privilige> priviliges = this.siardArchive.priviliges();
        if (priviliges.size() > 0) {
            rootItem.getChildren().add(TreeItemFactory.create("archive.tree.view.node.priviliges",
                                                              TreeContentView.PRIVILIGES,
                                                              new Priviliges(priviliges),
                                                              priviliges));
        }
    }

    private void addUsers(TreeItem<TreeAttributeWrapper> rootItem) {
        List<User> users = this.siardArchive.users();
        if (users.size() > 0) {
            rootItem.getChildren().add(TreeItemFactory.create("archive.tree.view.node.users",
                                                              TreeContentView.USERS,
                                                              new Users(users),
                                                              users));
        }
    }


    private TreeItem<TreeAttributeWrapper> createRoutinesItem(DatabaseSchema schema) {
        TreeItem<TreeAttributeWrapper> parametersItem;
        TreeItem<TreeAttributeWrapper> routinesItem;
        TreeItem<TreeAttributeWrapper> routineItem;

        List<Routine> routines = schema.routines();
        routinesItem = TreeItemFactory.create("archive.tree.view.node.routines",
                                              TreeContentView.ROUTINES,
                                              schema,
                                              routines);

        for (Routine routine : routines) {
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
        List<DatabaseView> views = schema.views();
        viewsItem = TreeItemFactory.create("archive.tree.view.node.views", TreeContentView.VIEWS,
                                           schema, views);

        for (DatabaseView view : views) {
            viewItem = new TreeItem<>(new TreeAttributeWrapper(view.name(), TreeContentView.VIEW, view));

            List<DatabaseColumn> columns = view.columns();
            columnsItem = TreeItemFactory.create("archive.tree.view.node.columns",
                                                 TreeContentView.COLUMNS,
                                                 view,
                                                 columns);


            for (DatabaseColumn column : columns) {

                columnItem = new TreeItem<>(new TreeAttributeWrapper(column.name(), TreeContentView.COLUMN, column));
                columnsItem.getChildren().add(columnItem);
            }
            viewItem.getChildren().add(columnsItem);
            viewsItem.getChildren().add(viewItem);
        }
        return viewsItem;
    }

    private TreeItem<TreeAttributeWrapper> createTablesItem(DatabaseSchema schema) {
        TreeItem<TreeAttributeWrapper> columnsItem;
        TreeItem<TreeAttributeWrapper> tablesItem;
        TreeItem<TreeAttributeWrapper> tableItem;
        TreeItem<TreeAttributeWrapper> columnItem;

        List<DatabaseTable> tables = schema.tables();
        tablesItem = TreeItemFactory.create("archive.tree.view.node.tables", TreeContentView.TABLES,
                                            schema, tables);

        for (DatabaseTable table : tables) {
            tableItem = new TreeItem<>(new TreeAttributeWrapper(table.name(), TreeContentView.TABLE, table));


            List<DatabaseColumn> columns = table.columns();
            columnsItem = TreeItemFactory.create("archive.tree.view.node.columns",
                                                 TreeContentView.COLUMNS,
                                                 table,
                                                 columns);

            for (DatabaseColumn column : columns) {

                columnItem = new TreeItem<>(new TreeAttributeWrapper(column.name(), TreeContentView.COLUMN, column));
                columnsItem.getChildren().add(columnItem);
            }

            if (!siardArchive.onlyMetaData()) {
                tableItem.getChildren().add(TreeItemFactory.create("archive.tree.view.node.rows",
                                                                   TreeContentView.ROWS,
                                                                   table,
                                                                   table.numberOfRows()));
            }

            tableItem.getChildren().add(columnsItem);
            tablesItem.getChildren().add(tableItem);
        }
        tablesItem.setExpanded(true);
        return tablesItem;
    }

    private TreeItem<TreeAttributeWrapper> createRootItem() {
        final TreeItem<TreeAttributeWrapper> rootItem = new TreeItem<>(new TreeAttributeWrapper(this.siardArchive.name(),
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

    public TreeItem<TreeAttributeWrapper> rootItem() {
        return this.treeView.getRoot();
    }
}
