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
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ArchiveBrowserView {

    private final SiardArchive siardArchive;

    public TreeItem<TreeAttributeWrapper> createRootItem() {
        val rootItem = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(this.siardArchive.name())
                        .viewTitle(I18nKey.of("tableContainer.title.siardFile"))
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(MetadataDetailsForm.create())
                        .build());

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
                .name(I18n.get(I18nKey.of("archive.tree.view.node.priviliges"), priviliges.size()))
                .viewTitle(I18nKey.of("tableContainer.title.priviliges"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(PrivilegesOverviewForm.create())
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForUsers(final List<User> users) {
        return new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(I18nKey.of("archive.tree.view.node.users"), users.size()))
                .viewTitle(I18nKey.of("tableContainer.title.users"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(UsersOverviewForm.create())
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForSchemas() {
        List<DatabaseSchema> schemas = this.siardArchive.schemas();

        val schemasItem = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(I18n.get(I18nKey.of("archive.tree.view.node.schemas"), schemas.size()))
                        .viewTitle(I18nKey.of("tableContainer.title.schemas"))
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(MetadataDetailsForm.create())
                        .build());

        val schemaItems = schemas.stream()
                .map(this::createItemsForSchema)
                .collect(Collectors.toList());
        schemasItem.getChildren().setAll(schemaItems);

        return schemasItem;
    }

    private void addSchemas(TreeItem<TreeAttributeWrapper> rootItem) {
        List<DatabaseSchema> schemas = this.siardArchive.schemas();

        val schemasItem = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(I18n.get(I18nKey.of("archive.tree.view.node.schemas"), schemas.size()))
                        .viewTitle(I18nKey.of("tableContainer.title.schemas"))
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(MetadataDetailsForm.create())
                        .build());
        schemasItem.setExpanded(true);

        schemas.forEach(schema -> addSchema(schemasItem, schema));

        rootItem.getChildren().add(schemasItem);
    }

    private TreeItem<TreeAttributeWrapper> createItemsForSchema(DatabaseSchema schema) {
        val schemaItem = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(schema.name())
                        .viewTitle(I18nKey.of("tableContainer.title.schema"))
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(SchemaOverviewForm.create(schema))
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

    private void addSchema(TreeItem<TreeAttributeWrapper> schemasItem, DatabaseSchema schema) {
        val schemaItem = new TreeItem<>(
                TreeAttributeWrapper.builder()
                        .name(schema.name())
                        .viewTitle(I18nKey.of("tableContainer.title.schema"))
                        .type(TreeContentView.FORM_RENDERER)
                        .renderableForm(SchemaOverviewForm.create(schema))
                        .databaseObject(schema)
                        .build());

        schemaItem.setExpanded(true);

        addTypes(schemaItem, schema);
        addIfNotEmpty(schemaItem, createItemForTables(schema));
        addIfNotEmpty(schemaItem, createItemForRoutines(schema));
        addIfNotEmpty(schemaItem, createItemForViews(schema));
        schemasItem.getChildren().add(schemaItem);
    }

    private TreeItem<TreeAttributeWrapper> createItemForTypes(final DatabaseSchema schema) {
        val types = schema.types();

        val typesItem = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(I18nKey.of("archive.tree.view.node.types"), types.size()))
                .viewTitle(I18nKey.of("treeContent.types.title"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TypesOverviewForm.create(schema))
                .build());

        val typeItems = types.stream()
                .map(this::createItemsForType)
                .collect(Collectors.toList());

        typesItem.getChildren().addAll(typeItems);

        return typesItem;
    }

    private void addTypes(TreeItem<TreeAttributeWrapper> schemaItem, DatabaseSchema schema) {
        List<DatabaseType> types = schema.types();
        if (!types.isEmpty()) {
            val typesItem = new TreeItem<>(TreeAttributeWrapper.builder()
                    .name(I18n.get(I18nKey.of("archive.tree.view.node.types"), types.size()))
                    .viewTitle(I18nKey.of("treeContent.types.title"))
                    .type(TreeContentView.FORM_RENDERER)
                    .renderableForm(TypesOverviewForm.create(schema))
                    .build());

            val typeItems = types.stream()
                    .map(this::createItemsForType)
                    .collect(Collectors.toList());

            typesItem.getChildren().addAll(typeItems);
            schemaItem.getChildren().add(typesItem);
        }
    }

    private TreeItem<TreeAttributeWrapper> createItemsForType(final DatabaseType type) {
        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(type.name())
                .viewTitle(I18nKey.of("tableContainer.title.type"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TypeDetailsForm.create(type))
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
                .viewTitle(I18nKey.of("tableContainer.title.attribute"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(AttributeDetailsForm.create(attribute))
                .build());
    }

    private void addPriviliges(TreeItem<TreeAttributeWrapper> rootItem) {
        List<Privilige> priviliges = this.siardArchive.priviliges();
        if (!priviliges.isEmpty()) {
            rootItem.getChildren().add(new TreeItem<>(TreeAttributeWrapper.builder()
                    .name(I18n.get(I18nKey.of("archive.tree.view.node.priviliges"), priviliges.size()))
                    .viewTitle(I18nKey.of("tableContainer.title.priviliges"))
                    .type(TreeContentView.FORM_RENDERER)
                    .renderableForm(PrivilegesOverviewForm.create())
                    .build()));
        }
    }

    private void addUsers(TreeItem<TreeAttributeWrapper> rootItem) {
        List<User> users = this.siardArchive.users();
        if (!users.isEmpty()) {
            rootItem.getChildren().add(new TreeItem<>(TreeAttributeWrapper.builder()
                    .name(I18n.get(I18nKey.of("archive.tree.view.node.users"), users.size()))
                    .viewTitle(I18nKey.of("tableContainer.title.users"))
                    .type(TreeContentView.FORM_RENDERER)
                    .renderableForm(UsersOverviewForm.create())
                    .build()));
        }
    }


    private TreeItem<TreeAttributeWrapper> createItemForRoutines(DatabaseSchema schema) {
        List<Routine> routines = schema.routines();

        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(I18nKey.of("archive.tree.view.node.routines"), routines.size()))
                .viewTitle(I18nKey.of("tableContainer.title.routines"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(RoutinesOverviewForm.create(schema))
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
                .viewTitle(I18nKey.of("tableContainer.title.routine"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(RoutineOverviewForm.create(routine))
                .build());

        item.getChildren().add(createItemForParameters(routine));

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemForParameters(final Routine routine) {
        val parameters = routine.parameters();
        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(I18nKey.of("archive.tree.view.node.parameters"), parameters.size()))
                .viewTitle(I18nKey.of("tableContainer.title.parameter"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(RoutineOverviewForm.create(routine))
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
                .viewTitle(I18nKey.of("tableContainer.title.parameters"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ParameterOverviewForm.create(parameter))
                .build());
    }

    private TreeItem<TreeAttributeWrapper> createItemForViews(DatabaseSchema schema) {
        List<DatabaseView> views = schema.views();
        val item = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(I18nKey.of("archive.tree.view.node.views"), views.size()))
                .viewTitle(I18nKey.of("tableContainer.title.views"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ViewsOverviewForm.create(schema))
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
                .viewTitle(I18nKey.of("tableContainer.title.view"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ViewOverviewForm.create(view.getMetaView()))
                .build());

        val columnsItem = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(I18nKey.of("archive.tree.view.node.columns"), view.columns().size()))
                .viewTitle(I18nKey.of("tableContainer.title.columns"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ViewOverviewForm.create(view.getMetaView()))
                .build());

        val columnItems = view.columns().stream()
                .map(this::createColumnItem)
                .collect(Collectors.toList());

        columnsItem.getChildren().addAll(columnItems);
        item.getChildren().add(columnsItem);

        return item;
    }

    private TreeItem<TreeAttributeWrapper> createItemForTables(DatabaseSchema schema) {

        List<DatabaseTable> tables = schema.tables();

        val tablesItem = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(I18nKey.of("archive.tree.view.node.tables"), tables.size()))
                .viewTitle(I18nKey.of("tableContainer.title.tables"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(SchemaOverviewForm.create(schema))
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
                .viewTitle(I18nKey.of("tableContainer.title.table"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TableOverviewForm.create(table))
                .databaseObject(table)
                .build());

        if (!siardArchive.onlyMetaData()) {
            val rowsItem = new TreeItem<>(TreeAttributeWrapper.builder()
                    .name(I18n.get(I18nKey.of("archive.tree.view.node.rows"), table.getTable().getMetaTable().getRows()))
                    .viewTitle(I18nKey.of("tableContainer.title.data"))
                    .type(TreeContentView.FORM_RENDERER)
                    .renderableForm(RowsOverviewForm.create(table))
                    .build());

            tableItem.getChildren().add(rowsItem);
        }

        tableItem.getChildren().add(createColumnItems(table));

        return tableItem;
    }

    private TreeItem<TreeAttributeWrapper> createColumnItems(DatabaseTable table) {
        List<DatabaseColumn> columns = table.columns();

        val columnsItem = new TreeItem<>(TreeAttributeWrapper.builder()
                .name(I18n.get(I18nKey.of("archive.tree.view.node.columns"), columns.size()))
                .viewTitle(I18nKey.of("tableContainer.title.columns"))
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(TableOverviewForm.create(table))
                .databaseObject(table)
                .build());

        val columnItems = columns.stream()
                .map(this::createColumnItem)
                .collect(Collectors.toList());
        columnsItem.getChildren().addAll(columnItems);

        return columnsItem;
    }

    private TreeItem<TreeAttributeWrapper> createColumnItem(DatabaseColumn column) {
        return new TreeItem<>(TreeAttributeWrapper.builder()
                .name(column.name())
                .viewTitle(I18nKey.of("tableContainer.title.column"))
                /*
                Caution: Mockups are showing different style for column details forms.
                For achieving that, a separate value renderer needs to be developed.

                TODO: Clarify requirements
                 */
                .type(TreeContentView.FORM_RENDERER)
                .renderableForm(ColumnDetailsForm.create(column))
                .build());
    }

    private void addIfNotEmpty(TreeItem<TreeAttributeWrapper> rootItem,
                               TreeItem<TreeAttributeWrapper> item) {
        if (item.getChildren().size() == 0) return;
        rootItem.getChildren().add(item);
    }
}
