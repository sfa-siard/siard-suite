package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.*;

import java.math.BigInteger;
import java.util.Collection;

public class ConvertableSiard22TableType extends TableType {
    public ConvertableSiard22TableType(String name, String description, String folder, BigInteger rows,
                                       UniqueKeyType primaryKey, Collection<ColumnType> columns,
                                       Collection<UniqueKeyType> candidateKeys,
                                       Collection<CheckConstraintType> checkConstraints,
                                       Collection<ForeignKeyType> foreignKeys, Collection<TriggerType> triggers) {
        super();
        this.name = name;
        this.description = description;
        this.folder = folder;
        this.rows = rows;

        this.primaryKey = primaryKey;
        if (columns.size() > 0) {
            this.columns = new ColumnsType();
            this.columns.getColumn()
                        .addAll(columns);
        }

        if (candidateKeys.size() > 0) {
            this.candidateKeys = new CandidateKeysType();
            this.candidateKeys.getCandidateKey()
                              .addAll(candidateKeys);
        }

        if (checkConstraints.size() > 0) {
            this.checkConstraints = new CheckConstraintsType();
            this.checkConstraints.getCheckConstraint()
                                 .addAll(checkConstraints);
        }

        if (foreignKeys.size() > 0) {
            this.foreignKeys = new ForeignKeysType();
            this.foreignKeys.getForeignKey()
                            .addAll(foreignKeys);
        }

        if (triggers.size() > 0) {
            this.triggers = new TriggersType();
            this.triggers.getTrigger()
                         .addAll(triggers);
        }
    }
}
