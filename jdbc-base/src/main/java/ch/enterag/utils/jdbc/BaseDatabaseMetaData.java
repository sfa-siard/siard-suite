package ch.enterag.utils.jdbc;

import java.sql.*;

/**
 * BaseDatabaseMetaData implements wrapped DatabaseMetaData and serves
 * as a base for derived JDBC wrappers.
 */
public abstract class BaseDatabaseMetaData implements DatabaseMetaData {

    public static final String _sQUERY_TEXT = "QUERY_TEXT";

    public String toPattern(String sIdentifier)
            throws SQLException {
        String sPattern = sIdentifier;
        if (sPattern != null) {
            if (!sPattern.isEmpty()) {
                sPattern = sPattern.
                        replace(getSearchStringEscape(), getSearchStringEscape() + getSearchStringEscape()).
                        replace("%", getSearchStringEscape() + "%").
                        replace("_", getSearchStringEscape() + "_");
            } else
                sPattern = "%";
        }
        return sPattern;
    }

    private final DatabaseMetaData _dmdWrapped;

    /**
     * convert an AbstractMethodError into an SQLFeatureNotSupportedException.
     * This error indicates that the JDBC driver wrapped implements an
     * earlier version of JDBC which did not include this method.
     *
     * @param ame - AbstractMethodError
     * @throws SQLFeatureNotSupportedException - thrown if the method is not supported
     */
    private void throwUndefinedMethod(AbstractMethodError ame)
            throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Undefined JDBC method!", ame);
    }

    /**
     * @param dmdWrapped database meta data to be wrapped.
     */
    public BaseDatabaseMetaData(DatabaseMetaData dmdWrapped) {
        _dmdWrapped = dmdWrapped;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return _dmdWrapped.getConnection();
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return _dmdWrapped.allProceduresAreCallable();
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return _dmdWrapped.allTablesAreSelectable();
    }

    @Override
    public String getURL() throws SQLException {
        return _dmdWrapped.getURL();
    }

    @Override
    public String getUserName() throws SQLException {
        return _dmdWrapped.getUserName();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return _dmdWrapped.isReadOnly();
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return _dmdWrapped.nullsAreSortedHigh();
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return _dmdWrapped.nullsAreSortedLow();
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return _dmdWrapped.nullsAreSortedAtStart();
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return _dmdWrapped.nullsAreSortedAtEnd();
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        return _dmdWrapped.getDatabaseProductName();
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return _dmdWrapped.getDatabaseProductVersion();
    }

    @Override
    public String getDriverName() throws SQLException {
        return _dmdWrapped.getDriverName();
    }

    @Override
    public String getDriverVersion() throws SQLException {
        return _dmdWrapped.getDriverVersion();
    }

    @Override
    public int getDriverMajorVersion() {
        return _dmdWrapped.getDriverMajorVersion();
    }

    @Override
    public int getDriverMinorVersion() {
        return _dmdWrapped.getDriverMinorVersion();
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        return _dmdWrapped.usesLocalFiles();
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return _dmdWrapped.usesLocalFilePerTable();
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return _dmdWrapped.supportsMixedCaseIdentifiers();
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return _dmdWrapped.storesUpperCaseIdentifiers();
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return _dmdWrapped.storesLowerCaseIdentifiers();
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return _dmdWrapped.storesMixedCaseIdentifiers();
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers()
            throws SQLException {
        return _dmdWrapped.supportsMixedCaseQuotedIdentifiers();
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return _dmdWrapped.storesUpperCaseQuotedIdentifiers();
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return _dmdWrapped.storesLowerCaseQuotedIdentifiers();
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return _dmdWrapped.storesMixedCaseQuotedIdentifiers();
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return _dmdWrapped.getIdentifierQuoteString();
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        return _dmdWrapped.getSQLKeywords();
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return _dmdWrapped.getNumericFunctions();
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return _dmdWrapped.getStringFunctions();
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return _dmdWrapped.getSystemFunctions();
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return _dmdWrapped.getTimeDateFunctions();
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return _dmdWrapped.getSearchStringEscape();
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        return _dmdWrapped.getExtraNameCharacters();
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return _dmdWrapped.supportsAlterTableWithAddColumn();
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return _dmdWrapped.supportsAlterTableWithDropColumn();
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return _dmdWrapped.supportsColumnAliasing();
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return _dmdWrapped.nullPlusNonNullIsNull();
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        return _dmdWrapped.supportsConvert();
    }

    @Override
    public boolean supportsConvert(int fromType, int toType)
            throws SQLException {
        return _dmdWrapped.supportsConvert(fromType, toType);
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return _dmdWrapped.supportsTableCorrelationNames();
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames()
            throws SQLException {
        return _dmdWrapped.supportsDifferentTableCorrelationNames();
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return _dmdWrapped.supportsExpressionsInOrderBy();
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return _dmdWrapped.supportsOrderByUnrelated();
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        return _dmdWrapped.supportsGroupBy();
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return _dmdWrapped.supportsGroupByUnrelated();
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return _dmdWrapped.supportsGroupByBeyondSelect();
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return _dmdWrapped.supportsLikeEscapeClause();
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return _dmdWrapped.supportsMultipleResultSets();
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return _dmdWrapped.supportsMultipleTransactions();
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return _dmdWrapped.supportsNonNullableColumns();
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return _dmdWrapped.supportsMinimumSQLGrammar();
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return _dmdWrapped.supportsCoreSQLGrammar();
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return _dmdWrapped.supportsExtendedSQLGrammar();
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return _dmdWrapped.supportsANSI92EntryLevelSQL();
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return _dmdWrapped.supportsANSI92IntermediateSQL();
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return _dmdWrapped.supportsANSI92FullSQL();
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility()
            throws SQLException {
        return _dmdWrapped.supportsIntegrityEnhancementFacility();
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return _dmdWrapped.supportsOuterJoins();
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return _dmdWrapped.supportsFullOuterJoins();
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return _dmdWrapped.supportsLimitedOuterJoins();
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        return _dmdWrapped.getSchemaTerm();
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        return _dmdWrapped.getProcedureTerm();
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        return _dmdWrapped.getCatalogTerm();
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return _dmdWrapped.isCatalogAtStart();
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        return _dmdWrapped.getCatalogSeparator();
    }

    @Override
    public boolean supportsSchemasInDataManipulation()
            throws SQLException {
        return _dmdWrapped.supportsSchemasInDataManipulation();
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return _dmdWrapped.supportsSchemasInProcedureCalls();
    }

    @Override
    public boolean supportsSchemasInTableDefinitions()
            throws SQLException {
        return _dmdWrapped.supportsSchemasInTableDefinitions();
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions()
            throws SQLException {
        return _dmdWrapped.supportsSchemasInIndexDefinitions();
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions()
            throws SQLException {
        return _dmdWrapped.supportsSchemasInPrivilegeDefinitions();
    }

    @Override
    public boolean supportsCatalogsInDataManipulation()
            throws SQLException {
        return _dmdWrapped.supportsCatalogsInDataManipulation();
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return _dmdWrapped.supportsCatalogsInProcedureCalls();
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions()
            throws SQLException {
        return _dmdWrapped.supportsCatalogsInTableDefinitions();
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions()
            throws SQLException {
        return _dmdWrapped.supportsCatalogsInIndexDefinitions();
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions()
            throws SQLException {
        return _dmdWrapped.supportsCatalogsInPrivilegeDefinitions();
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return _dmdWrapped.supportsPositionedDelete();
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return _dmdWrapped.supportsPositionedUpdate();
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return _dmdWrapped.supportsSelectForUpdate();
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return _dmdWrapped.supportsStoredProcedures();
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return _dmdWrapped.supportsSubqueriesInComparisons();
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return _dmdWrapped.supportsSubqueriesInExists();
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return _dmdWrapped.supportsSubqueriesInIns();
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return _dmdWrapped.supportsSubqueriesInQuantifieds();
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return _dmdWrapped.supportsCorrelatedSubqueries();
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        return _dmdWrapped.supportsUnion();
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        return _dmdWrapped.supportsUnionAll();
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return _dmdWrapped.supportsOpenCursorsAcrossCommit();
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback()
            throws SQLException {
        return _dmdWrapped.supportsOpenCursorsAcrossRollback();
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit()
            throws SQLException {
        return _dmdWrapped.supportsOpenStatementsAcrossCommit();
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback()
            throws SQLException {
        return _dmdWrapped.supportsOpenStatementsAcrossRollback();
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return _dmdWrapped.getMaxBinaryLiteralLength();
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return _dmdWrapped.getMaxCharLiteralLength();
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return _dmdWrapped.getMaxColumnNameLength();
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return _dmdWrapped.getMaxColumnsInGroupBy();
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return _dmdWrapped.getMaxColumnsInIndex();
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return _dmdWrapped.getMaxColumnsInOrderBy();
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return _dmdWrapped.getMaxColumnsInSelect();
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return _dmdWrapped.getMaxColumnsInTable();
    }

    @Override
    public int getMaxConnections() throws SQLException {
        return _dmdWrapped.getMaxConnections();
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return _dmdWrapped.getMaxCursorNameLength();
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return _dmdWrapped.getMaxIndexLength();
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return _dmdWrapped.getMaxSchemaNameLength();
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return _dmdWrapped.getMaxProcedureNameLength();
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return _dmdWrapped.getMaxCatalogNameLength();
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return _dmdWrapped.getMaxRowSize();
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return _dmdWrapped.doesMaxRowSizeIncludeBlobs();
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return _dmdWrapped.getMaxStatementLength();
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return _dmdWrapped.getMaxStatements();
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return _dmdWrapped.getMaxTableNameLength();
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return _dmdWrapped.getMaxTablesInSelect();
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return _dmdWrapped.getMaxUserNameLength();
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return _dmdWrapped.getDefaultTransactionIsolation();
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        return _dmdWrapped.supportsTransactions();
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level)
            throws SQLException {
        return _dmdWrapped.supportsTransactionIsolationLevel(level);
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions()
            throws SQLException {
        return _dmdWrapped.supportsDataDefinitionAndDataManipulationTransactions();
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly()
            throws SQLException {
        return _dmdWrapped.supportsDataManipulationTransactionsOnly();
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit()
            throws SQLException {
        return _dmdWrapped.dataDefinitionCausesTransactionCommit();
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions()
            throws SQLException {
        return _dmdWrapped.dataDefinitionIgnoredInTransactions();
    }

    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern,
                                   String procedureNamePattern) throws SQLException {
        return _dmdWrapped.getProcedures(catalog, schemaPattern, procedureNamePattern);
    }

    @Override
    public ResultSet getProcedureColumns(String catalog,
                                         String schemaPattern, String procedureNamePattern,
                                         String columnNamePattern) throws SQLException {
        return _dmdWrapped.getProcedureColumns(catalog, schemaPattern,
                                               procedureNamePattern, columnNamePattern);
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern,
                               String tableNamePattern, String[] types) throws SQLException {
        return _dmdWrapped.getTables(catalog, schemaPattern, tableNamePattern, types);
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        return _dmdWrapped.getSchemas();
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        return _dmdWrapped.getCatalogs();
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        return _dmdWrapped.getTableTypes();
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern,
                                String tableNamePattern, String columnNamePattern)
            throws SQLException {
        return _dmdWrapped.getColumns(catalog, schemaPattern, tableNamePattern,
                                      columnNamePattern);
    }

    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema,
                                         String table, String columnNamePattern) throws SQLException {
        return _dmdWrapped.getColumnPrivileges(catalog, schema, table,
                                               columnNamePattern);
    }

    @Override
    public ResultSet getTablePrivileges(String catalog,
                                        String schemaPattern, String tableNamePattern) throws SQLException {
        return _dmdWrapped.getTablePrivileges(catalog, schemaPattern,
                                              tableNamePattern);
    }

    @Override
    public ResultSet getBestRowIdentifier(String catalog, String schema,
                                          String table, int scope, boolean nullable) throws SQLException {
        return _dmdWrapped.getBestRowIdentifier(catalog, schema, table, scope,
                                                nullable);
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema,
                                       String table) throws SQLException {
        return _dmdWrapped.getVersionColumns(catalog, schema, table);
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema,
                                    String table) throws SQLException {
        return _dmdWrapped.getPrimaryKeys(catalog, schema, table);
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema,
                                     String table) throws SQLException {
        return _dmdWrapped.getImportedKeys(catalog, schema, table);
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema,
                                     String table) throws SQLException {
        return _dmdWrapped.getExportedKeys(catalog, schema, table);
    }

    @Override
    public ResultSet getCrossReference(String parentCatalog,
                                       String parentSchema, String parentTable, String foreignCatalog,
                                       String foreignSchema, String foreignTable) throws SQLException {
        return _dmdWrapped.getCrossReference(parentCatalog, parentSchema,
                                             parentTable, foreignCatalog, foreignSchema, foreignTable);
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        return _dmdWrapped.getTypeInfo();
    }

    @Override
    public ResultSet getIndexInfo(String catalog, String schema,
                                  String table, boolean unique, boolean approximate)
            throws SQLException {
        return _dmdWrapped.getIndexInfo(catalog, schema, table, unique,
                                        approximate);
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        return _dmdWrapped.supportsResultSetType(type);
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency)
            throws SQLException {
        return _dmdWrapped.supportsResultSetConcurrency(type, concurrency);
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return _dmdWrapped.ownUpdatesAreVisible(type);
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return _dmdWrapped.ownDeletesAreVisible(type);
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return _dmdWrapped.ownInsertsAreVisible(type);
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return _dmdWrapped.othersUpdatesAreVisible(type);
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return _dmdWrapped.othersDeletesAreVisible(type);
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return _dmdWrapped.othersInsertsAreVisible(type);
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        return _dmdWrapped.updatesAreDetected(type);
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        return _dmdWrapped.deletesAreDetected(type);
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        return _dmdWrapped.insertsAreDetected(type);
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return _dmdWrapped.supportsBatchUpdates();
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern,
                             String typeNamePattern, int[] types) throws SQLException {
        return _dmdWrapped.getUDTs(catalog, schemaPattern, typeNamePattern,
                                   types);
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        return _dmdWrapped.supportsSavepoints();
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return _dmdWrapped.supportsNamedParameters();
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return _dmdWrapped.supportsMultipleOpenResults();
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        return _dmdWrapped.supportsGetGeneratedKeys();
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern,
                                   String typeNamePattern) throws SQLException {
        return _dmdWrapped.getSuperTypes(catalog, schemaPattern, typeNamePattern);
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern,
                                    String tableNamePattern) throws SQLException {
        return _dmdWrapped.getSuperTables(catalog, schemaPattern, tableNamePattern);
    }

    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern,
                                   String typeNamePattern, String attributeNamePattern)
            throws SQLException {
        return _dmdWrapped.getAttributes(catalog, schemaPattern, typeNamePattern,
                                         attributeNamePattern);
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability)
            throws SQLException {
        return _dmdWrapped.supportsResultSetHoldability(holdability);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return _dmdWrapped.getResultSetHoldability();
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return _dmdWrapped.getDatabaseMajorVersion();
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return _dmdWrapped.getDatabaseMinorVersion();
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return _dmdWrapped.getJDBCMajorVersion();
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return _dmdWrapped.getJDBCMinorVersion();
    }

    @Override
    public int getSQLStateType() throws SQLException {
        return _dmdWrapped.getSQLStateType();
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        return _dmdWrapped.locatorsUpdateCopy();
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return _dmdWrapped.supportsStatementPooling();
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return _dmdWrapped.getRowIdLifetime();
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern)
            throws SQLException {
        return _dmdWrapped.getSchemas(catalog, schemaPattern);
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax()
            throws SQLException {
        return _dmdWrapped.supportsStoredFunctionsUsingCallSyntax();
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets()
            throws SQLException {
        return _dmdWrapped.autoCommitFailureClosesAllResultSets();
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        return _dmdWrapped.getClientInfoProperties();
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern,
                                  String functionNamePattern) throws SQLException {
        return _dmdWrapped.getFunctions(catalog, schemaPattern, functionNamePattern);
    }

    @Override
    public ResultSet getFunctionColumns(String catalog,
                                        String schemaPattern, String functionNamePattern,
                                        String columnNamePattern) throws SQLException {
        return _dmdWrapped.getFunctionColumns(catalog, schemaPattern,
                                              functionNamePattern, columnNamePattern);
    }

    @Override
    public ResultSet getPseudoColumns(String catalog,
                                      String schemaPattern, String tableNamePattern,
                                      String columnNamePattern) throws SQLException {
        ResultSet rs = null;
        try {
            rs = _dmdWrapped.getPseudoColumns(catalog, schemaPattern,
                                              tableNamePattern, columnNamePattern);
        } catch (AbstractMethodError ame) {
            throwUndefinedMethod(ame);
        }
        return rs;
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        boolean bGeneratedKeyAlwaysReturned = false;
        try {
            bGeneratedKeyAlwaysReturned = _dmdWrapped.generatedKeyAlwaysReturned();
        } catch (AbstractMethodError ame) {
            throwUndefinedMethod(ame);
        }
        return bGeneratedKeyAlwaysReturned;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface == DatabaseMetaData.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T wrapped = null;
        if (isWrapperFor(iface))
            wrapped = (T) _dmdWrapped;
        return wrapped;
    }

} 
