package ch.admin.bar.siard2.access.expression;

import ch.admin.bar.siard2.access.AccessSqlFactory;
import ch.enterag.sqlparser.expression.QuerySpecification;
import ch.enterag.sqlparser.identifier.QualifiedId;

public class AccessQuerySpecification
        extends QuerySpecification {
    public AccessQuerySpecification(AccessSqlFactory sf) {
        super(sf);
    }

    private boolean equalIgnoreCase(String s1, String s2) {
        boolean bEqual = true;
        if (s1 != null) {
            if (!s1.equalsIgnoreCase(s2))
                bEqual = false;
        } else if (s2 != null)
            bEqual = false;
        return bEqual;
    }

    @Override
    protected boolean equalTables(QualifiedId qiTable1, QualifiedId qiTable2) {
        return equalIgnoreCase(qiTable1.getName(), qiTable2.getName()) &&
                equalIgnoreCase(qiTable1.getSchema(), qiTable2.getSchema()) &&
                equalIgnoreCase(qiTable1.getCatalog(), qiTable2.getCatalog());
    }

}
