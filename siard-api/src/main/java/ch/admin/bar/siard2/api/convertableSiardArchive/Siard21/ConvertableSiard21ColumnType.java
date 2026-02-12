package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;


import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.ConvertableSiard22ColumnType;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.ColumnType;

public class ConvertableSiard21ColumnType extends ColumnType {

    public ConvertableSiard21ColumnType(ColumnType column) {
        super();
        this.name = column.getName();
        this.description = column.getDescription();
        this.defaultValue = column.getDefaultValue();
        this.lobFolder = column.getLobFolder();
        this.mimeType = column.getMimeType();
        this.type = column.getType();
        this.typeName = column.getTypeName();
        this.typeOriginal = column.getTypeOriginal();
        this.typeSchema = column.getTypeSchema();
        this.cardinality = column.getCardinality();
        this.nullable = column.isNullable();
        this.fields = column.getFields();
    }

    public ConvertableSiard22ColumnType accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
