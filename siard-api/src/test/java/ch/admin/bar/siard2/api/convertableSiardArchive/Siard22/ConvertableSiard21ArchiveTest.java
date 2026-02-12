package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.convertableSiardArchive.Siard21.ConvertableSiard21Archive;
import ch.admin.bar.siard2.api.generated.SiardArchive;
import ch.admin.bar.siard2.api.generated.old21.*;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;

public class ConvertableSiard21ArchiveTest {


    @Test
    public void shouldConvertSiardArchive21ToSiardArchive22() {
        // given
        Siard21ToSiard22Transformer visitor = new Siard21ToSiard22Transformer();
        ConvertableSiard21Archive convertableSiard21Archive = createExampleArchiveWithAllFieldsSet();

        // when
        SiardArchive result = convertableSiard21Archive.accept(visitor);

        // then
        assertSiardArchiveWithAllFieldsSet(result);
    }

    @Test
    public void shouldConvertMinimalSiardArchive21ToSiardArchive22() {
        // given
        ConvertableSiard21Archive convertableSiard21Archive = createMinimalArchive();

        // when
        SiardArchive result = convertableSiard21Archive.accept(new Siard21ToSiard22Transformer());

        // then
        assertNull(result.getSchemas());
        assertNull(result.getPrivileges());
        assertNull(result.getRoles());
        assertNull(result.getUsers());
    }


    private ConvertableSiard21Archive createMinimalArchive() {
        ConvertableSiard21Archive archive = new ConvertableSiard21Archive();
        archive.setDbname(DB_NAME);
        archive.setDescription(DESCRIPTION);
        archive.setArchiver(ARCHIVER);
        archive.setArchiverContact(ARCHIVER_CONTACT);
        archive.setDataOwner(DATA_OWNER);
        archive.setDataOriginTimespan(DATA_ORIGIN_TIMESPAN);
        archive.setLobFolder(LOB_FOLDER);
        archive.setProducerApplication(PRODUCER_APPLICATION);
        archive.setArchivalDate(ARCHIVAL_DATE);
        archive.getMessageDigest()
                .add(createMessageDigests());
        archive.setClientMachine(CLIENT_MACHINE);
        archive.setDatabaseProduct(DATABASE_PRODUCT);
        archive.setConnection(CONNECTION);
        archive.setDatabaseUser(DATABASE_USER);
        return archive;

    }

    private void assertSiardArchiveWithAllFieldsSet(SiardArchive result) {
        assertNotNull(result);
        assertEquals("2.2", result.getVersion());
        assertEquals(DB_NAME, result.getDbname());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(ARCHIVER, result.getArchiver());
        assertEquals(ARCHIVER_CONTACT, result.getArchiverContact());
        assertEquals(DATA_OWNER, result.getDataOwner());
        assertEquals(DATA_ORIGIN_TIMESPAN, result.getDataOriginTimespan());
        assertEquals(LOB_FOLDER, result.getLobFolder());
        assertEquals(PRODUCER_APPLICATION, result.getProducerApplication());
        assertEquals(ARCHIVAL_DATE, result.getArchivalDate());
        assertMessageDigests(result);
        assertEquals(CLIENT_MACHINE, result.getClientMachine());
        assertEquals(DATABASE_PRODUCT, result.getDatabaseProduct());
        assertEquals(CONNECTION, result.getConnection());
        assertEquals(DATABASE_USER, result.getDatabaseUser());
        assertUsers(result.getUsers());
        assertSchemas(result.getSchemas());
        assertRoles(result.getRoles());
        assertPriviliges(result.getPrivileges());
    }

    private ConvertableSiard21Archive createExampleArchiveWithAllFieldsSet() {
        ConvertableSiard21Archive archive = createMinimalArchive();
        archive.setSchemas(createSchemas());
        archive.setUsers(createUsers());
        archive.setRoles(createRoles());
        archive.setPrivileges(createPriviliges());
        return archive;
    }

    private void assertPriviliges(ch.admin.bar.siard2.api.generated.PrivilegesType privileges) {
        assertNotNull(privileges);
        ch.admin.bar.siard2.api.generated.PrivilegeType privilige = privileges.getPrivilege()
                .get(0);
        assertEquals(PRIVILIGE_TYPE, privilige.getType());
        assertEquals(PRIVILIGE_DESCRIPTION, privilige.getDescription());
        assertEquals(PRIVILIGE_GRANTEE, privilige.getGrantee());
        assertEquals(PRIVILIGE_GRANTOR, privilige.getGrantor());
        assertEquals(PRIVILIGE_OBJECT, privilige.getObject());
        assertEquals(ch.admin.bar.siard2.api.generated.PrivOptionType.ADMIN, privilige.getOption());
    }

    private PrivilegesType createPriviliges() {
        PrivilegesType privilegesType = new PrivilegesType();
        PrivilegeType privilige = new PrivilegeType();
        privilige.setType(PRIVILIGE_TYPE);
        privilige.setDescription(PRIVILIGE_DESCRIPTION);
        privilige.setGrantee(PRIVILIGE_GRANTEE);
        privilige.setGrantor(PRIVILIGE_GRANTOR);
        privilige.setObject(PRIVILIGE_OBJECT);
        privilige.setOption(PRIVILIGE_OPTION);

        privilegesType.getPrivilege()
                .add(privilige);
        return privilegesType;
    }


