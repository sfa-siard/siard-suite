package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.*;
import ch.enterag.utils.test.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import static org.junit.Assert.*;

public class TableRecordTester {
    private static final File _fileSIARD_10_SOURCE = new File("src/test/resources/testfiles/sql1999.siard");
    private static final File _fileSIARD_10 = new File("src/test/resources/tmp/sql1999.siard");
    private static final File _fileSIARD_21_SOURCE = new File("src/test/resources/testfiles/sql2008.siard");
    private static final File _fileSIARD_21 = new File("src/test/resources/tmp/sql2008.siard");
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final File _fileLOBS_FOLDER_SOURCE = new File("src/test/resources/testfiles/lobs");
    private static final File _fileLOBS_FOLDER = new File("src/test/resources/tmp/lobs");
    private static final File _fileIMPORT_XML = new File("src/test/resources/testfiles/import.xml");
    private static final URI _uriLOBS_FOLDER = URI.create("../lobs/"); // same relative to SIARD file
    private static final URI _uriLOBS_FIELD_FOLDER = URI.create("../lobs/field/");
    private static final URI _uriLOBS_FIELD_FIELD_FOLDER = URI.create("../lobs/field/field/");
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_USER_NAME = "TESTUSER";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_SIMPLE_TABLE_NAME = "TESTSIMPLETABLE";
    private static final String _sTEST_COMPLEX_TABLE_NAME = "TESTCOMPLEXTABLE";
    private static final String _sTEST_COLUMN1_NAME = "CCHAR";
    private static final String _sTEST_TYPE1_NAME = "CHAR";
    private static final String _sTEST_COLUMN2_NAME = "CVARCHAR";
    private static final String _sTEST_TYPE2_NAME = "VARCHAR(256)";
    private static final String _sTEST_COLUMN3_NAME = "CCLOB";
    private static final String _sTEST_TYPE3_NAME = "CLOB(4M)";
    private static final String _sTEST_COLUMN4_NAME = "CNCHAR";
    private static final String _sTEST_TYPE4_NAME = "NCHAR";
    private static final String _sTEST_COLUMN5_NAME = "CNCHAR_VARYING";
    private static final String _sTEST_TYPE5_NAME = "NCHAR VARYING(256)";
    private static final String _sTEST_COLUMN6_NAME = "CNCLOB";
    private static final String _sTEST_TYPE6_NAME = "NCLOB(4G)";
    private static final String _sTEST_COLUMN7_NAME = "CXML";
    private static final String _sTEST_TYPE7_NAME = "XML";
    private static final String _sTEST_COLUMN8_NAME = "CBINARY";
    private static final String _sTEST_TYPE8_NAME = "BINARY";
    private static final String _sTEST_COLUMN9_NAME = "CVARBINARY";
    private static final String _sTEST_TYPE9_NAME = "VARBINARY(256)";
    private static final String _sTEST_COLUMN10_NAME = "CBLOB";
    private static final String _sTEST_TYPE10_NAME = "BLOB";
    private static final String _sTEST_COLUMN11_NAME = "CNUMERIC";
    private static final String _sTEST_TYPE11_NAME = "NUMERIC(10,3)";
    private static final String _sTEST_COLUMN12_NAME = "CDECIMAL";
    private static final String _sTEST_TYPE12_NAME = "DECIMAL";
    private static final String _sTEST_COLUMN13_NAME = "CSMALLINT";
    private static final String _sTEST_TYPE13_NAME = "SMALLINT";
    private static final String _sTEST_COLUMN14_NAME = "CINTEGER";
    private static final String _sTEST_TYPE14_NAME = "INTEGER";
    private static final String _sTEST_COLUMN15_NAME = "CBIGINT";
    private static final String _sTEST_TYPE15_NAME = "BIGINT";
    private static final String _sTEST_COLUMN16_NAME = "CFLOAT";
    private static final String _sTEST_TYPE16_NAME = "FLOAT(7)";
    private static final String _sTEST_COLUMN17_NAME = "CREAL";
    private static final String _sTEST_TYPE17_NAME = "REAL";
    private static final String _sTEST_COLUMN18_NAME = "CDOUBLE";
    private static final String _sTEST_TYPE18_NAME = "DOUBLE PRECISION";
    private static final String _sTEST_COLUMN19_NAME = "CBOOLEAN";
    private static final String _sTEST_TYPE19_NAME = "BOOLEAN";
    private static final String _sTEST_COLUMN20_NAME = "CDATE";
    private static final String _sTEST_TYPE20_NAME = "DATE";
    private static final String _sTEST_COLUMN21_NAME = "CTIME";
    private static final String _sTEST_TYPE21_NAME = "TIME(3)";
    private static final String _sTEST_COLUMN22_NAME = "CTIMESTAMP";
    private static final String _sTEST_TYPE22_NAME = "TIMESTAMP(9)";
    private static final String _sTEST_COLUMN23_NAME = "CINTERVALYEAR";
    private static final String _sTEST_TYPE23_NAME = "INTERVAL YEAR(2) TO MONTH";
    private static final String _sTEST_COLUMN24_NAME = "CINTERVALDAY";
    private static final String _sTEST_TYPE24_NAME = "INTERVAL DAY TO MINUTE";
    private static final String _sTEST_COLUMN25_NAME = "CINTERVALSECOND";
    private static final String _sTEST_TYPE25_NAME = "INTERVAL SECOND(2,5)";

