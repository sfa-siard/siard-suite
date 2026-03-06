/*======================================================================
MetaDataToDb transfers meta data from SIARD files to databases. 
Application : Siard2
Description : Transfers meta data from SIARD files to databases.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 29.08.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import ch.enterag.utils.jdbc.*;
import ch.enterag.utils.EU;
import ch.enterag.utils.background.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * Transfers metadata from databases to SIARD files and back.
 *
 * @author Hartwig Thomas
 */
@Slf4j
public class MetaDataToDb
        extends MetaDataBase {
    private ArchiveMapping _am = null;

    public ArchiveMapping getArchiveMapping() {
        return _am;
    }

    private int _iMaxTableNameLength = -1;
    private int _iMaxColumnNameLength = -1;
    private Progress _progress = null;
    private int _iTablesCreated = -1;
    private int _iTables = -1;
    private int _iTablesPercent = -1;

    /**
     * increment the number or tables created, issuing a notification,
     * when a percent is reached.
     */
    private void incTablesCreated() {
        _iTablesCreated++;
        if ((_progress != null) && (_iTables > 0) && ((_iTablesCreated % _iTablesPercent) == 0)) {
            int iPercent = (100 * _iTablesCreated) / _iTables;
            _progress.notifyProgress(iPercent);
        }
    }

    /**
     * check if cancel was requested.
     *
     * @return true, if cancel was requested.
     */
    private boolean cancelRequested() {
        if (_progress != null && _progress.cancelRequested()) {
            LOG.info("Cancel uploading of meta data because of request");
            return true;
        }
        return false;
    }

    /**
     * create an attribute definition for a CREATE TYPE statement from
     * attribute metadata.
     *
     * @param ma attribute meta data
     * @param tm mapping of names in type.
     * @return SQL fragment defining the attribute.
     */
    private String createAttribute(MetaAttribute ma, TypeMapping tm) {
        StringBuilder sbSql = new StringBuilder(
                SqlLiterals.formatId(tm.getMappedAttributeName(ma.getName())));
        sbSql.append(" ");
        MetaType mt = ma.getMetaType();
        if (mt == null) {
            sbSql.append(ma.getType());
            if (ma.getCardinality() >= 0)
                sbSql.append(" ARRAY[").append(ma.getCardinality()).append("]");
        } else {
            SchemaMapping sm = _am.getSchemaMapping(ma.getTypeSchema());
            QualifiedId qiType = new QualifiedId(null,
                    sm.getMappedSchemaName(), sm.getMappedTypeName(ma.getTypeName()));
            sbSql.append(qiType.format());
        }
        return sbSql.toString();
    }

    /**
     * create a CREATE TYPE statement from type metadata and execute it.
     *
     * @param mt type meta data.
     * @param sm mapping of names in schema.
     * @throws SQLException if a database error occurred.
     */
    private void createType(MetaType mt, SchemaMapping sm)
            throws SQLException {
        TypeMapping tm = sm.getTypeMapping(mt.getName());
        QualifiedId qiType = new QualifiedId(null,
                sm.getMappedSchemaName(), tm.getMappedTypeName());
        /* type was not dropped if drop was not necessary */
        if (!existsType(qiType.getSchema(), qiType.getName())) {
            CategoryType cat = mt.getCategoryType();
            if ((cat != CategoryType.DISTINCT) || supportsDistincts()) {
                StringBuilder sbSql = new StringBuilder("CREATE TYPE " + qiType.format());
                sbSql.append(" AS ");
                if (cat == CategoryType.DISTINCT)
                    sbSql.append(mt.getBase());
                else {
                    // TODO: handle super type once we have an example!
                    sbSql.append("(");
                    for (int iAttribute = 0; iAttribute < mt.getMetaAttributes(); iAttribute++) {
                        if (iAttribute > 0)
                            sbSql.append(",\r\n");
                        MetaAttribute ma = mt.getMetaAttribute(iAttribute);
                        sbSql.append(createAttribute(ma, tm));
                    }
                    sbSql.append(")");
                    if (!mt.isFinal())
                        sbSql.append(" NOT");
                    sbSql.append(" FINAL\r\n");
                    if (!mt.isInstantiable())
                        sbSql.append(" NOT");
                    sbSql.append(" INSTANTIABLE");
                }
                val sqlStatement = sbSql.toString();
                LOG.trace("SQL statement: '{}'", sqlStatement);

                /* now execute it */
                Statement stmt = _dmd.getConnection().createStatement();
                stmt.setQueryTimeout(_iQueryTimeoutSeconds);
                stmt.executeUpdate(sbSql.toString());
                stmt.close();

                LOG.debug("Type '{}.{}' successfully created", qiType.getSchema(), qiType.getName());
            }
        }
    }

    /**
     * create all types of a schema.
     *
     * @param ms schema meta data.
     * @param sm mapping of names in schema.
     * @throws SQLException if a database error occurred.
     */
    private void createTypes(MetaSchema ms, SchemaMapping sm)
            throws SQLException {
        if (supportsUdts() || supportsDistincts()) {
            for (int iType = 0; iType < ms.getMetaTypes(); iType++) {
                MetaType mt = ms.getMetaType(iType);
                CategoryType cat = mt.getCategoryType();
                if (((cat == CategoryType.DISTINCT) && supportsDistincts()) ||
                        ((cat == CategoryType.UDT) && supportsUdts()))
                    createType(mt, sm);
            }
        }
    }

    /**
     * check, whether a type exists in the database.
     *
     * @param sMangledSchema schema name.
     * @param sMangledType   type name.
     * @return true, if type exists.
     * @throws SQLException if a database error occurred.
     */
    private boolean existsType(String sMangledSchema, String sMangledType)
            throws SQLException {
        boolean bExists = false;
        ResultSet rs = _dmd.getUDTs(null,
                ((BaseDatabaseMetaData) _dmd).toPattern(sMangledSchema),
                ((BaseDatabaseMetaData) _dmd).toPattern(sMangledType),
                new int[]{Types.STRUCT, Types.DISTINCT});
        while (rs.next())
            bExists = true;
        rs.close();
        return bExists;
    }

    /**
     * drop all types that will be created within a schema.
     *
     * @param ms schema meta data
     * @param sm mapping of names in schema.
     * @throws SQLException if a database error occurred.
     */
    private void dropTypes(MetaSchema ms, SchemaMapping sm)
            throws IOException, SQLException {
        if (supportsUdts()) {
            Set<String> setTypes = new HashSet<>();
            for (int iType = 0; iType < ms.getMetaTypes(); iType++) {
                MetaType mt = ms.getMetaType(iType);
                CategoryType cat = mt.getCategoryType();
                if (supportsDistincts() || (cat != CategoryType.DISTINCT))
                    setTypes.add(mt.getName());
            }
            while (!setTypes.isEmpty()) {
                for (Iterator<String> iterType = setTypes.iterator(); iterType.hasNext(); ) {
                    String sTypeName = iterType.next();
                    TypeMapping tm = sm.getTypeMapping(sTypeName);
                    if (existsType(sm.getMappedSchemaName(), tm.getMappedTypeName())) {
                        MetaType mt = ms.getMetaType(sTypeName);
                        if (matchType(mt, sm))
                            iterType.remove();
                        else {
                            QualifiedId qiType = new QualifiedId(null,
                                    sm.getMappedSchemaName(), tm.getMappedTypeName());
                            String sSql = "DROP TYPE " + qiType.format() + " RESTRICT";
                            LOG.trace("SQL statement: '{}'", sSql);

                            Statement stmt = _dmd.getConnection().createStatement();
                            try (stmt) {
                                stmt.setQueryTimeout(_iQueryTimeoutSeconds);
                                stmt.executeUpdate(sSql);
                                iterType.remove();

                                LOG.info("Type '{}.{}' successfully dropped", qiType.getSchema(), qiType.getName());
                            } catch (SQLException se) {
                            }
                        }
                    } else
                        iterType.remove();
                }
            }
        }
    }

    /**
     * create a column definition for a CREATE TABLE statement
     *
     * @param mc column meta data
     * @param tm mapping names within table.
     * @return SQL fragment defining the column.
     * @throws IOException if an I/O error occurred.
     */
    private String createColumn(MetaColumn mc, TableMapping tm)
            throws IOException {
        StringBuilder sbSql = new StringBuilder();
        sbSql.append(SqlLiterals.formatId(tm.getMappedColumnName(mc.getName())));
        sbSql.append(" ");
        MetaType mt = mc.getMetaType();
        if (mt == null) {
            sbSql.append(mc.getType());
            if (mc.getCardinality() >= 0)
                sbSql.append(" ARRAY[").append(mc.getCardinality()).append("]");
        } else {
            CategoryType cat = mt.getCategoryType();
            if ((cat == CategoryType.DISTINCT) && (!supportsDistincts())) {
                // for more complex base types we should have to parse the base type first ...
                sbSql.append(mt.getBase());
            } else {
                SchemaMapping sm = _am.getSchemaMapping(mc.getTypeSchema());
                QualifiedId qiType = new QualifiedId(null,
                        sm.getMappedSchemaName(), sm.getMappedTypeName(mc.getTypeName()));
                sbSql.append(qiType.format());
            }
        }
        if (!mc.isNullable())
            sbSql.append(" NOT NULL");
        return sbSql.toString();
    }

    /**
     * get the set of available tables in the database.
     *
     * @return set of tables.
     * @throws SQLException if a database error occurred.
     */
    private Set<QualifiedId> getTables()
            throws SQLException {
        Set<QualifiedId> setTables = new HashSet<>();
        ResultSet rs = _dmd.getTables(null, "%", "%", new String[]{"TABLE"});
        while (rs.next()) {
            String sCatalog = rs.getString("TABLE_CAT");
            String sSchema = rs.getString("TABLE_SCHEM");
            String sTable = rs.getString("TABLE_NAME");
            QualifiedId qiTable = new QualifiedId(sCatalog, sSchema, sTable);
            setTables.add(qiTable);
        }
        rs.close();
        return setTables;
    }

    /**
     * create a CREATE TABLE statement from table meta data and execute it.
     *
     * @param mt table meta data.
     * @param sm mapping of names in schema.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void createTable(MetaTable mt, SchemaMapping sm)
            throws IOException, SQLException {
        Set<QualifiedId> setBefore = getTables();
        TableMapping tm = sm.getTableMapping(mt.getName());
        QualifiedId qiTable = new QualifiedId(null, sm.getMappedSchemaName(), tm.getMappedTableName());
        StringBuilder sbSql = new StringBuilder("CREATE TABLE " + qiTable.format() + "(");
        List<List<String>> llColumnNames = mt.getColumnNames(supportsArrays(), supportsUdts());
        for (int iExtendedColumn = 0; iExtendedColumn < llColumnNames.size(); iExtendedColumn++) {
            List<String> listColumn = llColumnNames.get(iExtendedColumn);
            StringBuilder sbColumnName = new StringBuilder();
            for (int i = 0; i < listColumn.size(); i++) {
                if (i > 0)
                    sbColumnName.append(".");
                sbColumnName.append(listColumn.get(i));
            }
            if (iExtendedColumn > 0)
                sbSql.append(",\r\n");
            MetaColumn mc = mt.getMetaColumn(sbColumnName.toString());
            if (mc != null)
                sbSql.append(createColumn(mc, tm));
            else {
                String sMappedColumnName = tm.getMappedExtendedColumnName(sbColumnName.toString());
                sbSql.append(SqlLiterals.formatId(sMappedColumnName));
                sbSql.append(" ");
                sbSql.append(mt.getType(llColumnNames.get(iExtendedColumn)));
            }
        }
        /* add primary key */
        MetaUniqueKey mpk = mt.getMetaPrimaryKey();
        if (mpk != null) {
            StringBuilder sbPrimaryKey = new StringBuilder();
            sbPrimaryKey.append("PRIMARY KEY(");
            for (int iColumn = 0; iColumn < mpk.getColumns(); iColumn++) {
                if (iColumn > 0)
                    sbPrimaryKey.append(",");
                String sMappedColumnName = tm.getMappedColumnName(mpk.getColumn(iColumn));
                sbPrimaryKey.append(SqlLiterals.formatId(sMappedColumnName));
            }
            sbPrimaryKey.append(")");
            sbSql.append(",\r\n");
            sbSql.append(sbPrimaryKey);
        }
        /* unique and foreign keys are added in the end of upload */
        sbSql.append(")");
        /* now execute it */

        val sqlStatement = sbSql.toString();
        LOG.trace("SQL statement: '{}'", sqlStatement);

        try {
            Statement stmt = _dmd.getConnection().createStatement();
            stmt.setQueryTimeout(_iQueryTimeoutSeconds);
            stmt.executeUpdate(sqlStatement);
            stmt.close();
        } catch (Exception ex) {
            System.out.println("Failed SQL statement:\n" + sqlStatement);
            throw ex;
        }

        /* record names of created table and columns */
        Set<QualifiedId> setCreated = getTables();
        setCreated.removeAll(setBefore);
        if (!setCreated.contains(qiTable)) {
            for (QualifiedId qualifiedId : setCreated) qiTable = qualifiedId;
            sm.setMappedSchemaName(qiTable.getSchema());
            tm.setMappedTableName(qiTable.getName());
        }
        ResultSet rsColumns = _dmd.getColumns(null,
                ((BaseDatabaseMetaData) _dmd).toPattern(qiTable.getSchema()),
                ((BaseDatabaseMetaData) _dmd).toPattern(qiTable.getName()),
                "%");
        while (rsColumns.next()) {
            String sMappedColumnName = rsColumns.getString("COLUMN_NAME");
            int iOrdinalPosition = rsColumns.getInt("ORDINAL_POSITION");
            List<String> listColumn = llColumnNames.get(iOrdinalPosition - 1);
            StringBuilder sbColumnName = new StringBuilder();
            for (int i = 0; i < listColumn.size(); i++) {
                if (i > 0)
                    sbColumnName.append(".");
                sbColumnName.append(listColumn.get(i));
            }
            String sExtendedColumnName = sbColumnName.toString();
            if (!sMappedColumnName.equals(tm.getMappedColumnName(sExtendedColumnName)))
                tm.putMappedExtendedColumnName(sExtendedColumnName, sMappedColumnName);
        }
        rsColumns.close();

        LOG.debug("Table '{}.{}' successfully created", sm.getMappedSchemaName(), tm.getMappedTableName());
    }

    /**
     * create all tables of a schema.
     *
     * @param ms schema meta data.
     * @param sm mapping of names in schema.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void createTables(MetaSchema ms, SchemaMapping sm)
            throws IOException, SQLException {
        for (int iTable = 0; (iTable < ms.getMetaTables()) && (!cancelRequested()); iTable++) {
            MetaTable mt = ms.getMetaTable(iTable);
            QualifiedId qiTable = new QualifiedId(null, mt.getParentMetaSchema().getName(), mt.getName());
            System.out.println("  Table: " + qiTable.format());
            createTable(mt, sm);
            incTablesCreated();
        }
    }

    /**
     * check, whether table exists in the database.
     *
     * @param sMangledSchema schema name.
     * @param sMangledTable  table name.
     * @return true, if table exists.
     * @throws SQLException if a database error occurred.
     */
    private boolean existsTable(String sMangledSchema, String sMangledTable)
            throws SQLException {
        boolean bExists = false;
        ResultSet rs = _dmd.getTables(null,
                ((BaseDatabaseMetaData) _dmd).toPattern(sMangledSchema),
                ((BaseDatabaseMetaData) _dmd).toPattern(sMangledTable),
                new String[]{"TABLE"});
        if (rs.next())
            bExists = true;
        rs.close();
        return bExists;
    }

    /**
     * drop all tables that will be created within a schema.
     *
     * @param ms schema meta data
     * @param sm mapping of names in schema.
     * @throws SQLException if a database error occurred.
     */
    private void dropTables(MetaSchema ms, SchemaMapping sm)
            throws SQLException {
        for (int iTable = 0; iTable < ms.getMetaTables(); iTable++) {
            String sTableName = ms.getMetaTable(iTable).getName();
            TableMapping tm = sm.getTableMapping(sTableName);
            Statement stmt = _dmd.getConnection().createStatement();
            try (stmt) {
                stmt.setQueryTimeout(_iQueryTimeoutSeconds);
                if (existsTable(sm.getMappedSchemaName(), tm.getMappedTableName())) {
                    QualifiedId qiTable = new QualifiedId(null,
                            sm.getMappedSchemaName(), tm.getMappedTableName());
                    /* CASCADE must always drop! */
                    String sSql = "DROP TABLE " + qiTable.format() + " CASCADE";
                    LOG.trace("SQL statement: '{}'", sSql);
                    stmt.executeUpdate(sSql);
                    System.out.println("  Dropped: " + qiTable.format());

                    LOG.info("Table '{}.{}' successfully dropped", qiTable.getSchema(), qiTable.getName());
                }
            } catch (SQLException se) {
                System.out.println("  Could not drop " + tm.getMappedTableName() + " " + EU.getExceptionMessage(se));
            }
        }
    }

    private boolean existsSchemaInPostgres(String mangledSchema) throws SQLException {
        try (ResultSet rs = _dmd.getSchemas()) {
            while (rs.next()) {
                if (rs.getString("TABLE_SCHEM").equalsIgnoreCase(mangledSchema)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * check, whether schema exists in the database.
     *
     * @param sMangledSchema schema name.
     * @return true, if schema exists.
     * @throws SQLException if a database error occurred.
     */
    private boolean existsSchema(String sMangledSchema) throws SQLException {
        boolean isPostgresTarget = _dmd.getDatabaseProductName().toLowerCase().contains("postgresql");
        
        if (isPostgresTarget) {
            return existsSchemaInPostgres(sMangledSchema);
        } else {
            boolean bExists = false;
            ResultSet rs = _dmd.getSchemas(null, ((BaseDatabaseMetaData)_dmd).toPattern(sMangledSchema));
            if (rs.next())
                bExists = true;
            rs.close();
            return bExists;
        }
    }

    /**
     * create the schema if it does not exist.
     *
     * @param ms schema meta data.
     * @param sm mapping of names in schema.
     * @throws SQLException if a database error occurred.
     */
    private void createSchema(MetaSchema ms, SchemaMapping sm)
            throws SQLException {
        if (!existsSchema(sm.getMappedSchemaName())) {
            String sSql = "CREATE SCHEMA \"" + sm.getMappedSchemaName() + "\"";
            LOG.trace("SQL statement: '{}'", sSql);

            Statement stmt = _dmd.getConnection().createStatement();
            stmt.setQueryTimeout(_iQueryTimeoutSeconds);
            try {
                stmt.executeUpdate(sSql);
                stmt.getConnection().commit();

                LOG.debug("Schema '{}' successfully created", sm.getMappedSchemaName());
            } catch (SQLException se) {
                LOG.error("Can not create schema '{}' with SQL statement '{}'", sm.getMappedSchemaName(), sSql, se);
                stmt.getConnection().rollback();
                /* rethrow it (only caught for finally clause) */
                throw new SQLException(se.getMessage(), se.getCause());
            } finally {
                stmt.close();
            }
        }
    }

    /**
     * upload creates all types and tables in the database.
     *
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    public void upload(Progress progress)
            throws IOException, SQLException {
        LOG.info("Start meta data upload of archive {}",
                this._md.getArchive().getFile().getAbsoluteFile());

        System.out.println("Meta Data");
        _progress = progress;
        /* compute number of tables to create */
        _iTables = 0;
        for (int iSchema = 0; iSchema < _md.getMetaSchemas(); iSchema++) {
            MetaSchema ms = _md.getMetaSchema(iSchema);
            for (int iTable = 0; iTable < ms.getMetaTables(); iTable++)
                _iTables++;
        }
        _iTablesPercent = (_iTables + 99) / 100;
        _iTablesCreated = 0;
        for (int iSchema = 0; (iSchema < _md.getMetaSchemas()) && (!cancelRequested()); iSchema++) {
            MetaSchema ms = _md.getMetaSchema(iSchema);
            SchemaMapping sm = _am.getSchemaMapping(ms.getName());
            createSchema(ms, sm);
        }
        for (int iSchema = 0; (iSchema < _md.getMetaSchemas()) && (!cancelRequested()); iSchema++) {
            MetaSchema ms = _md.getMetaSchema(iSchema);
            SchemaMapping sm = _am.getSchemaMapping(ms.getName());
            if (existsSchema(sm.getMappedSchemaName()))
                dropTables(ms, sm);
            else
                throw new SQLException("Schema \"" + sm.getMappedSchemaName() + "\" could not be created! " +
                        "Map \"" + ms.getName() + "\" to existing schema.");
        }
        for (int iSchema = 0; (iSchema < _md.getMetaSchemas()) && (!cancelRequested()); iSchema++) {
            MetaSchema ms = _md.getMetaSchema(iSchema);
            SchemaMapping sm = _am.getSchemaMapping(ms.getName());
            if (existsSchema(sm.getMappedSchemaName()))
                dropTypes(ms, sm);
            else
                throw new SQLException("Schema \"" + sm.getMappedSchemaName() + "\" could not be created! " +
                        "Map \"" + ms.getName() + "\" to existing schema.");
        }
        for (int iSchema = 0; (iSchema < _md.getMetaSchemas()) && (!cancelRequested()); iSchema++) {
            MetaSchema ms = _md.getMetaSchema(iSchema);
            SchemaMapping sm = _am.getSchemaMapping(ms.getName());
            if (existsSchema(sm.getMappedSchemaName()))
                createTypes(ms, sm);
            else
                throw new SQLException("Schema \"" + sm.getMappedSchemaName() + "\" could not be created! " +
                        "Map \"" + ms.getName() + "\" to existing schema.");
        }
        for (int iSchema = 0; (iSchema < _md.getMetaSchemas()) && (!cancelRequested()); iSchema++) {
            MetaSchema ms = _md.getMetaSchema(iSchema);
            SchemaMapping sm = _am.getSchemaMapping(ms.getName());
            if (existsSchema(sm.getMappedSchemaName()))
                createTables(ms, sm);
            else
                throw new SQLException("Schema \"" + sm.getMappedSchemaName() + "\" could not be created! " +
                        "Map \"" + ms.getName() + "\" to existing schema.");
        }
        if (cancelRequested())
            throw new IOException("Upload of meta data cancelled!");
        _dmd.getConnection().commit();

        LOG.info("Meta data upload finished");
    }

    /**
     * returns the number of tables that exist in the database and will
     * be dropped, when upload is executed.
     *
     * @return number of tables that will be dropped, when upload is executed.
     */
    public int tablesDroppedByUpload()
            throws SQLException {
        int iTablesDropped = 0;
        for (int iSchema = 0; iSchema < _md.getMetaSchemas(); iSchema++) {
            MetaSchema ms = _md.getMetaSchema(iSchema);
            SchemaMapping sm = _am.getSchemaMapping(ms.getName());
            for (int iTable = 0; iTable < ms.getMetaTables(); iTable++) {
                MetaTable mt = ms.getMetaTable(iTable);
                if (existsTable(sm.getMappedSchemaName(), sm.getMappedTableName(mt.getName())))
                    iTablesDropped++;
            }
        }
        return iTablesDropped;
    }

    /**
     * matchAttributes returns true, if the attributes of the given
     * type meta data match the attributes in the database.
     *
     * @param mt type meta data.
     * @param sm mapping of names in schema.
     * @return true, if the attributes of the type in the database
     * match the attributes in the meta data.
     * @throws IOException  if an I/O error occurred
     * @throws SQLException if a database error occurred.
     */
    private boolean matchAttributes(MetaType mt, SchemaMapping sm)
            throws IOException, SQLException {
        boolean bMatches = true;
        TypeMapping tm = sm.getTypeMapping(mt.getName());
        int iPosition = 0;
        ResultSet rs = _dmd.getAttributes(null,
                ((BaseDatabaseMetaData) _dmd).toPattern(sm.getMappedSchemaName()),
                ((BaseDatabaseMetaData) _dmd).toPattern(tm.getMappedTypeName()), "%");
        while (bMatches && rs.next()) {
            iPosition++;
            String sTypeSchema = rs.getString("TYPE_SCHEM");
            if (!sTypeSchema.equals(_am.getMappedSchemaName(mt.getParentMetaSchema().getName())))
                throw new IOException("Attribute with unexpected type schema found!");
            String sTypeName = rs.getString("TYPE_NAME");
            if (!sTypeName.equals(sm.getMappedTypeName(mt.getName())))
                throw new IOException("Attribute with unexpected type name found");
            String sAttributeName = rs.getString("ATTR_NAME");
            // int iAttrSize = rs.getInt("ATTR_SIZE");
            // int iDecimalDigits = rs.getInt("DECIMAL_DIGITS");
            /* find attribute with this mangled name */
            MetaAttribute ma = null;
            for (int iAttribute = 0; (ma == null) && (iAttribute < mt.getMetaAttributes()); iAttribute++) {
                MetaAttribute maTemp = mt.getMetaAttribute(iAttribute);
                if (sAttributeName.equals(tm.getMappedAttributeName(maTemp.getName())))
                    ma = maTemp;
            }
            if (ma != null) {
                int iDataType = rs.getInt("DATA_TYPE");
                String sAttrTypeName = rs.getString("ATTR_TYPE_NAME");
                if ((iDataType != Types.DISTINCT) &&
                        (iDataType != Types.ARRAY) &&
                        (iDataType != Types.STRUCT)) {
                    if (iDataType != ma.getPreType())
                        bMatches = false;
                } else if (iDataType == Types.ARRAY) {
                    /* parse array constructor "<base> ARRAY[<n>]" */
                    Matcher m = MetaDataFromDb._patARRAY_CONSTRUCTOR.matcher(sTypeName);
                    if (m.matches()) {
                        if (!ma.getType().equals(m.group(1)))
                            bMatches = false;
                        if (ma.getCardinality() != Integer.parseInt(m.group(2)))
                            bMatches = false;
                    } else
                        throw new SQLException("Invalid ARRAY constructor for attribute " + ma.getName() + " of type " + mt.getName() + "!");
                } else {
                    try {
                        QualifiedId qiAttrType = new QualifiedId(sAttrTypeName);
                        if (qiAttrType.getSchema() == null)
                            qiAttrType.setSchema(sm.getMappedSchemaName());
                        /* find schema with this mangled name */
                        MetaSchema msAttr = null;
                        SchemaMapping smAttr = null;
                        for (int iSchema = 0; (msAttr == null) && (iSchema < _md.getMetaSchemas()); iSchema++) {
                            MetaSchema msTemp = _md.getMetaSchema(iSchema);
                            SchemaMapping smTemp = _am.getSchemaMapping(msTemp.getName());
                            if (smTemp.getMappedSchemaName().equals(qiAttrType.getSchema())) {
                                msAttr = msTemp;
                                smAttr = smTemp;
                            }
                        }
                        if (msAttr != null) {
                            /* find type with this mangled name */
                            MetaType mtAttr = null;
                            for (int iType = 0; (mtAttr == null) && (iType < msAttr.getMetaTypes()); iType++) {
                                MetaType mtTemp = msAttr.getMetaType(iType);
                                if (smAttr.getMappedTypeName(mtTemp.getName()).equals(qiAttrType.getName()))
                                    mtAttr = mtTemp;
                            }
                            if (mtAttr != null)
                                bMatches = matchType(mtAttr, smAttr);
                            else
                                bMatches = false;
                        } else
                            bMatches = false;
                    } catch (ParseException pe) {
                        throw new SQLException("Type of attribute " + ma.getName() + " of type " + mt.getName() + " could not be parsed!", pe);
                    }
                }
            } else
                bMatches = false;
        }
        rs.close();
        if (iPosition < mt.getMetaAttributes())
            bMatches = false;
        return bMatches;
    }

    /**
     * matchType returns true, if a type of the given name exists in the
     * database and has the same relevant properties.
     *
     * @param mt type meta data.
     * @param sm mapping of names in schema.
     * @return true, if the same type exists in the database.
     * @throws IOException  if an I/O error occurred
     * @throws SQLException if a database error occurred.
     */
    private boolean matchType(MetaType mt, SchemaMapping sm)
            throws IOException, SQLException {
        boolean bMatches = true;
        TypeMapping tm = sm.getTypeMapping(mt.getName());
        CategoryType cat = mt.getCategoryType();
        int iDataType = Types.STRUCT;
        if (cat == CategoryType.DISTINCT)
            iDataType = Types.DISTINCT;
        ResultSet rs = _dmd.getUDTs(null,
                ((BaseDatabaseMetaData) _dmd).toPattern(sm.getMappedSchemaName()),
                ((BaseDatabaseMetaData) _dmd).toPattern(tm.getMappedTypeName()), null);
        while (rs.next()) {
            /* the type only needs to be dropped if its base type
             * or its attributes are different */
            if (iDataType == rs.getInt("DATA_TYPE")) {
                if (iDataType == Types.DISTINCT) {
                    int iBaseType = rs.getInt("BASE_TYPE");
                    if (iBaseType != mt.getBasePreType())
                        bMatches = false;
                } else {
                    if (!matchAttributes(mt, sm))
                        bMatches = false;
                }
            } else
                bMatches = false;
        }
        rs.close();
        return bMatches;
    }

    /**
     * returns the number of types that exist in the database and will be
     * dropped, when upload is executed.
     *
     * @return number of tables that will be dropped, when upload is executed.
     * @throws IOException  if an I/O error occurred
     * @throws SQLException if a database error occurred.
     */
    public int typesDroppedByUpload()
            throws IOException, SQLException {
        int iTypesDropped = 0;
        if (supportsUdts()) {
            for (int iSchema = 0; iSchema < _md.getMetaSchemas(); iSchema++) {
                MetaSchema ms = _md.getMetaSchema(iSchema);
                SchemaMapping sm = _am.getSchemaMapping(ms.getName());
                for (int iType = 0; iType < ms.getMetaTypes(); iType++) {
                    MetaType mt = ms.getMetaType(iType);
                    if (!matchType(mt, sm))
                        iTypesDropped++;
                }
            }
        }
        return iTypesDropped;
    }

    /**
     * constructor
     *
     * @param dmd        database meta data.
     * @param md         SIARD meta data.
     * @param mapSchemas schema mapping to be used for upload, or empty or null.
     * @throws IOException  if an I/O error occurred
     * @throws SQLException if a database error occurred.
     */
    private MetaDataToDb(DatabaseMetaData dmd, MetaData md, Map<String, String> mapSchemas)
            throws IOException, SQLException {
        super(dmd, md);
        dmd.getConnection().setAutoCommit(false);
        _iMaxTableNameLength = _dmd.getMaxTableNameLength();
        _iMaxColumnNameLength = _dmd.getMaxColumnNameLength();
        _am = ArchiveMapping.newInstance(supportsArrays(), supportsUdts(),
                mapSchemas, _md, _iMaxTableNameLength, _iMaxColumnNameLength);
    }

    /**
     * factory
     *
     * @param dmd        database meta data.
     * @param md         SIARD meta data.
     * @param mapSchemas schema mapping to be used for upload, or null.
     * @return new instance.
     * @throws IOException  if an I/O error occurred
     * @throws SQLException if a database error occurred.
     */
    public static MetaDataToDb newInstance(DatabaseMetaData dmd, MetaData md, Map<String, String> mapSchemas)
            throws IOException, SQLException {
        return new MetaDataToDb(dmd, md, mapSchemas);
    }

}