    private void assertUsers(ch.admin.bar.siard2.api.generated.UsersType users) {
        assertNotNull(users);
        assertEquals(1, users.getUser()
                .size());
        ch.admin.bar.siard2.api.generated.UserType user = users.getUser()
                .get(0);
        assertEquals(USER_NAME, user.getName());
        assertEquals(USER_DESCRIPTION, user.getDescription());
    }

    private UsersType createUsers() {
        UsersType usersType = new UsersType();
        UserType user = new UserType();
        user.setName(USER_NAME);
        user.setDescription(USER_DESCRIPTION);
        usersType.getUser()
                .add(user);
        return usersType;
    }

    private RolesType createRoles() {
        RolesType roles = new RolesType();
        RoleType role = new RoleType();
        role.setName(ROLE_NAME);
        role.setDescription(ROLE_DESCRIPTION);
        role.setAdmin(ROLE_ADMIN);
        roles.getRole()
                .add(role);
        return roles;
    }

    private void assertRoles(ch.admin.bar.siard2.api.generated.RolesType roles) {
        assertNotNull(roles);
        ch.admin.bar.siard2.api.generated.RoleType role = roles.getRole()
                .get(0);
        assertEquals(ROLE_NAME, role.getName());
        assertEquals(ROLE_DESCRIPTION, role.getDescription());
        assertEquals(ROLE_ADMIN, role.getAdmin());
    }


    private void assertMessageDigests(SiardArchive result) {
        ch.admin.bar.siard2.api.generated.MessageDigestType actualMessageDigest = result.getMessageDigest()
                .get(0);
        assertEquals(MESSAGE_DIGEST, actualMessageDigest.getDigest());
        assertEquals(ch.admin.bar.siard2.api.generated.DigestTypeType.SHA_256, actualMessageDigest.getDigestType());
    }

    private MessageDigestType createMessageDigests() {
        MessageDigestType messageDigests = new MessageDigestType();
        messageDigests.setDigest(MESSAGE_DIGEST);
        messageDigests.setDigestType(DigestTypeType.SHA_256);
        return messageDigests;
    }

    private void assertSchemas(ch.admin.bar.siard2.api.generated.SchemasType schemas) {
        assertNotNull(schemas);
        assertEquals(schemas.getSchema()
                .size(), 1);
        ch.admin.bar.siard2.api.generated.SchemaType schema = schemas.getSchema()
                .get(0);
        assertNotNull(schema);
        assertEquals(schema.getName(), SCHEMA_NAME);
        assertEquals(schema.getDescription(), SCHEMA_DESCRIPTION);
        assertEquals(schema.getFolder(), SCHEMA_FOLDER);
        assertTypes(schema.getTypes());
        assertRoutines(schema.getRoutines());
        assertTables(schema.getTables());
        assertViews(schema.getViews());
    }

    private SchemasType createSchemas() {
        SchemasType schemas = new SchemasType();
        SchemaType schema = new SchemaType();
        schema.setName(SCHEMA_NAME);
        schema.setDescription(SCHEMA_DESCRIPTION);
        schema.setFolder(SCHEMA_FOLDER);
        schema.setTypes(createTypes());
        schema.setRoutines(createRoutines());
        schema.setViews(createViews());
        schema.setTables(createTables());
        schemas.getSchema()
                .add(schema);
        return schemas;
    }

    private void assertViews(ch.admin.bar.siard2.api.generated.ViewsType views) {
        assertNotNull(views);
        assertEquals(1, views.getView()
                .size());
        ch.admin.bar.siard2.api.generated.ViewType view = views.getView()
                .get(0);

        assertEquals(VIEW_NAME, view.getName());
        assertEquals(VIEW_DESCRIPTION, view.getDescription());
        assertEquals(VIEW_ROWS, view.getRows());
        assertEquals(VIEW_QUERY, view.getQuery());
        assertEquals(VIEW_QUERY_ORIGINAL, view.getQueryOriginal());

        assertViewColumns(view.getColumns());
    }

    private ViewsType createViews() {
        ViewsType views = new ViewsType();
        ViewType view = new ViewType();
        view.setName(VIEW_NAME);
        view.setDescription(VIEW_DESCRIPTION);
        view.setRows(VIEW_ROWS);
        view.setQuery(VIEW_QUERY);
        view.setQueryOriginal(VIEW_QUERY_ORIGINAL);

        view.setColumns(createColumns());
        views.getView()
                .add(view);
        return views;
    }