    private static final String _sTEST_DISTINCT_TYPE = "TDISTINCT";
    private static final String _sTEST_DISTINCT_COLUMN = "CDISTINCT";
    private static final String _sTEST_UDT_SIMPLE_TYPE = "TUDTS";
    private static final String _sTEST_UDT_SIMPLE_COLUMN = "CUDTS";
    private static final String _sTEST_UDT_SIMPLE_FIELD1_NAME = "TABLEID";
    private static final String _sTEST_UDT_SIMPLE_FIELD2_NAME = "TRANSCRIPTION";
    private static final String _sTEST_UDT_SIMPLE_FIELD3_NAME = "SOUND";
    private static final String _sTEST_ARRAY_COLUMN = "CARRAY";
    private static final String _sTEST_UDT_COMPLEX_TYPE = "TUDTC";
    private static final String _sTEST_UDT_COMPLEX_COLUMN = "CUDTC";
    private static final String _sTEST_UDT_COMPLEX_ATTRIBUTE1_NAME = "ID";
    private static final String _sTEST_UDT_COMPLEX_ATTRIBUTE2_NAME = "NESTEDROW";
    private static final DU _du = DU.getInstance("en", "dd.MM.yyyy");
    private static final long _lNOW_TIME = 1543832334062L;

    Table _tabSimpleNew = null;
    Table _tabComplexNew = null;
    Table _tabOld = null;

    private void setMandatoryMetaData(Schema schema)
            throws IOException {
        MetaData md = schema.getParentArchive()
                            .getMetaData();
        if (md != null) {
            if (!SU.isNotEmpty(md.getDbName()))
                md.setDbName(_sDBNAME);
            if (!SU.isNotEmpty(md.getDataOwner()))
                md.setDataOwner(_sDATA_OWNER);
            if (!SU.isNotEmpty(md.getDataOriginTimespan()))
                md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
            if (md.getMetaUser(_sTEST_USER_NAME) == null)
                md.createMetaUser(_sTEST_USER_NAME);
        }
    }

    private void createTypes(MetaSchema ms)
            throws IOException {
        MetaType mtDistinct = ms.createMetaType(_sTEST_DISTINCT_TYPE);
        mtDistinct.setCategory("distinct");
        mtDistinct.setBase("INTEGER");

        MetaType mtRow = ms.createMetaType(_sTEST_UDT_SIMPLE_TYPE);
        mtRow.setCategory("udt");
        MetaAttribute mr1 = mtRow.createMetaAttribute(_sTEST_UDT_SIMPLE_FIELD1_NAME);
        mr1.setType("INTEGER");
        MetaAttribute mr2 = mtRow.createMetaAttribute(_sTEST_UDT_SIMPLE_FIELD2_NAME);
        mr2.setType("CLOB");
        MetaAttribute mr3 = mtRow.createMetaAttribute(_sTEST_UDT_SIMPLE_FIELD3_NAME);
        mr3.setType("BLOB");

        MetaType mtUdt = ms.createMetaType(_sTEST_UDT_COMPLEX_TYPE);
        mtUdt.setCategory("udt");
        MetaAttribute mu1 = mtUdt.createMetaAttribute(_sTEST_UDT_COMPLEX_ATTRIBUTE1_NAME);
        mu1.setType("INTEGER");
        MetaAttribute mu2 = mtUdt.createMetaAttribute(_sTEST_UDT_COMPLEX_ATTRIBUTE2_NAME);
        mu2.setTypeName(_sTEST_UDT_SIMPLE_TYPE);
    } 

    private Table createSimpleTable(Schema schema)
            throws IOException {
        Table tab = schema.createTable(_sTEST_SIMPLE_TABLE_NAME);
        assertSame("Table create failed!", schema, tab.getParentSchema());

        MetaColumn mc1 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN1_NAME);
        mc1.setType(_sTEST_TYPE1_NAME);
        mc1.setNullable(false);

