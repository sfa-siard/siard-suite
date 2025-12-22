/*======================================================================
MetaDataFromDb transfers meta data from databases to SIARD files. 
Application : Siard2
Description : Transfers meta data from databases to SIARD files.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 29.08.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.admin.bar.siard2.api.generated.ReferentialActionType;
import ch.admin.bar.siard2.api.meta.*;
import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.EU;
import ch.enterag.utils.ProgramInfo;
import ch.enterag.utils.background.Progress;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Transfers meta data from databases to SIARD files.
 */
@Slf4j
public class MetaDataFromDb extends MetaDataBase {
    static final Pattern _patARRAY_CONSTRUCTOR = Pattern.compile("^\\s*(.*?)\\s+ARRAY\\s*\\[\\s*(\\d+)\\s*\\]$");
    private boolean _bMaxLobNeeded = false;
    private MetaColumn _mcMaxLob = null;

    /**
     * private constructor
     *
     * @param dmd database meta data.
     * @param md  SIARD meta data.
     */
    private MetaDataFromDb(DatabaseMetaData dmd, MetaData md) throws SQLException {
        super(dmd, md);
    }

    /**
     * factory method to create an instance
     *
     * @param dmd database meta data.
     * @param md  SIARD meta data.
     * @return new instance.
     */
    public static MetaDataFromDb newInstance(DatabaseMetaData dmd, MetaData md) throws SQLException {
        return new MetaDataFromDb(dmd, md);
    }

    public MetaColumn getMaxLobColumn() {
        return _mcMaxLob;
    }

    private long _lMaxLobSize = -1;
    private Progress _progress = null;
    private boolean _bViewsAsTables = false;
    private int _iTablesAnalyzed = -1;
    private int _iTables = -1;
    private int _iTablesPercent = -1;

    private boolean checkMetaTable(MetaTable mt) {
        boolean bMetaDataOk = true;
        System.out.println("    Check Meta Table " + mt.getName());
        try {
            for (int iColumn = 0; bMetaDataOk && (iColumn < mt.getMetaColumns()); iColumn++) {
                MetaColumn mc = mt.getMetaColumn(iColumn);
                System.out.println("      Check Meta Column " + mc.getName());
                String sTypeName = mc.getTypeName();
                String sTypeSchema = mc.getTypeSchema();
                if ((sTypeName != null) && (sTypeSchema == null)) {
                    System.err.println("Error in column " + mc.getName() + " of table " + mt.getName() + "!");
                    bMetaDataOk = false;
                }
                ((MetaColumnImpl) mc).getColumnType();
            }
            ((MetaTableImpl) mt).getTableType();
        } catch (Exception e) {
            System.err.println(EU.getExceptionMessage(e));
            bMetaDataOk = false;
        }
        return bMetaDataOk;
    }

    private boolean checkMetaView(MetaView mv) {
        boolean bMetaDataOk = true;
        System.out.println("    Check Meta View " + mv.getName());
        try {
            for (int iColumn = 0; bMetaDataOk && (iColumn < mv.getMetaColumns()); iColumn++) {
                MetaColumn mc = mv.getMetaColumn(iColumn);
                System.out.println("      Check Meta Column " + mc.getName());
                String sTypeName = mc.getTypeName();
                String sTypeSchema = mc.getTypeSchema();
                if ((sTypeName != null) && (sTypeSchema == null)) {
                    System.err.println("Error in column " + mc.getName() + " of view " + mv.getName() + "!");
                    bMetaDataOk = false;
                }
                ((MetaColumnImpl) mc).getColumnType();
            }
            ((MetaViewImpl) mv).getViewType();
        } catch (Exception e) {
            System.err.println(EU.getExceptionMessage(e));
            bMetaDataOk = false;
        }
        return bMetaDataOk;
    }

    private boolean checkMetaType(MetaType mt) {
        boolean bMetaDataOk = true;
        System.out.println("    Check Meta Type " + mt.getName());
        try {
            for (int iAttribute = 0; bMetaDataOk && (iAttribute < mt.getMetaAttributes()); iAttribute++) {
                MetaAttribute ma = mt.getMetaAttribute(iAttribute);
                System.out.println("      Check Meta Attribute " + ma.getName());
                String sTypeName = ma.getTypeName();
                String sTypeSchema = ma.getTypeSchema();
                if ((sTypeName != null) && (sTypeSchema == null)) {
                    System.err.println("Error in attribute " + ma.getName() + " of type " + mt.getName() + "!");
                    bMetaDataOk = false;
                }
                ((MetaAttributeImpl) ma).getAttributeType();
            }
            ((MetaTypeImpl) mt).getTypeType();
        } catch (Exception e) {
            System.err.println(EU.getExceptionMessage(e));
            bMetaDataOk = false;
        }
        return bMetaDataOk;
    }

    private boolean checkMetaRoutine(MetaRoutine mr) {
        boolean bMetaDataOk = true;
        System.out.println("    Check Meta Routine " + mr.getSpecificName());
        try {
            for (int iParameter = 0; bMetaDataOk && (iParameter < mr.getMetaParameters()); iParameter++) {
                MetaParameter mp = mr.getMetaParameter(iParameter);
                System.out.println("      Check Meta Parameter " + mp.getName());
                String sTypeName = mp.getTypeName();
                String sTypeSchema = mp.getTypeSchema();
                if ((sTypeName != null) && (sTypeSchema == null)) {
                    System.err.println("Error in parameter " + mr.getName() + " of routine " + mr.getName() + "!");
                    bMetaDataOk = false;
                }
                ((MetaParameterImpl) mp).getParameterType();
            }
            ((MetaRoutineImpl) mr).getRoutineType();
        } catch (Exception e) {
            System.err.println(EU.getExceptionMessage(e));
            bMetaDataOk = false;
        }
        return bMetaDataOk;
    }

    private boolean checkMetaSchema(MetaSchema ms) {
        boolean bMetaDataOk = true;
        System.out.println("  Check Meta Schema " + ms.getName());
        try {
            if (ms.getSchema().getTables() == ms.getMetaTables()) {
                for (int iTable = 0; bMetaDataOk && (iTable < ms.getMetaTables()); iTable++)
                    bMetaDataOk = checkMetaTable(ms.getMetaTable(iTable));
            } else {
                System.err.println("Invalid number of tables in schema " + ms.getName() + "!");
                bMetaDataOk = false;
            }
            for (int iView = 0; bMetaDataOk && (iView < ms.getMetaViews()); iView++)
                bMetaDataOk = checkMetaView(ms.getMetaView(iView));
            for (int iType = 0; bMetaDataOk && (iType < ms.getMetaTypes()); iType++)
                bMetaDataOk = checkMetaType(ms.getMetaType(iType));
            for (int iRoutine = 0; bMetaDataOk && (iRoutine < ms.getMetaRoutines()); iRoutine++)
                bMetaDataOk = checkMetaRoutine(ms.getMetaRoutine(iRoutine));
            ((MetaSchemaImpl) ms).getSchemaType();
        } catch (Exception e) {
            System.err.println(EU.getExceptionMessage(e));
            bMetaDataOk = false;
        }
        return bMetaDataOk;
    }