    private void assertViewColumns(ch.admin.bar.siard2.api.generated.ColumnsType columns) {
        assertNotNull(columns);
        assertEquals(1, columns.getColumn()
                .size());
        ch.admin.bar.siard2.api.generated.ColumnType column = columns.getColumn()
                .get(0);
        assertEquals(COLUMN_NAME, column.getName());
        assertEquals(COLUMN_DESCRIPTION, column.getDescription());
        assertEquals(COLUMN_DEFAULT_VALUE, column.getDefaultValue());
        assertEquals(COLUMN_LOG_FOLDER, column.getLobFolder());
        assertEquals(COLUMN_MIME_TYPE, column.getMimeType());
        assertEquals(COLUMN_TYPE, column.getType());
        assertEquals(COLUMN_TYPE_NAME, column.getTypeName());
        assertEquals(COLUMN_TYPE_ORIGINAL, column.getTypeOriginal());
        assertEquals(COLUMN_TYPE_SCHEMA, column.getTypeSchema());
        assertEquals(COLUMN_TYPE_CARDINALITY, column.getCardinality());
        assertEquals(COLUMN_IS_NULLABLE, column.isNullable());
        assertFields(column.getFields());
    }


    private ColumnsType createColumns() {
        ColumnsType columns = new ColumnsType();
        ColumnType column = new ColumnType();
        column.setName(COLUMN_NAME);
        column.setDescription(COLUMN_DESCRIPTION);
        column.setDefaultValue(COLUMN_DEFAULT_VALUE);
        column.setLobFolder(COLUMN_LOG_FOLDER);
        column.setMimeType(COLUMN_MIME_TYPE);
        column.setType(COLUMN_TYPE);
        column.setTypeName(COLUMN_TYPE_NAME);
        column.setTypeSchema(COLUMN_TYPE_SCHEMA);
        column.setTypeOriginal(COLUMN_TYPE_ORIGINAL);
        column.setCardinality(COLUMN_TYPE_CARDINALITY);
        column.setNullable(COLUMN_IS_NULLABLE);
        column.setFields(createFields());
        columns.getColumn()
                .add(column);
        return columns;
    }

    private void assertFields(ch.admin.bar.siard2.api.generated.FieldsType fields) {
        assertNotNull(fields);
        assertEquals(1, fields.getField()
                .size());
        ch.admin.bar.siard2.api.generated.FieldType field = fields.getField()
                .get(0);
        assertEquals(FIELD_NAME, field.getName());
        assertEquals(FIELD_DESCRIPTION, field.getDescription());
        assertEquals(FIELD_LOB_FOLDER, field.getLobFolder());
        assertEquals(FIELD_MIME_TYPE, field.getMimeType());

        assertSubFields(field.getFields());
    }


    private FieldsType createFields() {
        FieldsType fields = new FieldsType();
        FieldType field = new FieldType();
        field.setName(FIELD_NAME);
        field.setDescription(FIELD_DESCRIPTION);
        field.setLobFolder(FIELD_LOB_FOLDER);
        field.setMimeType(FIELD_MIME_TYPE);
        field.setFields(createSubFields());
        fields.getField()
                .add(field);
        return fields;
    }

    private void assertSubFields(ch.admin.bar.siard2.api.generated.FieldsType fields) {
        assertNotNull(fields);
        assertEquals(1, fields.getField()
                .size());
        ch.admin.bar.siard2.api.generated.FieldType field = fields.getField()
                .get(0);
        assertEquals(SUB_FIELD_NAME, field.getName());
        assertEquals(SUB_FIELD_DESCRIPTION, field.getDescription());
        assertEquals(SUB_FIELD_LOB_FOLDER, field.getLobFolder());
        assertEquals(SUB_FIELD_MIME_TYPE, field.getMimeType());

        assertNull(field.getFields());
    }

    private FieldsType createSubFields() {
        FieldsType subFields = new FieldsType();
        FieldType subField = new FieldType();
        subField.setName(SUB_FIELD_NAME);
        subField.setDescription(SUB_FIELD_DESCRIPTION);
        subField.setMimeType(SUB_FIELD_MIME_TYPE);
        subField.setLobFolder(SUB_FIELD_LOB_FOLDER);
        subField.setFields(null); // stop nesting of fields at this point - the schema allows further nesting, but two levels should be ok for this test
        subFields.getField()
                .add(subField);
        return subFields;
    }

    private void assertTables(ch.admin.bar.siard2.api.generated.TablesType tables) {
        assertNotNull(tables);
        assertEquals(1, tables.getTable()
                .size());
        ch.admin.bar.siard2.api.generated.TableType table = tables.getTable()
                .get(0);
        assertEquals(TABLE_NAME, table.getName());
        assertEquals(TABLE_DESCRIPTION, table.getDescription());
        assertEquals(TABLE_FOLDER, table.getFolder());
        assertEquals(TABLE_ROWS, table.getRows());
        assertTableColumns(table.getColumns());
        assertCandidateKeys(table.getCandidateKeys());
        assertCheckConstraints(table.getCheckConstraints());
        assertForeignKeys(table.getForeignKeys());
        assertTriggers(table.getTriggers());
    }