        MetaColumn mc2 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN2_NAME);
        mc2.setType(_sTEST_TYPE2_NAME);

        MetaColumn mc3 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN3_NAME);
        mc3.setType(_sTEST_TYPE3_NAME);
        mc3.setLobFolder(_uriLOBS_FOLDER); // lobs folder beside SIARD file

        MetaColumn mc4 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN4_NAME);
        mc4.setType(_sTEST_TYPE4_NAME);

        MetaColumn mc5 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN5_NAME);
        mc5.setType(_sTEST_TYPE5_NAME);

        MetaColumn mc6 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN6_NAME);
        mc6.setType(_sTEST_TYPE6_NAME);

        MetaColumn mc7 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN7_NAME);
        mc7.setType(_sTEST_TYPE7_NAME);

        MetaColumn mc8 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN8_NAME);
        mc8.setType(_sTEST_TYPE8_NAME);

        MetaColumn mc9 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN9_NAME);
        mc9.setType(_sTEST_TYPE9_NAME);

        MetaColumn mc10 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN10_NAME);
        mc10.setType(_sTEST_TYPE10_NAME);

        MetaColumn mc11 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN11_NAME);
        mc11.setType(_sTEST_TYPE11_NAME);

        MetaColumn mc12 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN12_NAME);
        mc12.setType(_sTEST_TYPE12_NAME);

        MetaColumn mc13 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN13_NAME);
        mc13.setType(_sTEST_TYPE13_NAME);

        MetaColumn mc14 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN14_NAME);
        mc14.setType(_sTEST_TYPE14_NAME);
        mc1.setNullable(false);

        MetaColumn mc15 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN15_NAME);
        mc15.setType(_sTEST_TYPE15_NAME);

        MetaColumn mc16 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN16_NAME);
        mc16.setType(_sTEST_TYPE16_NAME);

        MetaColumn mc17 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN17_NAME);
        mc17.setType(_sTEST_TYPE17_NAME);

        MetaColumn mc18 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN18_NAME);
        mc18.setType(_sTEST_TYPE18_NAME);

        MetaColumn mc19 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN19_NAME);
        mc19.setType(_sTEST_TYPE19_NAME);

        MetaColumn mc20 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN20_NAME);
        mc20.setType(_sTEST_TYPE20_NAME);

        MetaColumn mc21 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN21_NAME);
        mc21.setType(_sTEST_TYPE21_NAME);

        MetaColumn mc22 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN22_NAME);
        mc22.setType(_sTEST_TYPE22_NAME);

        MetaColumn mc23 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN23_NAME);
        mc23.setType(_sTEST_TYPE23_NAME);

        MetaColumn mc24 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN24_NAME);
        mc24.setType(_sTEST_TYPE24_NAME);

        MetaColumn mc25 = tab.getMetaTable()
                             .createMetaColumn(_sTEST_COLUMN25_NAME);
        mc25.setType(_sTEST_TYPE25_NAME);

        return tab;
    } 

    private Table createComplexTable(Schema schema)
            throws IOException {
        Table tab = schema.createTable(_sTEST_COMPLEX_TABLE_NAME);
        assertSame("Table create failed!", schema, tab.getParentSchema());

        MetaColumn mc1 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_COLUMN14_NAME);
        mc1.setType(_sTEST_TYPE14_NAME);
        mc1.setNullable(false);

        MetaColumn mc2 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_DISTINCT_COLUMN);
        mc2.setTypeName(_sTEST_DISTINCT_TYPE);

        MetaColumn mc3 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_UDT_SIMPLE_COLUMN);
        mc3.setTypeName(_sTEST_UDT_SIMPLE_TYPE);
        MetaField mfSound = mc3.getMetaField(_sTEST_UDT_SIMPLE_FIELD3_NAME);
        mfSound.setMimeType("audio/x-flac");
        mfSound.setLobFolder(_uriLOBS_FIELD_FOLDER);

        MetaColumn mc4 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_ARRAY_COLUMN);
        mc4.setType("VARCHAR(256)");
        mc4.setCardinality(4);

        MetaColumn mc5 = tab.getMetaTable()
                            .createMetaColumn(_sTEST_UDT_COMPLEX_COLUMN);
        mc5.setTypeName(_sTEST_UDT_COMPLEX_TYPE);
        MetaField mfNested = mc5.getMetaField(_sTEST_UDT_COMPLEX_ATTRIBUTE2_NAME);
        MetaField mfTranscription = mfNested.getMetaField(_sTEST_UDT_SIMPLE_FIELD2_NAME);
        mfTranscription.setLobFolder(_uriLOBS_FIELD_FIELD_FOLDER);

        return tab;
    } 

    private boolean populateSimpleCell(Cell cell, int iCell, int iRecord)
            throws IOException {
        boolean bNull = false;
        long lMillisPerDay = 1000 * 60 * 60 * 24;
        java.util.Date now = new java.util.Date(_lNOW_TIME);
        if (cell.getMetaColumn()
                .isNullable()) {
            if ((iRecord + 4) % 25 == iCell)
                bNull = true;
        }
        if (!bNull) {
            switch (cell.getMetaColumn()
                        .getName()) {
                case _sTEST_COLUMN1_NAME: // CHAR
                    cell.setString(TestUtils.getString(1));
                    break;
                case _sTEST_COLUMN2_NAME: // VARCHAR(256)
                    cell.setString("ABC\u0014DEF\\GHI  JKL" + TestUtils.getString(128));
                    break;
                case _sTEST_COLUMN3_NAME: // CLOB(4M)
                    cell.setReader(new TestReader(2000000));
                    break;
                case _sTEST_COLUMN4_NAME: // NCHAR
                    cell.setString(TestUtils.getNString(1));
                    break;
                case _sTEST_COLUMN5_NAME: // NCHAR VARYING(256)
                    cell.setString(TestUtils.getNString(128));
                    break;
                case _sTEST_COLUMN6_NAME: // NCLOB(4G)
                    cell.setReader(new TestNReader(1000000));
                    break;
                case _sTEST_COLUMN7_NAME: // XML
                    cell.setReader(new TestXmlReader(1000));
                    break;
                case _sTEST_COLUMN8_NAME: // BINARY
                    cell.setBytes(TestUtils.getBytes(1));
                    break;
                case _sTEST_COLUMN9_NAME: // VARBINARY
                    cell.setBytes(TestUtils.getBytes(128));
                    break;
                case _sTEST_COLUMN10_NAME: // BLOB
                    cell.setInputStream(new TestInputStream(1000000));
                    break;
                case _sTEST_COLUMN11_NAME: // NUMERIC(10,2)
                    BigDecimal bdNumeric = BigDecimal.valueOf((iRecord + 4) % 100)
                                                     .multiply(BigDecimal.valueOf(123456.09));
                    System.out.println(bdNumeric.toPlainString());
                    cell.setBigDecimal(bdNumeric);
                    break;
                case _sTEST_COLUMN12_NAME: // DECIMAL
                    BigDecimal bdDecimal = BigDecimal.valueOf((iRecord + 4) % 100)
                                                     .multiply(BigDecimal.valueOf(0.1234567890123456789));
                    System.out.println(bdDecimal.toPlainString());
                    cell.setBigDecimal(bdDecimal);
                    break;
                case _sTEST_COLUMN13_NAME: // SMALLINT
                    cell.setShort((short) 12345);
                    break;
                case _sTEST_COLUMN14_NAME: // INTEGER
                    cell.setInt(1234567890);
                    break;
                case _sTEST_COLUMN15_NAME: // BIGINT
                    cell.setLong(123456789012345678L);
                    break;
                case _sTEST_COLUMN16_NAME: // FLOAT(7)
                    cell.setDouble(0.3141592);
                    break;
                case _sTEST_COLUMN17_NAME: // REAL
                    cell.setFloat(0.3141592f);
                    break;
                case _sTEST_COLUMN18_NAME: // DOUBLE
                    cell.setDouble(3.14159265359);
                    break;
                case _sTEST_COLUMN19_NAME: // BOOLEAN
                    cell.setBoolean(true);
                    break;
                case _sTEST_COLUMN20_NAME: // DATE
                    // get date from now with 00:00:00 in local time zone
                    Calendar cal = _du.toGregorianCalendar(now);
                    cal.set(Calendar.MILLISECOND, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    Date date = new Date(cal.getTime()
                                            .getTime());
                    cell.setDate(date);
                    break;
                case _sTEST_COLUMN21_NAME: // TIME(3)
                    long lTime = now.getTime() % lMillisPerDay;
                    Time time = new Time(lTime);
                    cell.setTime(time);
                    break;
                case _sTEST_COLUMN22_NAME: // TIMESTAMP(9)
                    Timestamp ts = new Timestamp(now.getTime());
                    ts.setNanos(123456789);
                    cell.setTimestamp(ts);
                    break;
                case _sTEST_COLUMN23_NAME: // INTERVAL YEAR(2) TO MONTH
                    try {
                        Duration duration = DatatypeFactory.newInstance()
                                                           .newDurationYearMonth(true, 7, 3);
                        cell.setDuration(duration);
                    } catch (DatatypeConfigurationException dce) {
                        fail(EU.getExceptionMessage(dce));
                    }
                    break;
                case _sTEST_COLUMN24_NAME: // INTERVAL DAY TO MINUTE
                    try {
                        Duration duration = DatatypeFactory.newInstance()
                                                           .newDurationDayTime(true, 12345, 13, 15, 0);
                        cell.setDuration(duration);
                    } catch (DatatypeConfigurationException dce) {
                        fail(EU.getExceptionMessage(dce));
                    }
                    break;
                case _sTEST_COLUMN25_NAME: // INTERVAL SECOND(2,5)
                    try {
                        Duration duration = DatatypeFactory.newInstance()
                                                           .newDurationDayTime(1234567);
                        cell.setDuration(duration);
                    } catch (DatatypeConfigurationException dce) {
                        fail(EU.getExceptionMessage(dce));
                    }
                    break;
            }
        }
        return bNull;
    } 

    private void verifySimpleCell(Cell cell, int iCell, int iRecord)
            throws IOException {
        long lMillisPerDay = 1000 * 60 * 60 * 24;
        java.util.Date now = new java.util.Date(_lNOW_TIME);
        if (cell.getMetaColumn()
                .isNullable()) {
            if ((iRecord + 4) % 25 == iCell)
                assertTrue("NULL value not detected!", cell.isNull());
        }
        if (!cell.isNull()) {
            switch (cell.getMetaColumn()
                        .getName()) {
                case _sTEST_COLUMN1_NAME: // CHAR
                    assertEquals("Invalid CHAR value!", TestUtils.getString(1), cell.getString());
                    break;
                case _sTEST_COLUMN2_NAME: // VARCHAR(256)
                    assertEquals("Invalid VARCHAR(256) value!", "ABC\u0014DEF\\GHI  JKL" + TestUtils.getString(128), cell.getString());
                    break;
                case _sTEST_COLUMN3_NAME: // CLOB(4M)
                    assertEquals("Invalid CLOB length!", 2000000L, cell.getCharLength());
                    assertTrue("Invalid CLOB(4M) value!", TestUtils.equalReaders(new TestReader(2000000), cell.getReader()));
                    break;
                case _sTEST_COLUMN4_NAME: // NCHAR
                    assertEquals("Invalid NCHAR value!", TestUtils.getNString(1), cell.getString());
                    break;
                case _sTEST_COLUMN5_NAME: // NCHAR VARYING(256)
                    assertEquals("Invalid NCHAR VARYING(256) value!", TestUtils.getNString(128), cell.getString());
                    break;
                case _sTEST_COLUMN6_NAME: // NCLOB(4G)
                    assertEquals("Invalid NCLOB length!", 1000000L, cell.getCharLength());
                    assertTrue("Invalid NCLOB(4G) value!", TestUtils.equalReaders(new TestNReader(1000000), cell.getReader()));
                    break;
                case _sTEST_COLUMN7_NAME: // XML
                    assertTrue("Invalid XML value!", TestUtils.equalReaders(new TestXmlReader(1000), cell.getReader()));
                    break;
                case _sTEST_COLUMN8_NAME: // BINARY
                    assertArrayEquals("Invalid BINARY value!", TestUtils.getBytes(1), cell.getBytes());
                    break;
                case _sTEST_COLUMN9_NAME: // VARBINARY
                    assertArrayEquals("Invalid VARBINARY(128) value!", TestUtils.getBytes(128), cell.getBytes());
                    break;
                case _sTEST_COLUMN10_NAME: // BLOB
                    assertEquals("Invalid BLOB length!", 1000000L, cell.getByteLength());
                    assertTrue("Invalid BLOB value!", TestUtils.equalInputStreams(cell.getInputStream(), new TestInputStream(1000000)));
                    break;
                case _sTEST_COLUMN11_NAME: // NUMERIC(10,2)
                    BigDecimal bdNumeric = BigDecimal.valueOf((iRecord + 4) % 100)
                                                     .multiply(BigDecimal.valueOf(123456.09));
                    System.out.println(bdNumeric.toPlainString());
                    assertEquals("Invalid NUMERIC(10,2) value!", bdNumeric, cell.getBigDecimal());
                    break;
                case _sTEST_COLUMN12_NAME: // DECIMAL
                    BigDecimal bdDecimal = BigDecimal.valueOf((iRecord + 4) % 100)
                                                     .multiply(BigDecimal.valueOf(0.1234567890123456789));
                    System.out.println(bdDecimal.toPlainString());
                    assertEquals("Invalid DECIMAL value!", bdDecimal, cell.getBigDecimal());
                    break;
                case _sTEST_COLUMN13_NAME: // SMALLINT
                    assertEquals("Invalid SMALLINT value!", Integer.valueOf(12345), cell.getInt());
                    break;
                case _sTEST_COLUMN14_NAME: // INTEGER
                    assertEquals("Invalid INTEGER value!", Long.valueOf(1234567890L), cell.getLong());
                    break;
                case _sTEST_COLUMN15_NAME: // BIGINT
                    assertEquals("Invalid BIGINT value!", BigInteger.valueOf(123456789012345678L), cell.getBigInteger());
                    break;
                case _sTEST_COLUMN16_NAME: // FLOAT(7)
                    assertEquals("Invalid FLOAT(7) value!", Double.valueOf(0.3141592), cell.getDouble());
                    break;
                case _sTEST_COLUMN17_NAME: // REAL
                    assertEquals("Invalid REAL value!", Float.valueOf(0.3141592f), cell.getFloat());
                    break;
                case _sTEST_COLUMN18_NAME: // DOUBLE PRECISION
                    assertEquals("Invalid DOUBLE PRECISION value!", Double.valueOf(3.14159265359), cell.getDouble());
                    break;
                case _sTEST_COLUMN19_NAME: // BOOLEAN
                    assertEquals("Invalid BOOLEAN value!", Boolean.valueOf(true), cell.getBoolean());
                    break;
                case _sTEST_COLUMN20_NAME: // DATE
                    // get date from now with 00:00:00 in local time zone
                    Calendar cal = _du.toGregorianCalendar(now);
                    cal.set(Calendar.MILLISECOND, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    Date date = new Date(cal.getTime()
                                            .getTime());
                    Date dateCell = cell.getDate();
                    assertEquals("Invalid DATE value!", date, dateCell);
                    break;
                case _sTEST_COLUMN21_NAME: // TIME(3)
                    long lTime = now.getTime() % lMillisPerDay;
                    Time time = new Time(lTime);
                    assertEquals("Invalid TIME value!", time, cell.getTime());
                    break;
                case _sTEST_COLUMN22_NAME: // TIMESTAMP(9)
                    Timestamp ts = new Timestamp(now.getTime());
                    ts.setNanos(123456789);
                    assertEquals("Invalid TIMESTAMP value!", ts, cell.getTimestamp());
                    break;
                case _sTEST_COLUMN23_NAME: // INTERVAL YEAR(2) TO MONTH
                    try {
                        Duration duration = DatatypeFactory.newInstance()
                                                           .newDurationYearMonth(true, 7, 3);
                        assertEquals("Invalid INTERVAL YEAR(2) TO MONTH value!", duration, cell.getDuration());
                    } catch (DatatypeConfigurationException dce) {
                        fail(EU.getExceptionMessage(dce));
                    }
                    break;
                case _sTEST_COLUMN24_NAME: // INTERVAL DAY TO MINUTE
                    try {
                        Duration duration = DatatypeFactory.newInstance()
                                                           .newDurationDayTime(true, 12345, 13, 15, 0);
                        assertEquals("Invalid INTERVAL DAY TO MINUTE value!", duration, cell.getDuration());
                    } catch (DatatypeConfigurationException dce) {
                        fail(EU.getExceptionMessage(dce));
                    }
                    break;
                case _sTEST_COLUMN25_NAME: // INTERVAL SECOND(2,5)
                    try {
                        Duration duration = DatatypeFactory.newInstance()
                                                           .newDurationDayTime(1234567);
                        assertEquals("Invalid INTERVAL SECOND(2,5) value!", duration, cell.getDuration());
                    } catch (DatatypeConfigurationException dce) {
                        fail(EU.getExceptionMessage(dce));
                    }
                    break;
                default:
                    fail("Unexpected column name encountered: " + cell.getMetaColumn()
                                                                      .getName() + "!");
                    break;
            }
        }
    } 

    private boolean populateComplexCell(Cell cell, int iCell, int iRecord)
            throws IOException {
        boolean bNull = false;
        switch (cell.getMetaColumn()
                    .getName()) {
            case _sTEST_COLUMN14_NAME: // INTEGER
                cell.setInt(1234567890);
                break;
            case _sTEST_DISTINCT_COLUMN:
                cell.setInt(987654321);
                break;
            case _sTEST_ARRAY_COLUMN:
                for (int iElement = 0; iElement < cell.getMetaColumn()
                                                      .getCardinality(); iElement++) {
                    Field element = cell.getElement(iElement);
                    switch (iElement) {
                        case 0:
                            element.setString("element 1");
                            break;
                        case 1:
                            break; // null
                        case 2:
                            element.setString("element 3");
                            break;
                        case 3:
                            element.setString("element 4");
                            break;
                    }
                }
                break;
            case _sTEST_UDT_SIMPLE_COLUMN:
                for (int iAttribute = 0; iAttribute < cell.getAttributes(); iAttribute++) {
                    Field attribute = cell.getAttribute(iAttribute);
                    switch (iAttribute) {
                        case 0: // _sTEST_UDT_SIMPLE_FIELD1_NAME INTEGER
                            attribute.setInt(12345);
                            break;
                        case 1: // _sTEST_UDT_SIMPLE_FIELD2_NAME CLOB
                            attribute.setString(TestUtils.getString(20000));
                            break;
                        case 2: // _sTEST_UDT_SIMPLE_FIELD3_NAME BLOB
                            attribute.setBytes(TestUtils.getBytes(2016));
                            break;
                    }
                }
                break;
            case _sTEST_UDT_COMPLEX_COLUMN:
                for (int iAttribute = 0; iAttribute < cell.getAttributes(); iAttribute++) {
                    Field attribute = cell.getAttribute(iAttribute);
                    switch (iAttribute) {
                        case 0:
                            attribute.setInt(-15);
                            break;
                        case 1:
                            for (int iSubAttribute = 0; iSubAttribute < attribute.getAttributes(); iSubAttribute++) {
                                Field field = attribute.getAttribute(iSubAttribute);
                                switch (iSubAttribute) {
                                    case 0: // _sTEST_UDT_SIMPLE_FIELD1_NAME INTEGER
                                        field.setInt(-12345);
                                        break;
                                    case 1: // _sTEST_UDT_SIMPLE_FIELD2_NAME CLOB
                                        field.setReader(new TestReader(2345678));
                                        break;
                                    case 2: // _sTEST_UDT_SIMPLE_FIELD3_NAME BLOB
                                        field.setBytes(TestUtils.getBytes(4567));
                                        break;
                                }
                            }
                            break;
                    }
                }
                break;
        }
        return bNull;
    } 

    private void verifyComplexCell(Cell cell, int iCell, int iRecord)
            throws IOException {
        switch (cell.getMetaColumn()
                    .getName()) {
            case _sTEST_COLUMN14_NAME: // INTEGER
                assertEquals("Invalid INTEGER value!", Integer.valueOf(1234567890), cell.getInt());
                break;
            case _sTEST_DISTINCT_COLUMN:
                assertEquals("", Integer.valueOf(987654321), cell.getInt());
                break;
            case _sTEST_UDT_SIMPLE_COLUMN:
                assertEquals("Invalid number of attributes for " + _sTEST_UDT_SIMPLE_COLUMN + "!", 3, cell.getAttributes());
                for (int iAttribute = 0; iAttribute < cell.getAttributes(); iAttribute++) {
                    Field field = cell.getAttribute(iAttribute);
                    switch (iAttribute) {
                        case 0: // _sTEST_UDT_SIMPLE_FIELD1_NAME INTEGER
                            assertEquals("Invalid INTEGER value for field" + _sTEST_UDT_SIMPLE_FIELD1_NAME + "!",
                                         Integer.valueOf(12345), field.getInt());
                            break;
                        case 1: // _sTEST_UDT_SIMPLE_FIELD2_NAME CLOB
                            assertEquals("Invalid CLOB value for field " + _sTEST_UDT_SIMPLE_FIELD2_NAME + "!",
                                         TestUtils.getString(20000), field.getString());
                            break;
                        case 2: // _sTEST_UDT_SIMPLE_FIELD3_NAME BLOB
                            assertArrayEquals("Invalid BLOB value for field " + _sTEST_UDT_SIMPLE_FIELD3_NAME + "!", TestUtils.getBytes(2016), field.getBytes());
                            break;
                    }
                }
                break;
            case _sTEST_ARRAY_COLUMN:
                assertEquals("Invalid number of elements for " + _sTEST_ARRAY_COLUMN + "!", 4, cell.getElements());
                for (int iElement = 0; iElement < cell.getElements(); iElement++) {
                    Field element = cell.getElement(iElement);
                    switch (iElement) {
                        case 0:
                            assertEquals("Invalid array element(" + (iElement + 1) + ")!",
                                         "element 1", element.getString());
                            break;
                        case 1:
                            assertTrue("array element(" + (iElement + 1) + ") is not NULL!",
                                       element.isNull());
                            break; // null
                        case 2:
                            assertEquals("Invalid array element(" + (iElement + 1) + ")!",
                                         "element 3", element.getString());
                            break;
                        case 3:
                            assertEquals("Invalid array element(" + (iElement + 1) + ")!",
                                         "element 4", element.getString());
                            break;
                    }
                }
                break;
            case _sTEST_UDT_COMPLEX_COLUMN:
                assertEquals("Invalid number of attributes of " + _sTEST_UDT_COMPLEX_COLUMN + "!", 2, cell.getAttributes());
                for (int iAttribute = 0; iAttribute < cell.getAttributes(); iAttribute++) {
                    Field attribute = cell.getAttribute(iAttribute);
                    String sName = attribute.getMetaField()
                                            .getMetaAttribute()
                                            .getName();
                    switch (iAttribute) {
                        case 0:
                            assertEquals("Invalid INTEGER value for UDT field " + sName + "!",
                                         Integer.valueOf(-15), attribute.getInt());
                            break;
                        case 1:
                            assertEquals("Invalid number of UDT attributes " + sName + "!", 3, attribute.getAttributes());
                            for (int iSubAttribute = 0; iSubAttribute < attribute.getAttributes(); iSubAttribute++) {
                                Field field = attribute.getAttribute(iSubAttribute);
                                switch (iSubAttribute) {
                                    case 0: // _sTEST_UDT_SIMPLE_FIELD1_NAME INTEGER
                                        assertEquals("Invalid INTEGER value for field " + _sTEST_UDT_SIMPLE_FIELD1_NAME + " of UDT field " + sName + "!",
                                                     Integer.valueOf(-12345), field.getInt());
                                        break;
                                    case 1: // _sTEST_UDT_SIMPLE_FIELD2_NAME CLOB
                                        assertTrue("Invalid CLOB value for field " + _sTEST_UDT_SIMPLE_FIELD2_NAME + " of UDT field " + sName + "!",
                                                   TestUtils.equalReaders(new TestReader(2345678), field.getReader()));
                                        break;
                                    case 2: // _sTEST_UDT_SIMPLE_FIELD3_NAME BLOB
                                        assertArrayEquals("Invalid BLOB value for field " + _sTEST_UDT_SIMPLE_FIELD3_NAME + " of UDT field " + sName + "!", TestUtils.getBytes(4567), field.getBytes());
                                        break;
                                }
                            }
                            break;
                    }
                }
                break;
        }
    } 

    @Before
    public void setUp() {
        try {
            Files.copy(_fileSIARD_10_SOURCE.toPath(), _fileSIARD_10.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (_fileSIARD_21_SOURCE.exists()) {
                System.out.println("Copying " + _fileSIARD_21_SOURCE.getAbsolutePath() + " to " + _fileSIARD_21.getAbsolutePath());
                Files.copy(_fileSIARD_21_SOURCE.toPath(), _fileSIARD_21.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            FU.copyFiles(_fileLOBS_FOLDER_SOURCE, _fileLOBS_FOLDER, true);
            Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
            System.out.println("Creating " + _fileSIARD_21_NEW);
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            /**
             // the relative folders start with ..
             URI uriGlobal = URI.create("file:/D:/Temp/lobs/");
             archive.getMetaData().setLobFolder(uriGlobal);
             ***/
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            createTypes(schema.getMetaSchema());
            _tabSimpleNew = createSimpleTable(schema);
            _tabComplexNew = createComplexTable(schema);
            archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_10);
            schema = archive.getSchema(0);
            _tabOld = schema.getTable(0);
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @After
    public void tearDown() {
        try {
            setMandatoryMetaData(_tabSimpleNew.getParentSchema());
            _tabOld.getParentSchema()
                   .getParentArchive()
                   .close();
            _tabSimpleNew.getParentSchema()
                         .getParentArchive()
                         .close(); // may throw an exception!
        } catch (IOException ie) {
            System.out.println(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testOld() {
        try {
            TableRecordDispenser rd = _tabOld.openTableRecords();
            TableRecord tableRecord = rd.get();
            assertSame("Invalid record!", _tabOld, tableRecord.getParentTable());
            assertEquals("Invalid cell count!", _tabOld.getMetaTable()
                                                       .getMetaColumns(), tableRecord.getCells());
            for (int i = 0; i < tableRecord.getCells(); i++) {
                Cell cell = tableRecord.getCell(i);
                assertEquals("Invalid cell!", tableRecord, cell.getParentRecord());
                assertSame("Invalid cell meta data!", _tabOld.getMetaTable()
                                                             .getMetaColumn(i), cell.getMetaColumn());
                System.out.print(cell.getMetaColumn()
                                     .getName() + " (" + cell.getMetaColumn()
                                                             .getType() + "): ");
                String sValue = "NULL";
                if (!cell.isNull()) {
                    Object o = cell.getObject();
                    sValue = o.getClass()
                              .getName();
                }
                System.out.println(sValue);
            }
            for (int i = 0; i < tableRecord.getCells(); i++) {
                Cell cell = tableRecord.getCell(i);
                assertEquals("Invalid cell!", tableRecord, cell.getParentRecord());
                assertSame("Invalid cell meta data!", _tabOld.getMetaTable()
                                                             .getMetaColumn(i), cell.getMetaColumn());
                System.out.print(cell.getMetaColumn()
                                     .getName() + " (" + cell.getMetaColumn()
                                                             .getType() + "): ");
                String sValue = "NULL";
                long lLength = -1;
                if (!cell.isNull()) {
                    switch (cell.getMetaColumn()
                                .getPreType()) {
                        case Types.CHAR:
                        case Types.VARCHAR:
                        case Types.NCHAR:
                        case Types.NVARCHAR:
                            sValue = cell.getString();
                            break;
                        case Types.CLOB:
                        case Types.NCLOB:
                        case Types.SQLXML:
                            lLength = cell.getCharLength();
                            if (lLength < 80)
                                sValue = cell.getString();
                            else {
                                Reader rdr = cell.getReader();
                                char[] cbuf = new char[80];
                                int iRead = rdr.read(cbuf);
                                rdr.close();
                                assertEquals("could not read the first 80 characters!", 80, iRead);
                                sValue = new String(cbuf) + "... (" + lLength + ")";
                            }
                            break;
                        case Types.BINARY:
                        case Types.VARBINARY:
                            sValue = BU.toHex(cell.getBytes());
                            break;
                        case Types.BLOB:
                            lLength = cell.getByteLength();
                            if (lLength < 40)
                                sValue = BU.toHex(cell.getBytes());
                            else {
                                InputStream is = cell.getInputStream();
                                byte[] buf = new byte[40];
                                int iRead = is.read(buf);
                                is.close();
                                assertEquals("could not read the first 40 bytes!", 40, iRead);
                                sValue = BU.toHex(buf) + "... (" + lLength + ")";
                            }
                            break;
                        case Types.DECIMAL:
                        case Types.NUMERIC:
                            sValue = cell.getBigDecimal()
                                         .toPlainString();
                            break;
                        case Types.SMALLINT:
                            sValue = cell.getInt()
                                         .toString();
                            break;
                        case Types.INTEGER:
                        case Types.BIGINT:
                            sValue = cell.getBigInteger()
                                         .toString();
                            break;
                        case Types.FLOAT:
                        case Types.DOUBLE:
                            sValue = cell.getDouble()
                                         .toString();
                            break;
                        case Types.REAL:
                            sValue = cell.getFloat()
                                         .toString();
                            break;
                        case Types.BOOLEAN:
                            sValue = cell.getBoolean()
                                         .toString();
                            break;
                        case Types.DATE:
                            sValue = _du.toXsDate(cell.getDate());
                            break;
                        case Types.TIME:
                            sValue = _du.toXsTime(cell.getTime());
                            break;
                        case Types.TIMESTAMP:
                            sValue = _du.toXsDateTime(cell.getTimestamp());
                            break;
                        case Types.OTHER:
                            sValue = cell.getDuration()
                                         .toString();
                            break;
                    }
                }
                System.out.println(sValue);
            }
            rd.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    } 

//  @Test
//  public void testCreateSimple()
//  {
//    URI uriLobsFolder = _fileSIARD_21_NEW.toURI();
//    uriLobsFolder = URI.create(uriLobsFolder.toString()+"/"); // treat ZIP file as a folder ...
//    uriLobsFolder = uriLobsFolder.resolve(_uriLOBS_FOLDER);
//    File fileLobsFolder = new File(uriLobsFolder);
//    FU.deleteFiles(fileLobsFolder);
//
//    RecordRetainer rr = null;
//    try
//    {
//      rr = _tabSimpleNew.createRecords();
//      Record record = rr.create();
//      for (int iCell = 0; iCell < record.getCells(); iCell++)
//      {
//        Cell cell = record.getCell(iCell);
//        populateSimpleCell(cell,iCell,0);
//      }
//      rr.put(record);
//    }
//    catch (IOException ie) { fail(EU.getExceptionMessage(ie)); }
//    finally
//    {
//      if (rr != null)
//      {
//        try 
//        { 
//          rr.close();
//          assertEquals("Invalid number of rows!",1,_tabSimpleNew.getMetaTable().getRows());
//        }
//        catch(IOException ie) {}
//      }
//    }
//  } 

    @Test
    public void testVerifySimple() {
        try {
            System.out.println("Verifying " + _fileSIARD_21.getAbsolutePath());
            Archive archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_21);
            Schema schema = archive.getSchema(0);
            Table tabSimple = schema.getTable(_sTEST_SIMPLE_TABLE_NAME);
            TableRecordDispenser rd = tabSimple.openTableRecords();
            TableRecord tableRecord = rd.get();
            for (int iCell = 0; iCell < tableRecord.getCells(); iCell++) {
                Cell cell = tableRecord.getCell(iCell);
                verifySimpleCell(cell, iCell, 0);
            }
            rd.close();
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    } 

    @Test
    public void testCreateComplex() {
        try {
            URI uriLobsFolder = _fileSIARD_21_NEW.toURI();
            uriLobsFolder = URI.create(uriLobsFolder + "/"); // treat ZIP file as a folder ...
            uriLobsFolder = uriLobsFolder.resolve(_uriLOBS_FOLDER);
            File fileLobsFolder = new File(uriLobsFolder);
            FU.deleteFiles(fileLobsFolder);

            TableRecordRetainer rr = _tabComplexNew.createTableRecords();
            TableRecord tableRecord = rr.create();
            for (int iCell = 0; iCell < tableRecord.getCells(); iCell++) {
                Cell cell = tableRecord.getCell(iCell);
                populateComplexCell(cell, iCell, 0);
            }
            rr.put(tableRecord);
            rr.close();
            assertEquals("Invalid number of rows!", 1, _tabComplexNew.getMetaTable()
                                                                     .getRows());

        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    } 

    @Test
    public void testVerifyComplex() {
        try {
            System.out.println("Verifying " + _fileSIARD_21.getAbsolutePath());
            Archive archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_21);
            Schema schema = archive.getSchema(0);
            Table tabComplex = schema.getTable(_sTEST_COMPLEX_TABLE_NAME);
            TableRecordDispenser rd = tabComplex.openTableRecords();
            TableRecord tableRecord = rd.get();
            for (int iCell = 0; iCell < tableRecord.getCells(); iCell++) {
                Cell cell = tableRecord.getCell(iCell);
                verifyComplexCell(cell, iCell, 0);
            }
            rd.close();
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    } 

    @Test
    public void testCreateBoth() {
        try {
            // test template import at the beginning
            // InputStream is = new FileInputStream("testfiles/import.xml");
            // _tabSimpleNew.getSchema().getArchive().importMetaDataTemplate(is);
            // is.close();

            URI uriLobsFolder = _fileSIARD_21_NEW.toURI();
            uriLobsFolder = URI.create(uriLobsFolder + "/"); // treat ZIP file as a folder ...
            uriLobsFolder = uriLobsFolder.resolve(_uriLOBS_FOLDER);
            File fileLobsFolder = new File(uriLobsFolder);
            FU.deleteFiles(fileLobsFolder);

            TableRecordRetainer rr = _tabSimpleNew.createTableRecords();
            TableRecord tableRecord = rr.create();
            for (int iCell = 0; iCell < tableRecord.getCells(); iCell++) {
                Cell cell = tableRecord.getCell(iCell);
                populateSimpleCell(cell, iCell, 0);
            }
            rr.put(tableRecord);
            rr.close();
            assertEquals("Invalid number of rows!", 1, _tabSimpleNew.getMetaTable()
                                                                    .getRows());

            rr = _tabComplexNew.createTableRecords();
            tableRecord = rr.create();
            for (int iCell = 0; iCell < tableRecord.getCells(); iCell++) {
                Cell cell = tableRecord.getCell(iCell);
                populateComplexCell(cell, iCell, 0);
            }
            rr.put(tableRecord);
            rr.close();
            assertEquals("Invalid number of rows!", 1, _tabComplexNew.getMetaTable()
                                                                     .getRows());

            setMandatoryMetaData(_tabSimpleNew.getParentSchema());

            OutputStream osXml = new FileOutputStream("src/test/resources/tmp/import.xml");
            _tabSimpleNew.getParentSchema()
                         .getParentArchive()
                         .exportMetaData(osXml);
            osXml.close();

            // test template import at the end
            InputStream is = new FileInputStream(_fileIMPORT_XML);
            _tabSimpleNew.getParentSchema()
                         .getParentArchive()
                         .importMetaDataTemplate(is);
            is.close();
            File file = _tabSimpleNew.getParentSchema()
                                     .getParentArchive()
                                     .getFile();
            _tabSimpleNew.getParentSchema()
                         .getParentArchive()
                         .close();
            Archive archive = ArchiveImpl.newInstance();
            archive.open(file);
            assertTrue("We now have a valid archive!", archive.isValid());
            archive.close();

            if (!_fileSIARD_21_SOURCE.exists()) {
                System.out.println("Copying " + _fileSIARD_21_NEW.getAbsolutePath() + " to " + _fileSIARD_21_SOURCE.getAbsolutePath());
                Files.copy(_fileSIARD_21_NEW.toPath(), _fileSIARD_21_SOURCE.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            FU.copyFiles(_fileLOBS_FOLDER, _fileLOBS_FOLDER_SOURCE, true);
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