    public boolean checkMetaData() {
        boolean bMetaDataOk = true;
        System.out.println("Check Meta Data");
        try {
            if (_md.getArchive().getSchemas() == _md.getMetaSchemas()) {
                for (int iSchema = 0; bMetaDataOk && (iSchema < _md.getMetaSchemas()); iSchema++)
                    bMetaDataOk = checkMetaSchema(_md.getMetaSchema(iSchema));
                ((MetaDataImpl) _md).getSiardArchive();
            } else {
                System.err.println("Invalid number of schema!");
                bMetaDataOk = false;
            }
        } catch (Exception e) {
            System.err.println(EU.getExceptionMessage(e));
            bMetaDataOk = false;
        }
        return bMetaDataOk;
    }

    /**
     * increment the number of tables analyzed, issuing a notification,
     * when a percent is reached.
     */
    private void incTablesAnalyzed() {
        _iTablesAnalyzed++;
        if ((_progress != null) && (_iTables > 0) && ((_iTablesAnalyzed % _iTablesPercent) == 0)) {
            int iPercent = (100 * _iTablesAnalyzed) / _iTables;
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
            LOG.info("Cancel downloading of meta data because of request");
            return true;
        }
        return false;
    }

    /**
     * translate referential action.
     *
     * @param iReferentialAction DatabaseMetaData.imported... constant.
     * @return ReferentialAction value.
     */
    private String getReferentialAction(int iReferentialAction) {
        ReferentialActionType rat = null;
        switch (iReferentialAction) {
            case DatabaseMetaData.importedKeyCascade:
                rat = ReferentialActionType.CASCADE;
                break;
            case DatabaseMetaData.importedKeySetNull:
                rat = ReferentialActionType.SET_NULL;
                break;
            case DatabaseMetaData.importedKeySetDefault:
                rat = ReferentialActionType.SET_DEFAULT;
                break;
            case DatabaseMetaData.importedKeyRestrict:
                rat = ReferentialActionType.RESTRICT;
                break;
            case DatabaseMetaData.importedKeyNoAction:
                rat = ReferentialActionType.NO_ACTION;
        }
        return rat.value();
    }

