package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.ParameterType;

import java.math.BigInteger;

public class ConvertablSiard22Parameter extends ParameterType {
    public ConvertablSiard22Parameter(String name, String description, BigInteger cardinality, String mode, String type,
                                      String typeName, String typeSchema, String typeOriginal) {
        super();
        this.name = name;
        this.description = description;
        this.cardinality = cardinality;
        this.mode = mode;
        this.type = type;
        this.typeName = typeName;
        this.typeSchema = typeSchema;
        this.typeOriginal = typeOriginal;
    }
}