    private void assertTableColumns(ch.admin.bar.siard2.api.generated.ColumnsType columns) {
        assertNotNull(columns);
        assertEquals(1, columns.getColumn()
                .size());
        ch.admin.bar.siard2.api.generated.ColumnType column = columns.getColumn()
                .get(0);
        assertEquals(TABLE_COLUMN_NAME, column.getName());
        assertEquals(TABLE_COLUMN_DESCRIPTION, column.getDescription());
        assertEquals(TABLE_COLUMN_DEFAULT_VALUE, column.getDefaultValue());
        assertEquals(TABLE_COLUMN_LOB_FOLDER, column.getLobFolder());
        assertEquals(TABLE_COLUMN_MIME_TYPE, column.getMimeType());
        assertEquals(TABLE_COLUMN_TYPE, column.getType());
        assertEquals(TABLE_COLUMN_TYPE_NAME, column.getTypeName());
        assertEquals(TABLE_COLUMN_TYPE_SCHEMA, column.getTypeSchema());
        assertEquals(TABLE_COLUMN_TYPE_ORIGINAL, column.getTypeOriginal());
        assertEquals(TABLE_COLUMN_TYPE_CARDINALITY, column.getCardinality());
        assertEquals(TABLE_COLUMN_IS_NULLABLE, column.isNullable());
    }

    private ColumnsType createTableColumns() {
        ColumnsType columnsType = new ColumnsType();

        ColumnType column = new ColumnType();
        column.setName(TABLE_COLUMN_NAME);
        column.setDescription(TABLE_COLUMN_DESCRIPTION);
        column.setDefaultValue(TABLE_COLUMN_DEFAULT_VALUE);
        column.setLobFolder(TABLE_COLUMN_LOB_FOLDER);
        column.setMimeType(TABLE_COLUMN_MIME_TYPE);
        column.setType(TABLE_COLUMN_TYPE);
        column.setTypeName(TABLE_COLUMN_TYPE_NAME);
        column.setTypeSchema(TABLE_COLUMN_TYPE_SCHEMA);
        column.setTypeOriginal(TABLE_COLUMN_TYPE_ORIGINAL);
        column.setCardinality(TABLE_COLUMN_TYPE_CARDINALITY);
        column.setNullable(TABLE_COLUMN_IS_NULLABLE);
        column.setFields(new FieldsType());
        columnsType.getColumn()
                .add(column);
        return columnsType;
    }


    private TablesType createTables() {
        TablesType tables = new TablesType();
        TableType table = new TableType();
        table.setName(TABLE_NAME);
        table.setDescription(TABLE_DESCRIPTION);
        table.setFolder(TABLE_FOLDER);
        table.setRows(TABLE_ROWS);
        UniqueKeyType primaryKey = new UniqueKeyType();
        table.setPrimaryKey(primaryKey);

        table.setColumns(createTableColumns());
        table.setCandidateKeys(createCandidateKeys());
        table.setCheckConstraints(createCheckConstraintsType());
        table.setForeignKeys(createForeignKeysType());
        table.setTriggers(createTriggers());
        tables.getTable()
                .add(table);
        return tables;
    }

    private void assertTriggers(ch.admin.bar.siard2.api.generated.TriggersType triggers) {
        assertNotNull(triggers);
        ch.admin.bar.siard2.api.generated.TriggerType trigger = triggers.getTrigger()
                .get(0);

        assertEquals(TRIGGER_NAME, trigger.getName());
        assertEquals(TRIGGER_DESCRIPTION, trigger.getDescription());
        assertEquals(TRIGGER_ALIAS_LIST, trigger.getAliasList());
        assertEquals(TRIGGER_TRIGGERED_ACTION, trigger.getTriggeredAction());
        assertEquals(TRIGGER_TRIGGER_EVENT, trigger.getTriggerEvent());
        assertEquals(ch.admin.bar.siard2.api.generated.ActionTimeType.INSTEAD_OF, trigger.getActionTime());
    }

    private TriggersType createTriggers() {
        TriggersType triggers = new TriggersType();
        TriggerType trigger = new TriggerType();
        trigger.setName(TRIGGER_NAME);
        trigger.setDescription(TRIGGER_DESCRIPTION);
        trigger.setAliasList(TRIGGER_ALIAS_LIST);
        trigger.setTriggeredAction(TRIGGER_TRIGGERED_ACTION);
        trigger.setTriggerEvent(TRIGGER_TRIGGER_EVENT);
        trigger.setActionTime(TRIGGER_ACTION_TIME);
        triggers.getTrigger()
                .add(trigger);
        return triggers;
    }

    private void assertForeignKeys(ch.admin.bar.siard2.api.generated.ForeignKeysType foreignKeys) {
        assertNotNull(foreignKeys);
        assertEquals(1, foreignKeys.getForeignKey()
                .size());
        ch.admin.bar.siard2.api.generated.ForeignKeyType foreignKey = foreignKeys.getForeignKey()
                .get(0);

        assertEquals(FOREIGN_KEY_NAME, foreignKey.getName());
        assertEquals(FOREIGN_KEY_DESCRIPTION, foreignKey.getDescription());
        assertEquals(ch.admin.bar.siard2.api.generated.MatchTypeType.FULL, foreignKey.getMatchType());
        assertEquals(ch.admin.bar.siard2.api.generated.ReferentialActionType.NO_ACTION, foreignKey.getDeleteAction());
        assertEquals(ch.admin.bar.siard2.api.generated.ReferentialActionType.CASCADE, foreignKey.getUpdateAction());
        assertEquals(FOREIGN_KEY_REFERENCED_SCHEMA, foreignKey.getReferencedSchema());
        assertEquals(FOREIGN_KEY_REFERENCED_TABLE, foreignKey.getReferencedTable());
        assertReferences(foreignKey.getReference());
    }

