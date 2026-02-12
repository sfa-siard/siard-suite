package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.*;
import ch.admin.bar.siard2.api.generated.MessageDigestType;
import ch.admin.bar.siard2.api.generated.SiardArchive;

public interface Siard21Transformer {
    SiardArchive visit(ConvertableSiard21Archive siard21Archive);

    MessageDigestType visit(ConvertableSiard21MessageDigestType messageDigest);

    ConvertableSiard22SchemaType visit(ConvertableSiard21SchemaType convertableSiard21SchemaType);

    ConvertableSiard22TypeType visit(ConvertableSiard21TypeType convertableSiard21TypeType);

    ConvertableSiard22AttributeType visit(ConvertableSiard21AttributeType convertableSiard21AttributeType);

    ConvertableSiard22RoutineType visit(ConvertableSiard21Routine convertableSiard21Routine);

    ConvertablSiard22Parameter visit(ConvertableSiard21Parameter convertableSiard21Parameter);

    ConvertableSiard22TableType visit(ConvertableSiard21Table convertableSiard21Table);

    ConvertableSiard22UniqueKeyType visit(ConvertableSiard21UniqueKeyType convertableSiard21UniqueKeyType);

    ConvertableSiard22CheckConstraintType visit(
            ConvertableSiard21CheckConstraintType convertableSiard21CheckConstraintType);

    ConvertableSiard22ForeignKeyTypes visit(ConvertableSiard21ForeignKeyTypes convertableSiard21ForeignKeyTypes);

    ConvertableSiard22ReferenceType visit(ConvertableSiard21ReferenceType convertableSiard21ReferenceType);

    ConvertableSiard22ViewType visit(ConvertableSiard21ViewType convertableSiard21ViewType);

    ConvertableSiard22ColumnType visit(ConvertableSiard21ColumnType convertableSiard21ColumnType);

    ConvertableSiard22FieldType visit(ConvertableSiard21FieldType convertableSiard21FieldType);

    ConvertableSiard22UserType visit(ConvertableSiard21UserType convertableSiard21UserType);

    ConvertableSiard22RoleType visit(ConvertableSiard21RoleType convertableSiard21RoleType);

    ConvertableSiard22PriviligeType visit(ConvertableSiard21PriviligeType convertableSiard21PriviligesType);

    ConvertableSiard22TriggerType visit(ConvertableSiard21TriggerType convertableSiard21TriggerType);
}
