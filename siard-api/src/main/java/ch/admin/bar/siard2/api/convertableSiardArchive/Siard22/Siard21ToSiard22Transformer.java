package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard21.*;
import ch.admin.bar.siard2.api.generated.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

// understands transformation from SIARD 2.1 to the current SIARD Archive
public class Siard21ToSiard22Transformer implements Siard21Transformer {

    @Override
    public SiardArchive visit(ConvertableSiard21Archive siard21Archive) {

        return new ConvertableSiard22Archive(Archive.sMETA_DATA_VERSION,
                                             siard21Archive.getDbname(),
                                             siard21Archive.getDescription(),
                                             siard21Archive.getArchiver(),
                                             siard21Archive.getArchiverContact(),
                                             siard21Archive.getDataOwner(),
                                             siard21Archive.getDataOriginTimespan(),
                                             siard21Archive.getLobFolder(),
                                             siard21Archive.getProducerApplication(),
                                             siard21Archive.getArchivalDate(),
                                             siard21Archive.getClientMachine(),
                                             siard21Archive.getDatabaseProduct(),
                                             siard21Archive.getConnection(),
                                             siard21Archive.getDatabaseUser(),
                                             convertElements(siard21Archive.getMessageDigest(),
                                                             md -> md,
                                                             ConvertableSiard21MessageDigestType::new,
                                                             md -> md.accept(this)),
                                             convertElements(siard21Archive.getSchemas(),
                                                             ch.admin.bar.siard2.api.generated.old21.SchemasType::getSchema,
                                                             ConvertableSiard21SchemaType::new,
                                                             s -> s.accept(this)),
                                             convertElements(siard21Archive.getUsers(),
                                                             ch.admin.bar.siard2.api.generated.old21.UsersType::getUser,
                                                             ConvertableSiard21UserType::new,
                                                             u -> u.accept(this)),
                                             convertElements(siard21Archive.getRoles(),
                                                             ch.admin.bar.siard2.api.generated.old21.RolesType::getRole,
                                                             ConvertableSiard21RoleType::new,
                                                             r -> r.accept(this)),
                                             convertElements(siard21Archive.getPrivileges(),
                                                             ch.admin.bar.siard2.api.generated.old21.PrivilegesType::getPrivilege,
                                                             ConvertableSiard21PriviligeType::new,
                                                             p -> p.accept(this)));
    }

    @Override
    public MessageDigestType visit(ConvertableSiard21MessageDigestType messageDigest) {
        return new ConvertableSiard22MessageDigestType(messageDigest.getDigest(),
                                                       safeConvert(messageDigest.getDigestType(),
                                                                   md -> md.value(),
                                                                   DigestTypeType::fromValue));
    }

    @Override
    public ConvertableSiard22SchemaType visit(ConvertableSiard21SchemaType siard21Schema) {
        return new ConvertableSiard22SchemaType(siard21Schema.getName(),
                                                siard21Schema.getDescription(),
                                                siard21Schema.getFolder(),
                                                convertElements(siard21Schema.getTypes(),
                                                                ch.admin.bar.siard2.api.generated.old21.TypesType::getType,
                                                                ConvertableSiard21TypeType::new,
                                                                t -> t.accept(this)),
                                                convertElements(siard21Schema.getRoutines(),
                                                                ch.admin.bar.siard2.api.generated.old21.RoutinesType::getRoutine,
                                                                ConvertableSiard21Routine::new,
                                                                r -> r.accept(this)),
                                                convertElements(siard21Schema.getTables(),
                                                                ch.admin.bar.siard2.api.generated.old21.TablesType::getTable,
                                                                ConvertableSiard21Table::new,
                                                                t1 -> t1.accept(this)),
                                                convertElements(siard21Schema.getViews(),
                                                                ch.admin.bar.siard2.api.generated.old21.ViewsType::getView,
                                                                ConvertableSiard21ViewType::new,
                                                                v -> v.accept(this)));
    }

