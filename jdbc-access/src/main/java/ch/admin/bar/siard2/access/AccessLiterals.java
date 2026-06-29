package ch.admin.bar.siard2.access;

public class AccessLiterals {
    // special characters not allowed in identifiers in MS Access
    // \.[]{}()*+-?^$| must be escaped in regex
    private static final String REGEX_SPECIAL_CHARS = "[ \"'@`#%><!\\.\\[\\]\\*\\$;:\\?\\^\\{\\}\\+\\-=~\\\\]";

    private static final String REGEX_KEYWORD_NO_SPACE = "(?i)(?<=[^\\s])(FROM|WHERE|GROUP\\s+BY|HAVING|ORDER\\s+BY|UNION)";

    public static String normalizeId(String sIdentifier) {
        if (sIdentifier != null)
            sIdentifier = sIdentifier.replaceAll(REGEX_SPECIAL_CHARS, "_");
        return sIdentifier;
    }

    /**
     * Ensures SQL keywords are preceded by at least one space in a query string
     * returned by Jackcess {@code Query.toSQLString()}.
     * <p>
     * Access stores query definitions as raw SQL text in the .accdb file. When
     * Jackcess reconstructs this text via {@code toSQLString()}, it replays the
     * stored tokens verbatim without guaranteeing whitespace between them. This
     * can result in malformed SQL such as {@code "... AS AliasFROM table ..."}.

     * @param rawSql raw SQL string from {@code Query.toSQLString()}.
     * @return SQL string with whitespace before SQL keywords guaranteed.
     */
    public static String normalizeQueryText(String rawSql) {
        return rawSql.replaceAll(REGEX_KEYWORD_NO_SPACE, " $1");
    }

}