    private ForeignKeysType createForeignKeysType() {
        ForeignKeysType foreignKeys = new ForeignKeysType();
        ForeignKeyType foreignKey = new ForeignKeyType();
        foreignKey.setName(FOREIGN_KEY_NAME);
        foreignKey.setDescription(FOREIGN_KEY_DESCRIPTION);
        foreignKey.setMatchType(FOREIGN_KEY_MATCH_TYPE);
        foreignKey.setDeleteAction(FOREIGN_KEY_DELETE_ACTION);
        foreignKey.setUpdateAction(FOREIGN_KEY_UPDATE_ACTION);
        foreignKey.setReferencedTable(FOREIGN_KEY_REFERENCED_TABLE);
        foreignKey.setReferencedSchema(FOREIGN_KEY_REFERENCED_SCHEMA);
        foreignKey.getReference()
                .add(createReference());
        foreignKeys.getForeignKey()
                .add(foreignKey);
        return foreignKeys;
    }

    private void assertReferences(List<ch.admin.bar.siard2.api.generated.ReferenceType> references) {
        assertNotNull(references);
        assertEquals(1, references.size());
        ch.admin.bar.siard2.api.generated.ReferenceType reference = references.get(0);
        assertEquals(REFERENCED, reference.getReferenced());
        assertEquals(REFERENCE_COLUMN, reference.getColumn());
    }

    private ReferenceType createReference() {
        ReferenceType referenceType = new ReferenceType();
        referenceType.setReferenced(REFERENCED);
        referenceType.setColumn(REFERENCE_COLUMN);
        return referenceType;
    }

    private void assertCheckConstraints(ch.admin.bar.siard2.api.generated.CheckConstraintsType checkConstraints) {
        assertNotNull(checkConstraints);
        assertEquals(1, checkConstraints.getCheckConstraint()
                .size());
        ch.admin.bar.siard2.api.generated.CheckConstraintType checkConstraint = checkConstraints.getCheckConstraint()
                .get(0);

        assertEquals(CHECK_CONSTRAINT_NAME, checkConstraint.getName());
        assertEquals(CHECK_CONSTRAINT_DESCRIPTION, checkConstraint.getDescription());
        assertEquals(CHECK_CONSTRAINT_CONDITION, checkConstraint.getCondition());
    }


    private CheckConstraintsType createCheckConstraintsType() {
        CheckConstraintsType checkConstraintsType = new CheckConstraintsType();
        CheckConstraintType checkConstraint = new CheckConstraintType();
        checkConstraint.setName(CHECK_CONSTRAINT_NAME);
        checkConstraint.setDescription(CHECK_CONSTRAINT_DESCRIPTION);
        checkConstraint.setCondition(CHECK_CONSTRAINT_CONDITION);
        checkConstraintsType.getCheckConstraint()
                .add(checkConstraint);
        return checkConstraintsType;
    }

    private void assertCandidateKeys(ch.admin.bar.siard2.api.generated.CandidateKeysType candidateKeys) {
        assertNotNull(candidateKeys);
        assertEquals(1, candidateKeys.getCandidateKey()
                .size());
        ch.admin.bar.siard2.api.generated.UniqueKeyType candidateKey = candidateKeys.getCandidateKey()
                .get(0);
        assertEquals(CANDIDATE_KEY_NAME, candidateKey.getName());
        assertEquals(CANDIDATE_KEY_DESCRIPTION, candidateKey.getDescription());
        assertThat(candidateKey.getColumn(), hasItems(CANDIDATE_KEY_COLUMN_1, CANDIDATE_KEY_COLUMN_2));
    }


    private CandidateKeysType createCandidateKeys() {
        CandidateKeysType candidateKeysType = new CandidateKeysType();
        UniqueKeyType candidateKey = new UniqueKeyType();
        candidateKey.setName(CANDIDATE_KEY_NAME);
        candidateKey.setDescription(CANDIDATE_KEY_DESCRIPTION);
        candidateKey.getColumn()
                .addAll(Arrays.asList(CANDIDATE_KEY_COLUMN_1, CANDIDATE_KEY_COLUMN_2));
        candidateKeysType.getCandidateKey()
                .add(candidateKey);
        return candidateKeysType;
    }


    private void assertRoutines(ch.admin.bar.siard2.api.generated.RoutinesType routines) {
        assertNotNull(routines);
        assertEquals(1, routines.getRoutine()
                .size());
        ch.admin.bar.siard2.api.generated.RoutineType routine = routines.getRoutine()
                .get(0);
        assertEquals(ROUTINE_NAME, routine.getName());
        assertEquals(ROUTINE_DESCRIPTION, routine.getDescription());
        assertEquals(ROUTINE_RETURN_TYPE, routine.getReturnType());
        assertEquals(ROUTINE_BODY, routine.getBody());
        assertEquals(ROUTINE_CHARACTERISTICS, routine.getCharacteristic());
        assertEquals(ROUTINE_SPECIFIC_NAME, routine.getSpecificName());
        assertEquals(ROUTINE_SOURCE, routine.getSource());
        assertParameters(routine.getParameters());
    }

