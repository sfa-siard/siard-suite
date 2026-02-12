package ch.admin.bar.siard2.access;

import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.DmlStatement;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.SqlStatement;
import ch.enterag.sqlparser.datatype.DataType;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.datatype.enums.PreType;
import ch.enterag.sqlparser.dml.DeleteStatement;
import ch.enterag.sqlparser.dml.UpdateStatement;
import ch.enterag.sqlparser.expression.QuerySpecification;
import ch.enterag.sqlparser.expression.SelectSublist;
import ch.enterag.sqlparser.expression.TablePrimary;
import ch.enterag.utils.csv.CsvParser;
import ch.enterag.utils.csv.CsvParserImpl;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.PropertyMap;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.complex.Attachment;
import com.healthmarketscience.jackcess.complex.ComplexDataType;
import com.healthmarketscience.jackcess.complex.ComplexValueForeignKey;
import com.healthmarketscience.jackcess.complex.SingleValue;
import com.healthmarketscience.jackcess.util.OleBlob;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract public class Shunting {
    public static final int iMAX_BLOB_LENGTH = 16777215;
    public static final int iMAX_CLOB_LENGTH = 8388607;
    private static final SqlFactory _sf = new BaseSqlFactory();


    /** Return the appropriate column name of the select sublist.
     * @param ss select sublist.
     * @return column name.
     */
    public static String getColumnName(SelectSublist ss) {
        String sColumnName = null;
        if (ss.getColumnNames()
              .size() > 0) // alias
            sColumnName = ss.getColumnNames()
                            .get(0)
                            .get();
        if (sColumnName == null)
            sColumnName = ss.format()
                            .replace("\"", "");
        return sColumnName;
    }

    public static DataType convertTypeFromAccess(Column column,
                                                 int iPrecision, int iScale, int iLength, int iLengthInUnits,
                                                 DatabaseMetaData dmd)
            throws IOException, SQLException {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        if (column.getType() != com.healthmarketscience.jackcess.DataType.COMPLEX_TYPE) {
            dt.initPredefinedDataType(pt);
            switch (column.getType()) {
                case BOOLEAN:
                    pt.initBooleanType();
                    break;
                case BYTE:
                case INT:
                    pt.initSmallIntType();
                    break;
                case LONG:
                    pt.initIntegerType();
                    break;
                case MONEY:
                    pt.initDecimalType(iPrecision, iScale);
                    break;
                case FLOAT:
                    pt.initRealType();
                    break;
                case DOUBLE:
                    pt.initDoubleType();
                    break;
                case SHORT_DATE_TIME:
                    pt.initTimestampType(PredefinedType.iUNDEFINED, null);
                    break;
                case TEXT:
                    pt.initVarCharType(iLengthInUnits);
                    break;
                case OLE:
                    pt.initBlobType(iLength, null);
                    break;
                case MEMO:
                    pt.initClobType(iLengthInUnits, null);
                    break;
                case GUID:
                    pt.initCharType(32 + 4 + 2);
                    break; // 16 bytes = 32 hex + 4 hyphens + 2 braces
                case NUMERIC:
                    pt.initNumericType(iPrecision, iScale);
                    break;
                case UNSUPPORTED_FIXEDLEN:
                case BINARY:
                    pt.initBinaryType(iLength);
                    break;
                case UNKNOWN_0D:
                case UNKNOWN_11:
                case UNSUPPORTED_VARLEN:
                    pt.initVarbinaryType(iLength);
                    break;
                case COMPLEX_TYPE:
                    break;
            }
        } else // COMPLEX_TYPE
        {
            int iCardinality = Byte.MAX_VALUE;
            ComplexDataType cdt = column.getComplexInfo()
                                        .getType();
            if (cdt == ComplexDataType.ATTACHMENT)
                pt.initBlobType(PredefinedType.iUNDEFINED, null);
            else if (cdt == ComplexDataType.MULTI_VALUE) {
                PropertyMap pm = column.getProperties();
                String sRowSourceType = (String) pm.getValue("RowSourceType");
                if (sRowSourceType.equals("Value List")) {
                    String sRowSource = (String) pm.getValue("RowSource");
                    CsvParser cp = new CsvParserImpl(';');
                    String[] as = cp.parseLine(sRowSource);
                    iCardinality = as.length;
                    // TODO: handle parsing of datetimes!
                    int iSqlType = Types.INTEGER;
                    iPrecision = PredefinedType.iUNDEFINED;
                    iScale = PredefinedType.iUNDEFINED;
                    iLengthInUnits = PredefinedType.iUNDEFINED;
                    for (int i = 0; i < as.length; i++) {
                        if (iSqlType == Types.INTEGER) {
                            try {
                                Integer.parseInt(as[i]);
                            } catch (NumberFormatException nfe) {
                                iSqlType = Types.DOUBLE;
                            }
                        }
                        if (iSqlType == Types.DOUBLE) {
                            try {
                                Double.parseDouble(as[i]);
                            } catch (NumberFormatException nfe) {
                                iSqlType = Types.DECIMAL;
                            }
                        }
                        if (iSqlType == Types.DECIMAL) {
                            try {
                                BigDecimal bd = new BigDecimal(as[i]);
                                if (bd.precision() > iPrecision)
                                    iPrecision = bd.precision();
                                if (bd.scale() > iScale)
                                    iScale = bd.scale();
                            } catch (NumberFormatException nfe) {
                                iSqlType = Types.VARCHAR;
                            }
                        }
                        if (iSqlType == Types.VARCHAR) {
                            if (as[i].length() > iLengthInUnits)
                                iLengthInUnits = as[i].length();
                        }
                    }
                    switch (iSqlType) {
                        case Types.INTEGER:
                            pt.initIntegerType();
                            break;
                        case Types.DOUBLE:
                            pt.initDoubleType();
                            break;
                        case Types.DECIMAL:
                            pt.initDecimalType(iPrecision, iScale);
                            break;
                        case Types.VARCHAR:
                            pt.initVarCharType(iLengthInUnits);
                            break;
                    }
                } else if (sRowSourceType.equals("Table/Query")) {
                    iCardinality = (short) pm.getValue("ListRows");
                    /***
                     iScale = (byte)pm.getValue("DecimalPlaces");
                     iLength = (short)pm.getValue("ColumnWidth");
                     ***/
                    String sQuery = (String) pm.getValue("RowSource");
                    /* we should parse the query in order to get data type */
                    if (sQuery.startsWith("SELECT")) {
                        String sColumns = sQuery.substring("SELECT".length())
                                                .trim();
                        /* this is shoddy parsing: we assume no "FROM" appears in the column names */
                        int iFrom = sColumns.indexOf("FROM");
                        if (iFrom > 0) {
                            String sTable = sColumns.substring(iFrom + "FROM".length())
                                                    .trim();
                            sColumns = sColumns.substring(0, iFrom)
                                               .trim();
                            /* this is shoddy parsing: we assume no "WHERE" appears in the table names */
                            /* we really assume that there is only one table involved */
                            int iWhere = sTable.indexOf("WHERE");
                            if (iWhere > 0)
                                sTable = sTable.substring(0, iWhere)
                                               .trim();
                            int iOrder = sTable.indexOf("ORDER");
                            if (iOrder > 0)
                                sTable = sTable.substring(0, iOrder)
                                               .trim();
                            if (sTable.startsWith("[") && (sTable.endsWith("]")))
                                sTable = sTable.substring(1, sTable.length() - 1);
                            /* this is shoddy parsing: we assume no commas in the columns names */
                            String[] asColumn = sColumns.split(",");
                            int iColumn = (short) pm.getValue("BoundColumn") - 1;
                            String sColumn = asColumn[iColumn].trim();
                            if (sColumn.startsWith("[") && (sColumn.endsWith("]")))
                                sColumn = sColumn.substring(1, sColumn.length() - 1);
                            BaseDatabaseMetaData bdmd = (BaseDatabaseMetaData) dmd;
                            ResultSet rs = bdmd.getColumns(
                                    null,
                                    "%",
                                    bdmd.toPattern(sTable),
                                    bdmd.toPattern(sColumn));
                            if (rs.next()) {
                                int iDataType = rs.getInt(AccessDatabaseMetaData.sJDBC_DATA_TYPE);
                                iPrecision = rs.getInt(AccessDatabaseMetaData.sJDBC_COLUMN_SIZE);
                                iScale = rs.getInt(AccessDatabaseMetaData.sJDBC_DECIMAL_DIGITS);
                                pt.initialize(iDataType, iPrecision, iScale);
                                dt.initPredefinedDataType(pt);
                            } else
                                throw new IllegalArgumentException("Column " + sColumn + " of " + sTable + " not found!");
                            rs.close();
                        } else
                            throw new IllegalArgumentException("Row source of multi-value list of type Table/Query does not refer to a FROM table or view!");
                    } else
                        throw new IllegalArgumentException("Row source of multi-value list of type Table/Query does not start with SELECT!");
                } else
                    throw new IllegalArgumentException("Cannot (yet) handle value lists of dates!");
            } else if (cdt == ComplexDataType.VERSION_HISTORY) {
                /* version history is a pseudo column (not part of the columns) */
                dt = null;
            } else
                throw new IllegalArgumentException("Cannot handle complex data type " + cdt.toString() + "!");
            if (dt != null)
                dt.initArrayType(pt, iCardinality);
        }
        return dt;
    }


    /** Convert JDBC sql type to SQL data types.
     * N.B.: In this direction we do not handle array types!
     * @param iSqlType a java.sql.Types constant.
     * @param iPrecision maximum precision/length.
     * @param iScale maximum scale.
     * @return SQL data type.
     */
    public static DataType convertTypeFromJdbc(int iSqlType,
                                               int iPrecision, int iScale) {
        DataType dt = _sf.newDataType();
        PredefinedType pt = _sf.newPredefinedType();
        dt.initPredefinedDataType(pt);
        switch (iSqlType) {
            case Types.BIGINT:
                pt.initBigIntType();
                break;
            case Types.BINARY:
                pt.initBinaryType(iPrecision);
                break;
            case Types.BLOB:
            case Types.LONGVARBINARY:
            case Types.DATALINK:
                pt.initBlobType(iPrecision, null);
                break;
            case Types.BOOLEAN:
                pt.initBooleanType();
                break;
            case Types.CHAR:
                pt.initCharType(iPrecision);
                break;
            case Types.CLOB:
            case Types.LONGVARCHAR:
                pt.initClobType(iPrecision, null);
                break;
            case Types.DATE:
                pt.initDateType();
                break;
            case Types.DECIMAL:
                pt.initDecimalType(iPrecision, iScale);
                break;
            case Types.DOUBLE:
                pt.initDoubleType();
                break;
            case Types.FLOAT:
                pt.initFloatType(iPrecision);
                break;
            case Types.INTEGER:
                pt.initIntegerType();
                break;
            case Types.NCHAR:
                pt.initNCharType(iPrecision);
                break;
            case Types.NCLOB:
            case Types.LONGNVARCHAR:
                pt.initNClobType(iPrecision, null);
                break;
            case Types.NUMERIC:
                pt.initNumericType(iPrecision, iScale);
                break;
            case Types.REAL:
                pt.initRealType();
                break;
            case Types.SMALLINT:
            case Types.TINYINT:
                pt.initSmallIntType();
                break;
            case Types.SQLXML:
                pt.initXmlType();
                break;
            case Types.TIME:
                pt.initTimeType(PredefinedType.iUNDEFINED, null);
                break;
            case Types.TIMESTAMP:
                pt.initTimestampType(PredefinedType.iUNDEFINED, null);
                break;
            case Types.VARBINARY:
                pt.initVarbinaryType(iPrecision);
                break;
            case Types.VARCHAR:
                pt.initVarCharType(iPrecision);
                break;
        }
        return dt;
    }

    private static Object getSimpleSqlValue(Row row, String sColumnName, DataType dt)
            throws IOException {
        Object oValue = null;
        PreType pt = dt.getPredefinedType()
                       .getType();
        oValue = row.get(sColumnName);
        if (oValue != null) {
            switch (pt) {
                case BOOLEAN:
                    oValue = row.getBoolean(sColumnName);
                    break;
                case SMALLINT:
                    Short w = -1;
                    if (oValue instanceof Byte)
                        w = Short.valueOf(((Byte) oValue).shortValue());
                    else if (oValue instanceof Short)
                        w = (Short) oValue;
                    else
                        throw new IllegalArgumentException("Unexpected value for SMALLINT!");
                    oValue = BigDecimal.valueOf(w.shortValue());
                    break;
                case INTEGER:
                    Integer i = row.getInt(sColumnName);
                    oValue = BigDecimal.valueOf(i.longValue());
                    break;
                case BIGINT:
                    Long l = (Long) oValue;
                    oValue = BigDecimal.valueOf(l);
                    break;
                case NUMERIC:
                case DECIMAL:
                    oValue = row.getBigDecimal(sColumnName);
                    break;
                case REAL:
                    Float fReal = row.getFloat(sColumnName);
                    oValue = Double.valueOf(fReal.doubleValue());
                    break;
                case FLOAT:
                    Double d = 0.0;
                    if (oValue instanceof Float)
                        d = Double.valueOf(((Float) oValue).doubleValue());
                    else if (oValue instanceof Double)
                        d = (Double) oValue;
                    else
                        throw new IllegalArgumentException("Unexpected value for DOUBLE!");
                    oValue = d;
                    break;
                case DOUBLE:
                    oValue = row.getDouble(sColumnName);
                    break;
                case DATE:
                    oValue = new Date(row.getDate(sColumnName)
                                         .getTime());
                    break;
                case TIME:
                    oValue = new Time(row.getDate(sColumnName)
                                         .getTime());
                    break;
                case TIMESTAMP:
                    oValue = new Timestamp(row.getDate(sColumnName)
                                              .getTime());
                    break;
                case BINARY:
                case VARBINARY:
                    oValue = row.getBytes(sColumnName);
                    break;
                case BLOB:
                case DATALINK:
                    if (oValue instanceof Blob) {
                        Blob blob = (Blob) oValue;
                        try {
                            oValue = blob.getBytes(1L, (int) blob.length());
                        } catch (SQLException se) {
                            throw new IOException("Blob conversion failed!", se);
                        }
                    } else {
                        try {
                            OleBlob oblob = row.getBlob(sColumnName);
                            OleBlob.Content content = oblob.getContent();
                            switch (content.getType()) {
                                case SIMPLE_PACKAGE:
                                    OleBlob.SimplePackageContent spc = (OleBlob.SimplePackageContent) content;
                                    byte[] buf = new byte[(int) spc.length()];
                                    InputStream is = spc.getStream();
                                    is.read(buf);
                                    is.close();
                                    oValue = buf;
                                    break;
                                case UNKNOWN:
                                    oValue = row.getBytes(sColumnName);
                                    break;
                                default:
                                    throw new IllegalArgumentException("Unsupported OleBlob type " + content.getType()
                                                                                                            .toString() + "!");
                            }
                        } catch (IOException ie) {
                            oValue = row.getBytes(sColumnName);
                        }
                    }
                    break;
                case CHAR:
                case NCHAR:
                case VARCHAR:
                case NVARCHAR:
                    oValue = row.getString(sColumnName);
                    break;
                case CLOB:
                    if (oValue instanceof Clob) {
                        Clob clob = (Clob) oValue;
                        try {
                            oValue = clob.getSubString(1L, (int) clob.length());
                        } catch (SQLException se) {
                            throw new IOException("Clob conversion failed!", se);
                        }
                    } else
                        oValue = row.getString(sColumnName);
                    break;
                case NCLOB:
                    if (oValue instanceof NClob) {
                        NClob nclob = (NClob) oValue;
                        try {
                            oValue = nclob.getSubString(1L, (int) nclob.length());
                        } catch (SQLException se) {
                            throw new IOException("NClob conversion failed!", se);
                        }
                    } else if (oValue instanceof Clob) {
                        Clob clob = (Clob) oValue;
                        try {
                            oValue = clob.getSubString(1L, (int) clob.length());
                        } catch (SQLException se) {
                            throw new IOException("Clob conversion failed!", se);
                        }
                    } else
                        oValue = row.getString(sColumnName);
                case XML:
                    if (oValue instanceof SQLXML) {
                        SQLXML sqlxml = (SQLXML) oValue;
                        try {
                            oValue = sqlxml.getString();
                        } catch (SQLException se) {
                            throw new IOException("SQLXML conversion failed!", se);
                        }
                    } else
                        oValue = row.getString(sColumnName);
                    break;
                default:
                    throw new RuntimeException("SQL data type " + dt.format() + " cannot be handled!");
            }
        }
        return oValue;
    }


    /** get complex value from row for filling SQL statement.
     * @param row row.
     * @param sColumnName colum name.
     * @param dt column data type.
     * @return value according to type.
     * @throws IOException
     */
    private static Object getComplexSqlValue(Row row, String sColumnName, DataType dt)
            throws IOException {
        // COMPLEX_TYPE is always an ARRAY type
        Object oValue = row.get(sColumnName);
        List<Object> listValues = new ArrayList<Object>();
        if (oValue instanceof AccessArray) {
            Array array = (Array) oValue;
            try {
                listValues = Arrays.asList((Object[]) array.getArray());
            } catch (SQLException se) {
                throw new IOException("Array conversion failed!", se);
            }
        } else {
            ComplexValueForeignKey cvfk = row.getForeignKey(sColumnName);
            switch (cvfk.getComplexType()) {
                case ATTACHMENT:
                    if (cvfk.getAttachments()
                            .size() > 0) {
                        for (int iAttachment = 0; iAttachment < cvfk.getAttachments()
                                                                    .size(); iAttachment++) {
                            Attachment att = cvfk.getAttachments()
                                                 .get(iAttachment);
                            listValues.add(att.getFileData());
                        }
                    }
                    break;
                case MULTI_VALUE:
                    if (cvfk.getMultiValues()
                            .size() > 0) {
                        for (int iSingleValue = 0; iSingleValue < cvfk.getMultiValues()
                                                                      .size(); iSingleValue++) {
                            SingleValue sv = cvfk.getMultiValues()
                                                 .get(iSingleValue);
                            listValues.add(sv.get());
                        }
                    }
                    break;
                case VERSION_HISTORY:
                    throw new IOException("Unsupported version history!");
                case UNSUPPORTED:
                    throw new IOException("Unsupported complex type!");
            }
        }
        oValue = listValues;
        return oValue;
    }


    /** fill sql statement with values from row.
     * N.B.: It is assumed that global values, the column names and the types
     * have already been set in the sql statement.
     * @param row row (for column values)
     * @param sqlstmt sql statement.
     */
    public static void fillSqlValues(Row row, SqlStatement sqlstmt)
            throws IOException {
        QuerySpecification qs = sqlstmt.getQuerySpecification();
        DmlStatement dstmt = sqlstmt.getDmlStatement();
        if (qs != null) {
            /* fill query with values from row */
            TablePrimary tp = qs.getTableReferences()
                                .get(0)
                                .getTablePrimary();
            for (int iColumn = 0; iColumn < tp.getColumnNames()
                                              .size(); iColumn++) {
                String sColumnName = tp.getColumnNames()
                                       .get(iColumn);
                DataType dt = tp.getColumnType(sColumnName);
                Object oValue = null;
                if (dt.getLength() == DataType.iUNDEFINED)
                    oValue = getSimpleSqlValue(row, sColumnName, dt);
                else
                    oValue = getComplexSqlValue(row, sColumnName, dt);
                tp.setColumnValue(sColumnName, oValue);
            }
        } else if (dstmt != null) {
            UpdateStatement us = dstmt.getUpdateStatement();
            DeleteStatement ds = dstmt.getDeleteStatement();
            if (us != null) {
                for (int iColumn = 0; iColumn < us.getColumnNames()
                                                  .size(); iColumn++)
                    us.setColumnValue(iColumn + 1, row.get(us.getColumnNames()
                                                             .get(iColumn)));
            } else if (ds != null) {
                for (int iColumn = 0; iColumn < ds.getColumnNames()
                                                  .size(); iColumn++)
                    ds.setColumnValue(iColumn + 1, row.get(ds.getColumnNames()
                                                             .get(iColumn)));
            } else
                throw new IllegalArgumentException("Cannot fill INSERT statement with row values.");
        } else
            throw new IllegalArgumentException("Cannot fill DDL statement with row values!");
    }


    /** transform predefined SQL value to a result set row value.
     * @param oValue SQL value.
     * @param pt predefined type of SQL value.
     * @return row value.
     */
    private static Object getRowValue(Object oValue, PreType pt)
            throws IOException {
        BigDecimal bd = null;
        if (oValue != null) {
            switch (pt) {
                case BOOLEAN:
                    break;
                case SMALLINT:
                    if (oValue instanceof BigDecimal) {
                        bd = (BigDecimal) oValue;
                        long l = bd.longValueExact();
                        oValue = Short.valueOf((short) l);
                    } else if (oValue instanceof Short)
                        ;
                    else
                        throw new IOException(oValue.getClass()
                                                    .getName() + " could not be converted to Short!");
                    break;
                case INTEGER:
                    if (oValue instanceof BigDecimal) {
                        bd = (BigDecimal) oValue;
                        oValue = Integer.valueOf((int) bd.longValueExact());
                    } else if (oValue instanceof Integer)
                        ;
                    else
                        throw new IOException(oValue.getClass()
                                                    .getName() + " could not be converted to Integer!");
                    break;
                case BIGINT:
                    if (oValue instanceof BigDecimal) {
                        bd = (BigDecimal) oValue;
                        oValue = Long.valueOf(bd.longValueExact());
                    } else if (oValue instanceof Long)
                        ;
                    else
                        throw new IOException(oValue.getClass()
                                                    .getName() + " could not be converted to Long!");
                    break;
                case NUMERIC:
                case DECIMAL:
                    oValue = oValue;
                    break;
                case REAL:
                    Double d = (Double) oValue;
                    oValue = Float.valueOf(d.floatValue());
                    break;
                case FLOAT:
                case DOUBLE:
                    oValue = oValue;
                    break;
                case DATE:
                    Date date = (Date) oValue;
                    oValue = date;
                    break;
                case TIME:
                    Time time = (Time) oValue;
                    oValue = time;
                    break;
                case TIMESTAMP:
                    Timestamp ts = (Timestamp) oValue;
                    oValue = ts;
                    break;
                case BINARY:
                case VARBINARY:
                    oValue = oValue;
                    break;
                case BLOB:
                case DATALINK:
                    Blob blob = new AccessBlob();
                    try {
                        blob.setBytes(1L, (byte[]) oValue);
                    } catch (SQLException se) {
                        throw new IOException("Blob.setBytes() failed!", se);
                    }
                    oValue = blob;
                    break;
                case CHAR:
                case NCHAR:
                case VARCHAR:
                case NVARCHAR:
                    oValue = oValue;
                    break;
                case CLOB:
                    Clob clob = new AccessClob();
                    try {
                        clob.setString(1L, (String) oValue);
                    } catch (SQLException se) {
                        throw new IOException("Clob.setString() failed!", se);
                    }
                    oValue = clob;
                    break;
                case NCLOB:
                    NClob nclob = new AccessNClob();
                    try {
                        nclob.setString(1L, (String) oValue);
                    } catch (SQLException se) {
                        throw new IOException("NClob.setString() failed!", se);
                    }
                    oValue = nclob;
                    break;
                case XML:
                    SQLXML sqlxml = new AccessSqlXml();
                    try {
                        sqlxml.setString((String) oValue);
                    } catch (SQLException se) {
                        throw new IOException("SQLXML.setString() failed!", se);
                    }
                    oValue = sqlxml;
                    break;
                default:
                    throw new RuntimeException("SQL data type " + pt.getKeyword() + " cannot be handled!");
            }
        }
        return oValue;
    }


    /** get an MS Access row value from an SQL value.
     * @param oValue SQL value.
     * @param dt type of select sublist.
     */
    private static Object getSimpleRowValue(Object oValue, DataType dt)
            throws IOException {
        PreType pt = dt.getPredefinedType()
                       .getType();
        if (oValue != null)
            oValue = getRowValue(oValue, pt);
        return oValue;
    }


    /** get an MS Access row value from an SQL value.
     * @param oValue SQL value.
     * @param dt type of select sublist.
     */
    public static Object getComplexRowValue(Object oValue, DataType dt)
            throws IOException {
        Array array = null;
        @SuppressWarnings("unchecked") // In MS Access all complex types are arrays.
        List<Object> listValues = (List<Object>) oValue;
        if (listValues != null) {
            Object[] ao = new Object[listValues.size()];
            for (int iElement = 0; iElement < listValues.size(); iElement++)
                ao[iElement] = getRowValue(listValues.get(iElement), dt.getDataType()
                                                                       .getPredefinedType()
                                                                       .getType());
            array = new AccessArray(dt.getDataType()
                                      .format(), ao);
        }
        return array;
    }


    /** fill a result set row with values from sql.
     * @param sqlstmt sql statement with initialized column values.
     * @param row instance to be filled.
     */
    public static void fillRowValues(SqlStatement sqlstmt, ResultSetRow row)
            throws IOException {
        QuerySpecification qs = sqlstmt.getQuerySpecification();
        DmlStatement dstmt = sqlstmt.getDmlStatement();
        if (qs != null) {
            for (int iSelect = 0; iSelect < qs.getSelectSublists()
                                              .size(); iSelect++) {
                String sColumnName = null;
                SelectSublist ss = qs.getSelectSublists()
                                     .get(iSelect);
                if (!ss.isAsterisk()) {
                    sColumnName = getColumnName(ss);
                    Object oValue = null;
                    DataType dt = ss.getDataType(sqlstmt);
                    if (dt.getLength() == DataType.iUNDEFINED)
                        oValue = getSimpleRowValue(ss.evaluate(sqlstmt), dt);
                    else
                        oValue = getComplexRowValue(ss.evaluate(sqlstmt), dt);
                    row.put(sColumnName, oValue);
                } else
                    throw new IllegalArgumentException("Cannot handle Asterisk in evaluation!");
            }
        } else if (dstmt != null) {
            UpdateStatement us = dstmt.getUpdateStatement();
            DeleteStatement ds = dstmt.getDeleteStatement();
            if (us != null) {
                for (int iColumn = 0; iColumn < us.getColumnNames()
                                                  .size(); iColumn++) {
                    String sColumnName = us.getColumnNames()
                                           .get(iColumn);
                    row.put(sColumnName, us.getColumnValue(sColumnName));
                }
            } else if (ds != null) {
                for (int iColumn = 0; iColumn < ds.getColumnNames()
                                                  .size(); iColumn++) {
                    String sColumnName = ds.getColumnNames()
                                           .get(iColumn);
                    row.put(sColumnName, ds.getColumnValue(sColumnName));
                }
            } else
                throw new IllegalArgumentException("Cannot fill row values from an INSERT statement.");
        } else
            throw new IllegalArgumentException("Cannot fill row values from a DDL statement!");
    }

}
