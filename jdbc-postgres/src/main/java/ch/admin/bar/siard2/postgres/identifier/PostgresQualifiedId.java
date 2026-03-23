package ch.admin.bar.siard2.postgres.identifier;

import ch.admin.bar.siard2.postgres.PostgresLiterals;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.identifier.QualifiedId;

import java.text.ParseException;

public class PostgresQualifiedId
        extends QualifiedId {
    public String format() {
        return PostgresLiterals.formatQualifiedName(getCatalog(), getSchema(), getName());
    }

    private String handleUppercaseRegular(String sIdentifier) {
        if (sIdentifier != null) {
            if ((sIdentifier.equals(sIdentifier.toUpperCase())) && (SqlLiterals.isRegular(sIdentifier)))
                sIdentifier = sIdentifier.toLowerCase();
        }
        return sIdentifier;
    }

    public PostgresQualifiedId() {
        super();
    }

    public PostgresQualifiedId(String sCatalog, String sSchema, String sName) {
        super(sCatalog, sSchema, sName);
    }

    public PostgresQualifiedId(String sFormatted)
            throws ParseException {
        super(sFormatted);
        setCatalog(handleUppercaseRegular(getCatalog()));
        setSchema(handleUppercaseRegular(getSchema()));
        setName(handleUppercaseRegular(getName()));
    }


}
