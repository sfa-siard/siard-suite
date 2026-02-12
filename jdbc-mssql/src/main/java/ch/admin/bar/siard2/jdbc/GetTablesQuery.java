package ch.admin.bar.siard2.jdbc;

import ch.enterag.sqlparser.SqlLiterals;

// understands how to get tables, views, table types and system tables of an MS SQL Server Database
class GetTablesQuery {

    private final String[] types;
    private final String catalog;
    private final String schemaPattern;
    private final String tableNamePattern;
    private final String searchStringEscape;

    public GetTablesQuery(
            String catalog,
            String schemaPattern,
            String tableNamePattern,
            String[] types,
            String searchStringEscape
    ) {
        this.types = types == null ? new String[]{"TABLE", "VIEW", "TABLE TYPE", "SYSTEM TABLE"} : types;
        this.catalog = catalog;
        this.schemaPattern = schemaPattern;
        this.tableNamePattern = tableNamePattern;
        this.searchStringEscape = searchStringEscape;
    }

    String build() {
        return "SELECT\r\n" + "  DB_NAME() AS TABLE_CAT,\r\n" +
                "  s.name AS TABLE_SCHEM,\r\n" +
                "  o.name AS TABLE_NAME,\r\n" +
                getCaseTableType() +
                " AS TABLE_TYPE,\r\n" +
                " STUFF((SELECT DISTINCT '| ' + CONVERT(VARCHAR, p.value)\r\n" +
                " FROM sys.extended_properties p\r\n" +
                " WHERE (p.major_id = o.object_id and p.minor_id = 0 and p.class = 1)\r\n" +
                " FOR XML PATH ('')), 1, 2, '') AS REMARKS,\r\n" +
                "  NULL AS TYPE_CAT,\r\n" +
                "  NULL AS TYPE_SCHEM,\r\n" +
                "  NULL AS TYPE_NAME,\r\n" +
                "  NULL AS SELF_REFERENCING_COL_NAME,\r\n" +
                "  NULL AS REF_GENERATION,\r\n" +
                "  m.definition as " + MsSqlDatabaseMetaData._sQUERY_TEXT + "\r\n" +
                "FROM sys.all_objects o\r\n" +
                "  JOIN sys.schemas s\r\n" +
                "    ON (o.schema_id = s.schema_id)\r\n" +
                "  LEFT JOIN sys.sql_modules m\r\n" +
                "    ON (m.object_id = o.object_id)\r\n" +
                "WHERE\r\n" +
                getCondition() +
                "ORDER BY TABLE_TYPE, TABLE_CAT, TABLE_SCHEM, TABLE_NAME";
    }

    private static String getCaseTableType() {
        return "  case o.type\r\n" + "    when 'U' then 'TABLE'\r\n" +
                "    when 'V' then 'VIEW'\r\n" +
                "    when 'TT' then 'TABLE TYPE'\r\n" +
                "    else 'SYSTEM TABLE'\r\n" +
                "  end";
    }

    private StringBuilder getCondition() {
        StringBuilder queryCondition = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            if (i == 0) {
                if (types.length > 1) queryCondition.append(" (");
            } else queryCondition.append("  OR ");
            queryCondition.append("o.type = '");
            switch (types[i]) {
                case "TABLE":
                    queryCondition.append("U");
                    break;
                case "VIEW":
                    queryCondition.append("V");
                    break;
                case "TABLE TYPE":
                    queryCondition.append("TT");
                    break;
                case "SYSTEM TABLE":
                    queryCondition.append("S");
                    break;
            }
            queryCondition.append("' AND is_ms_shipped = ");
            if (!types[i].equals("SYSTEM TABLE")) queryCondition.append("0");
            else queryCondition.append("1");
            if (i == types.length - 1) {
                if (types.length > 1) queryCondition.append(")");
            }
            queryCondition.append("\r\n");
        }
        if (catalog != null)
            queryCondition.append(" AND DB_NAME() = ").append(SqlLiterals.formatStringLiteral(catalog)).append("\r\n");
        if (schemaPattern != null)
            queryCondition.append(" AND s.name LIKE ")
                       .append(SqlLiterals.formatStringLiteral(schemaPattern))
                       .append(" ESCAPE '")
                       .append(searchStringEscape)
                       .append("'\r\n");
        if (tableNamePattern != null)
            queryCondition.append(" AND o.name LIKE ")
                       .append(SqlLiterals.formatStringLiteral(tableNamePattern))
                       .append(" ESCAPE '")
                       .append(searchStringEscape)
                       .append("'\r\n");
        return queryCondition;
    }
}