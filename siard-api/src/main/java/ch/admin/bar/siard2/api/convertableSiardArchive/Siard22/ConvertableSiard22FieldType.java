package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.FieldType;
import ch.admin.bar.siard2.api.generated.FieldsType;

import java.util.List;

public class ConvertableSiard22FieldType extends FieldType {
    public ConvertableSiard22FieldType(String name, String description, String mimeType, String lobFolder,
                                       List<FieldType> fields) {
        super();
        this.name = name;
        this.description = description;
        this.mimeType = mimeType;
        this.lobFolder = lobFolder;

        if (fields != null && !fields.isEmpty()) {
            this.fields = new FieldsType();
            this.fields.getField()
                       .addAll(fields);
        }
    }
}
