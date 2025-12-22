package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.ConvertableSiard22AttributeType;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.AttributeType;

public class ConvertableSiard21AttributeType extends AttributeType {
    public ConvertableSiard21AttributeType(AttributeType attribute) {
        this.name = attribute.getName();
        this.description = attribute.getDescription();
        this.type = attribute.getType();
        this.typeSchema = attribute.getTypeSchema();
        this.typeName = attribute.getTypeName();
        this.cardinality = attribute.getCardinality();
        this.defaultValue = attribute.getDefaultValue();
        this.nullable = attribute.isNullable();
        this.typeOriginal = attribute.getTypeOriginal();
    }

    public ConvertableSiard22AttributeType accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