    private RoutinesType createRoutines() {
        RoutinesType routines = new RoutinesType();
        RoutineType routine = new RoutineType();
        routine.setName(ROUTINE_NAME);
        routine.setDescription(ROUTINE_DESCRIPTION);
        routine.setReturnType(ROUTINE_RETURN_TYPE);
        routine.setBody(ROUTINE_BODY);
        routine.setCharacteristic(ROUTINE_CHARACTERISTICS);
        routine.setSpecificName(ROUTINE_SPECIFIC_NAME);
        routine.setSource(ROUTINE_SOURCE);
        routine.setParameters(createParameters());
        routines.getRoutine()
                .add(routine);
        return routines;
    }

    private void assertParameters(ch.admin.bar.siard2.api.generated.ParametersType parameters) {
        assertNotNull(parameters);
        assertEquals(1, parameters.getParameter()
                .size());
        ch.admin.bar.siard2.api.generated.ParameterType parameter = parameters.getParameter()
                .get(0);
        assertEquals(PARAMETER_NAME, parameter.getName());
        assertEquals(PARAMETER_DESCRIPTION, parameter.getDescription());
        assertEquals(PARAMETER_CARDINALITY, parameter.getCardinality());
        assertEquals(PARAMETER_MODE, parameter.getMode());
        assertEquals(PARAMETER_TYPE, parameter.getType());
        assertEquals(PARAMETER_ORIGINAL, parameter.getTypeOriginal());
        assertEquals(PARAMETER_SCHEMA, parameter.getTypeSchema());
    }

    private ParametersType createParameters() {
        ParametersType parameters = new ParametersType();
        ParameterType parameter = new ParameterType();
        parameter.setName(PARAMETER_NAME);
        parameter.setDescription(PARAMETER_DESCRIPTION);
        parameter.setCardinality(PARAMETER_CARDINALITY);
        parameter.setMode(PARAMETER_MODE);
        parameter.setType(PARAMETER_TYPE);
        parameter.setTypeOriginal(PARAMETER_ORIGINAL);
        parameter.setTypeSchema(PARAMETER_SCHEMA);
        parameters.getParameter()
                .add(parameter);
        return parameters;
    }

    private void assertTypes(ch.admin.bar.siard2.api.generated.TypesType types) {
        assertNotNull(types);
        assertNotNull(types.getType());
        assertEquals(1, types.getType()
                .size());
        ch.admin.bar.siard2.api.generated.TypeType type = types.getType()
                .get(0);
        assertEquals(TYPE_NAME, type.getName());
        assertEquals(TYPE_DESCRIPTION, type.getDescription());
        assertEquals(TYPE_BASE, type.getBase());
        assertEquals(TYPE_UNDER_TYPE, type.getUnderType());
        assertEquals(TYPE_UNDER_SCHEMA, type.getUnderSchema());
        assertEquals(TYPE_IS_FINAL, type.isFinal());
        assertEquals(TYPE_INSTANTIABLE, type.isInstantiable());
        assertEquals(ch.admin.bar.siard2.api.generated.CategoryType.DISTINCT, type.getCategory());
        assertAttributes(type.getAttributes());
    }

    private TypesType createTypes() {
        TypesType types = new TypesType();
        TypeType type = new TypeType();
        type.setName(TYPE_NAME);
        type.setDescription(TYPE_DESCRIPTION);
        type.setBase(TYPE_BASE);
        type.setUnderSchema(TYPE_UNDER_SCHEMA);
        type.setUnderType(TYPE_UNDER_TYPE);
        type.setFinal(TYPE_IS_FINAL);
        type.setInstantiable(TYPE_INSTANTIABLE);
        type.setCategory(TYPE_CATEGORY);
        type.setAttributes(createAttributes());
        types.getType()
                .add(type);
        return types;
    }

    private void assertAttributes(ch.admin.bar.siard2.api.generated.AttributesType attributes) {
        assertNotNull(attributes);
        assertEquals(1, attributes.getAttribute()
                .size());
        ch.admin.bar.siard2.api.generated.AttributeType attribute = attributes.getAttribute()
                .get(0);
        assertEquals(ATTRIBUTE_NAME, attribute.getName());
        assertEquals(ATTRIBUTE_DESCRIPTION, attribute.getDescription());
        assertEquals(ATTRIBUTE_TYPE, attribute.getType());
        assertEquals(ATTRIBUTE_TYPE_SCHEMA, attribute.getTypeSchema());
        assertEquals(ATTRIBUTE_TYPE_NAME, attribute.getTypeName());
        assertEquals(ATTRIBUTE_CARDINALITY, attribute.getCardinality());
        assertEquals(ATTRIBUTE_DEFAULT_VALUE, attribute.getDefaultValue());
        assertEquals(ATTRIBUTE_IS_NULLABLE, attribute.isNullable());
        assertEquals(ATTRIBUTE_TYPE_ORIGINAL, attribute.getTypeOriginal());
    }

