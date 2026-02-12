package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.ColumnType;
import ch.admin.bar.siard2.api.generated.FieldType;
import ch.admin.bar.siard2.api.generated.FieldsType;

import java.math.BigInteger;
import java.util.List;

public class ConvertableSiard22ColumnType extends ColumnType {

    public ConvertableSiard22ColumnType(String name, String description, String defaultValue, String lobFolder,
                                        String mimeType, String type, String typeName, String typeSchema,
                                        String typeOriginal, BigInteger cardinality, Boolean nullable,
                                        List<FieldType> fields) {
        super();
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.lobFolder = lobFolder;
        this.mimeType = mimeType;
        this.type = type;
        this.typeName = typeName;
        this.typeSchema = typeSchema;
        this.typeOriginal = typeOriginal;
        this.cardinality = cardinality;
        this.nullable = nullable;
        if (fields.size() > 0) {
            this.fields = new FieldsType();
            this.fields.getField()
                       .addAll(fields);
        }
    }
}