    @Override
    public ConvertableSiard22TypeType visit(ConvertableSiard21TypeType siard21Type) {

        return new ConvertableSiard22TypeType(siard21Type.getName(),
                                              siard21Type.getDescription(),
                                              siard21Type.getBase(),
                                              siard21Type.getUnderType(),
                                              siard21Type.getUnderSchema(),
                                              siard21Type.isFinal(),
                                              siard21Type.isInstantiable(),
                                              safeConvert(siard21Type.getCategory(),
                                                          c -> c.value(),
                                                          CategoryType::fromValue),
                                              convertElements(siard21Type.getAttributes(),
                                                              ch.admin.bar.siard2.api.generated.old21.AttributesType::getAttribute,
                                                              ConvertableSiard21AttributeType::new,
                                                              a -> a.accept(this)));
    }

    @Override
    public ConvertableSiard22AttributeType visit(ConvertableSiard21AttributeType siard21Attribute) {
        return new ConvertableSiard22AttributeType(siard21Attribute.getName(),
                                                   siard21Attribute.getDescription(),
                                                   siard21Attribute.getType(),
                                                   siard21Attribute.getTypeSchema(),
                                                   siard21Attribute.getTypeName(),
                                                   siard21Attribute.getCardinality(),
                                                   siard21Attribute.getDefaultValue(),
                                                   siard21Attribute.isNullable(),
                                                   siard21Attribute.getTypeOriginal());
    }

    @Override
    public ConvertableSiard22RoutineType visit(ConvertableSiard21Routine siard21Routine) {
        return new ConvertableSiard22RoutineType(siard21Routine.getName(),
                                                 siard21Routine.getDescription(),
                                                 siard21Routine.getBody(),
                                                 siard21Routine.getCharacteristic(),
                                                 siard21Routine.getReturnType(),
                                                 siard21Routine.getSpecificName(),
                                                 siard21Routine.getSource(),
                                                 convertElements(siard21Routine.getParameters(),
                                                                 ch.admin.bar.siard2.api.generated.old21.ParametersType::getParameter,
                                                                 ConvertableSiard21Parameter::new,
                                                                 p -> p.accept(this)));
    }


    @Override
    public ConvertablSiard22Parameter visit(ConvertableSiard21Parameter siard21Parameter) {
        return new ConvertablSiard22Parameter(siard21Parameter.getName(),
                                              siard21Parameter.getDescription(),
                                              siard21Parameter.getCardinality(),
                                              siard21Parameter.getMode(),
                                              siard21Parameter.getType(),
                                              siard21Parameter.getTypeName(),
                                              siard21Parameter.getTypeSchema(),
                                              siard21Parameter.getTypeOriginal());
    }

    @Override
    public ConvertableSiard22TableType visit(ConvertableSiard21Table siard21Table) {
        return new ConvertableSiard22TableType(siard21Table.getName(),
                                               siard21Table.getDescription(),
                                               siard21Table.getFolder(),
                                               siard21Table.getRows(),
                                               convertPrimaryKey(siard21Table.getPrimaryKey()),
                                               convertElements(siard21Table.getColumns(),
                                                               ch.admin.bar.siard2.api.generated.old21.ColumnsType::getColumn,
                                                               ConvertableSiard21ColumnType::new,
                                                               c -> c.accept(this)),
                                               convertElements(siard21Table.getCandidateKeys(),
                                                               ch.admin.bar.siard2.api.generated.old21.CandidateKeysType::getCandidateKey,
                                                               ConvertableSiard21UniqueKeyType::new,
                                                               ck -> ck.accept(this)),
                                               convertElements(siard21Table.getCheckConstraints(),
                                                               ch.admin.bar.siard2.api.generated.old21.CheckConstraintsType::getCheckConstraint,
                                                               ConvertableSiard21CheckConstraintType::new,
                                                               c1 -> c1.accept(this)),
                                               convertElements(siard21Table.getForeignKeys(),
                                                               ch.admin.bar.siard2.api.generated.old21.ForeignKeysType::getForeignKey,
                                                               ConvertableSiard21ForeignKeyTypes::new,
                                                               f -> f.accept(this)),
                                               convertElements(siard21Table.getTriggers(),
                                                               ch.admin.bar.siard2.api.generated.old21.TriggersType::getTrigger,
                                                               ConvertableSiard21TriggerType::new,
                                                               t -> t.accept(this)));
    }


