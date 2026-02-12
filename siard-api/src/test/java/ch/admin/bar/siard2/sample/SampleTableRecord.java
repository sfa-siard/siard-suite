package ch.admin.bar.siard2.sample;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.enterag.utils.BU;
import ch.enterag.utils.DU;
import ch.enterag.utils.test.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;

public class SampleTableRecord {
    public static final int iSIMPLE_RECORDS = 4;
    public static final int iCOMPLEX_RECORDS = 2;
    private static final DU _du = DU.getInstance("en", "dd.MM.yyyy");
    private TableRecord _tableRecord = null;
    private int _iRecord = -1;

    public static String getCircleJpgUrl() {
        return "file://localhost" + new File("src/test/resources/testfiles/circle.jpg").getAbsolutePath();
    }

    public static void printValue(String sLabel, String sValue) {
        SampleColumn.printValue("  " + sLabel, sValue);
    } 

    public SampleTableRecord(TableRecord tableRecord, int iRecord) {
        _tableRecord = tableRecord;
        _iRecord = iRecord;
    } 

    private String getValue(Value value, int iPreType)
            throws IOException {
        String sValue = null;
        switch (iPreType) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.DATALINK:
                sValue = value.getString();
                break;
            case Types.CLOB:
            case Types.NCLOB:
            case Types.SQLXML:
                long lCharLength = value.getCharLength();
                if (lCharLength < _tableRecord.getParentTable()
                                         .getParentSchema()
                                         .getParentArchive()
                                         .getMaxInlineSize())
                    sValue = value.getString();
                else
                    sValue = "[length " + lCharLength + "]";
                break;
            case Types.BINARY:
            case Types.VARBINARY:
                sValue = BU.toHex(value.getBytes());
                break;
            case Types.BLOB:
                long lByteLength = value.getByteLength();
                if (lByteLength < _tableRecord.getParentTable()
                                         .getParentSchema()
                                         .getParentArchive()
                                         .getMaxInlineSize() / 2)
                    sValue = value.getString();
                else
                    sValue = "[length " + lByteLength + "]";
                break;
            case Types.NUMERIC:
            case Types.DECIMAL:
                BigDecimal bd = value.getBigDecimal();
                sValue = bd.toPlainString();
                break;
            case Types.SMALLINT:
                sValue = String.valueOf(value.getInt()
                                             .intValue());
                break;
            case Types.INTEGER:
                sValue = String.valueOf(value.getLong()
                                             .longValue());
                break;
            case Types.BIGINT:
                BigInteger bi = value.getBigInteger();
                sValue = bi.toString();
                break;
            case Types.REAL:
                sValue = String.valueOf(value.getFloat()
                                             .floatValue());
                break;
            case Types.FLOAT:
            case Types.DOUBLE:
                sValue = String.valueOf(value.getDouble()
                                             .doubleValue());
                break;
            case Types.BOOLEAN:
                sValue = String.valueOf(value.getBoolean()
                                             .booleanValue());
                break;
            case Types.DATE:
                sValue = _du.toXsDate(value.getDate());
                break;
            case Types.TIME:
                sValue = _du.toXsTime(value.getTime());
                break;
            case Types.TIMESTAMP:
                sValue = _du.toXsDateTime(value.getTimestamp());
                break;
            case Types.OTHER:
                sValue = String.valueOf(value.getDuration());
                break;
        }
        return sValue;
    } 

    private int readSubFields(Value value, int iCardinality, MetaType mt)
            throws IOException {
        int iReturn = SampleArchive.iRETURN_OK;
        System.out.print("(");
        if (iCardinality >= 0) {
            for (int iElement = 0; (iReturn == SampleArchive.iRETURN_OK) && (iElement < value.getElements()); iElement++) {
                if (iElement > 0)
                    System.out.print(", ");
                iReturn = readField(value.getElement(iElement), iElement);
            }
        } else {
            CategoryType ct = mt.getCategoryType();
            if (ct == CategoryType.UDT) {
                for (int iAttribute = 0; (iReturn == SampleArchive.iRETURN_OK) && (iAttribute < value.getAttributes()); iAttribute++) {
                    if (iAttribute > 0)
                        System.out.print(", ");
                    iReturn = readField(value.getAttribute(iAttribute), iAttribute);
                }
            }
        }
        System.out.print(")");
        return iReturn;
    } 

    private int readField(Field field, int iField) {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            int iResult = SampleArchive.iRETURN_OK;
            if (!field.isNull()) {
                MetaField mf = field.getMetaField();
                int iPreType = mf.getPreType();
                if ((iPreType != Types.NULL) && (mf.getCardinality() < 0))
                    System.out.print(getValue(field, iPreType));
                else {
                    MetaType mt = mf.getMetaType();
                    iResult = readSubFields(field, mf.getCardinality(), mt);
                }
            } else
                System.out.print("NULL");
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int readCell(Cell cell, int iCell) {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            int iResult = SampleArchive.iRETURN_OK;
            if (!cell.isNull()) {
                MetaColumn mc = cell.getMetaColumn();
                int iPreType = mc.getPreType();
                if ((iPreType != Types.NULL) && (mc.getCardinality() < 0))
                    System.out.print(getValue(cell, iPreType));
                else {
                    MetaType mt = mc.getMetaType();
                    iResult = readSubFields(cell, mc.getCardinality(), mt);
                }
            } else
                System.out.print("NULL");
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    public int readTableRecord() {
        int iReturn = SampleArchive.iRETURN_ERROR;
        System.out.print("      Record[" + _iRecord + "]: ");
        try {
            int iResult = SampleArchive.iRETURN_OK;
            for (int iCell = 0; (iResult == SampleArchive.iRETURN_OK) && (iCell < _tableRecord.getCells()); iCell++) {
                if (iCell > 0)
                    System.out.print("\t");
                iResult = readCell(_tableRecord.getCell(iCell), iCell);
            }
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        System.out.println();
        return iReturn;
    } 

    private int createSimpleCell(Cell cell, int iCell) {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            int iResult = SampleArchive.iRETURN_OK;
            long lMillisPerDay = 1000 * 60 * 60 * 24;
            java.util.Date nowTestable = new java.util.Date();
            nowTestable.setTime(1471338523879L);
            DatatypeFactory df = null;
            try {
                df = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException dce) {
                System.err.println(SampleArchive.getExceptionMessage(dce));
            }
            switch (cell.getMetaColumn()
                        .getName()) {
                case SampleTable.sCOLUMN1_NAME: // CHAR
                    String s1 = TestUtils.getString(iSIMPLE_RECORDS);
                    cell.setString(s1.substring(_iRecord, _iRecord + 1));
                    break;
                case SampleTable.sCOLUMN2_NAME: // VARCHAR(256)
                    String s2 = TestUtils.getString(512);
                    switch (_iRecord) {
                        case 0:
                            cell.setString(s2.substring(0, 256));
                            break;
                        case 1:
                            cell.setString(s2.substring(256, 384));
                            break;
                        case 2:
                            cell.setString(s2.substring(384, 448));
                            break;
                        case 3:
                            cell.setString(s2.substring(448));
                            break;
                    }
                    break;
                case SampleTable.sCOLUMN3_NAME: // CLOB(4M)
                    switch (_iRecord) {
                        case 0:
                            cell.setReader(new TestReader(2000000));
                            break;
                        case 1:
                            cell.setString(TestUtils.getString(2345));
                            break;
                        case 2: 
                            break;
                        case 3:
                            cell.setReader(new TestReader(234567));
                            break;
                    }
                    break;
                case SampleTable.sCOLUMN4_NAME: // NCHAR
                    String s4 = TestUtils.getNString(iSIMPLE_RECORDS);
                    cell.setString(s4.substring(_iRecord, _iRecord + 1));
                    break;
                case SampleTable.sCOLUMN5_NAME: // NCHAR VARYING(256)
                    String s5 = TestUtils.getNString(512);
                    switch (_iRecord) {
                        case 0:
                            cell.setString(s5.substring(0, 256));
                            break;
                        case 1:
                            cell.setString(s5.substring(256, 384));
                            break;
                        case 2:
                            cell.setString(s5.substring(384, 448));
                            break;
                        case 3:
                            cell.setString(s5.substring(448));
                            break;
                    }
                    break;
                case SampleTable.sCOLUMN6_NAME: // NCLOB(4G)
                    switch (_iRecord) {
                        case 0:
                            cell.setReader(new TestNReader(1000000));
                            break;
                        case 1: 
                            break;
                        case 2:
                            cell.setString(TestUtils.getNString(1234));
                            break;
                        case 3:
                            cell.setReader(new TestNReader(123456));
                            break;
                    }
                    break;
                case SampleTable.sCOLUMN7_NAME: // XML
                    switch (_iRecord) {
                        case 0:
                            cell.setReader(new TestXmlReader(1000));
                            break;
                        case 1:
                            cell.setString("<a attr=\"none\">some XML fragment</a>");
                            break;
                        case 2:
                            cell.setReader(new TestXmlReader(10000));
                            break;
                        case 3: 
                            break;
                    }
                    break;
                case SampleTable.sCOLUMN8_NAME: // BINARY
                    byte[] buf8 = TestUtils.getBytes(iSIMPLE_RECORDS);
                    cell.setBytes(Arrays.copyOfRange(buf8, _iRecord, _iRecord + 1));
                    break;
                case SampleTable.sCOLUMN9_NAME: // VARBINARY
                    byte[] buf9 = TestUtils.getBytes(512);
                    switch (_iRecord) {
                        case 0:
                            cell.setBytes(Arrays.copyOfRange(buf9, 0, 256));
                            break;
                        case 1:
                            cell.setBytes(Arrays.copyOfRange(buf9, 200, 345));
                            break;
                        case 2:
                            cell.setBytes(Arrays.copyOfRange(buf9, 345, 456));
                            break;
                        case 3:
                            cell.setBytes(Arrays.copyOfRange(buf9, 400, 512));
                            break;
                    }
                    break;
                case SampleTable.sCOLUMN10_NAME: // BLOB
                    switch (_iRecord) {
                        case 0:
                            cell.setBytes(TestUtils.getBytes(1000));
                            break;
                        case 1:
                            cell.setInputStream(new TestInputStream(1000000));
                            break;
                        case 2:
                            cell.setBytes(TestUtils.getBytes(100));
                            break;
                        case 3:
                            cell.setInputStream(new TestInputStream(100000));
                            break;
                    }
                    break;
                case SampleTable.sCOLUMN11_NAME: // NUMERIC(10,2)
                    BigDecimal bdNumeric = BigDecimal.valueOf((_iRecord + 4) % 100)
                                                     .multiply(BigDecimal.valueOf(123456.09));
                    cell.setBigDecimal(bdNumeric);
                    break;
                case SampleTable.sCOLUMN12_NAME: // DECIMAL
                    BigDecimal bdDecimal = BigDecimal.valueOf((_iRecord + 4) % 100)
                                                     .multiply(BigDecimal.valueOf(0.123456789));
                    cell.setBigDecimal(bdDecimal);
                    break;
                case SampleTable.sCOLUMN13_NAME: // SMALLINT must be a short
                    cell.setShort((short) ((_iRecord + 1) * 12345));
                    break;
                case SampleTable.sCOLUMN14_NAME: // INTEGER must be an int
                    cell.setInt((_iRecord + 1) * 12345678);
                    break;
                case SampleTable.sCOLUMN15_NAME: // BIGINT must be a long
                    cell.setLong((_iRecord + 1) * 12345678901234567L);
                    break;
                case SampleTable.sCOLUMN16_NAME: // FLOAT(7)
                    cell.setDouble((_iRecord + 1) * 0.3141592);
                    break;
                case SampleTable.sCOLUMN17_NAME: // REAL
                    cell.setFloat(0.3141592f);
                    break;
                case SampleTable.sCOLUMN18_NAME: // DOUBLE
                    cell.setDouble(3.14159265359);
                    break;
                case SampleTable.sCOLUMN19_NAME: // BOOLEAN
                    switch (_iRecord) {
                        case 0:
                            cell.setBoolean(true);
                            break;
                        case 1: 
                            break;
                        case 2:
                            cell.setBoolean(false);
                            break;
                        case 3:
                            cell.setBoolean(true);
                            break;
                    }
                    break;
                case SampleTable.sCOLUMN20_NAME: // DATE
                    long lDate = lMillisPerDay * (nowTestable.getTime() / lMillisPerDay);
                    if (_iRecord != 0) {
                        java.util.Date now = new java.util.Date();
                        lDate = lMillisPerDay * (now.getTime() / lMillisPerDay);
                    }
                    Date date = new Date(lDate);
                    cell.setDate(date);
                    break;
                case SampleTable.sCOLUMN21_NAME: // TIME(3)
                    long lTime = nowTestable.getTime() % lMillisPerDay;
                    if (_iRecord != 0) {
                        java.util.Date now = new java.util.Date();
                        lTime = now.getTime() % lMillisPerDay;
                    }
                    Time time = new Time(lTime);
                    cell.setTime(time);
                    break;
                case SampleTable.sCOLUMN22_NAME: // TIMESTAMP(9)
                    long lMillis = nowTestable.getTime();
                    if (_iRecord != 0) {
                        java.util.Date now = new java.util.Date();
                        lMillis = now.getTime();
                    }
                    Timestamp ts = new Timestamp(lMillis);
                    ts.setNanos(123456789);
                    cell.setTimestamp(ts);
                    break;
                case SampleTable.sCOLUMN23_NAME: // INTERVAL YEAR(2) TO MONTH
                    Duration duration23 = null;
                    switch (_iRecord) {
                        case 0:
                            duration23 = df.newDurationYearMonth(true, 7, 3);
                            break;
                        case 1: 
                            break;
                        case 2:
                            duration23 = df.newDurationYearMonth(true, 17, 1);
                            break;
                        case 3:
                            duration23 = df.newDurationYearMonth(true, 1, 11);
                            break;
                    }
                    if (duration23 != null)
                        cell.setDuration(duration23);
                    break;
                case SampleTable.sCOLUMN24_NAME: // INTERVAL DAY TO MINUTE
                    Duration duration24 = null;
                    switch (_iRecord) {
                        case 0:
                            duration24 = df.newDurationDayTime(true, 45, 13, 15, 0);
                            break;
                        case 1:
                            duration24 = df.newDurationDayTime(false, 34, 0, 0, 0);
                            break;
                        case 2:
                            duration24 = df.newDurationDayTime(true, 23, 7, 30, 0);
                            break;
                        case 3:
                            duration24 = df.newDurationDayTime(true, 12, 12, 0, 0);
                            break;
                    }
                    cell.setDuration(duration24);
                    break;
                case SampleTable.sCOLUMN25_NAME: // INTERVAL SECOND(2,5)
                    Duration duration25 = null;
                    switch (_iRecord) {
                        case 0:
                            duration25 = df.newDurationDayTime(12345);
                            break;
                        case 1:
                            duration25 = df.newDurationDayTime(23456);
                            break;
                        case 2:
                            duration25 = df.newDurationDayTime(34567);
                            break;
                        case 3:
                            duration25 = df.newDurationDayTime(45678);
                            break;
                    }
                    cell.setDuration(duration25);
                    break;
                case SampleTable.sCOLUMN26_NAME: // DATALINK
                    cell.setInputStream(new URL(getCircleJpgUrl()).openStream(), getCircleJpgUrl());
                    break;
            }
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createComplexCell(Cell cell, int iCell) {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            int iResult = SampleArchive.iRETURN_OK;
            if ((iCell != 1) || (_iRecord != 1)) // DISTINCT of second record is NULL
            {
                switch (cell.getMetaColumn()
                            .getName()) {
                    case SampleTable.sCOLUMN_ID: // INTEGER
                        if (_iRecord == 0)
                            cell.setInt(1234567890);
                        else
                            cell.setInt(1987654321);
                        break;
                    case SampleTable.sCOLUMN_DISTINCT: // base type: INTEGER
                        if (_iRecord == 0)
                            cell.setInt(987654321);
                        else
                            cell.setInt(123456789);
                        break;
                    case SampleTable.sCOLUMN_ARRAY:
                        for (int iElement = 0; iElement < cell.getMetaColumn()
                                                              .getCardinality(); iElement++) {
                            Field element = cell.getElement(iElement);
                            switch (iElement) {
                                case 0:
                                    element.setString("element " + _iRecord + ",1");
                                    break;
                                case 1:
                                    if (_iRecord == 1)
                                        element.setString("element " + _iRecord + ",2");
                                    // else null
                                    break;
                                case 2:
                                    element.setString("element " + _iRecord + ",3");
                                    break;
                                case 3:
                                    element.setString("element " + _iRecord + ",4");
                                    break;
                            }
                        }
                        break;
                    case SampleTable.sCOLUMN_UDTS:
                        for (int iAttribute = 0; iAttribute < cell.getAttributes(); iAttribute++) {
                            Field field = cell.getAttribute(iAttribute);
                            switch (iAttribute) {
                                case 0: // sUDTS_ATTRIBUTE1_NAME INTEGER
                                    if (_iRecord == 0)
                                        field.setInt(12345);
                                    else
                                        field.setInt(4321);
                                    break;
                                case 1: // _sUDTS_ATTRIBUTE2_NAME CLOB
                                    if (_iRecord == 0)
                                        field.setString(TestUtils.getString(20000));
                                    // else NULL
                                    break;
                                case 2: // _sUDTS_ATTRIBUTE3_NAME BLOB
                                    if (_iRecord == 0)
                                        field.setBytes(TestUtils.getBytes(2016));
                                    else
                                        field.setInputStream(new TestInputStream(20000000));
                                    break;
                                case 3: // _sUDTS_ATTRIBUTE4_NAME DATALINK
                                    if (_iRecord == 0) {
                                        field.setInputStream(new URL(getCircleJpgUrl()).openStream(), getCircleJpgUrl());
                                    }
                                    // else NULL
                                    break;
                            }
                        }
                        break;
                    case SampleTable.sCOLUMN_UDTC:
                        for (int iAttribute = 0; iAttribute < cell.getAttributes(); iAttribute++) {
                            Field attribute = cell.getAttribute(iAttribute);
                            switch (iAttribute) {
                                case 0:
                                    if (_iRecord == 0)
                                        attribute.setInt(-15);
                                    else
                                        attribute.setInt(0);
                                    break;
                                case 1:
                                    for (int iSubAttribute = 0; iSubAttribute < attribute.getAttributes(); iSubAttribute++) {
                                        Field field = attribute.getAttribute(iSubAttribute);
                                        switch (iSubAttribute) {
                                            case 0: // _sTEST_ROW_FIELD1_NAME INTEGER
                                                if (_iRecord == 0)
                                                    field.setInt(-12345);
                                                else
                                                    field.setInt(12345);
                                                break;
                                            case 1: // _sTEST_ROW_FIELD2_NAME CLOB
                                                if (_iRecord == 0)
                                                    field.setReader(new TestReader(2345678));
                                                // else NULL
                                                break;
                                            case 2: // _sTEST_ROW_FIELD3_NAME BLOB
                                                if (_iRecord == 0)
                                                    field.setBytes(TestUtils.getBytes(4567));
                                                else
                                                    field.setInputStream(new TestInputStream(100000));
                                                break;
                                            case 3: // _sTEST_ROW_FIELD4_NAME DATALINK
                                                if (_iRecord == 0) {
                                                    field.setInputStream(new URL(getCircleJpgUrl()).openStream(), getCircleJpgUrl());
                                                }
                                                // else NULL
                                                break;
                                        }
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createSimpleRecord() {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            int iResult = SampleArchive.iRETURN_OK;
            for (int iCell = 0; (iResult == SampleArchive.iRETURN_OK) && (iCell < _tableRecord.getCells()); iCell++)
                iResult = createSimpleCell(_tableRecord.getCell(iCell), iCell);
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createComplexRecord() {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            int iResult = SampleArchive.iRETURN_OK;
            for (int iCell = 0; (iResult == SampleArchive.iRETURN_OK) && (iCell < _tableRecord.getCells()); iCell++)
                iResult = createComplexCell(_tableRecord.getCell(iCell), iCell);
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    public int createRecord() {
        int iReturn = SampleArchive.iRETURN_ERROR;
        if (SampleTable.sTABLE_SIMPLE.equals(_tableRecord.getParentTable()
                                                    .getMetaTable()
                                                    .getName()))
            iReturn = createSimpleRecord();
        else if (SampleTable.sTABLE_COMPLEX.equals(_tableRecord.getParentTable()
                                                          .getMetaTable()
                                                          .getName()))
            iReturn = createComplexRecord();
        return iReturn;
    } 

} 
