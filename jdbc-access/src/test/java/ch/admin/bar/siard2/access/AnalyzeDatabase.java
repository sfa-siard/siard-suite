package ch.admin.bar.siard2.access;

import ch.enterag.utils.EU;
import ch.enterag.utils.database.SqlTypes;
import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.complex.*;
import com.healthmarketscience.jackcess.query.Query;
import com.healthmarketscience.jackcess.util.OleBlob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import static org.junit.Assert.*;

public class AnalyzeDatabase {
    private static final String _sACCESS_DATABASE = "src/test/resources/testfiles/testaccess.accdb";
    /** Jackcess database */
    private Database _db = null;

    public AnalyzeDatabase(String[] args)
            throws IOException {
        String sDatabase = _sACCESS_DATABASE;
        if (args.length > 0)
            sDatabase = args[0];
        _db = new DatabaseBuilder().setReadOnly(true)
                                   .setFile(new File(sDatabase))
                                   .open();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            AnalyzeDatabase ad = new AnalyzeDatabase(args);
            ad.analyze();
            ad.close();
        } catch (Exception e) {
            System.err.println(EU.getExceptionMessage(e));
        }
    }

    /** @return Jackcess database */
    Database getDatabase() {
        return _db;
    }
    public void analyzePropertyMap(PropertyMap pm) {
        System.out.println("    PropertyMap " + pm.getName() + " (" + pm.getClass()
                                                                        .getSimpleName() + ")");
        for (Iterator<PropertyMap.Property> iterProperty = pm.iterator(); iterProperty.hasNext(); ) {
            PropertyMap.Property prop = iterProperty.next();
            System.out.println("      " + prop.getName() + " " + prop.getType()
                                                                     .toString() + ": " + prop.getValue());
        }
    }

    public void analyzeDataType(DataType dt)
            throws SQLException {
        System.out.println("    DataType " + dt.toString() + " (" + dt.getDeclaringClass()
                                                                      .getSimpleName() + ")");
        if (dt != DataType.GUID)
            System.out.println("      SQL Type " + SqlTypes.getTypeName(dt.getSQLType()));
        else
            System.out.println("      SQL Type " + SqlTypes.getTypeName(Types.BINARY));
        System.out.println("      Long " + dt.isLongValue());
        System.out.println("      Textual " + dt.isTextual());
        System.out.println("      TrueVariableLength " + dt.isTrueVariableLength());
        int iUnitSize = dt.getUnitSize();
        if (iUnitSize > 1)
            System.out.println("      Unit size " + iUnitSize);
    }

    @SuppressWarnings("rawtypes")
    public void analyzeComplexInfo(ComplexColumnInfo cci, PropertyMap pm) {
        System.out.println("    ComplexColumnInfo " + cci.getType()
                                                         .toString() + " (" + cci.getClass()
                                                                                 .getSimpleName() + ")");
        if (cci instanceof AttachmentColumnInfo) {
            AttachmentColumnInfo aci = (AttachmentColumnInfo) cci;
            System.out.println("      " + aci);
        } else if (cci instanceof MultiValueColumnInfo) {
            MultiValueColumnInfo mvci = (MultiValueColumnInfo) cci;
            System.out.println("      " + mvci);
            String sRowSourceType = (String) pm.getValue("RowSourceType");
            if (sRowSourceType.equals("Value List")) {
                String sRowSource = (String) pm.getValue("RowSource");
                System.out.println("      Row source: " + sRowSource);
            }
        }
    }

    public void analyzeColumn(Column column)
            throws SQLException, IOException {
        System.out.println("  Column " + column.getName() + " (" + column.getClass()
                                                                         .getSimpleName() + ")");
        System.out.println("    Precision: " + String.valueOf(column.getPrecision()));
        System.out.println("    Scale: " + String.valueOf(column.getScale()));
        System.out.println("    Length: " + String.valueOf(column.getLength()));
        System.out.println("    LengthInUnits: " + String.valueOf(column.getLengthInUnits()));
        PropertyMap pm = column.getProperties();
        String sDescription = (String) pm.getValue("Description");
        if (sDescription != null)
            System.out.println("    Description: " + sDescription);
        // pm.getValue("Format") could be used to distinguish DATE, TIME and TIMESTAMP
        // as well as the number of significant decimals in MONEY
        // and the Rich Text property
        analyzePropertyMap(pm);
        DataType dt = column.getType();
        if (dt != DataType.COMPLEX_TYPE) {
            analyzeDataType(dt);
            if ((dt != DataType.GUID) && (dt.getSQLType() != column.getSQLType()))
                throw new IllegalArgumentException("SQL type inconsistent!");
        } else {
            @SuppressWarnings("rawtypes")
            ComplexColumnInfo cci = column.getComplexInfo();
            if (cci != null)
                analyzeComplexInfo(cci, pm);
        }
    }

    public void analyzeIndex(Index index)
            throws IOException {
        System.out.println("  Index " + index.getName() + " (" + index.getClass()
                                                                      .getSimpleName() + ")");
        for (int iColumn = 0; iColumn < index.getColumns()
                                             .size(); iColumn++) {
            Index.Column idxcol = index.getColumns()
                                       .get(iColumn);
            System.out.println("    " + idxcol.getName() + " (" + (idxcol.isAscending() ? "ASC" : "DESC") + ")");
        }
        Index indexReferenced = index.getReferencedIndex();
        if (indexReferenced != null)
            System.out.println("    Referenced " + indexReferenced.getName());
    }

    public void analyzeRow(Row row, List<? extends Column> listColumns)
            throws SQLException, IOException {
        System.out.println("  Row " + row.getId() + " (" + row.getClass()
                                                              .getSimpleName() + ")");
        for (int iColumn = 0; iColumn < listColumns.size(); iColumn++) {
            Column column = listColumns.get(iColumn);
            Object oValue = column.getRowValue(row);
            String sColumn = column.getName();
            DataType dt = column.getType();
            switch (dt) {
                case BINARY:
                    byte[] buf = row.getBytes(sColumn);
                    assertArrayEquals("Invalid BINARY value!", (byte[]) oValue, buf);
                    break;
                case BOOLEAN:
                    Boolean bool = row.getBoolean(sColumn);
                    assertEquals("Invalid BOOLEAN value!", oValue, bool);
                    break;
                case BYTE:
                    Byte by = row.getByte(sColumn);
                    assertEquals("Invalid BYTE value!", oValue, by);
                    break;
                case COMPLEX_TYPE:
                    ComplexValueForeignKey cvfk = row.getForeignKey(sColumn);
                    assertEquals("Invalid COMPLEX value!", oValue, cvfk);
                    switch (cvfk.getComplexType()) {
                        case ATTACHMENT:
                            for (Iterator<Attachment> iterAttachment = cvfk.getAttachments()
                                                                           .iterator(); iterAttachment.hasNext(); ) {
                                Attachment att = iterAttachment.next();
                                String sFilename = att.getFileName();
                                byte[] bufAttachment = att.getFileData();
                                sFilename = _db.getFile()
                                               .getAbsoluteFile()
                                               .getParentFile()
                                               .getParent() + "\\testfiles\\" + sFilename;
                                FileOutputStream fos = new FileOutputStream(sFilename);
                                fos.write(bufAttachment);
                                fos.close();
                            }
                            break;
                        case MULTI_VALUE:
                            Set<String> setExpected = new HashSet<String>(); // could be parsed from PropertyMap
                            setExpected.add("DE");
                            setExpected.add("FR");
                            setExpected.add("IT");
                            setExpected.add("EN");
                            for (Iterator<SingleValue> iterSingleValue = cvfk.getMultiValues()
                                                                             .iterator(); iterSingleValue.hasNext(); ) {
                                SingleValue sv = iterSingleValue.next();
                                String sSingle = (String) sv.get();
                                assertTrue("Invalid MULTI_VALUE value!", setExpected.contains(sSingle));
                            }
                            break;
                        case VERSION_HISTORY:
                            throw new SQLException("Unsupported version history!");
                        case UNSUPPORTED:
                            throw new SQLException("Unsupported complex type!");
                    }
                    break;
                case DOUBLE:
                    Double d = row.getDouble(sColumn);
                    assertEquals("Invalid DOUBLE value!", oValue, d);
                    break;
                case FLOAT:
                    Float f = row.getFloat(sColumn);
                    assertEquals("Invalid FLOAT value!", oValue, f);
                    break;
                case GUID:
                    String sGuid = row.getString(sColumn);
                    assertEquals("Invalid GUID value!", oValue, sGuid);
                    break;
                case INT:
                    Short sh = row.getShort(sColumn);
                    assertEquals("Invalid INT value!", oValue, sh);
                    break;
                case LONG:
                    Integer i = row.getInt(sColumn);
                    assertEquals("Invalid LONG value!", oValue, i);
                    break;
                case MEMO:
                    String sMemo = row.getString(sColumn);
                    assertEquals("Invalid MEMO value!", oValue, sMemo);
                    break;
                case MONEY:
                    BigDecimal bdMoney = row.getBigDecimal(sColumn);
                    assertEquals("Invalid MONEY value!", oValue, bdMoney);
                    break;
                case NUMERIC:
                    BigDecimal bdNumeric = row.getBigDecimal(sColumn);
                    assertEquals("Invalid NUMERIC value!", oValue, bdNumeric);
                    break;
                case OLE:
                    OleBlob blob = row.getBlob(sColumn);
                    // length is longer than actual content, which starts at
                    // _position in getBytes()
                    OleBlob.Content content = blob.getContent();
                    if (content.getType() == OleBlob.ContentType.SIMPLE_PACKAGE) {
                        OleBlob.SimplePackageContent spc = (OleBlob.SimplePackageContent) content;
                        String sFilename = spc.getFileName();
                        if ((sFilename == null) || (sFilename.length() == 0))
                            sFilename = "simple.bin";
                        sFilename = _db.getFile()
                                       .getAbsoluteFile()
                                       .getParentFile()
                                       .getParent() + "\\testfiles\\" + sFilename;
                        FileOutputStream fos = new FileOutputStream(sFilename);
                        spc.writeTo(fos);
                        fos.close();
                    } else if (content.getType() == OleBlob.ContentType.UNKNOWN) {
                        int iLength = (int) blob.length();
                        byte[] bufOle = blob.getBytes(1L, iLength);
                        assertArrayEquals("Invalid OLE value!", (byte[]) oValue, bufOle);
                    }
                    break;
                case SHORT_DATE_TIME:
                    Date date = row.getDate(sColumn);
                    assertEquals("Invalid DATE value!", oValue, date);
                    break;
                case TEXT:
                    String sText = row.getString(sColumn);
                    assertEquals("Invalid TEXT value!", oValue, sText);
                    break;
                default:
                    throw new SQLException("Invalid data type " + dt + " encountered!");
            }
        }
    }

    public void analyzeTable(Table table)
            throws SQLException, IOException {
        System.out.println("Table " + table.getName() + " (" + table.getClass()
                                                                    .getSimpleName() + ")");
        PropertyMap pm = table.getProperties();
        analyzePropertyMap(pm);
        String sDescription = (String) pm.getValue("Description");
        if (sDescription != null)
            System.out.println("  Description " + sDescription);
        for (int iColumn = 0; iColumn < table.getColumnCount(); iColumn++) {
            Column column = table.getColumns()
                                 .get(iColumn);
            analyzeColumn(column);
        }
        for (int iIndex = 0; iIndex < table.getIndexes()
                                           .size(); iIndex++) {
            Index index = table.getIndexes()
                               .get(iIndex);
            analyzeIndex(index);
        }
        for (int iRow = 0; iRow < table.getRowCount(); iRow++) {
            Row row = table.getNextRow();
            analyzeRow(row, table.getColumns());
        }
    }

    public void analyzeQuery(Query query) {
        System.out.println("Query " + query.getName() + " (" + query.getClass()
                                                                    .getSimpleName() + ")");
    }

    public void analyzeRelationship(Relationship rel) {
        System.out.println("Relationship " + rel.getName() + " (" + rel.getClass()
                                                                       .getSimpleName() + ")");
    }

    public void analyze()
            throws SQLException, IOException {
        System.out.println("TABLES");
        for (Iterator<String> iterTable = _db.getTableNames()
                                             .iterator(); iterTable.hasNext(); ) {
            String sTable = iterTable.next();
            analyzeTable(_db.getTable(sTable));
        }
        System.out.println("QUERIES");
        for (Iterator<Query> iterQuery = _db.getQueries()
                                            .iterator(); iterQuery.hasNext(); ) {
            Query query = iterQuery.next();
            analyzeQuery(query);
        }
        System.out.println("RELATIONSHIPS");
        for (Iterator<Relationship> iterRelationship = _db.getRelationships()
                                                          .iterator(); iterRelationship.hasNext(); ) {
            Relationship rel = iterRelationship.next();
            analyzeRelationship(rel);
        }
        // _db.getRelationships()
    }


    public void close()
            throws IOException {
        _db.close();
    }

}