    @Override
    public ConvertableSiard22UniqueKeyType visit(ConvertableSiard21UniqueKeyType siard21UniqueKey) {
        return new ConvertableSiard22UniqueKeyType(siard21UniqueKey.getName(),
                                                   siard21UniqueKey.getDescription(),
                                                   siard21UniqueKey.getColumn());
    }

    @Override
    public ConvertableSiard22CheckConstraintType visit(ConvertableSiard21CheckConstraintType siard21CheckConstraint) {
        return new ConvertableSiard22CheckConstraintType(siard21CheckConstraint.getName(),
                                                         siard21CheckConstraint.getDescription(),
                                                         siard21CheckConstraint.getCondition());
    }

    @Override
    public ConvertableSiard22ForeignKeyTypes visit(ConvertableSiard21ForeignKeyTypes siard21ForeignKey) {
        return new ConvertableSiard22ForeignKeyTypes(siard21ForeignKey.getName(),
                                                     siard21ForeignKey.getDescription(),
                                                     safeConvert(siard21ForeignKey.getMatchType(),
                                                                 ch.admin.bar.siard2.api.generated.old21.MatchTypeType::value,
                                                                 MatchTypeType::fromValue),
                                                     safeConvert(siard21ForeignKey.getDeleteAction(),
                                                                 ch.admin.bar.siard2.api.generated.old21.ReferentialActionType::value,
                                                                 ReferentialActionType::fromValue),
                                                     safeConvert(siard21ForeignKey.getUpdateAction(),
                                                                 ch.admin.bar.siard2.api.generated.old21.ReferentialActionType::value,
                                                                 ReferentialActionType::fromValue),
                                                     siard21ForeignKey.getReferencedSchema(),
                                                     siard21ForeignKey.getReferencedTable(),
                                                     convertElements(siard21ForeignKey.getReference(),
                                                                     r -> r,
                                                                     ConvertableSiard21ReferenceType::new,
                                                                     r -> r.accept(this)));
    }

    @Override
    public ConvertableSiard22ReferenceType visit(ConvertableSiard21ReferenceType siard21Reference) {
        return new ConvertableSiard22ReferenceType(siard21Reference.getReferenced(), siard21Reference.getColumn());
    }

    @Override
    public ConvertableSiard22ViewType visit(ConvertableSiard21ViewType siard21View) {
        return new ConvertableSiard22ViewType(siard21View.getName(),
                                              siard21View.getDescription(),
                                              siard21View.getRows(),
                                              siard21View.getQuery(),
                                              siard21View.getQueryOriginal(),
                                              convertElements(siard21View.getColumns(),
                                                              ch.admin.bar.siard2.api.generated.old21.ColumnsType::getColumn,
                                                              ConvertableSiard21ColumnType::new,
                                                              c -> c.accept(this)));
    }

    @Override
    public ConvertableSiard22ColumnType visit(ConvertableSiard21ColumnType siard21Column) {

        return new ConvertableSiard22ColumnType(siard21Column.getName(),
                                                siard21Column.getDescription(),
                                                siard21Column.getDefaultValue(),
                                                siard21Column.getLobFolder(),
                                                siard21Column.getMimeType(),
                                                siard21Column.getType(),
                                                siard21Column.getTypeName(),
                                                siard21Column.getTypeSchema(),
                                                siard21Column.getTypeOriginal(),
                                                siard21Column.getCardinality(),
                                                siard21Column.isNullable(),
                                                convertElements(siard21Column.getFields(),
                                                                ch.admin.bar.siard2.api.generated.old21.FieldsType::getField,
                                                                ConvertableSiard21FieldType::new,
                                                                field -> field.accept(this)));
    }

