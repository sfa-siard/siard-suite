package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.ConvertablSiard22Parameter;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.ParameterType;

public class ConvertableSiard21Parameter extends ParameterType {

    public ConvertableSiard21Parameter(ParameterType parameter) {
        this.name = parameter.getName();
        this.description = parameter.getDescription();
        this.cardinality = parameter.getCardinality();
        this.mode = parameter.getMode();
        this.type = parameter.getType();
        this.typeName = parameter.getTypeName();
        this.typeOriginal = parameter.getTypeOriginal();
        this.typeSchema = parameter.getTypeSchema();
    }

    public ConvertablSiard22Parameter accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
