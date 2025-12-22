package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.AttributeType;

import java.math.BigInteger;

public class ConvertableSiard22AttributeType extends AttributeType {
    public ConvertableSiard22AttributeType(String name, String description, String type, String typeSchema,
                                           String typeName, BigInteger cardinality, String defaultValue,
                                           Boolean nullable, String typeOriginal) {
        super();
        this.name = name;
        this.description = description;
        this.type = type;
        this.typeSchema = typeSchema;
        this.typeName = typeName;
        this.cardinality = cardinality;
        this.defaultValue = defaultValue;
        this.nullable = nullable;
        this.typeOriginal = typeOriginal;
    }
}
