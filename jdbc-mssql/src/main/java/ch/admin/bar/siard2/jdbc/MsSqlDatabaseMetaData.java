/*======================================================================
MsSqlDatabaseMetaData implements wrapped MSSQL DatabaseMetaData.
Version     : $Id: $
Application : SIARD2
Description : MsSqlDatabaseMetaData implements wrapped MSSQL DatabaseMetaData.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 01.06.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import ch.admin.bar.siard2.mssql.MsSqlType;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MsSqlDatabaseMetaData implements wrapped MSSQL DatabaseMetaData.
 *
 * @author Hartwig Thomas
 */
public class MsSqlDatabaseMetaData
        extends BaseDatabaseMetaData
        implements DatabaseMetaData {
    private final MsSqlConnection _conn;

    private static final Map<Integer, Integer> _mapTYPE_MSSQL_TO_JAVA = new HashMap<>();
    private static final Map<Integer, Integer> _mapTYPE_TO_ISO = new HashMap<>();

    static {
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.IMAGE.getSystemTypeId(), Types.BLOB);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.TEXT.getSystemTypeId(), Types.CLOB);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.UUID.getSystemTypeId(), Types.CHAR);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.DATE.getSystemTypeId(), Types.DATE);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.TIME.getSystemTypeId(), Types.TIME);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.DATETIME2.getSystemTypeId(), Types.TIMESTAMP);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.DATETIMEOFFSET.getSystemTypeId(), Types.OTHER);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.TINYINT.getSystemTypeId(), Types.TINYINT);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.SMALLINT.getSystemTypeId(), Types.SMALLINT);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.INTEGER.getSystemTypeId(), Types.INTEGER);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.SMALLDATETIME.getSystemTypeId(), Types.TIMESTAMP);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.REAL.getSystemTypeId(), Types.REAL);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.MONEY.getSystemTypeId(), Types.DECIMAL);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.DATETIME.getSystemTypeId(), Types.TIMESTAMP);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.FLOAT.getSystemTypeId(), Types.FLOAT);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.SQL_VARIANT.getSystemTypeId(), Types.OTHER);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.NTEXT.getSystemTypeId(), Types.NCLOB);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.BIT.getSystemTypeId(), Types.BOOLEAN);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.DECIMAL.getSystemTypeId(), Types.DECIMAL);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.NUMERIC.getSystemTypeId(), Types.NUMERIC);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.SMALLMONEY.getSystemTypeId(), Types.DECIMAL);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.BIGINT.getSystemTypeId(), Types.BIGINT);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.VARBINARY.getSystemTypeId(), Types.VARBINARY);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.VARCHAR.getSystemTypeId(), Types.VARCHAR);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.BINARY.getSystemTypeId(), Types.BINARY);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.CHAR.getSystemTypeId(), Types.CHAR);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.TIMESTAMP.getSystemTypeId(), Types.BINARY);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.NVARCHAR.getSystemTypeId(), Types.NVARCHAR);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.NCHAR.getSystemTypeId(), Types.NCHAR);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.CLRUDT.getSystemTypeId(), Types.JAVA_OBJECT);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.XML.getSystemTypeId(), Types.SQLXML);
        _mapTYPE_MSSQL_TO_JAVA.put(MsSqlType.TABLEUDT.getSystemTypeId(), Types.STRUCT);

        _mapTYPE_TO_ISO.put(Types.CHAR, Types.CHAR);
        _mapTYPE_TO_ISO.put(Types.VARCHAR, Types.VARCHAR);
        _mapTYPE_TO_ISO.put(Types.CLOB, Types.CLOB);
        _mapTYPE_TO_ISO.put(Types.NCHAR, Types.NCHAR);
        _mapTYPE_TO_ISO.put(Types.NVARCHAR, Types.NVARCHAR);
        _mapTYPE_TO_ISO.put(Types.NCLOB, Types.NCLOB);
        _mapTYPE_TO_ISO.put(Types.SQLXML, Types.SQLXML);
        _mapTYPE_TO_ISO.put(Types.VARBINARY, Types.VARBINARY);
        _mapTYPE_TO_ISO.put(Types.BINARY, Types.BINARY);
        _mapTYPE_TO_ISO.put(Types.BLOB, Types.BLOB);
        _mapTYPE_TO_ISO.put(Types.DECIMAL, Types.DECIMAL);
        _mapTYPE_TO_ISO.put(Types.NUMERIC, Types.NUMERIC);
        _mapTYPE_TO_ISO.put(Types.TINYINT, Types.SMALLINT);
        _mapTYPE_TO_ISO.put(Types.SMALLINT, Types.SMALLINT);
        _mapTYPE_TO_ISO.put(Types.INTEGER, Types.INTEGER);
        _mapTYPE_TO_ISO.put(Types.BIGINT, Types.BIGINT);
        _mapTYPE_TO_ISO.put(Types.REAL, Types.REAL);
        _mapTYPE_TO_ISO.put(Types.FLOAT, Types.FLOAT);
        _mapTYPE_TO_ISO.put(Types.BOOLEAN, Types.BOOLEAN);
        _mapTYPE_TO_ISO.put(Types.DATE, Types.DATE);
        _mapTYPE_TO_ISO.put(Types.TIME, Types.TIME);
        _mapTYPE_TO_ISO.put(Types.TIMESTAMP, Types.TIMESTAMP);
        _mapTYPE_TO_ISO.put(Types.OTHER, Types.BLOB);
        _mapTYPE_TO_ISO.put(Types.JAVA_OBJECT, Types.BLOB);
    }

    private class MetaSpecificName extends MsSqlResultSet {
        private static final String sSPECIFIC_NAME = "SPECIFIC_NAME";
        private int _iSpecificName = -1;
        private int _iProcedureName = -1;

        public MetaSpecificName(ResultSet rsWrapped, MsSqlConnection conn,
                                int iProcedureName, int iSpecificName) {
            super(rsWrapped, conn);
            _iSpecificName = iSpecificName;
            _iProcedureName = iProcedureName;
        }

        @Override
        public String getString(int columnIndex) throws SQLException {
            if (columnIndex == _iSpecificName)
                columnIndex = _iProcedureName;
            return super.getString(columnIndex);
        }

        @Override
        public String getString(String columnLabel) throws SQLException {
            String sResult;
            if (columnLabel.equals(sSPECIFIC_NAME)) {
                /**
                 ResultSetMetaData rsmd = getMetaData();
                 for (int i = 1; i <= rsmd.getColumnCount(); i++)
                 {
                 System.out.println(String.valueOf(i)+" "+rsmd.getColumnName(i)+
                 ": "+getString(rsmd.getColumnLabel(i)));
                 }
                 */
                sResult = getString(_iSpecificName);
            } else
                sResult = super.getString(columnLabel);
            return sResult;
        }
    }

    /**
     * constructor
     *
     * @param dmdWrapped database meta data to be wrapped.
     */
    public MsSqlDatabaseMetaData(DatabaseMetaData dmdWrapped, MsSqlConnection conn) {
        super(dmdWrapped);
        _conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        return _conn;
    }

    /**
     * {@inheritDoc}
     * Use MsSqlMetaColumn for data type translation.
     */
    @Override
    public ResultSet getColumns(String catalog, String schemaPattern,
                                String tableNamePattern, String columnNamePattern)
            throws SQLException {
        return new MsSqlMetaColumns(super.getColumns(catalog, schemaPattern,
                tableNamePattern, columnNamePattern), _conn,
                1, 2, 5, 6, 7, 7, 9);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getTypeInfo() throws SQLException {
        return super.getTypeInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern,
                                   String procedureNamePattern) throws SQLException {
        return new MetaSpecificName(
                super.getProcedures(catalog, schemaPattern, procedureNamePattern),
                _conn, 3, 9);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getProcedureColumns(String catalog,
                                         String schemaPattern, String procedureNamePattern,
                                         String columnNamePattern) throws SQLException {
        return new MetaSpecificName(
                new MsSqlMetaColumns(
                        super.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern),
                        _conn, 1, 2, 6, 7, 8, 9, 10),
                _conn, 3, 20);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern,
                               String[] types) throws SQLException {
        return getConnection().createStatement()
                .unwrap(Statement.class)
                .executeQuery(new GetTablesQuery(catalog, schemaPattern, tableNamePattern, types, getSearchStringEscape()).build());
    }

    @Override
    public ResultSet getTableTypes()
            throws SQLException {
        return super.getTableTypes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern,
                             String typeNamePattern, int[] types) throws SQLException {
        StringBuilder sbCaseDataType = new StringBuilder("case t.system_type_id\r\n");
        sbCaseDataType.append("when ");
        sbCaseDataType.append(MsSqlType.CLRUDT.getSystemTypeId());
        sbCaseDataType.append(" then ");
        sbCaseDataType.append(Types.JAVA_OBJECT); // using "JAVA_OBJECT" for "CLR UDT"
        sbCaseDataType.append("\r\n");
        sbCaseDataType.append("when ");
        sbCaseDataType.append(MsSqlType.TABLEUDT.getSystemTypeId());
        sbCaseDataType.append(" then ");
        sbCaseDataType.append(Types.STRUCT);
        sbCaseDataType.append("\r\n");
        sbCaseDataType.append("else ");
        sbCaseDataType.append(Types.DISTINCT);
        sbCaseDataType.append("\r\nend");

        // Type name may be "fully qualified". In that case it cannot be "as it is stored in the database"!
        try {
            // attempt to parse the typeNamePattern as a fully qualified name.
            QualifiedId qi = new QualifiedId(typeNamePattern);
            // if it succeeds and is qualified at least by a schema, then overwrite schemaPattern and catalog
            if (qi.getSchema() != null) {
                schemaPattern = qi.getSchema();
                catalog = qi.getCatalog();
            }
        } catch (ParseException ignored) {
        }
        StringBuilder sbCondition = new StringBuilder("t.system_type_id <> t.user_type_id\r\n");
        if (catalog != null)
            sbCondition.append("AND DB_NAME() = " + SqlLiterals.formatStringLiteral(catalog) + "\r\n");
        if (schemaPattern != null)
            sbCondition.append("AND s.name LIKE " + SqlLiterals.formatStringLiteral(schemaPattern) + " ESCAPE '" + getSearchStringEscape() + "'\r\n");
        if (typeNamePattern != null)
            sbCondition.append("AND t.name LIKE " + SqlLiterals.formatStringLiteral(typeNamePattern) + " ESCAPE '" + getSearchStringEscape() + "'\r\n");
        if (types != null) {
            StringBuilder sbTypesSet = new StringBuilder();
            for (int i = 0; i < types.length; i++) {
                if (i > 0)
                    sbTypesSet.append(", ");
                sbTypesSet.append(types[i]);
            }
            if (sbTypesSet.length() > 0)
                sbCondition.append("AND " + sbCaseDataType + " IN (" + sbTypesSet + ")\r\n");
        }

        StringBuilder sbCaseBaseType = new StringBuilder("case t.system_type_id\r\n");
        for (int i = 0; i < MsSqlType.values().length; i++) {
            MsSqlType mst = MsSqlType.values()[i];
            int iType = _mapTYPE_MSSQL_TO_JAVA.get(mst.getSystemTypeId());
            if (iType != Types.STRUCT) {
                if (i > 0)
                    sbCaseBaseType.append("\r\n");
                sbCaseBaseType.append("when ");
                sbCaseBaseType.append(mst.getSystemTypeId());
                sbCaseBaseType.append(" then ");
                sbCaseBaseType.append(_mapTYPE_TO_ISO.get(iType).toString());
            }
        }
        sbCaseBaseType.append("\r\nelse NULL\r\n");
        sbCaseBaseType.append("end");

        String sSql = "select\r\n" +
                "DB_NAME() AS TYPE_CAT,\r\n" +
                "s.name AS TYPE_SCHEM,\r\n" +
                "t.name AS TYPE_NAME,\r\n" +
                "at.assembly_qualified_name AS CLASSNAME,\r\n" + // is really the CLR class ...
                sbCaseDataType + " AS DATA_TYPE,\r\n" +
                "NULL AS REMARKS,\r\n" +
                sbCaseBaseType + " AS BASE_TYPE\r\n" +
                "from\r\n" +
                "sys.schemas s\r\n" +
                "  INNER JOIN\r\n" +
                "(sys.types t\r\n" +
                "   LEFT JOIN\r\n" +
                " sys.assembly_types at\r\n" +
                " ON t.user_type_id = at.user_type_id\r\n" +
                ")\r\n" +
                "ON s.schema_id = t.schema_id\r\n" +
                "WHERE " + sbCondition +
                "ORDER BY DATA_TYPE, TYPE_CAT, TYPE_SCHEM, TYPE_NAME";
        Statement stmt = getConnection().createStatement();
        return stmt.unwrap(Statement.class).executeQuery(sSql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern,
                                   String typeNamePattern, String attributeNamePattern)
            throws SQLException {
        return new MsSqlMetaColumns(
                super.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern),
                _conn, 1, 2, 5, 6, 7, 7, 8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern,
                                  String functionNamePattern) throws SQLException {
        return new MetaSpecificName(
                super.getFunctions(catalog, schemaPattern, functionNamePattern),
                _conn, 3, 6);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getFunctionColumns(String catalog,
                                        String schemaPattern, String functionNamePattern,
                                        String columnNamePattern) throws SQLException {
        return new MetaSpecificName(
                new MsSqlMetaColumns(
                        super.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern),
                        _conn, 1, 2, 6, 7, 8, 9, 10),
                _conn, 3, 17);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet getCrossReference(
            String parentCatalog, String parentSchema, String parentTable,
            String foreignCatalog, String foreignSchema, String foreignTable)
            throws SQLException {
        String sSql = "SELECT\r\n" +
                "  TCPK.TABLE_CATALOG AS PKTABLE_CAT,\r\n" +
                "  TCPK.TABLE_SCHEMA AS PKTABLE_SCHEM,\r\n" +
                "  TCPK.TABLE_NAME AS PKTABLE_NAME,\r\n" +
                "  KCUPK.COLUMN_NAME AS PKCOLUMN_NAME,\r\n" +
                "  TCFK.TABLE_CATALOG AS FKTABLE_CAT,\r\n" +
                "  TCFK.TABLE_SCHEMA AS FKTABLE_SCHEM,\r\n" +
                "  TCFK.TABLE_NAME AS FKTABLE_NAME,\r\n" +
                "  KCUFK.COLUMN_NAME AS FKCOLUMN_NAME,\r\n" +
                "  KCUFK.ORDINAL_POSITION AS KEY_SEQ,\r\n" +
                "  CASE RC.UPDATE_RULE\r\n" +
                "    WHEN 'NO ACTION' THEN " + DatabaseMetaData.importedKeyNoAction + "\r\n" +
                "    WHEN 'CASCADE' THEN " + DatabaseMetaData.importedKeyCascade + "\r\n" +
                "    WHEN 'SET NULL' THEN " + DatabaseMetaData.importedKeySetNull + "\r\n" +
                "    WHEN 'SET DEFAULT' THEN " + DatabaseMetaData.importedKeySetDefault + "\r\n" +
                "    WHEN 'RESTRICT' THEN " + DatabaseMetaData.importedKeyRestrict + "\r\n" +
                "  END AS UPDATE_RULE,\r\n" +
                "  CASE RC.DELETE_RULE\r\n" +
                "    WHEN 'NO ACTION' THEN " + DatabaseMetaData.importedKeyNoAction + "\r\n" +
                "    WHEN 'CASCADE' THEN " + DatabaseMetaData.importedKeyCascade + "\r\n" +
                "    WHEN 'SET NULL' THEN " + DatabaseMetaData.importedKeySetNull + "\r\n" +
                "    WHEN 'SET DEFAULT' THEN " + DatabaseMetaData.importedKeySetDefault + "\r\n" +
                "    WHEN 'RESTRICT' THEN " + DatabaseMetaData.importedKeyRestrict + "\r\n" +
                "  END AS DELETE_RULE,\r\n" +
                "  TCFK.CONSTRAINT_NAME AS FK_NAME,\r\n" +
                "  TCPK.CONSTRAINT_NAME AS PK_NAME,\r\n" +
                "  CASE\r\n" +
                "    WHEN TCFK.IS_DEFERRABLE = 'NO' THEN " + DatabaseMetaData.importedKeyNotDeferrable + "\r\n" +
                "    WHEN TCFK.IS_DEFERRABLE = 'YES' AND TCFK.INITIALLY_DEFERRED = 'YES' THEN " + DatabaseMetaData.importedKeyInitiallyDeferred + "\r\n" +
                "    ELSE " + DatabaseMetaData.importedKeyInitiallyImmediate + "\r\n" +
                "  END AS DEFERRABILITY\r\n" +
                "FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS RC\r\n" +
                "  INNER JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS TCFK\r\n" +
                "    ON (RC.CONSTRAINT_CATALOG = TCFK.CONSTRAINT_CATALOG AND\r\n" +
                "        RC.CONSTRAINT_SCHEMA = TCFK.CONSTRAINT_SCHEMA AND\r\n" +
                "        RC.CONSTRAINT_NAME = TCFK.CONSTRAINT_NAME AND\r\n" +
                "        TCFK.CONSTRAINT_TYPE = 'FOREIGN KEY')\r\n" +
                "  INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE KCUFK\r\n" +
                "    ON (RC.CONSTRAINT_CATALOG = KCUFK.CONSTRAINT_CATALOG AND\r\n" +
                "        RC.CONSTRAINT_SCHEMA = KCUFK.CONSTRAINT_SCHEMA AND\r\n" +
                "        RC.CONSTRAINT_NAME = KCUFK.CONSTRAINT_NAME)\r\n" +
                "  INNER JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS TCPK\r\n" +
                "    ON (RC.UNIQUE_CONSTRAINT_CATALOG = TCPK.CONSTRAINT_CATALOG AND\r\n" +
                "        RC.UNIQUE_CONSTRAINT_SCHEMA = TCPK.CONSTRAINT_SCHEMA AND\r\n" +
                "        RC.UNIQUE_CONSTRAINT_NAME = TCPK.CONSTRAINT_NAME)\r\n" +
                "  INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE KCUPK\r\n" +
                "    ON (RC.UNIQUE_CONSTRAINT_CATALOG = KCUPK.CONSTRAINT_CATALOG AND\r\n" +
                "        RC.UNIQUE_CONSTRAINT_SCHEMA = KCUPK.CONSTRAINT_SCHEMA AND\r\n" +
                "        RC.UNIQUE_CONSTRAINT_NAME = KCUPK.CONSTRAINT_NAME)\r\n";
        StringBuilder sbCondition = new StringBuilder();
        if (parentCatalog != null) {
            if (sbCondition.length() > 0) sbCondition.append(" AND ");
            sbCondition.append("TCPK.TABLE_CATALOG = " + SqlLiterals.formatStringLiteral(parentCatalog));
        }
        if (parentSchema != null) {
            if (sbCondition.length() > 0) sbCondition.append(" AND ");
            sbCondition.append("TCPK.TABLE_SCHEMA = " + SqlLiterals.formatStringLiteral(parentSchema));
        }
        if (parentTable != null) {
            if (sbCondition.length() > 0) sbCondition.append(" AND ");
            sbCondition.append("TCPK.TABLE_NAME = " + SqlLiterals.formatStringLiteral(parentTable));
        }
        if (foreignCatalog != null) {
            if (sbCondition.length() > 0) sbCondition.append(" AND ");
            sbCondition.append("TCFK.TABLE_CATALOG = " + SqlLiterals.formatStringLiteral(foreignCatalog));
        }
        if (foreignSchema != null) {
            if (sbCondition.length() > 0) sbCondition.append(" AND ");
            sbCondition.append("TCFK.TABLE_SCHEMA = " + SqlLiterals.formatStringLiteral(foreignSchema));
        }
        if (foreignTable != null) {
            if (sbCondition.length() > 0) sbCondition.append(" AND ");
            sbCondition.append("TCFK.TABLE_NAME = " + SqlLiterals.formatStringLiteral(foreignTable));
        }
        if (sbCondition.length() > 0)
            sSql = sSql + "WHERE " + sbCondition + "\r\n" +
                    "ORDER BY FKTABLE_SCHEM, FKTABLE_NAME, KEY_SEQ";
        // "ORDER BY TC.TABLE_SCHEMA, TC.TABLE_NAME,KCU.POSITION_IN_UNIQUE_CONSTRAINT";
        Statement stmt = this.getConnection().createStatement();
        return stmt.unwrap(Statement.class).executeQuery(sSql);
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT\r\n");
        sb.append("  SCHEMA_NAME AS TABLE_SCHEM,\r\n");
        sb.append("  CATALOG_NAME AS TABLE_CATALOG\r\n");
        sb.append("FROM INFORMATION_SCHEMA.SCHEMATA\r\n");

        List<String> whereClauses = new ArrayList<>();

        if (catalog != null) {
            whereClauses.add("CATALOG_NAME = " + SqlLiterals.formatStringLiteral(catalog));
        }

        if (schemaPattern != null) {
            whereClauses.add("SCHEMA_NAME LIKE " + SqlLiterals.formatStringLiteral(schemaPattern) +
                    " ESCAPE '" + getSearchStringEscape() + "'");
        }

        if (!whereClauses.isEmpty()) {
            sb.append("WHERE ");
            sb.append(String.join(" AND ", whereClauses));
            sb.append("\r\n");
        }

        sb.append("ORDER BY TABLE_CATALOG, TABLE_SCHEM");

        Statement stmt = getConnection().createStatement();
        return stmt.unwrap(Statement.class).executeQuery(sb.toString());
    }


}