    /**
     * download attribute metadata of a type.
     *
     * @param mt type meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getAttributes(MetaType mt) throws IOException, SQLException {
        int iPosition = 0;
        CategoryType cat = mt.getCategoryType();
        ResultSet rs = _dmd.getAttributes(null,
                                          ((BaseDatabaseMetaData) _dmd).toPattern(mt.getParentMetaSchema().getName()),
                                          ((BaseDatabaseMetaData) _dmd).toPattern(mt.getName()),
                                          "%");
        while (rs.next()) {
            iPosition++;
            String sTypeSchema = rs.getString("TYPE_SCHEM");
            if (!sTypeSchema.equals(mt.getParentMetaSchema().getName()))
                throw new IOException("Attribute with unexpected type schema found!");
            String sTypeName = rs.getString("TYPE_NAME");
            if (!sTypeName.equals(mt.getName())) throw new IOException("Attribute with unexpected type name found");
            String sAttributeName = rs.getString("ATTR_NAME");
            int iDataType = rs.getInt("DATA_TYPE");
            String sAttrTypeName = rs.getString("ATTR_TYPE_NAME");
            int iAttrSize = rs.getInt("ATTR_SIZE");
            int iDecimalDigits = rs.getInt("DECIMAL_DIGITS");
            MetaType mtAttr = null;
            if ((iDataType == Types.DISTINCT) || (iDataType == Types.STRUCT))
                mtAttr = createType(sAttrTypeName, mt.getParentMetaSchema().getName(), iAttrSize, iDecimalDigits);
            if (cat == CategoryType.DISTINCT) {
                mt.setBase(sTypeName);
                if ((iDataType != Types.DISTINCT) && (iDataType != Types.ARRAY) && (iDataType != Types.STRUCT))
                    mt.setBasePreType(iDataType, iAttrSize, iDecimalDigits);
            } else {
                MetaAttribute ma = mt.createMetaAttribute(sAttributeName);
                if ((iDataType != Types.DISTINCT) && (iDataType != Types.ARRAY) && (iDataType != Types.STRUCT)) {
                    if (iDataType == Types.OTHER) ma.setType(sAttrTypeName);
                    else ma.setPreType(iDataType, iAttrSize, iDecimalDigits);
                } else if (iDataType == Types.ARRAY) {
                    /* parse array constructor "<base> ARRAY[<n>]" */
                    Matcher m = _patARRAY_CONSTRUCTOR.matcher(sAttrTypeName);
                    if (m.matches()) {
                        String sBaseType = m.group(1);
                        // handle non-predefined base type
                        BaseSqlFactory bsf = new BaseSqlFactory();
                        DataType dt = bsf.newDataType();
                        dt.parse(sBaseType);
                        if (dt.getPredefinedType() != null) ma.setType(dt.format());
                        else {
                            MetaType mtyBase = createType(sBaseType, sTypeSchema, -1, -1);
                            QualifiedId qiTypeBase = new QualifiedId(null,
                                                                     mtyBase.getParentMetaSchema().getName(),
                                                                     mtyBase.getName());
                            ma.setTypeName(qiTypeBase.getName());
                            ma.setTypeSchema(qiTypeBase.getSchema());
                        }
                        ma.setCardinality(Integer.parseInt(m.group(2)));
                    } else
                        throw new SQLException("Invalid ARRAY constructor for attribute " + ma.getName() + " of type " + mt.getName() + "!");
                } else {
                    ma.setTypeName(mtAttr.getName());
                    ma.setTypeSchema(mtAttr.getParentMetaSchema().getName());
                }
                int iNullable = rs.getInt("NULLABLE");
                if (iNullable == DatabaseMetaData.attributeNoNulls) ma.setNullable(false);
                else if (iNullable == DatabaseMetaData.attributeNullable) ma.setNullable(true);
                String sAttributeDefault = rs.getString("ATTR_DEF");
                if (sAttributeDefault != null) ma.setDefaultValue(sAttributeDefault);
                String sRemarks = rs.getString("REMARKS");
                if (sRemarks != null) ma.setDescription(sRemarks);
                int iOrdinalPosition = rs.getInt("ORDINAL_POSITION");
                if (iOrdinalPosition != iPosition) throw new IOException("Invalid ordinal position of attribute!");
            }
        }
        rs.close();
    }

    /**
     * download type meta data.
     *
     * @param sTypeName      type name - possibly qualified by schema.
     * @param sDefaultSchema type schema, if not given in qualified type name.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private MetaType createType(String sTypeName, String sDefaultSchema, int iPrecision,
                                int iScale) throws IOException, SQLException {
        MetaType mt;
        try {
            QualifiedId qiType = new QualifiedId(sTypeName);
            String sTypeSchema = qiType.getSchema();
            if (sTypeSchema == null) {
                sTypeSchema = sDefaultSchema;
                qiType.setSchema(sTypeSchema);
                qiType.parseName(sTypeName);
            }
            Schema schema = _md.getArchive().getSchema(qiType.getSchema());
            if (schema == null) schema = _md.getArchive().createSchema(qiType.getSchema());
            MetaSchema ms = schema.getMetaSchema();
            mt = ms.getMetaType(qiType.getName());
            if (mt == null) {
                System.out.println("  Type: " + qiType.format());
                mt = ms.createMetaType(qiType.getName());
                ResultSet rs = _dmd.getUDTs(null,
                                            ((BaseDatabaseMetaData) _dmd).toPattern(qiType.getSchema()),
                                            ((BaseDatabaseMetaData) _dmd).toPattern(qiType.getName()),
                                            new int[]{Types.DISTINCT, Types.STRUCT});
                boolean hasNext = rs.next();
                if (!hasNext && "PL/SQL BOOLEAN".equals(qiType.getName())) {
                    BaseSqlFactory bsf = new BaseSqlFactory();
                    PredefinedType pt = bsf.newPredefinedType();
                    mt.setCategory(CategoryType.DISTINCT.value());
                    pt.initialize(Types.BOOLEAN, 0, 0);
                    mt.setBase(pt.format());
                } else if (hasNext && qiType.getName().equals(rs.getString("TYPE_NAME")) && qiType.getSchema()
                                                                                 .equals(rs.getString("TYPE_SCHEM"))) {
                    String sRemarks = rs.getString("REMARKS");
                    if (sRemarks != null) mt.setDescription(sRemarks);
                    BaseSqlFactory bsf = new BaseSqlFactory();
                    PredefinedType pt = bsf.newPredefinedType();
                    int iBaseType = rs.getInt("BASE_TYPE");
                    if (iBaseType != Types.NULL) {
                        mt.setCategory(CategoryType.DISTINCT.value());
                        pt.initialize(iBaseType, iPrecision, iScale);
                        mt.setBase(pt.format());
                    } else {
                        mt.setCategory(CategoryType.UDT.value());
                        getAttributes(mt);
                    }
                } else throw new SQLException("Invalid type meta data found!");
                rs.close();
            }
        } catch (ParseException pe) {
            throw new SQLException("Type name " + sTypeName + " could not be parsed!", pe);
        }
        return mt;
    }

    /**
     * download column meta data of a view.
     *
     * @param mv view meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getColumns(MetaView mv) throws IOException, SQLException {
        ResultSet rs = _dmd.getColumns(null,
                                       ((BaseDatabaseMetaData) _dmd).toPattern(mv.getParentMetaSchema().getName()),
                                       ((BaseDatabaseMetaData) _dmd).toPattern(mv.getName()),
                                       "%");
        while (rs.next()) {
            String sTableSchema = rs.getString("TABLE_SCHEM");
            if (!sTableSchema.equals(mv.getParentMetaSchema().getName()))
                throw new IOException("Invalid view schema for column found!");
            String sTableName = rs.getString("TABLE_NAME");
            if (!sTableName.equals(mv.getName())) throw new IOException("Invalid view name for column found!");
            String sColumnName = rs.getString("COLUMN_NAME");
            MetaColumn mc = mv.createMetaColumn(sColumnName);
            getColumnData(rs, mc);
        }
        rs.close();
    }

    /**
     * get all parameter metadata associated with the given procedure.
     *
     * @param mr routine meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException in a database error occurred.
     */
    private void getProcedureParameters(MetaRoutine mr) throws IOException, SQLException {
        ResultSet rs = _dmd.getProcedureColumns(null,
                                                ((BaseDatabaseMetaData) _dmd).toPattern(mr.getParentMetaSchema()
                                                                                          .getName()),
                                                ((BaseDatabaseMetaData) _dmd).toPattern(mr.getName()),
                                                "%");
        while (rs.next()) {
            String sProcedureSchema = rs.getString("PROCEDURE_SCHEM");
            if (!sProcedureSchema.equals(mr.getParentMetaSchema().getName()))
                throw new IOException("Invalid procedure parameter schema encountered!");
            String sProcedureName = rs.getString("PROCEDURE_NAME");
            if (!sProcedureName.equals(mr.getName()))
                throw new IOException("Invalid procedure parameter name encountered!");
            String sParameterName = rs.getString("COLUMN_NAME");
            int iColumnType = rs.getInt("COLUMN_TYPE");
            int iDataType = rs.getInt("DATA_TYPE");
            String sTypeName = rs.getString("TYPE_NAME");
            MetaType mt = null;
            long lPrecision = rs.getLong("PRECISION");
            int iScale = rs.getInt("SCALE");
            if ((iDataType == Types.DISTINCT) || (iDataType == Types.STRUCT)) {
                mt = createType(sTypeName, mr.getParentMetaSchema().getName(), (int) lPrecision, iScale);
            }
            String sRemarks = rs.getString("REMARKS");
            int iOrdinalPosition = rs.getInt("ORDINAL_POSITION");
            String sSpecificName = rs.getString("SPECIFIC_NAME");
            if (sSpecificName == null) sSpecificName = sProcedureName;
            /* we are only interested in the parameters of this specific routine */
            if (sSpecificName.equals(mr.getSpecificName())) {
                if ((iColumnType == DatabaseMetaData.procedureColumnReturn) || (iColumnType == DatabaseMetaData.procedureColumnResult)) {
                    mr.setReturnType(sTypeName);
                    if ((iDataType != Types.DISTINCT) && (iDataType != Types.ARRAY) && (iDataType != Types.STRUCT))
                        mr.setReturnPreType(iDataType, lPrecision, iScale);
                } else {
                    MetaParameter mp = mr.createMetaParameter(sParameterName);
                    if (iOrdinalPosition != mr.getMetaParameters())
                        throw new IOException("Invalid order of procedure columns!");
                    switch (iColumnType) {
                        case DatabaseMetaData.procedureColumnIn:
                            mp.setMode("IN");
                            break;
                        case DatabaseMetaData.procedureColumnOut:
                            mp.setMode("OUT");
                            break;
                        case DatabaseMetaData.procedureColumnInOut:
                            mp.setMode("INOUT");
                            break;
                        default:
                            break;
                    }
                    if ((iDataType != Types.DISTINCT) && (iDataType != Types.ARRAY) && (iDataType != Types.STRUCT)) {
                        mp.setPreType(iDataType, lPrecision, iScale);
                        mp.setTypeOriginal(sTypeName);
                    } else if (iDataType == Types.ARRAY) {
                        /* parse array constructor "<base> ARRAY[<n>]" */
                        Matcher m = _patARRAY_CONSTRUCTOR.matcher(sTypeName);
                        if (m.matches()) {
                            String sBaseType = m.group(1);
                            // handle non-predefined base type
                            BaseSqlFactory bsf = new BaseSqlFactory();
                            DataType dt = bsf.newDataType();
                            dt.parse(sBaseType);
                            if (dt.getPredefinedType() != null) mp.setType(dt.format());
                            else {
                                MetaType mtyBase = createType(sBaseType, sProcedureSchema, -1, -1);
                                QualifiedId qiTypeBase = new QualifiedId(null,
                                                                         mtyBase.getParentMetaSchema().getName(),
                                                                         mtyBase.getName());
                                mp.setTypeName(qiTypeBase.getName());
                                mp.setTypeSchema(qiTypeBase.getSchema());
                            }
                            mp.setCardinality(Integer.parseInt(m.group(2)));
                        } else
                            throw new SQLException("Invalid ARRAY constructor for parameter " + mp.getName() + " of routine " + mr.getName() + "!");
                    } else {
                        mp.setTypeName(mt.getName());
                        mp.setTypeSchema(mt.getParentMetaSchema().getName());
                    }
                    if (sRemarks != null) mp.setDescription(sRemarks);
                }
            }
        }
        rs.close();
    }

    /**
     * get all parameter metadata associated with the given function.
     *
     * @param mr routine meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException in a database error occurred.
     */
    private void getFunctionParameters(MetaRoutine mr) throws IOException, SQLException {

        ResultSet rs = _dmd.getFunctionColumns(null,
                                               ((BaseDatabaseMetaData) _dmd).toPattern(mr.getParentMetaSchema()
                                                                                         .getName()),
                                               ((BaseDatabaseMetaData) _dmd).toPattern(mr.getName()),
                                               "%");
        while (rs.next()) {
            String sFunctionSchema = rs.getString("FUNCTION_SCHEM");
            if (!mr.getParentMetaSchema().getName().equals(sFunctionSchema))
                throw new IOException("Invalid function parameter schema encountered!");
            String sFunctionName = rs.getString("FUNCTION_NAME");
            if (!mr.getName().equals(sFunctionName))
                throw new IOException("Invalid function parameter name encountered!");
            String sParameterName = rs.getString("COLUMN_NAME");
            int iColumnType = rs.getInt("COLUMN_TYPE");
            int iDataType = rs.getInt("DATA_TYPE");
            String sTypeName = rs.getString("TYPE_NAME");
            long lPrecision = rs.getLong("PRECISION");
            int iScale = rs.getInt("SCALE");
            MetaType mt = null;
            if ((iDataType == Types.DISTINCT) || (iDataType == Types.STRUCT))
                mt = createType(sTypeName, mr.getParentMetaSchema().getName(), (int) lPrecision, iScale);
            String sRemarks = rs.getString("REMARKS");
            int iOrdinalPosition = rs.getInt("ORDINAL_POSITION");
            String sSpecificName = rs.getString("SPECIFIC_NAME");
            if (sSpecificName == null) sSpecificName = sFunctionName;
            /* we are only interested in the parameters of this specific routine */
            if (sSpecificName.equals(mr.getSpecificName())) {
                /* the functionColumn... constants are defined inconsistently and
                 * probably were supposed to be identical with the corresponding
                 * procedureColumn... constants.
                 */
                if ((iColumnType == DatabaseMetaData.procedureColumnReturn) || (iColumnType == DatabaseMetaData.procedureColumnResult)) {
                    mr.setReturnType(sTypeName);
                    if ((iDataType != Types.DISTINCT) && (iDataType != Types.ARRAY) && (iDataType != Types.STRUCT))
                        mr.setReturnPreType(iDataType, lPrecision, iScale);
                } else {
                    MetaParameter mp = mr.createMetaParameter(sParameterName);
                    if (iOrdinalPosition != mr.getMetaParameters())
                        throw new IOException("Invalid order of function columns!");
                    switch (iColumnType) {
                        case DatabaseMetaData.procedureColumnIn:
                            mp.setMode("IN");
                            break;
                        case DatabaseMetaData.procedureColumnOut:
                            mp.setMode("OUT");
                            break;
                        case DatabaseMetaData.procedureColumnInOut:
                            mp.setMode("INOUT");
                            break;
                        default:
                            break;
                    }
                    if ((iDataType != Types.DISTINCT) && (iDataType != Types.ARRAY) && (iDataType != Types.STRUCT)) {
                        mp.setPreType(iDataType, lPrecision, iScale);
                        mp.setTypeOriginal(sTypeName);
                    } else if (iDataType == Types.ARRAY) {
                        /* parse array constructor "<base> ARRAY[<n>]" */
                        Matcher m = _patARRAY_CONSTRUCTOR.matcher(sTypeName);
                        if (m.matches()) {
                            String sBaseType = m.group(1);
                            // handle non-predefined base type
                            BaseSqlFactory bsf = new BaseSqlFactory();
                            DataType dt = bsf.newDataType();
                            dt.parse(sBaseType);
                            if (dt.getPredefinedType() != null) mp.setType(dt.format());
                            else {
                                MetaType mtyBase = createType(sBaseType, sFunctionSchema, -1, -1);
                                QualifiedId qiTypeBase = new QualifiedId(null,
                                                                         mtyBase.getParentMetaSchema().getName(),
                                                                         mtyBase.getName());
                                mp.setTypeName(qiTypeBase.getName());
                                mp.setTypeSchema(qiTypeBase.getSchema());
                            }
                            mp.setCardinality(Integer.parseInt(m.group(2)));
                        } else
                            throw new SQLException("Invalid ARRAY constructor for parameter " + mp.getName() + " of routine " + mr.getName() + "!");
                    } else {
                        mp.setTypeName(mt.getName());
                        mp.setTypeSchema(mt.getParentMetaSchema().getName());
                    }
                    if (sRemarks != null) mp.setDescription(sRemarks);
                }
            }
        }
        rs.close();
    }

    /**
     * add the references to a foreign key in the correct order
     *
     * @param mt              MetaTable instance
     * @param sForeignKeyName foreign key name
     * @param mapFkColumns    map from position to column name.
     * @param mapPkColumns    map from column name to references column name.
     * @throws IOException if an I/O error occurred.
     */
    private void addReferences(MetaTable mt, String sForeignKeyName, Map<Integer, String> mapFkColumns,
                               Map<String, String> mapPkColumns) throws IOException {
        if (sForeignKeyName != null) {
            /* add the columns in the proper order */
            MetaForeignKey mfk = mt.getMetaForeignKey(sForeignKeyName);
            for (int iColumn = 0; iColumn < mapFkColumns.size(); iColumn++) {
                String sFkColumnName = mapFkColumns.get(iColumn + 1);
                mfk.addReference(sFkColumnName, mapPkColumns.get(sFkColumnName));
            }
            mapFkColumns.clear();
            mapPkColumns.clear();
        }
    }

    /**
     * add column names to candidate key in correct order.
     *
     * @param mt               table meta data.
     * @param sUniqueKeyName   name of candidate key.
     * @param mapUniqueColumns map from position to column name.
     * @throws IOException if an I/O error occurred.
     */
    private void addColumns(MetaTable mt, String sUniqueKeyName,
                            Map<Integer, String> mapUniqueColumns) throws IOException {
        if (sUniqueKeyName != null) {
            MetaUniqueKey muk = mt.getMetaCandidateKey(sUniqueKeyName);
            for (int iColumn = 0; iColumn < mapUniqueColumns.size(); iColumn++) {
                String sColumnName = mapUniqueColumns.get(iColumn + 1);
                muk.addColumn(sColumnName);
            }
            mapUniqueColumns.clear();
        }
    }

    /**
     * download column data from a result set record.
     *
     * @param rs result set of getColumns().
     * @param mc column meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getColumnData(ResultSet rs, MetaColumn mc) throws IOException, SQLException {
        int iDataType = rs.getInt("DATA_TYPE");
        String sTypeName = rs.getString("TYPE_NAME");
        long lColumnSize = rs.getLong("COLUMN_SIZE");
        int iDecimalDigits = rs.getInt("DECIMAL_DIGITS");
        MetaSchema ms;
        QualifiedId qiParent;
        if (mc.getParentMetaTable() != null) {
            MetaTable mt = mc.getParentMetaTable();
            ms = mt.getParentMetaSchema();
            qiParent = new QualifiedId(null, ms.getName(), mt.getName());
        } else {
            MetaView mv = mc.getParentMetaView();
            ms = mv.getParentMetaSchema();
            qiParent = new QualifiedId(null, ms.getName(), mv.getName());
        }
        MetaType mty = null;
        if ((iDataType == Types.DISTINCT) || (iDataType == Types.STRUCT))
            mty = createType(sTypeName, ms.getName(), (int) lColumnSize, iDecimalDigits);
        if ((iDataType != Types.DISTINCT) && (iDataType != Types.ARRAY) && (iDataType != Types.STRUCT)) {
            if (iDataType != Types.OTHER) mc.setPreType(iDataType, lColumnSize, iDecimalDigits);
            else mc.setType(sTypeName);
            mc.setTypeOriginal(sTypeName);
        } else if (iDataType == Types.ARRAY) {
            /* parse array constructor "<base> ARRAY[<n>]" */
            Matcher m = _patARRAY_CONSTRUCTOR.matcher(sTypeName);
            if (m.matches()) {
                String sBaseType = m.group(1);
                // handle non-predefined base type
                BaseSqlFactory bsf = new BaseSqlFactory();
                DataType dt = bsf.newDataType();
                dt.parse(sBaseType);
                if (dt.getPredefinedType() != null) mc.setType(dt.format());
                else {
                    MetaType mtyBase = createType(sBaseType, ms.getName(), -1, -1);
                    QualifiedId qiTypeBase = new QualifiedId(null,
                                                             mtyBase.getParentMetaSchema().getName(),
                                                             mtyBase.getName());
                    mc.setTypeName(qiTypeBase.getName());
                    mc.setTypeSchema(qiTypeBase.getSchema());
                }
                mc.setCardinality(Integer.parseInt(m.group(2)));
                mc.setTypeOriginal(sTypeName);
            } else
                throw new SQLException("Invalid ARRAY constructor for column " + mc.getName() + " of table " + qiParent.format() + "!");
        } else {
            mc.setTypeName(mty.getName());
            mc.setTypeSchema(mty.getParentMetaSchema().getName());
        }
        int iNullable = rs.getInt("NULLABLE");
        if (mc.getParentMetaTable() != null) {
            if (iNullable == DatabaseMetaData.columnNoNulls) mc.setNullable(false);
            else if (iNullable == DatabaseMetaData.columnNullable) mc.setNullable(true);
        }
        String sRemarks = rs.getString("REMARKS");
        if (sRemarks != null) mc.setDescription(sRemarks);
        String sColumnDefault = rs.getString("COLUMN_DEF");
        if (mc.getParentMetaTable() != null) {
            if (sColumnDefault != null) mc.setDefaultValue(sColumnDefault);
        }
        int iOrdinalPosition = rs.getInt("ORDINAL_POSITION");
        if (iOrdinalPosition != mc.getPosition()) throw new IOException("Invalid column position found!");
    }

    /**
     * get all roles that are grantees of table privileges and not users.
     *
     * @throws IOException  if an I/O error occurred.
     */
    private void getRoles() throws IOException {
        /* all grantees that are not users (probably) are roles */
        for (int iPrivilege = 0; iPrivilege < _md.getMetaPrivileges(); iPrivilege++) {
            MetaPrivilege mp = _md.getMetaPrivilege(iPrivilege);
            String sGrantee = mp.getGrantee();
            MetaRole mrGrantee = _md.getMetaRole(sGrantee);
            MetaUser muGrantee = _md.getMetaUser(sGrantee);
            if ((mrGrantee == null) && (muGrantee == null)) {
                mrGrantee = _md.createMetaRole(sGrantee, null);
                mrGrantee.setAdmin(mp.getGrantor());
            }
        }
    }

    /**
     * get the current user and all users that are grantors of table privileges.
     *
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException in a database error occurred.
     */
    private void getUsers() throws IOException, SQLException {
        _md.createMetaUser(_dmd.getUserName());
        /* all grantors are users */
        for (int iPrivilege = 0; iPrivilege < _md.getMetaPrivileges(); iPrivilege++) {
            MetaPrivilege mp = _md.getMetaPrivilege(iPrivilege);
            String sGrantor = mp.getGrantor();
            MetaUser muGrantor = _md.getMetaUser(sGrantor);
            if (muGrantor == null) muGrantor = _md.createMetaUser(sGrantor);
        }
    }

    /**
     * get all table privileges.
     *
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException in a database error occurred.
     */
    private void getPrivileges() throws IOException, SQLException {
        for (int iSchema = 0; iSchema < _md.getMetaSchemas(); iSchema++) {
            MetaSchema ms = _md.getMetaSchema(iSchema);
            for (int iTable = 0; iTable < ms.getMetaTables(); iTable++) {
                MetaTable mt = ms.getMetaTable(iTable);
                ResultSet rs = _dmd.getTablePrivileges(null,
                                                       ((BaseDatabaseMetaData) _dmd).toPattern(ms.getName()),
                                                       ((BaseDatabaseMetaData) _dmd).toPattern(mt.getName()));
                while (rs.next()) {
                    String sTableSchema = rs.getString("TABLE_SCHEM");
                    String sTableName = rs.getString("TABLE_NAME");
                    String sGrantor = rs.getString("GRANTOR");
                    String sGrantee = rs.getString("GRANTEE");
                    String sPrivilege = rs.getString("PRIVILEGE");
                    String sIsGrantable = rs.getString("IS_GRANTABLE");
                    MetaPrivilege mp = _md.createMetaPrivilege(sPrivilege,
                                                               (new QualifiedId(null,
                                                                                sTableSchema,
                                                                                sTableName)).format(),
                                                               sGrantor,
                                                               sGrantee);
                    if (!sIsGrantable.equalsIgnoreCase("NO")) mp.setOption("GRANT");
                }
                rs.close();
            }
        }
    }


    /**
     * strip view definitions of "create view ..." or "alter view ..." portion.
     *
     * @param sViewDefinition view definition.
     * @return SELECT statement of view definition.
     */
    private String getQuery(String sViewDefinition) {
        // split on "AS SELECT" removing "CREATE VIEW " or "ALTER VIEW" part
        // complex "AS (...) UNION (...)" cannot be handles satisfactorily.
        String sQuery = sViewDefinition;
        String[] asParts = sQuery.split("(A|a)(S|s)\\s+(\\-\\-[^\\n]*\\s+)?(S|s)(E|e)(L|l)(E|e)(C|c)(T|t)");
        if (asParts.length > 1) sQuery = "SELECT" + asParts[1];
        return sQuery;
    }

    /**
     * get all views in a schema.
     *
     * @param ms schema meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException in a database error occurred.
     */
    private void getViews(MetaSchema ms) throws IOException, SQLException {
        ResultSet rs = _dmd.getTables(null,
                                      ((BaseDatabaseMetaData) _dmd).toPattern(ms.getName()),
                                      "%",
                                      new String[]{"VIEW"});
        while (rs.next()) {
            String sTableSchema = rs.getString("TABLE_SCHEM");
            if (!sTableSchema.equals(ms.getName())) throw new IOException("Invalid view schema found!");
            String sViewName = rs.getString("TABLE_NAME");
            QualifiedId qiView = new QualifiedId(null, sTableSchema, sViewName);
            System.out.println("  View: " + qiView.format());
            MetaView mv = ms.createMetaView(sViewName);
            try {
                String sTableType = rs.getString("TABLE_TYPE");
                if (!sTableType.equals("VIEW")) throw new IOException("Invalid table type for view found!");
                String sRemarks = rs.getString("REMARKS");
                if (sRemarks != null) mv.setDescription(sRemarks);
                String sQueryText = null;
                try {
                    sQueryText = getQuery(rs.getString(BaseDatabaseMetaData._sQUERY_TEXT));
                } catch (SQLException ignored) {
                }
                if (sQueryText != null) mv.setQueryOriginal(sQueryText);
                getColumns(mv);
            } catch (SQLException se) {
                System.err.println("View " + qiView.format() + " could not be archived (" + EU.getExceptionMessage(se) + ")!");
                ms.removeMetaView(mv);
            }
        }
        rs.close();
    }

    /**
     * get all routine metadata associated with the given schema.
     *
     * @param ms schema meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException in a database error occurred.
     */
    private void getRoutines(MetaSchema ms) throws IOException, SQLException {
        ResultSet rs = _dmd.getProcedures(null, ((BaseDatabaseMetaData) _dmd).toPattern(ms.getName()), "%");
        while (rs.next()) {
            String sProcedureSchema = rs.getString("PROCEDURE_SCHEM");
            if (!sProcedureSchema.equals(ms.getName())) throw new IOException("Invalid procedure schema found!");
            String sProcedureName = rs.getString("PROCEDURE_NAME");
            QualifiedId qiRoutine = new QualifiedId(null, sProcedureSchema, sProcedureName);
            System.out.println("  Routine: " + qiRoutine.format());
            String sRemarks = rs.getString("REMARKS");
            String sSpecificName = rs.getString("SPECIFIC_NAME");
            if (sSpecificName == null) sSpecificName = sProcedureName;
            MetaRoutine mr = ms.createMetaRoutine(sSpecificName);
            mr.setName(sProcedureName);
            if (sRemarks != null) mr.setDescription(sRemarks);
            getProcedureParameters(mr);
        }
        rs.close();
        try {
            rs = _dmd.getFunctions(null, ((BaseDatabaseMetaData) _dmd).toPattern(ms.getName()), "%");
            while (rs.next()) {
                String sFunctionSchema = rs.getString("FUNCTION_SCHEM");
                if (!sFunctionSchema.equals(ms.getName())) throw new IOException("Invalid function schema found!");
                String sFunctionName = rs.getString("FUNCTION_NAME");
                String sRemarks = rs.getString("REMARKS");
                String sSpecificName = rs.getString("SPECIFIC_NAME");
                if (sSpecificName == null) sSpecificName = sFunctionName;
                MetaRoutine mr = ms.getMetaRoutine(sSpecificName);
                if (mr == null) // functions may have been returned as procedures ...
                {
                    QualifiedId qiFunction = new QualifiedId(null, sFunctionSchema, sFunctionName);
                    System.out.println("  Function: " + qiFunction.format());
                    mr = ms.createMetaRoutine(sSpecificName);
                    mr.setName(sFunctionName);
                    if (sRemarks != null) mr.setDescription(sRemarks);
                    getFunctionParameters(mr);
                }
            }
            rs.close();
        } catch (java.sql.SQLFeatureNotSupportedException ignored) {
        }
    }

    /**
     * query the number of rows and the sizes of the LOB columns and
     * update the LOB column of maximum size.
     *
     * @param mt
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getRows(MetaTable mt) throws IOException, SQLException {
        /* query rows and LOB sizes */
        int iLobs = 0;
        String sQuery = "SELECT COUNT(*) AS RECORDS";
        if (_bMaxLobNeeded) {
            for (int iColumn = 0; iColumn < mt.getMetaColumns(); iColumn++) {
                MetaColumn mc = mt.getMetaColumn(iColumn);
                int iPreType = mc.getPreType();
                if ((mc.getCardinality() < 0) && ((iPreType == Types.CLOB) || (iPreType == Types.NCLOB) || (iPreType == Types.BLOB)
                        //        ||  (iPreType == Types.SQLXML) || // DB/2 stores XML as a hierarchical object tree ...
                )) {
                    if (!mc.getTypeOriginal().equals("\"LONG\"")) // Oracle idiocy
                    {
                        sQuery = sQuery + ",\r\n SUM(OCTET_LENGTH(" + SqlLiterals.formatId(mc.getName()) + "))" + " AS " + SqlLiterals.formatId(
                                mc.getName() + "_SIZE");
                        iLobs++;
                    }
                }
            }
        }
        QualifiedId qiTable = new QualifiedId(null, mt.getParentMetaSchema().getName(), mt.getName());
        sQuery = sQuery + "\r\nFROM " + qiTable.format();
        Statement stmtSizes = _dmd.getConnection().createStatement();
        stmtSizes.setQueryTimeout(_iQueryTimeoutSeconds);
        ResultSet rsSizes = stmtSizes.executeQuery(sQuery);
        ResultSetMetaData rsmd = rsSizes.getMetaData();
        if (rsSizes.next()) {
            long lRows = rsSizes.getLong("RECORDS");
            mt.setRows(lRows);
            if (_bMaxLobNeeded) {
                for (int iLob = 0; iLob < iLobs; iLob++) {
                    String sLobName = rsmd.getColumnLabel(iLob + 2);
                    long lLobSize = rsSizes.getLong(sLobName);
                    if (_lMaxLobSize < lLobSize) {
                        _lMaxLobSize = lLobSize;
                        _mcMaxLob = mt.getMetaColumn(sLobName.substring(0, sLobName.length() - "_SIZE".length()));
                    }
                }
            }
        } else throw new IOException("Size of table " + mt.getName() + " could not be determined!");

        LOG.debug("Size of table '{}.{}' successfully determined",
                qiTable.getSchema(),
                qiTable.getName());

        rsSizes.close();
        stmtSizes.close();
    }

    /**
     * download unique key meta data of a table.
     *
     * @param mt meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getUniqueKeys(MetaTable mt) throws IOException, SQLException {
        String sUniqueKeyName = null;
        Map<Integer, String> mapUniqueColumns = new HashMap<>();
        ResultSet rs = _dmd.getIndexInfo(null, ((BaseDatabaseMetaData) _dmd).toPattern(mt.getParentMetaSchema().getName()), ((BaseDatabaseMetaData) _dmd).toPattern(mt.getName()), true, false);
        while (rs.next()) {
            String sTableSchema = rs.getString("TABLE_SCHEM");
            if (!sTableSchema.equals(mt.getParentMetaSchema().getName()))
                throw new IOException("Invalid unique key table schema found!");
            String sTableName = rs.getString("TABLE_NAME");
            if (!sTableName.equals(mt.getName())) throw new IOException("Invalid unique key table name found!");
            boolean bNonUnique = rs.getBoolean("NON_UNIQUE");
            if (bNonUnique) throw new IOException("Invalid non-unique unique index found!");
            String sIndexName = rs.getString("INDEX_NAME");
            int iIndexType = rs.getInt("TYPE");
            /* do not list primary key among the candidate keys */
            boolean bPrimary = (mt.getMetaPrimaryKey() != null) && (mt.getMetaPrimaryKey().getName().equals(sIndexName));
            if ((iIndexType != DatabaseMetaData.tableIndexStatistic) && (iIndexType != DatabaseMetaData.tableIndexOther) && (!bPrimary)) {
                MetaUniqueKey muk = mt.getMetaCandidateKey(sIndexName);
                if (muk == null) {
                    addColumns(mt, sUniqueKeyName, mapUniqueColumns);
                    sUniqueKeyName = sIndexName;
                    muk = mt.createMetaCandidateKey(sUniqueKeyName);
                }
                int iOrdinalPosition = rs.getInt("ORDINAL_POSITION");
                String sColumnName = rs.getString("COLUMN_NAME");
                mapUniqueColumns.put(iOrdinalPosition, sColumnName);
            }

            LOG.debug("Metadata for unique key '{}' (table '{}.{}') loaded",
                    sUniqueKeyName,
                    sTableSchema,
                    sTableName);
        }
        rs.close();
        addColumns(mt, sUniqueKeyName, mapUniqueColumns);
    }

    /**
     * download foreign key metadata of a table.
     *
     * @param mt meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getForeignKeys(MetaTable mt) throws IOException, SQLException {
        String sForeignKeyName = null;
        Map<Integer, String> mapFkColumns = new HashMap<>();
        Map<String, String> mapPkColumns = new HashMap<>();
        ResultSet rs = _dmd.getImportedKeys(null, mt.getParentMetaSchema().getName(), mt.getName());
        while (rs.next()) {
            String sPkTableSchema = rs.getString("PKTABLE_SCHEM");
            String sPkTableName = rs.getString("PKTABLE_NAME");
            String sPkColumnName = rs.getString("PKCOLUMN_NAME");
            String sFkTableSchema = rs.getString("FKTABLE_SCHEM");
            if (!sFkTableSchema.equals(mt.getParentMetaSchema().getName()))
                throw new IOException("Invalid foreign key table schema found!");
            String sFkTableName = rs.getString("FKTABLE_NAME");
            if (!sFkTableName.equals(mt.getName())) throw new IOException("Invalid foreign key table name found!");
            String sFkColumnName = rs.getString("FKCOLUMN_NAME");
            MetaColumn mc = mt.getMetaColumn(sFkColumnName);
            if (mc == null) throw new IOException("Invalid foreign key column name found!");
            int iKeySeq = rs.getInt("KEY_SEQ");
            int iUpdateRule = rs.getInt("UPDATE_RULE");
            int iDeleteRule = rs.getInt("DELETE_RULE");
            String sFkName = rs.getString("FK_NAME");
            MetaForeignKey mfk = mt.getMetaForeignKey(sFkName);
            if (mfk == null) {
                /* add references to previous foreign key */
                addReferences(mt, sForeignKeyName, mapFkColumns, mapPkColumns);
                /* create a new foreign key */
                sForeignKeyName = sFkName;
                mfk = mt.createMetaForeignKey(sForeignKeyName);
            }
            mapPkColumns.put(sFkColumnName, sPkColumnName);
            mapFkColumns.put(iKeySeq, sFkColumnName);
            mfk.setReferencedSchema(sPkTableSchema);
            mfk.setReferencedTable(sPkTableName);
            mfk.setDeleteAction(getReferentialAction(iDeleteRule));
            mfk.setUpdateAction(getReferentialAction(iUpdateRule));

            LOG.debug("Metadata for foreign key '{}' (table '{}.{}') loaded",
                    sForeignKeyName,
                    sPkTableSchema,
                    sPkTableName);
        }
        rs.close();
        /* add references to last foreign key */
        addReferences(mt, sForeignKeyName, mapFkColumns, mapPkColumns);
    }

    /**
     * download primary key metadata of a table.
     *
     * @param mt meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getPrimaryKey(MetaTable mt) throws IOException, SQLException {
        String sPkName = "PK_" + mt.getName();
        Map<Integer, String> mapPkColumns = new HashMap<>();
        ResultSet rs = _dmd.getPrimaryKeys(null, mt.getParentMetaSchema().getName(), mt.getName());
        while (rs.next()) {
            String sTableSchema = rs.getString("TABLE_SCHEM");
            if (!sTableSchema.equals(mt.getParentMetaSchema().getName()))
                throw new IOException("Invalid table schema for primary key found!");
            String sTableName = rs.getString("TABLE_NAME");
            if (!mt.getName().equals(sTableName)) throw new IOException("Invalid table name for primary key found!");
            String sColumnName = rs.getString("COLUMN_NAME");
            int iKeySeq = rs.getInt("KEY_SEQ");
            mapPkColumns.put(iKeySeq, sColumnName);
            String s = rs.getString("PK_NAME");
            if (s != null) {
                sPkName = s;
            } else {
                LOG.info("No name for primary key of column '{}.{}.{}' available. Used '{}' instead.",
                        sTableSchema,
                        sTableName,
                        sColumnName,
                        sPkName);
            }

            LOG.debug("Metadata for primary key '{}' (column '{}.{}.{}') loaded",
                    sPkName,
                    sTableSchema,
                    sTableName,
                    sColumnName);
        }
        rs.close();
        if (mapPkColumns.size() > 0) {
            MetaUniqueKey muk = mt.createMetaPrimaryKey(sPkName);
            for (int iColumn = 0; iColumn < mapPkColumns.size(); iColumn++) {
                String sColumnName = mapPkColumns.get(iColumn + 1);
                muk.addColumn(sColumnName);
            }
        }
    }

    /**
     * download column metadata of a table.
     *
     * @param mt table meta data.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getColumns(MetaTable mt) throws IOException, SQLException {
        ResultSet rs = _dmd.getColumns(null,
                                       ((BaseDatabaseMetaData) _dmd).toPattern(mt.getParentMetaSchema().getName()),
                                       ((BaseDatabaseMetaData) _dmd).toPattern(mt.getName()),
                                       "%");
        while (rs.next()) {
            String sTableSchema = rs.getString("TABLE_SCHEM");
            if (!sTableSchema.equals(mt.getParentMetaSchema().getName()))
                throw new IOException("Invalid table schema for column found!");
            String sTableName = rs.getString("TABLE_NAME");
            if (!sTableName.equals(mt.getName())) throw new IOException("Invalid table name for column found!");
            String sColumnName = rs.getString("COLUMN_NAME");
            MetaColumn mc = mt.createMetaColumn(sColumnName);
            getColumnData(rs, mc);

            LOG.debug("Metadata for column '{}.{}.{}' loaded", sTableSchema, sTableName, sColumnName);
        }
        if (mt.getMetaColumns() == 0) throw new SQLException("Table " + mt.getName() + " has no columns!");
        rs.close();
    }

    /**
     * get all global meta data.
     *
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException in a database error occurred.
     */
    private void getGlobalMetaData() throws IOException, SQLException {
        /* get table privileges for all tables */
        getPrivileges();
        /* get the current user and all users that are grantor in a table privilege */
        getUsers();
        /* get all roles that are grantees in a table privilege and not users */
        getRoles();
    }

    /**
     * get all schema meta data.
     *
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException in a database error occurred.
     */
    private void getSchemaMetaData() throws IOException, SQLException {
        for (int iSchema = 0; iSchema < _md.getMetaSchemas(); iSchema++) {
            MetaSchema ms = _md.getMetaSchema(iSchema);
            if (ms.getMetaTables() > 0) {
                if (!_bViewsAsTables) getViews(ms);
                getRoutines(ms);
            }
        }
    }

    /**
     * get all table metadata.
     *
     * @param schemaName schema instead of "%" pattern.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException in a database error occurred.
     */
    private void getTables(String schemaName) throws IOException, SQLException {
        /* first count the tables for progress */
        String[] asTypes = new String[]{"TABLE"};
        if (_bViewsAsTables) asTypes = new String[]{"TABLE", "VIEW"};
        ResultSet rs = _dmd.getTables(null, schemaName, "%", asTypes);
        _iTables = 0;
        while (rs.next()) _iTables++;
        rs.close();
        _iTablesPercent = (_iTables + 99) / 100;
        _iTablesAnalyzed = 0;
        rs = _dmd.getTables(null, schemaName, "%", asTypes);
        while ((rs.next()) && (!cancelRequested())) {
            String sTableSchema = rs.getString("TABLE_SCHEM");
            String sTableName = rs.getString("TABLE_NAME");
            String sTableType = rs.getString("TABLE_TYPE");
            if (!Arrays.asList(asTypes).contains(sTableType)) throw new IOException("Invalid table type found!");
            String sRemarks = rs.getString("REMARKS");
            Schema schema = _md.getArchive().getSchema(sTableSchema);
            if (schema == null) schema = _md.getArchive().createSchema(sTableSchema);
            Table table = schema.getTable(sTableName);
            if (table == null) table = schema.createTable(sTableName);
            MetaTable mt = table.getMetaTable();
            QualifiedId qiTable = new QualifiedId(null, sTableSchema, sTableName);
            System.out.println("  Table: " + qiTable.format());
            if ((sRemarks != null) && (sRemarks.length() > 0)) mt.setDescription(sRemarks);

            LOG.debug("Load metadata for table '{}.{}'", sTableSchema, sTableName);

            getColumns(mt);
            getPrimaryKey(mt);
            getForeignKeys(mt);
            getUniqueKeys(mt);
            getRows(mt);
            incTablesAnalyzed();
        }
        rs.close();
    }

    /**
     * log global download meta data
     *
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException in a database error occurred.
     */
    private void logDownload() throws IOException, SQLException {
        if (_md.getDataOwner() == null) _md.setDataOwner(MetaData.sPLACE_HOLDER);
        if (_md.getDataOriginTimespan() == null) _md.setDataOriginTimespan(MetaData.sPLACE_HOLDER);
        if (_md.getDbName() == null) _md.setDbName(MetaData.sPLACE_HOLDER);
        ProgramInfo pi = ProgramInfo.getProgramInfo();
        _md.setProducerApplication(pi.getProgram() + " " + pi.getVersion() + " " + pi.getCopyright());
        /* client machine: here */
        try {
            InetAddress ia = InetAddress.getLocalHost();
            _md.setClientMachine(ia.getCanonicalHostName());
        } catch (UnknownHostException uhe) {
            LOG.error("Can not determine host", uhe);
        }
        /* database product (incl. version) */
        _md.setDatabaseProduct(_dmd.getDatabaseProductName() + " " + _dmd.getDatabaseProductVersion());
        /* connection */
        _md.setConnection(_dmd.getURL());
        /* database user */
        _md.setDatabaseUser(_dmd.getUserName());
    }

    /**
     * download gets the metadata from the database connection.
     *
     * @param bViewsAsTables if true, views are saved as tables.
     * @param schema         the schema to be downloaded.
     * @param progress       receives progress notifications and sends cancel
     *                       requests.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    public void download(
            boolean bViewsAsTables,
            boolean bMaxLobNeeded,
            String schema,
            Progress progress
    ) throws IOException, SQLException {
        LOG.info("Start meta data download to archive {} (view-as-tables: {}, max-lob-needed: {}, schema: {})",
                this._md.getArchive().getFile().getAbsoluteFile(),
                bViewsAsTables,
                bMaxLobNeeded,
                schema);

        System.out.println("Meta Data");
        _progress = progress;
        _bViewsAsTables = bViewsAsTables;
        _bMaxLobNeeded = bMaxLobNeeded;
        /* global meta data */
        logDownload();
        /* get tables (and Types and relevant schemas) */
        getTables(schema);
        /* get schema meta data (Views, Routines and Types) */
        if (!cancelRequested()) getSchemaMetaData();
        /* get global meta data (Users, Roles, Privileges) */
        if (!cancelRequested()) getGlobalMetaData();
        if (cancelRequested()) throw new IOException("Meta data download cancelled!");

        LOG.info("Meta data download finished");
    }

    public void download(
            boolean bViewsAsTables,
            boolean bMaxLobNeeded,
            Progress progress
    ) throws IOException, SQLException {
        this.download(bViewsAsTables, bMaxLobNeeded, "%", progress);
    }
}