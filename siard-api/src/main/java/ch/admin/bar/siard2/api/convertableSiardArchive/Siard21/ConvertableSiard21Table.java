package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.ConvertableSiard22TableType;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.TableType;

public class ConvertableSiard21Table extends TableType {
    public ConvertableSiard21Table(TableType table) {
        super();
        this.name = table.getName();
        this.description = table.getDescription();
        this.folder = table.getFolder();
        this.rows = table.getRows();
        this.primaryKey = table.getPrimaryKey();
        this.candidateKeys = table.getCandidateKeys();
        this.checkConstraints = table.getCheckConstraints();
        this.columns = table.getColumns();
        this.foreignKeys = table.getForeignKeys();
        this.primaryKey = table.getPrimaryKey();
        this.triggers = table.getTriggers();
    }

    public ConvertableSiard22TableType accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