    @Override
    public ConvertableSiard22FieldType visit(ConvertableSiard21FieldType siard21Field) {

        return new ConvertableSiard22FieldType(siard21Field.getName(),
                                               siard21Field.getDescription(),
                                               siard21Field.getMimeType(),
                                               siard21Field.getLobFolder(),
                                               convertElements(siard21Field.getFields(),
                                                               ch.admin.bar.siard2.api.generated.old21.FieldsType::getField,
                                                               ConvertableSiard21FieldType::new,
                                                               field -> field.accept(this)));
    }

    @Override
    public ConvertableSiard22UserType visit(ConvertableSiard21UserType siard21User) {
        return new ConvertableSiard22UserType(siard21User.getName(), siard21User.getDescription());
    }

    @Override
    public ConvertableSiard22RoleType visit(ConvertableSiard21RoleType role) {
        return new ConvertableSiard22RoleType(role.getName(), role.getDescription(), role.getAdmin());
    }

    @Override
    public ConvertableSiard22PriviligeType visit(ConvertableSiard21PriviligeType privilige) {
        return new ConvertableSiard22PriviligeType(privilige.getType(),
                                                   privilige.getDescription(),
                                                   privilige.getGrantee(),
                                                   privilige.getGrantor(),
                                                   privilige.getObject(),
                                                   safeConvert(privilige.getOption(),
                                                               ch.admin.bar.siard2.api.generated.old21.PrivOptionType::value,
                                                               PrivOptionType::fromValue));
    }

    @Override
    public ConvertableSiard22TriggerType visit(ConvertableSiard21TriggerType trigger) {
        return new ConvertableSiard22TriggerType(trigger.getName(),
                                                 trigger.getDescription(),
                                                 trigger.getAliasList(),
                                                 trigger.getTriggeredAction(),
                                                 trigger.getTriggerEvent(),
                                                 safeConvert(trigger.getActionTime(),
                                                             t -> t.value(),
                                                             ActionTimeType::fromValue));
    }

    /**
     * This helper method converts all element in the container that can be get by the getElements function to an intermediate
     * convertable - that means visitable - siard type and then calls the accept method on this element to transform it into
     * a type know to SIARD 2.2
     * <p>
     * This particular design is the result of some constraints due to the fact that the classes in use are autogenerated and
     * there is no easy and well maintained way to make the generated code implement specific interfaces.
     *
     * @param container   - the container that contains the list - might be null
     * @param getElements - how to get the list from the container
     * @param make        - how to make a visitable item from each element of the list
     * @param accept      - how to call the accept method on every visitable item
     * @param <I>         the type of the container that holds the list
     * @param <T>         the type of the elements in the containers list
     * @param <V>         the type of the visitable element that implement the accept method
     * @param <R>         the return type
     * @return List of converted elements
     */
    private <I, T, V, R> List<R> convertElements(I container, Function<I, List<T>> getElements, Function<T, V> make,
                                                 Function<V, R> accept) {
        if (container == null) return Collections.emptyList();
        return getElements.apply(container)
                          .stream()
                          .map(make)
                          .map(accept)
                          .collect(Collectors.toList());
    }

    /**
     * Converts an thing from to another enum value
     * This only works for enums with identical values - but that are from different types - because they are generated code...
     *
     * @param thing   the thing that should be converted - usually an enum
     * @param toValue how to get the value from the given thing
     * @param from    how to convert from the string to the other thing that will be returned
     * @param <I>     the type of the original
     * @param <R>     the type of the target enum that will be returned
     * @return
     */
    private <I, R> R safeConvert(I thing, Function<I, String> toValue, Function<String, R> from) {
        if (thing == null) return null;
        return from.apply(toValue.apply(thing));
    }

    private ConvertableSiard22UniqueKeyType convertPrimaryKey(
            ch.admin.bar.siard2.api.generated.old21.UniqueKeyType primaryKey) {
        if (primaryKey == null) return null;
        return new ConvertableSiard21UniqueKeyType(primaryKey).accept(this);
    }
}