    private AttributesType createAttributes() {
        AttributesType attributes = new AttributesType();
        AttributeType attribute = new AttributeType();
        attribute.setName(ATTRIBUTE_NAME);
        attribute.setDescription(ATTRIBUTE_DESCRIPTION);
        attribute.setType(ATTRIBUTE_TYPE);
        attribute.setTypeSchema(ATTRIBUTE_TYPE_SCHEMA);
        attribute.setTypeName(ATTRIBUTE_TYPE_NAME);
        attribute.setCardinality(ATTRIBUTE_CARDINALITY);
        attribute.setDefaultValue(ATTRIBUTE_DEFAULT_VALUE);
        attribute.setNullable(ATTRIBUTE_IS_NULLABLE);
        attribute.setTypeOriginal(ATTRIBUTE_TYPE_ORIGINAL);
        attributes.getAttribute()
                .add(attribute);
        return attributes;
    }

    private static XMLGregorianCalendar xmlCalendar() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar("2020-03-16");
        } catch (DatatypeConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final String DB_NAME = "Convertable SIARD Archive in Format 2.1";
    private static final String DESCRIPTION = "Description";
    private static final String ARCHIVER = "Archiver";
    private static final String ARCHIVER_CONTACT = "Archiver Contact";
    private static final String DATA_OWNER = "Data Owner";
    private static final String DATA_ORIGIN_TIMESPAN = "First half of 2020";
    private static final String LOB_FOLDER = "/lob/folder";
    private static final String PRODUCER_APPLICATION = "Producer Application";
    private static final XMLGregorianCalendar ARCHIVAL_DATE = xmlCalendar();
    private static final String CONNECTION = "Connection";
    private static final String MESSAGE_DIGEST = "Message Digest";
    private static final String CLIENT_MACHINE = "Client Machine";
    private static final String DATABASE_PRODUCT = "Database Product";
    private static final String DATABASE_USER = "Database User";
    private static final String SCHEMA_NAME = "Schema Name";
    private static final String SCHEMA_DESCRIPTION = "Schema Description";
    private static final String SCHEMA_FOLDER = "Schema Folder";
    private static final String TYPE_DESCRIPTION = "Type Description";
    private static final String TYPE_NAME = "Type Name";
    private static final String TYPE_BASE = "Type Base";
    private static final String TYPE_UNDER_TYPE = "Type under Type";
    private static final boolean TYPE_IS_FINAL = false;

    public static final CategoryType TYPE_CATEGORY = CategoryType.DISTINCT;

    public static final String TYPE_UNDER_SCHEMA = "Type Under Schema";
    public static final boolean TYPE_INSTANTIABLE = true;

    private static final String ATTRIBUTE_NAME = "Attribute Name";
    private static final String ATTRIBUTE_DESCRIPTION = "Attribute Description";
    private static final String ATTRIBUTE_TYPE = "Attribute Type";
    private static final String ATTRIBUTE_TYPE_SCHEMA = "Attribute Type Schema";
    private static final String ATTRIBUTE_TYPE_NAME = "Attribute Type Name";
    private static final String ATTRIBUTE_DEFAULT_VALUE = "Attribute Default Value";
    private static final String ATTRIBUTE_TYPE_ORIGINAL = "Attribute Type Original";
    private static final boolean ATTRIBUTE_IS_NULLABLE = true;
    private static final BigInteger ATTRIBUTE_CARDINALITY = BigInteger.TEN;
    private static final String ROUTINE_DESCRIPTION = "Routine Description";
    private static final String ROUTINE_NAME = "Routine Name";
    private static final String ROUTINE_RETURN_TYPE = "Routine Return Type";
    private static final String ROUTINE_BODY = "Routine Body";
    private static final String ROUTINE_CHARACTERISTICS = "Routine Characteristics";
    private static final String ROUTINE_SPECIFIC_NAME = "Routine Specific Name";
    private static final String ROUTINE_SOURCE = "Routine Source";
    private static final String PARAMETER_NAME = "Parameter Name";
    private static final String PARAMETER_DESCRIPTION = "Parameter Description";
    private static final BigInteger PARAMETER_CARDINALITY = BigInteger.ONE;
    private static final String PARAMETER_MODE = "Parameter Mode";
    private static final String PARAMETER_TYPE = "Parameter Type";
    private static final String PARAMETER_ORIGINAL = "Parameter Original";
    private static final String PARAMETER_SCHEMA = "Parameter Schema";
    private static final String TABLE_NAME = "Table Name";
    private static final String TABLE_DESCRIPTION = "Table Description";
    private static final String TABLE_FOLDER = "Table Folder";
    private static final BigInteger TABLE_ROWS = BigInteger.valueOf(1024);
    private static final String CANDIDATE_KEY_NAME = "Candidate Key Name";
    private static final String CANDIDATE_KEY_DESCRIPTION = "Candidate Key Description";
    private static final String CANDIDATE_KEY_COLUMN_1 = "Candidate Key Column 1";
    private static final String CANDIDATE_KEY_COLUMN_2 = "Candidate Key Column 2";
    private static final String CHECK_CONSTRAINT_NAME = "Check Constraint Name";
    private static final String CHECK_CONSTRAINT_DESCRIPTION = "Check Constraint Description";
    private static final String CHECK_CONSTRAINT_CONDITION = "Check Constraint Condition";
    private static final String FOREIGN_KEY_NAME = "Foreign Key Name";
    private static final String FOREIGN_KEY_DESCRIPTION = "Foreign Key Description";
    private static final MatchTypeType FOREIGN_KEY_MATCH_TYPE = MatchTypeType.FULL;
    private static final ReferentialActionType FOREIGN_KEY_DELETE_ACTION = ReferentialActionType.NO_ACTION;
    private static final ReferentialActionType FOREIGN_KEY_UPDATE_ACTION = ReferentialActionType.CASCADE;
    private static final String FOREIGN_KEY_REFERENCED_TABLE = "Foreign Key Referenced Table";
    private static final String FOREIGN_KEY_REFERENCED_SCHEMA = "Foreign Key Referenced Schema";
    private static final String REFERENCED = "Referenced";
    private static final String REFERENCE_COLUMN = "Reference Column";
    private static final String VIEW_NAME = "View Name";
    private static final String VIEW_DESCRIPTION = "View Description";
    private static final BigInteger VIEW_ROWS = BigInteger.valueOf(512);
    private static final String VIEW_QUERY = "View Query";
    private static final String VIEW_QUERY_ORIGINAL = "View Query Original";
    private static final String COLUMN_NAME = "Column Name";
    private static final String COLUMN_DESCRIPTION = "Column Description";
    private static final String COLUMN_DEFAULT_VALUE = "Column Default Value";
    private static final String COLUMN_LOG_FOLDER = "Column Log Folder";
    private static final String COLUMN_MIME_TYPE = "Column Mime Type";
    private static final String COLUMN_TYPE = "Column Type";
    private static final String COLUMN_TYPE_NAME = "Column Type Name";
    private static final String COLUMN_TYPE_SCHEMA = "Column Type Schema";
    private static final String COLUMN_TYPE_ORIGINAL = "Column Type Original";
    private static final BigInteger COLUMN_TYPE_CARDINALITY = BigInteger.valueOf(9);
    private static final boolean COLUMN_IS_NULLABLE = true;
    private static final String FIELD_NAME = "Field Name";
    private static final String FIELD_DESCRIPTION = "Field Description";
    private static final String FIELD_LOB_FOLDER = "Field Lob Folder";
    private static final String FIELD_MIME_TYPE = "Field Mime Type";
    private static final String SUB_FIELD_NAME = "Sub Field Name";
    private static final String SUB_FIELD_DESCRIPTION = "Sub Field Description";
    private static final String SUB_FIELD_MIME_TYPE = "Sub Field Mime Type";
    private static final String SUB_FIELD_LOB_FOLDER = "Sub Field Lob Folder";
    private static final String TABLE_COLUMN_NAME = "Table Column Name";
    private static final String TABLE_COLUMN_DESCRIPTION = "Table Column Description";
    private static final String TABLE_COLUMN_DEFAULT_VALUE = "Table Column Default Value";
    private static final String TABLE_COLUMN_LOB_FOLDER = "Table Column Lob Folder";
    private static final String TABLE_COLUMN_MIME_TYPE = "Table Column Mime Type";
    private static final String TABLE_COLUMN_TYPE = "Table Column Type";
    private static final String TABLE_COLUMN_TYPE_NAME = "Table Column Type Name";
    private static final String TABLE_COLUMN_TYPE_SCHEMA = "Table Column Type Schema";
    private static final String TABLE_COLUMN_TYPE_ORIGINAL = "Table Column Type Original";
    private static final BigInteger TABLE_COLUMN_TYPE_CARDINALITY = BigInteger.valueOf(5);
    private static final Boolean TABLE_COLUMN_IS_NULLABLE = false;
    private static final String USER_NAME = "User Name";
    private static final String USER_DESCRIPTION = "User Description";

    public static final String ROLE_NAME = "Role Name";
    public static final String ROLE_DESCRIPTION = "Role Description";
    public static final String ROLE_ADMIN = "Role Admin";

    public static final String PRIVILIGE_TYPE = "Privilige Type";
    public static final String PRIVILIGE_DESCRIPTION = "Privilige Description";
    public static final String PRIVILIGE_GRANTEE = "Privilige Grantee";
    public static final String PRIVILIGE_GRANTOR = "Privilige Grantor";
    public static final String PRIVILIGE_OBJECT = "Privilige Object";
    public static final PrivOptionType PRIVILIGE_OPTION = PrivOptionType.ADMIN;
    public static final String TRIGGER_NAME = "Trigger Name";
    public static final String TRIGGER_DESCRIPTION = "Trigger Description";
    public static final String TRIGGER_ALIAS_LIST = "Trigger Alias List";
    public static final String TRIGGER_TRIGGERED_ACTION = "Trigger Triggered Action";
    public static final String TRIGGER_TRIGGER_EVENT = "Trigger Trigger Event";
    public static final ActionTimeType TRIGGER_ACTION_TIME = ActionTimeType.INSTEAD_OF;
}
