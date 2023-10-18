package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaParameter;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siard2.api.MetaView;
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
import ch.admin.bar.siardsuite.component.rendered.utils.ListAssembler;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.model.database.Privilige;
import ch.admin.bar.siardsuite.model.database.Routine;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.model.database.User;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKeyArg;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TreeBuilder {

    private static final I18nKey ROOT_ELEMENT_NAME = I18nKey.of("tableContainer.title.siardFile");

    private static final I18nKeyArg<Number> PRIVILEGES_ELEMENT_NAME = I18nKeyArg.of("archive.tree.view.node.priviliges");
    private static final I18nKey PRIVILEGES_VIEW_TITLE = I18nKey.of("tableContainer.title.priviliges");

    private static final I18nKeyArg<Number> USERS_ELEMENT_NAME = I18nKeyArg.of("archive.tree.view.node.users");
    private static final I18nKey USERS_VIEW_TITLE = I18nKey.of("tableContainer.title.users");

    private static final I18nKeyArg<Number> SCHEMAS_ELEMENT_NAME = I18nKeyArg.of("archive.tree.view.node.schemas");
    private static final I18nKey SCHEMAS_VIEW_TITLE = I18nKey.of("tableContainer.title.schemas");
    private static final I18nKey SCHEMA_VIEW_TITLE = I18nKey.of("tableContainer.title.schema");

    private static final I18nKeyArg<Number> TYPES_ELEMENT_NAME = I18nKeyArg.of("archive.tree.view.node.types");
    private static final I18nKey TYPES_VIEW_TITLE = I18nKey.of("treeContent.types.title");
    private static final I18nKey TYPE_VIEW_TITLE = I18nKey.of("tableContainer.title.type");

    private static final I18nKey ATTRIBUTE_VIEW_TITLE = I18nKey.of("tableContainer.title.attribute");

    private static final I18nKeyArg<Number> ROUTINES_ELEMENT_NAME = I18nKeyArg.of("archive.tree.view.node.routines");
    private static final I18nKey ROUTINES_VIEW_TITLE = I18nKey.of("tableContainer.title.routines");
    private static final I18nKey ROUTINE_VIEW_TITLE = I18nKey.of("tableContainer.title.routine");

    private static final I18nKeyArg<Number> PARAMETERS_ELEMENT_NAME = I18nKeyArg.of("archive.tree.view.node.parameters");
    private static final I18nKey PARAMETER_VIEW_TITLE = I18nKey.of("tableContainer.title.parameter");

    private static final I18nKeyArg<Number> VIEWS_ELEMENT_NAME = I18nKeyArg.of("archive.tree.view.node.views");
    private static final I18nKey VIEWS_VIEW_TITLE = I18nKey.of("tableContainer.title.views");
    private static final I18nKey VIEW_VIEW_TITLE = I18nKey.of("tableContainer.title.view");

    private static final I18nKeyArg<Number> COLUMNS_ELEMENT_NAME = I18nKeyArg.of("archive.tree.view.node.columns");
    private static final I18nKey COLUMNS_VIEW_TITLE = I18nKey.of("tableContainer.title.columns");
    private static final I18nKey COLUMN_VIEW_TITLE = I18nKey.of("tableContainer.title.column");

    private static final I18nKeyArg<Number> TABLES_ELEMENT_NAME = I18nKeyArg.of("archive.tree.view.node.tables");
    private static final I18nKey TABLES_VIEW_TITLE = I18nKey.of("tableContainer.title.tables");
    private static final I18nKey TABLE_VIEW_TITLE = I18nKey.of("tableContainer.title.table");

    private static final I18nKeyArg<Number> ROWS_ELEMENT_NAME = I18nKeyArg.of("archive.tree.view.node.rows");
    private static final I18nKey ROWS_VIEW_TITLE = I18nKey.of("tableContainer.title.data");

    private final SiardArchive siardArchive;
    private final boolean readonly;

    public TreeBuilder(SiardArchive siardArchive) {
        this.siardArchive = siardArchive;
        this.readonly = false;
    }

    public TreeItem<TreeAttributeWrapper> createRootItem() {
        val rootItem = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(DisplayableText.of(this.siardArchive.name()))
                        .viewTitle(DisplayableText.of(ROOT_ELEMENT_NAME))
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
                .name(DisplayableText.of(PRIVILEGES_ELEMENT_NAME, priviliges.size()))
                .viewTitle(DisplayableText.of(PRIVILEGES_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(PrivilegesOverviewForm.create(siardArchive).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForUsers(final List<User> users) {
        return new TreeItem<>(TreeAttributeWrapper.builder()
                .name(DisplayableText.of(USERS_ELEMENT_NAME, users.size()))
                .viewTitle(DisplayableText.of(USERS_VIEW_TITLE))
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
                        .name(DisplayableText.of(SCHEMAS_ELEMENT_NAME, schemas.size()))
                        .viewTitle(DisplayableText.of(SCHEMAS_VIEW_TITLE))
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
                        .name(DisplayableText.of(schema.name()))
                        .viewTitle(DisplayableText.of(SCHEMA_VIEW_TITLE))
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(SchemaOverviewForm.create(schema).toBuilder()
                                .readOnlyForm(readonly)
                                .build())
                        .build());

        schemaItem.setExpanded(true);

        val metaSchema = schema.getSchema().getMetaSchema();
        if (metaSchema.getMetaTypes() > 0) {
            schemaItem.getChildren().add(createItemForTypes(metaSchema));
        }

        if (!schema.getTables().isEmpty()) {
            schemaItem.getChildren().add(createItemForTables(schema));
        }

        if (!schema.getRoutines().isEmpty()) {
            schemaItem.getChildren().add(createItemForRoutines(schema));
        }

        if (metaSchema.getMetaViews() > 0) {
            schemaItem.getChildren().add(createItemForViews(metaSchema));
        }

        return schemaItem;
    }

    private TreeItem<TreeAttributeWrapper> createItemForTypes(final MetaSchema schema) {

        val types = new ListAssembler<>(schema.getMetaTypes(), schema::getMetaType)
                .assemble();

        val typesItem = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(DisplayableText.of(TYPES_ELEMENT_NAME, types.size()))
                .viewTitle(DisplayableText.of(TYPES_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TypesOverviewForm.create(schema, types).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val typeItems = types.stream()
                .map(this::createItemsForType)
                .collect(Collectors.toList());

        typesItem.getChildren().addAll(typeItems);

        return typesItem;
    }

    private TreeItem<TreeAttributeWrapper> createItemsForType(final MetaType type) {
        val attributes = new ListAssembler<>(type.getMetaAttributes(), type::getMetaAttribute).assemble();

        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(DisplayableText.of(type.getName()))
                .viewTitle(DisplayableText.of(TYPE_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TypeDetailsForm.create(type, attributes).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val attributeItems = attributes.stream()
                .map(this::createItemsForAttribute)
                .collect(Collectors.toList());

        item.getChildren().addAll(attributeItems);

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemsForAttribute(final MetaAttribute attribute) {
        return new TreeItem<>(TreeAttributeWrapper.builder()
                .name(DisplayableText.of(attribute.getName()))
                .viewTitle(DisplayableText.of(ATTRIBUTE_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(AttributeDetailsForm.create(attribute).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForRoutines(DatabaseSchema schema) {
        List<Routine> routines = schema.routines();

        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(DisplayableText.of(ROUTINES_ELEMENT_NAME, routines.size()))
                .viewTitle(DisplayableText.of(ROUTINES_VIEW_TITLE))
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
                .name(DisplayableText.of(routine.name()))
                .viewTitle(DisplayableText.of(ROUTINE_VIEW_TITLE))
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
                .name(DisplayableText.of(PARAMETERS_ELEMENT_NAME, parameters.size()))
                .viewTitle(DisplayableText.of(ROUTINE_VIEW_TITLE))
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
                .name(DisplayableText.of(parameter.getName()))
                .viewTitle(DisplayableText.of(PARAMETER_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ParameterOverviewForm.create(parameter).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForViews(MetaSchema schema) {
        val views = new ListAssembler<>(schema.getMetaViews(), schema::getMetaView).assemble();

        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(DisplayableText.of(VIEWS_ELEMENT_NAME, views.size()))
                .viewTitle(DisplayableText.of(VIEWS_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ViewsOverviewForm.create(schema, views).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val viewItems = views.stream()
                .map(this::createItemForView)
                .collect(Collectors.toList());

        item.getChildren().addAll(viewItems);

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemForView(MetaView view) {
        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(DisplayableText.of(view.getName()))
                .viewTitle(DisplayableText.of(VIEW_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ViewOverviewForm.create(view).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        item.getChildren().add(createItemForColumns(view));

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemForColumns(MetaView view) {
        val columns = new ListAssembler<>(view.getMetaColumns(), view::getMetaColumn).assemble();

        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(DisplayableText.of(COLUMNS_ELEMENT_NAME, columns.size()))
                .viewTitle(DisplayableText.of(COLUMNS_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ViewOverviewForm.create(view).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val columnItems = columns.stream()
                .map(this::createColumnItem)
                .collect(Collectors.toList());

        item.getChildren().addAll(columnItems);

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createColumnItem(MetaColumn column) {
        return new TreeItem<>(TreeAttributeWrapper.builder()
                .name(DisplayableText.of(column.getName()))
                .viewTitle(DisplayableText.of(COLUMN_VIEW_TITLE))
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
                .name(DisplayableText.of(TABLES_ELEMENT_NAME, tables.size()))
                .viewTitle(DisplayableText.of(TABLES_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(SchemaOverviewForm.create(schema).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
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
                .name(DisplayableText.of(table.name()))
                .viewTitle(DisplayableText.of(TABLE_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TableOverviewForm.create(table).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        if (!siardArchive.onlyMetaData()) {
            val rowsItem = new TreeItem<>(TreeAttributeWrapper.builder()
                    .name(DisplayableText.of(ROWS_ELEMENT_NAME, table.getTable().getMetaTable().getRows()))
                    .viewTitle(DisplayableText.of(ROWS_VIEW_TITLE))
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
        val metaTable = table.getTable().getMetaTable();
        val columns = new ListAssembler<>(metaTable.getMetaColumns(), metaTable::getMetaColumn).assemble();

        val columnsItem = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(DisplayableText.of(COLUMNS_ELEMENT_NAME, columns.size()))
                .viewTitle(DisplayableText.of(COLUMNS_VIEW_TITLE))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TableOverviewForm.create(table).toBuilder()
                        .readOnlyForm(readonly)
                        .build())
                .build());

        val columnItems = columns.stream()
                .map(this::createColumnItem)
                .collect(Collectors.toList());
        columnsItem.getChildren().addAll(columnItems);

        return columnsItem;
    }
}
