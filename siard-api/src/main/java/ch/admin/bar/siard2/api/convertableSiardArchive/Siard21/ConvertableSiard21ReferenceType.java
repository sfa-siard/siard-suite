package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.ConvertableSiard22ReferenceType;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.ReferenceType;

public class ConvertableSiard21ReferenceType extends ReferenceType {

    public ConvertableSiard21ReferenceType(ReferenceType referenceType) {
        super();
        this.referenced = referenceType.getReferenced();
        this.column = referenceType.getColumn();
    }

    public ConvertableSiard22ReferenceType accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
