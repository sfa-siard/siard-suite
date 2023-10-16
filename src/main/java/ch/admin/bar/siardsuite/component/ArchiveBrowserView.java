package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siardsuite.component.rendered.AttributeDetailsForm;
import ch.admin.bar.siardsuite.component.rendered.ColumnDetailsForm;
import ch.admin.bar.siardsuite.component.rendered.MetadataDetailsForm;
import ch.admin.bar.siardsuite.component.rendered.ParameterOverviewForm;
import ch.admin.bar.siardsuite.component.rendered.PrivilegesOverviewForm;
import ch.admin.bar.siardsuite.component.rendered.RoutineOverviewForm;
import ch.admin.bar.siardsuite.component.rendered.RoutinesOverviewForm;
import ch.admin.bar.siardsuite.component.rendered.RowsOverviewForm;
import ch.admin.bar.siardsuite.component.rendered.SchemaOverviewForm;
import ch.admin.bar.siardsuite.component.rendered.TableOverviewForm;
import ch.admin.bar.siardsuite.component.rendered.TypeDetailsForm;
import ch.admin.bar.siardsuite.component.rendered.TypesOverviewForm;
import ch.admin.bar.siardsuite.component.rendered.UsersOverviewForm;
import ch.admin.bar.siardsuite.component.rendered.ViewOverviewForm;
import ch.admin.bar.siardsuite.component.rendered.ViewsOverviewForm;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.DatabaseAttribute;
import ch.admin.bar.siardsuite.model.database.DatabaseColumn;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.model.database.DatabaseType;
import ch.admin.bar.siardsuite.model.database.DatabaseView;
import ch.admin.bar.siardsuite.model.database.Privilige;
import ch.admin.bar.siardsuite.model.database.Routine;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.model.database.User;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.I18nKey;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ArchiveBrowserView {

    private static final I18nKey ROOT_ELEMENT_NAME = I18nKey.of("tableContainer.title.siardFile");

    private static final I18nKey PRIVILEGES_ELEMENT_NAME = I18nKey.of("archive.tree.view.node.priviliges");
    private static final I18nKey PRIVILEGES_VIEW_TITLE = I18nKey.of("tableContainer.title.priviliges");

    private static final I18nKey USERS_ELEMENT_NAME = I18nKey.of("archive.tree.view.node.users");
    private static final I18nKey USERS_VIEW_TITLE = I18nKey.of("tableContainer.title.users");

    private static final I18nKey SCHEMAS_ELEMENT_NAME = I18nKey.of("archive.tree.view.node.schemas");
    private static final I18nKey SCHEMAS_VIEW_TITLE = I18nKey.of("tableContainer.title.schemas");
    private static final I18nKey SCHEMA_VIEW_TITLE = I18nKey.of("tableContainer.title.schema");

    private static final I18nKey TYPES_ELEMENT_NAME = I18nKey.of("archive.tree.view.node.types");
    private static final I18nKey TYPES_VIEW_TITLE = I18nKey.of("treeContent.types.title");
    private static final I18nKey TYPE_VIEW_TITLE = I18nKey.of("tableContainer.title.type");

    private static final I18nKey ATTRIBUTE_VIEW_TITLE = I18nKey.of("tableContainer.title.attribute");

    private static final I18nKey ROUTINES_ELEMENT_NAME = I18nKey.of("archive.tree.view.node.routines");
    private static final I18nKey ROUTINES_VIEW_TITLE = I18nKey.of("tableContainer.title.routines");
    private static final I18nKey ROUTINE_VIEW_TITLE = I18nKey.of("tableContainer.title.routine");

    private static final I18nKey PARAMETERS_ELEMENT_NAME = I18nKey.of("archive.tree.view.node.parameters");
    private static final I18nKey PARAMETER_VIEW_TITLE = I18nKey.of("tableContainer.title.parameter");

    private static final I18nKey VIEWS_ELEMENT_NAME = I18nKey.of("archive.tree.view.node.views");
    private static final I18nKey VIEWS_VIEW_TITLE = I18nKey.of("tableContainer.title.views");
    private static final I18nKey VIEW_VIEW_TITLE = I18nKey.of("tableContainer.title.view");

    private static final I18nKey COLUMNS_ELEMENT_NAME = I18nKey.of("archive.tree.view.node.columns");
    private static final I18nKey COLUMNS_VIEW_TITLE = I18nKey.of("tableContainer.title.columns");
    private static final I18nKey COLUMN_VIEW_TITLE = I18nKey.of("tableContainer.title.column");

    private static final I18nKey TABLES_ELEMENT_NAME = I18nKey.of("archive.tree.view.node.tables");
    private static final I18nKey TABLES_VIEW_TITLE = I18nKey.of("tableContainer.title.tables");
    private static final I18nKey TABLE_VIEW_TITLE = I18nKey.of("tableContainer.title.table");

    private static final I18nKey ROWS_ELEMENT_NAME = I18nKey.of("archive.tree.view.node.rows");
    private static final I18nKey ROWS_VIEW_TITLE = I18nKey.of("tableContainer.title.data");

    private final SiardArchive siardArchive;
    private final boolean readonly;

    public ArchiveBrowserView(SiardArchive siardArchive) {
        this.siardArchive = siardArchive;
        this.readonly = false;
    }

    public TreeItem<TreeAttributeWrapper> createRootItem() {
        val rootItem = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(this.siardArchive.name())
                        .viewTitle(ROOT_ELEMENT_NAME)
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(MetadataDetailsForm.create(siardArchive)
                                .toBuilder()
                                .readOnlyForm(readonly)
                                .build())
                        .build(),
                new ImageView(Icon.db));

        rootItem.setExpanded(true);
        rootItem.getChildren().add(createItemForSchemas());

        if (!siardArchive.users().isEmpty()) {
            rootItem.getChildren().add(createItemForUsers(siardArchive.users()));
        }

        if (!siardArchive.priviliges().isEmpty()) {
            rootItem.getChildren().add(createItemForPrivileges(siardArchive.priviliges()));
        }

        return rootItem;
    }

    private TreeItem<TreeAttributeWrapper> createItemForPrivileges(List<Privilige> priviliges) {
        return new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(PRIVILEGES_ELEMENT_NAME, priviliges.size()))
                .viewTitle(PRIVILEGES_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(PrivilegesOverviewForm.create(siardArchive).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForUsers(final List<User> users) {
        return new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(USERS_ELEMENT_NAME, users.size()))
                .viewTitle(USERS_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(UsersOverviewForm.create(siardArchive).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForSchemas() {
        List<DatabaseSchema> schemas = this.siardArchive.schemas();

        val schemasItem = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(I18n.get(SCHEMAS_ELEMENT_NAME, schemas.size()))
                        .viewTitle(SCHEMAS_VIEW_TITLE)
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(MetadataDetailsForm.create(siardArchive).toBuilder()
                                .readOnlyForm(readonly)
                                .build())
                        .build());

        val schemaItems = schemas.stream()
                .map(this::createItemsForSchema)
                .collect(Collectors.toList());
        schemasItem.getChildren().setAll(schemaItems);

        return schemasItem;
    }

    private TreeItem<TreeAttributeWrapper> createItemsForSchema(DatabaseSchema schema) {
        val schemaItem = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(schema.name())
                        .viewTitle(SCHEMA_VIEW_TITLE)
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(SchemaOverviewForm.create(schema).toBuilder()
                                .readOnlyForm(readonly)
                                .build())
                        .databaseObject(schema)
                        .build());

        schemaItem.setExpanded(true);

        if (!schema.getTypes().isEmpty()) {
            schemaItem.getChildren().add(createItemForTypes(schema));
        }

        if (!schema.getTables().isEmpty()) {
            schemaItem.getChildren().add(createItemForTables(schema));
        }

        if (!schema.getRoutines().isEmpty()) {
            schemaItem.getChildren().add(createItemForRoutines(schema));
        }

        if (!schema.getViews().isEmpty()) {
            schemaItem.getChildren().add(createItemForViews(schema));
        }

        return schemaItem;
    }

    private TreeItem<TreeAttributeWrapper> createItemForTypes(final DatabaseSchema schema) {
        val types = schema.types();

        val typesItem = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(TYPES_ELEMENT_NAME, types.size()))
                .viewTitle(TYPES_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TypesOverviewForm.create(schema).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val typeItems = types.stream()
                .map(this::createItemsForType)
                .collect(Collectors.toList());

        typesItem.getChildren().addAll(typeItems);

        return typesItem;
    }

    private TreeItem<TreeAttributeWrapper> createItemsForType(final DatabaseType type) {
        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(type.name())
                .viewTitle(TYPE_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TypeDetailsForm.create(type).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val attributeItems = type.attributes().stream()
                .map(this::createItemsForAttribute)
                .collect(Collectors.toList());

        item.getChildren().addAll(attributeItems);

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemsForAttribute(final DatabaseAttribute attribute) {
        return new TreeItem<>(TreeAttributeWrapper.builder()
                .name(attribute.getName())
                .viewTitle(ATTRIBUTE_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(AttributeDetailsForm.create(attribute).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForRoutines(DatabaseSchema schema) {
        List<Routine> routines = schema.routines();

        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(ROUTINES_ELEMENT_NAME, routines.size()))
                .viewTitle(ROUTINES_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(RoutinesOverviewForm.create(schema).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val routineItems = routines.stream()
                .map(this::createItemsForRoutine)
                .collect(Collectors.toList());

        item.getChildren().addAll(routineItems);

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemsForRoutine(final Routine routine) {
        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(routine.name())
                .viewTitle(ROUTINE_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(RoutineOverviewForm.create(routine).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        item.getChildren().add(createItemForParameters(routine));

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemForParameters(final Routine routine) {
        val parameters = routine.parameters();
        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(PARAMETERS_ELEMENT_NAME, parameters.size()))
                .viewTitle(ROUTINE_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(RoutineOverviewForm.create(routine).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val parameterItems = parameters.stream()
                .map(this::createItemsForParameter)
                .collect(Collectors.toList());

        item.getChildren().addAll(parameterItems);

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemsForParameter(final MetaParameter parameter) {
        return new TreeItem<>(TreeAttributeWrapper.builder()
                .name(parameter.getName())
                .viewTitle(PARAMETER_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ParameterOverviewForm.create(parameter).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForViews(DatabaseSchema schema) {
        List<DatabaseView> views = schema.views();
        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(VIEWS_ELEMENT_NAME, views.size()))
                .viewTitle(VIEWS_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ViewsOverviewForm.create(schema).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val viewItems = views.stream()
                .map(this::createItemForView)
                .collect(Collectors.toList());

        item.getChildren().addAll(viewItems);

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemForView(DatabaseView view) {
        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(view.name())
                .viewTitle(VIEW_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ViewOverviewForm.create(view.getMetaView()).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        item.getChildren().add(createItemForColumns(view));

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemForColumns(DatabaseView view) {
        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(COLUMNS_ELEMENT_NAME, view.columns().size()))
                .viewTitle(COLUMNS_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ViewOverviewForm.create(view.getMetaView()).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val columnItems = view.columns().stream()
                .map(this::createColumnItem)
                .collect(Collectors.toList());

        item.getChildren().addAll(columnItems);

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createColumnItem(DatabaseColumn column) {
        return new TreeItem<>(TreeAttributeWrapper.builder()
                .name(column.name())
                .viewTitle(COLUMN_VIEW_TITLE)
                /*
                Caution: Mockups are showing different style for column details forms.
                For achieving that, a separate value renderer needs to be developed.

                TODO: Clarify requirements
                 */
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ColumnDetailsForm.create(column).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForTables(DatabaseSchema schema) {

        List<DatabaseTable> tables = schema.tables();

        val tablesItem = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(TABLES_ELEMENT_NAME, tables.size()))
                .viewTitle(TABLES_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(SchemaOverviewForm.create(schema).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .databaseObject(schema)
                .build());

        tablesItem.getChildren()
                .addAll(tables.stream()
                        .map(this::createItemForTable)
                        .collect(Collectors.toList()));

        tablesItem.setExpanded(true);

        return tablesItem;
    }

    private TreeItem<TreeAttributeWrapper> createItemForTable(DatabaseTable table) {
        val tableItem = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(table.name())
                .viewTitle(TABLE_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TableOverviewForm.create(table).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .databaseObject(table)
                .build());

        if (!siardArchive.onlyMetaData()) {
            val rowsItem = new TreeItem<>(TreeAttributeWrapper.builder()
                    .name(I18n.get(ROWS_ELEMENT_NAME, table.getTable().getMetaTable().getRows()))
                    .viewTitle(ROWS_VIEW_TITLE)
                    .type(TreeContentView.FORM_RENDERER)
                    .renderableForm(RowsOverviewForm.create(table)
                            .toBuilder()
                            .readOnlyForm(readonly)
                            .build())
                    .build());

            tableItem.getChildren().add(rowsItem);
        }

        tableItem.getChildren().add(createColumnItem(table));

        return tableItem;
    }

    private TreeItem<TreeAttributeWrapper> createColumnItem(DatabaseTable table) {
        List<DatabaseColumn> columns = table.columns();

        val columnsItem = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(COLUMNS_ELEMENT_NAME, columns.size()))
                .viewTitle(COLUMNS_VIEW_TITLE)
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TableOverviewForm.create(table).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .databaseObject(table)
                .build());

        val columnItems = columns.stream()
                .map(this::createColumnItem)
                .collect(Collectors.toList());
        columnsItem.getChildren().addAll(columnItems);

        return columnsItem;
    }
}
