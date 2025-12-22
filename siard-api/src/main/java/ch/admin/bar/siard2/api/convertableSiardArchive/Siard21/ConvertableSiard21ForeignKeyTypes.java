package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.ConvertableSiard22ForeignKeyTypes;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.ForeignKeyType;

public class ConvertableSiard21ForeignKeyTypes extends ForeignKeyType {

    public ConvertableSiard21ForeignKeyTypes(ForeignKeyType foreignKey) {
        this.name = foreignKey.getName();
        this.description = foreignKey.getDescription();
        this.matchType = foreignKey.getMatchType();
        this.deleteAction = foreignKey.getDeleteAction();
        this.updateAction = foreignKey.getUpdateAction();
        this.referencedSchema = foreignKey.getReferencedSchema();
        this.referencedTable = foreignKey.getReferencedTable();
        this.reference = foreignKey.getReference();
    }

    public ConvertableSiard22ForeignKeyTypes accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
