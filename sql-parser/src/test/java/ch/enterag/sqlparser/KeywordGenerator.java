package ch.enterag.sqlparser;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class KeywordGenerator {

    private static final String ENUM_FILE_PATH = "src/main/java/ch/enterag/sqlparser/K.java";
    private static final String KEYWORD_FILE_PATH = "src/main/antlr/LexSql.g4";
    private static final String[] RESERVER_WORDS = new String[]
            {
                    "A",
                    "ABS",
                    "ACTION",
                    "ADD",
                    "AFTER",
                    "ALL",
                    "ALLOCATE",
                    "ALTER",
                    "ALWAYS",
                    "AND",
                    "ANY",
                    "ARE",
                    "ARRAY",
                    "AS",
                    "ASC",
                    "ASENSITIVE",
                    "ASYMMETRIC",
                    "AT",
                    "ATOMIC",
                    "AUTHORIZATION",
                    "AVG",
                    "BEFORE",
                    "BEGIN",
                    "BERNOULLI",
                    "BETWEEN",
                    "BIGINT",
                    "BINARY",
                    "BLOB",
                    "BOOLEAN",
                    "BOTH",
                    "BY",
                    "CALL",
                    "CALLED",
                    "CARDINALITY",
                    "CASCADE",
                    "CASCADED",
                    "CASE",
                    "CAST",
                    "CEIL",
                    "CEILING",
                    "CHAR",
                    "CHARACTER",
                    "CHARACTER_LENGTH",
                    "CHAR_LENGTH",
                    "CHECK",
                    "CHECKED",
                    "CLOB",
                    "CLOSE",
                    "COALESCE",
                    "COLLATE",
                    "COLLECT",
                    "COLUMN",
                    "COMMIT",
                    "CONNECT",
                    "CONSTRAINT",
                    "CONTINUE",
                    "CORRESPONDING",
                    "COVAR_POP",
                    "COVAR_SAMP",
                    "COUNT",
                    "CREATE",
                    "CROSS",
                    "CUBE",
                    "CUME_DIST",
                    "CURRENT",
                    "CURRENT_DATE",
                    "CURRENT_DEFAULT_TRANSFORM_GROUP",
                    "CURRENT_PATH",
                    "CURRENT_ROLE",
                    "CURRENT_TIME",
                    "CURRENT_TIMESTAMP",
                    "CURRENT_TRANSFORM_GROUP_FOR_TYPE",
                    "CURRENT_USER",
                    "CURSOR",
                    "CYCLE",
                    "DATA",
                    "DATALINK",
                    "DATE",
                    "DAY",
                    "DEALLOCATE",
                    "DEC",
                    "DECIMAL",
                    "DECLARE",
                    "DEFAULT",
                    "DEFERRABLE",
                    "DEFERRED",
                    "DELETE",
                    "DENSE_RANK",
                    "DEREF",
                    "DERIVED",
                    "DESC",
                    "DESCRIBE",
                    "DETERMINISTIC",
                    "DISCONNECT",
                    "DISTINCT",
                    "DOUBLE",
                    "DROP",
                    "DYNAMIC",
                    "EACH",
                    "ELEMENT",
                    "ELSE",
                    "END",
                    "END-EXEC",
                    "ESCAPE",
                    "EVERY",
                    "EXCEPT",
                    "EXCLUDING",
                    "EXEC",
                    "EXECUTE",
                    "EXISTS",
                    "EXP",
                    "EXTERNAL",
                    "EXTRACT",
                    "FALSE",
                    "FETCH",
                    "FILTER",
                    "FINAL",
                    "FLOAT",
                    "FLOOR",
                    "FOLLOWING",
                    "FOR",
                    "FOREIGN",
                    "FREE",
                    "FROM",
                    "FULL",
                    "FUNCTION",
                    "FUSION",
                    "G",
                    "GENERATED",
                    "GET",
                    "GLOBAL",
                    "GRANT",
                    "GROUP",
                    "GROUPING",
                    "HAVING",
                    "HOLD",
                    "HOUR",
                    "IDENTITY",
                    "IMMEDIATE",
                    "IN",
                    "INCLUDING",
                    "INDICATOR",
                    "INITIALLY",
                    "INNER",
                    "INOUT",
                    "INPUT",
                    "INSENSITIVE",
                    "INSERT",
                    "INSTANCE",
                    "INSTANTIABLE",
                    "INT",
                    "INTEGER",
                    "INTERSECT",
                    "INTERSECTION",
                    "INTERVAL",
                    "INTO",
                    "IS",
                    "ISOLATION",
                    "JOIN",
                    "K",
                    "KEY",
                    "LANGUAGE",
                    "LARGE",
                    "LATERAL",
                    "LEADING",
                    "LEFT",
                    "LIKE",
                    "LN",
                    "LOCAL",
                    "LOCALTIME",
                    "LOCALTIMESTAMP",
                    "LOWER",
                    "M",
                    "MATCH",
                    "MAX",
                    "MEMBER",
                    "MERGE",
                    "METHOD",
                    "MIN",
                    "MINUTE",
                    "MOD",
                    "MODIFIES",
                    "MODULE",
                    "MONTH",
                    "MULTISET",
                    "NATIONAL",
                    "NATURAL",
                    "NCHAR",
                    "NCLOB",
                    "NEW",
                    "NEXT",
                    "NO",
                    "NONE",
                    "NORMALIZE",
                    "NORMALIZED",
                    "NOT",
                    "NULL",
                    "NULLIF",
                    "NUMERIC",
                    "OBJECT",
                    "OCTET_LENGTH",
                    "OF",
                    "OLD",
                    "ON",
                    "ONLY",
                    "OPEN",
                    "OPTION",
                    "OPTIONS",
                    "OR",
                    "ORDER",
                    "ORDINALITY",
                    "OUT",
                    "OUTER",
                    "OUTPUT",
                    "OVER",
                    "OVERLAPS",
                    "OVERRIDING",
                    "PARAMETER",
                    "PARTITION",
                    "PERCENT_RANK",
                    "PERCENTILE_CONT",
                    "PERCENTILE_DISC",
                    "POWER",
                    "PRECEDING",
                    "PRECISION",
                    "PREPARE",
                    "PRIMARY",
                    "PROCEDURE",
                    "RANGE",
                    "RANK",
                    "READS",
                    "REAL",
                    "RECURSIVE",
                    "REF",
                    "REFERENCES",
                    "REFERENCING",
                    "REGR_AVGX",
                    "REGR_AVGY",
                    "REGR_COUNT",
                    "REGR_INTERCEPT",
                    "REGR_R2",
                    "REGR_SLOPE",
                    "REGR_SXX",
                    "REGR_SXY",
                    "REGR_SYY",
                    "RELEASE",
                    "REPEATABLE",
                    "RESTRICT",
                    "RESULT",
                    "RETURN",
                    "RETURNS",
                    "REVOKE",
                    "RIGHT",
                    "ROLLBACK",
                    "ROLLUP",
                    "ROW",
                    "ROW_NUMBER",
                    "ROWS",
                    "SAVEPOINT",
                    "SCHEMA",
                    "SCOPE",
                    "SCROLL",
                    "SEARCH",
                    "SECOND",
                    "SELECT",
                    "SENSITIVE",
                    "SESSION_USER",
                    "SET",
                    "SIMILAR",
                    "SIMPLE",
                    "SMALLINT",
                    "SOME",
                    "SPECIFIC",
                    "SPECIFICTYPE",
                    "SQL",
                    "SQLEXCEPTION",
                    "SQLSTATE",
                    "SQLWARNING",
                    "SQRT",
                    "START",
                    "STATIC",
                    "STDDEV_POP",
                    "STDDEV_SAMP",
                    "STYLE",
                    "SUBMULTISET",
                    "SUBSTRING",
                    "SUM",
                    "SYMMETRIC",
                    "SYSTEM",
                    "SYSTEM_USER",
                    "TABLE",
                    "TABLESAMPLE",
                    "TEMPORARY",
                    "THEN",
                    "TIME",
                    "TIMESTAMP",
                    "TIMEZONE_HOUR",
                    "TIMEZONE_MINUTE",
                    "TO",
                    "TRAILING",
                    "TRANSLATION",
                    "TREAT",
                    "TRIGGER",
                    "TRIM",
                    "TRUE",
                    "TYPE",
                    "UESCAPE",
                    "UNBOUNDED",
                    "UNDER",
                    "UNION",
                    "UNIQUE",
                    "UNKNOWN",
                    "UNNEST",
                    "UPDATE",
                    "UPPER",
                    "USER",
                    "USING",
                    "VALUE",
                    "VALUES",
                    "VAR_POP",
                    "VAR_SAMP",
                    "VARBINARY",
                    "VARCHAR",
                    "VARYING",
                    "VIEW",
                    "WHEN",
                    "WHENEVER",
                    "WHERE",
                    "WIDTH_BUCKET",
                    "WINDOW",
                    "WITH",
                    "WITHIN",
                    "WITHOUT",
                    "XML",
                    "YEAR",
                    "ZONE"
            };
    private static final String[] NON_RESERVER_WORDS = new String[]
            {
                    "ABSOLUTE",
                    "ADA",
                    "ADMIN",
                    "ASSERTION",
                    "ASSIGNMENT",
                    "ATTRIBUTE",
                    "ATTRIBUTES",
                    "BREADTH",
                    "C",
                    "CATALOG",
                    "CATALOG_NAME",
                    "CHAIN",
                    "CHARACTERISTICS",
                    "CHARACTERS",
                    "CHARACTER_SET_CATALOG",
                    "CHARACTER_SET_NAME",
                    "CHARACTER_SET_SCHEMA",
                    "CLASS_ORIGIN",
                    "COBOL",
                    "CODE_UNITS",
                    "COLLATION",
                    "COLLATION_CATALOG",
                    "COLLATION_NAME",
                    "COLLATION_SCHEMA",
                    "COLUMN_NAME",
                    "COMMAND_FUNCTION",
                    "COMMAND_FUNCTION_CODE",
                    "COMMITTED",
                    "CONDITION",
                    "CONDITION_NUMBER",
                    "CONNECTION_NAME",
                    "CONSTRAINTS",
                    "CONSTRAINT_CATALOG",
                    "CONSTRAINT_NAME",
                    "CONSTRAINT_SCHEMA",
                    "CONSTRUCTOR",
                    "CONSTRUCTORS",
                    "CONTAINS",
                    "CONVERT",
                    "CORR",
                    "CURRENT_COLLATION",
                    "CURSOR_NAME",
                    "DATETIME_INTERVAL_CODE",
                    "DATETIME_INTERVAL_PRECISION",
                    "DEFAULTS",
                    "DEFINED",
                    "DEFINER",
                    "DEGREE",
                    "DEPTH",
                    "DESCRIPTOR",
                    "DIAGNOSTICS",
                    "DISPATCH",
                    "DOMAIN",
                    "DYNAMIC_FUNCTION",
                    "DYNAMIC_FUNCTION_CODE",
                    "EQUALS",
                    "EXCEPTION",
                    "EXCLUDE",
                    "FIRST",
                    "FORTRAN",
                    "FOUND",
                    "GENERAL",
                    "GO",
                    "GOTO",
                    "GRANTED",
                    "HIERARCHY",
                    "IMPLEMENTATION",
                    "INCREMENT",
                    "INVOKER",
                    "ISOLATION",
                    "KEY_MEMBER",
                    "KEY_TYPE",
                    "LAST",
                    "LENGTH",
                    "LEVEL",
                    "LOCATOR",
                    "MAP",
                    "MATCHED",
                    "MAXVALUE",
                    "MESSAGE_LENGTH",
                    "MESSAGE_OCTET_LENGTH",
                    "MESSAGE_TEXT",
                    "MINVALUE",
                    "MORE",
                    "MUMPS",
                    "NAME",
                    "NAMES",
                    "NESTING",
                    "NULLABLE",
                    "NULLS",
                    "NUMBER",
                    "OCTETS",
                    "ORDERING",
                    "OTHERS",
                    "OVERLAY",
                    "PAD",
                    "PARAMETER_MODE",
                    "PARAMETER_NAME",
                    "PARAMETER_ORDINAL_POSITION",
                    "PARAMETER_SPECIFIC_CATALOG",
                    "PARAMETER_SPECIFIC_NAME",
                    "PARAMETER_SPECIFIC_SCHEMA",
                    "PARTIAL",
                    "PASCAL",
                    "PATH",
                    "PLACING",
                    "PLI",
                    "POSITION",
                    "PRESERVE",
                    "PRIOR",
                    "PRIVILEGES",
                    "PUBLIC",
                    "READ",
                    "RELATIVE",
                    "RESTART",
                    "RETURNED_CARDINALITY",
                    "RETURNED_LENGTH",
                    "RETURNED_OCTET_LENGTH",
                    "RETURNED_SQLSTATE",
                    "ROLE",
                    "ROUTINE",
                    "ROUTINE_CATALOG",
                    "ROUTINE_NAME",
                    "ROUTINE_SCHEMA",
                    "ROW_COUNT",
                    "SCALE",
                    "SCHEMA_NAME",
                    "SCOPE_CATALOG",
                    "SCOPE_NAME",
                    "SCOPE_SCHEMA",
                    "SECTION",
                    "SECURITY",
                    "SELF",
                    "SEQUENCE",
                    "SERIALIZABLE",
                    "SERVER_NAME",
                    "SESSION",
                    "SETS",
                    "SIZE",
                    "SOURCE",
                    "SPACE",
                    "SPECIFIC_NAME",
                    "STATE",
                    "STATEMENT",
                    "STRUCTURE",
                    "SUBCLASS_ORIGIN",
                    "TABLE_NAME",
                    "TIES",
                    "TOP_LEVEL_COUNT",
                    "TRANSACTION",
                    "TRANSACTIONS_COMMITTED",
                    "TRANSACTIONS_ROLLED_BACK",
                    "TRANSACTION_ACTIVE",
                    "TRANSFORM",
                    "TRANSFORMS",
                    "TRANSLATE",
                    "TRIGGER_CATALOG",
                    "TRIGGER_NAME",
                    "TRIGGER_SCHEMA",
                    "UNCOMMITTED",
                    "UNNAMED",
                    "USAGE",
                    "USER_DEFINED_TYPE_CATALOG",
                    "USER_DEFINED_TYPE_CODE",
                    "USER_DEFINED_TYPE_NAME",
                    "USER_DEFINED_TYPE_SCHEMA",
                    "WORK",
                    "WRITE"
            };

    private static final String KEYWORD_GENERATED_START = "/** >> Generated by KeywordGenerator */" + System.lineSeparator();
    private static final String KEYWORD_GENERATED_END = "/** << Generated by KeywordGenerator */" + System.lineSeparator();
    private static final String ENUM_GENERATED_START = "// >> Generated by KeywordGenerator" + System.lineSeparator();
    private static final String ENUM_GENERATED_END = "// << Generated by KeywordGenerator" + System.lineSeparator();
    private String keywordHeader = null;
    private String keywordFooter = null;
    private String enumHeader = null;
    private String enumFooter = null;

    private void readKeywordHeaderAndFooter(BufferedReader reader) throws IOException {
        StringBuilder header = new StringBuilder();
        StringBuilder footer = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null && !line.trim()
                                                          .equals(KEYWORD_GENERATED_START.trim())) {
            header.append(line)
                  .append(System.lineSeparator());
        }

        while ((line = reader.readLine()) != null && !line.trim()
                                                          .equals(KEYWORD_GENERATED_END.trim())) {
            // intentionally empty to skip lines between headers and footer
        }

        while ((line = reader.readLine()) != null) {
            footer.append(line)
                  .append(System.lineSeparator());
        }

        keywordHeader = header.toString();
        keywordFooter = footer.toString();
    }

    private void readEnumHeaderAndFooter(BufferedReader reader) throws IOException {
        StringBuilder header = new StringBuilder();
        StringBuilder footer = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null && !line.trim()
                                                          .contains(ENUM_GENERATED_START.trim())) {
            header.append(line)
                  .append(System.lineSeparator());
        }

        while ((line = reader.readLine()) != null && !line.trim()
                                                          .contains(ENUM_GENERATED_END.trim())) {
        }

        while ((line = reader.readLine()) != null) {
            footer.append(line)
                  .append(System.lineSeparator());
        }

        enumHeader = header.toString();
        enumFooter = footer.toString();
    }

    private void writeKeywordHeader(Writer wr)
            throws IOException {
        wr.write(keywordHeader);
        wr.write(KEYWORD_GENERATED_START);
    }

    private void writeKeywordFooter(Writer wr)
            throws IOException {
        wr.write(KEYWORD_GENERATED_END);
        wr.write(keywordFooter);
    }

    private void writeEnumHeader(Writer wr)
            throws IOException {
        wr.write(enumHeader);
        wr.write(ENUM_GENERATED_START);
    }

    private void writeEnumFooter(Writer wr)
            throws IOException {
        wr.write(ENUM_GENERATED_END);
        wr.write(enumFooter);
    }

    private String getRule(String keyword) {
        String rule = keyword.replace("-", "_")
                             .replace("2", "SQUARED");
        /* ANTLR4 reserved word */
        if (rule.equalsIgnoreCase("more"))
            rule = keyword + "_";
        return rule;
    }

    private String formatKeywordLine(String keyword) {
        StringBuilder keywordLine = new StringBuilder();
        String rule = getRule(keyword);
        keywordLine.append(rule);
        keywordLine.append(" :");
        if (rule.length() > 1) {
            for (int j = 0; j < keyword.length(); j++) {
                keywordLine.append(" ");
                char c = keyword.charAt(j);
                if (c == '-')
                    keywordLine.append("MINUS_SIGN");
                else if (c == '_')
                    keywordLine.append("UNDERSCORE");
                else if (c == '2')
                    keywordLine.append("TWO");
                else if (Character.isUpperCase(c))
                    keywordLine.append(c);
                else
                    throw new IllegalArgumentException("Unexpected keyword character!");
            }
        } else {
            keywordLine.append(" '")
                       .append(rule.toUpperCase())
                       .append("' | '")
                       .append(rule.toLowerCase())
                       .append("'");
        }
        keywordLine.append(";");
        return keywordLine.toString();
    }

    private String formatEnumLine(String keyword, boolean isReserved, boolean isLast) {
        StringBuilder keywordLine = new StringBuilder();
        keywordLine.append("  ")
                   .append(keyword.replace('-', '_'))
                   .append("(\"")
                   .append(keyword)
                   .append("\", ")
                   .append(String.valueOf(isReserved))
                   .append(")");
        if (!isLast) {
            keywordLine.append(",");
        } else {
            keywordLine.append(";");
        }
        return keywordLine.toString();
    }

    private void writeKeywordIdentifier(Writer writer) throws IOException {
        writer.write(System.lineSeparator() + "IDENTIFIER: REGULAR_IDENTIFIER |  NON_RESERVED_KEYWORD | DELIMITED_IDENTIFIER;" + System.lineSeparator() + System.lineSeparator());
    }

    private void writeKeywords(Writer enumeWriter, Writer keywordWriter, String[] keywords, Set<String> keywordsSet, boolean isReserved, boolean isLast) throws IOException {
        Set<String> reserverSet = null;
        if (isReserved) {
            keywordWriter.write("/** Reserved Keywords: */" + System.lineSeparator());
            enumeWriter.write("  // Reserved Keywords:" + System.lineSeparator());
        } else {
            reserverSet = new HashSet<String>(keywordsSet);
            keywordWriter.write("/** Non-Reserved Keywords: */" + System.lineSeparator());
            enumeWriter.write("  // Non-Reserved Keywords:" + System.lineSeparator());
        }
        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];
            // throw out duplicates
            if (!keywordsSet.contains(keyword)) {
                keywordsSet.add(keyword);
                keywordWriter.write(formatKeywordLine(keyword) + System.lineSeparator());
                boolean isLastLine = isLast && (i == (keywords.length - 1));
                enumeWriter.write(formatEnumLine(keyword, isReserved, isLastLine) + System.lineSeparator());
            }
        }
        if (!isReserved) {
            keywordWriter.write(System.lineSeparator() + "NON_RESERVED_KEYWORD" + System.lineSeparator());
            for (int i = 0; i < keywords.length; i++) {
                String keyword = keywords[i];
                // throw out duplicates
                if (!reserverSet.contains(keyword)) {
                    reserverSet.add(keyword);
                    String sRule = getRule(keyword);
                    keywordWriter.write("  ");
                    if (i == 0) {
                        keywordWriter.write(": " + sRule + System.lineSeparator());
                    } else {
                        keywordWriter.write("| " + sRule + System.lineSeparator());
                    }
                }
            }
            keywordWriter.write("  ;" + System.lineSeparator());
        }
    }

    private KeywordGenerator() throws IOException {
        try (BufferedReader enumReader = new BufferedReader(new FileReader(ENUM_FILE_PATH))) {
            readEnumHeaderAndFooter(enumReader);
        }

        try (BufferedReader keywordReader = new BufferedReader(new FileReader(KEYWORD_FILE_PATH))) {
            readKeywordHeaderAndFooter(keywordReader);
        }

        FileWriter enumWriter = new FileWriter(ENUM_FILE_PATH);
        FileWriter keywordWriter = new FileWriter(KEYWORD_FILE_PATH);

        writeEnumHeader(enumWriter);
        writeKeywordHeader(keywordWriter);

        Set<String> keywords = new HashSet<>();
        writeKeywords(enumWriter, keywordWriter, RESERVER_WORDS, keywords, true, false);
        writeKeywordIdentifier(keywordWriter);
        writeKeywords(enumWriter, keywordWriter, NON_RESERVER_WORDS, keywords, false, true);

        writeKeywordFooter(keywordWriter);
        writeEnumFooter(enumWriter);
        keywordWriter.close();
        enumWriter.close();
    }

    public static void main(String[] args) {
        try {
            new KeywordGenerator();
            System.exit(0);

        } catch (Exception e) {
            System.err.println(e.getClass()
                                .getName() + ": " + e.getMessage());
            System.exit(8);
        }
    }

}
