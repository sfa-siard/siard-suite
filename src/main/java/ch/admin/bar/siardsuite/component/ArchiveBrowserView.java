package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.DatabaseAttribute;
import ch.admin.bar.siardsuite.model.database.DatabaseColumn;
import ch.admin.bar.siardsuite.model.database.DatabaseParameter;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.model.database.DatabaseType;
import ch.admin.bar.siardsuite.model.database.DatabaseView;
import ch.admin.bar.siardsuite.model.database.Privilige;
import ch.admin.bar.siardsuite.model.database.Routine;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;
import ch.admin.bar.siardsuite.model.database.User;
import ch.admin.bar.siardsuite.model.database.Users;
import ch.admin.bar.siardsuite.presenter.Privileges;
import ch.admin.bar.siardsuite.presenter.tree.TreeItemFactory;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.I18nKey;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.val;

import java.net.URI;
import java.util.List;
import java.util.Optional;
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
        val form = RenderableForm.<DatabaseSchema>builder()
                .dataExtractor(controller -> schema)
                .saveAction((controller, siardArchiveMetaData) -> {
                    System.out.println("Speichern!");
                    siardArchiveMetaData.write(siardArchive.getArchive());
                })
                .group(RenderableFormGroup.<DatabaseSchema>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("tableContainer.labelSchema"),
                                DatabaseSchema::name))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("tableContainer.labelDescSchema"),
                                DatabaseSchema::getDescription,
                                DatabaseSchema::setDescription
                        ))
                        .property(RenderableTable.<DatabaseSchema, DatabaseTable>builder()
                                .dataExtractor(DatabaseSchema::getTables)
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.row"),
                                        databaseTable -> (schema.getTables().indexOf(databaseTable) + 1) + ""
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.tableName"),
                                        DatabaseTable::getName
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.numberOfColumns"),
                                        databaseTable -> databaseTable.getColumns().size() + ""
                                ))
                                .property(new ReadOnlyStringProperty<>(
                                        I18nKey.of("tableContainer.table.header.numberOfRows"),
                                        databaseTable -> databaseTable.getRows().size() + ""
                                ))
                                .build())
                        .build())
                .build();

        val schemaItem = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(schema.name())
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(form)
                        .databaseObject(schema)
                        .build());

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
                schema,
                types);

        types.forEach(type -> {
            TreeItem<TreeAttributeWrapper> typeItem = TreeItemFactory.create(type.name(),
                    TreeContentView.TYPE,
                    type,
                    String.valueOf(type.numberOfAttributes()));
            addAttributes(typeItem, type);
            typesItem.getChildren().add(typeItem);
        });
        schemaItem.getChildren().add(typesItem);
    }

    private void addAttributes(TreeItem<TreeAttributeWrapper> typeItem, DatabaseType type) {
        List<DatabaseAttribute> attributes = type.attributes();
        if (attributes.isEmpty()) return;
        TreeItem<TreeAttributeWrapper> attributesItem = TreeItemFactory.create(
                "archive.tree.view.node.attributes",
                TreeContentView.ATTRIBUTES,
                type,
                String.valueOf(attributes.size()));

        attributes.forEach(attribute ->
                addAttribute(attributesItem, attribute)
        );
        typeItem.getChildren().add(attributesItem);
    }

    private void addAttribute(TreeItem<TreeAttributeWrapper> attributesItem, DatabaseAttribute attribute) {
        TreeItem<TreeAttributeWrapper> attributeItem = TreeItemFactory.create(attribute.name(),
                TreeContentView.ATTRIBUTE,
                attribute,
                "");
        attributesItem.getChildren().add(attributeItem);
    }

    private void addPriviliges(TreeItem<TreeAttributeWrapper> rootItem) {
        List<Privilige> priviliges = this.siardArchive.priviliges();
        if (priviliges.size() > 0) {
            rootItem.getChildren().add(TreeItemFactory.create("archive.tree.view.node.priviliges",
                    TreeContentView.PRIVILIGES,
                    new Privileges(priviliges),
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
        val form = RenderableForm.<SiardArchiveMetaData>builder()
                .dataExtractor(controller -> controller.getSiardArchive().getMetaData())
                .saveAction((controller, siardArchiveMetaData) -> {
                    val archive = controller.getSiardArchive().getArchive();
                    siardArchiveMetaData.write(archive);

                    // dirty hack: mark metadata as changed
                    CastHelper.tryCast(archive, ArchiveImpl.class)
                            .ifPresent(archiveImpl -> archiveImpl.isMetaDataDifferent(new Object(), new Object()));

//                    try {
//                        //archive.saveMetaData();
//                    } catch (IOException e) {
//                        throw new RuntimeException("Failed to store edited metadata", e);
//                    }
                })
                .group(RenderableFormGroup.<SiardArchiveMetaData>builder()
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelFormat"),
                                SiardArchiveMetaData::getSiardFormatVersion))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelDb"),
                                SiardArchiveMetaData::getDatabaseName,
                                SiardArchiveMetaData::setDatabaseName
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelProduct"),
                                SiardArchiveMetaData::getDatabaseProduct))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelConnection"),
                                SiardArchiveMetaData::getDatabaseConnectionURL))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelUsername"),
                                SiardArchiveMetaData::getDatabaseUsername))
                        .build())
                .group(RenderableFormGroup.<SiardArchiveMetaData>builder()
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelDesc"),
                                SiardArchiveMetaData::getDatabaseDescription,
                                SiardArchiveMetaData::setDatabaseDescription
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelOwner"),
                                SiardArchiveMetaData::getDataOwner,
                                SiardArchiveMetaData::setDataOwner
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelCreationDate"),
                                SiardArchiveMetaData::getDataOriginTimespan,
                                SiardArchiveMetaData::setDataOriginTimespan
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelArchiveDate"),
                                siardArchiveMetaData -> I18n.getLocaleDate(siardArchiveMetaData.getArchivingDate())
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelArchiveUser"),
                                SiardArchiveMetaData::getArchiverName,
                                SiardArchiveMetaData::setArchiverName
                        ))
                        .property(new ReadWriteStringProperty<>(
                                I18nKey.of("archiveDetails.labelContactArchiveUser"),
                                SiardArchiveMetaData::getArchiverContact,
                                SiardArchiveMetaData::setArchiverContact
                        ))
                        .property(new ReadOnlyStringProperty<>(
                                I18nKey.of("archiveDetails.labelLOBFolder"),
                                siardArchiveMetaData -> Optional.ofNullable(siardArchiveMetaData.getLobFolder())
                                        .map(URI::getPath)
                                        .orElse("N/A")
                        ))
                        .build())
                .build();

        val item = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(this.siardArchive.name())
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(form)
                        .build());

        return item;
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
