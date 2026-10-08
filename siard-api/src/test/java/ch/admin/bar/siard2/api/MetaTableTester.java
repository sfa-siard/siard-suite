package ch.admin.bar.siard2.api;


import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.api.primary.TableImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MetaTableTester {
    private static final File _fileSIARD_10_SOURCE = new File("src/test/resources/testfiles/sql1999.siard");
    private static final File _fileSIARD_10 = new File("src/test/resources/tmp/sql1999.siard");
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final ConfigurationProperties _cp = new ConfigurationProperties();
    private static final File _fileLOBS_FOLDER = new File(_cp.getLobsFolder());
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_TABLE_NAME = "TESTTABLE";
    private static final String _sTEST_COLUMN1_NAME = "ID";
    private static final String _sTEST_PRIMARY_KEY_NAME = "TESTPK";
    private static final String _sTEST_CANDIDATE_KEY_NAME = "TESTCK";
    private static final String _sTEST_FOREIGN_KEY_NAME = "TESTFK";
    private static final String _sTEST_CHECK_CONSTRAINT_NAME = "TESTCC";
    private static final String _sTEST_TRIGGER_NAME = "TESTTRG";
    private static final String _sTEST_DISTINCT_TYPE = "TDISTINCT";
    private static final String _sTEST_DISTINCT_COLUMN = "CDISTINCT";
    private static final String _sTEST_UDTS_TYPE = "TUDTS";
    private static final String _sTEST_UDTS_COLUMN = "CUDTS";
    private static final String _sTEST_UDTS_ATTRIBUTE1_NAME = "TABLEID";
    private static final String _sTEST_UDTS_ATTRIBUTE2_NAME = "TRANSCRIPTION";
    private static final String _sTEST_UDTS_ATTRIBUTE3_NAME = "SOUND";
    private static final String _sTEST_ARRAY_COLUMN = "CARRAY";
    private static final String _sTEST_UDTC_TYPE = "TUDTC";
    private static final String _sTEST_UDTC_COLUMN = "CUDTC";
    private static final String _sTEST_UDTC_ATTRIBUTE1_NAME = "ID";
    private static final String _sTEST_UDTC_ATTRIBUTE2_NAME = "NESTEDROW";
    private static final String _sTEST_UDTC_ATTRIBUTE3_NAME = "NESTEDARRAY";

    MetaTable _mtNew = null;
    MetaTable _mtOld = null;

    private void setMandatoryMetaData(Schema schema) {
        try {
            MetaData md = schema.getParentArchive()
                                .getMetaData();
            if (!SU.isNotEmpty(md.getDbName()))
                md.setDbName(_sDBNAME);
            if (!SU.isNotEmpty(md.getDataOwner()))
                md.setDataOwner(_sDATA_OWNER);
            if (!SU.isNotEmpty(md.getDataOriginTimespan()))
                md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
            if (md.getMetaSchemas() == 0)
                md.getArchive()
                  .createSchema("TEST_SCHEMA");
            if (_mtNew.getMetaColumn(_sTEST_COLUMN1_NAME) == null) {
                MetaColumn mc = _mtNew.createMetaColumn(_sTEST_COLUMN1_NAME);
                mc.setType("INTEGER");
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    private void deleteFolder(File fileFolder)
            throws IOException {
        if (fileFolder.exists()) {
            if (fileFolder.isDirectory()) {
                File[] afile = fileFolder.listFiles();
                for (int iFile = 0; iFile < afile.length; iFile++) {
                    File file = afile[iFile];
                    if (file.isDirectory())
                        deleteFolder(file);
                    else
                        file.delete();
                }
                fileFolder.delete();
            } else
                throw new IOException("deleteFolder only deletes directories!");
        }
    }

    private void createTypes(MetaSchema ms)
            throws IOException {
        MetaType mtDistinct = ms.createMetaType(_sTEST_DISTINCT_TYPE);
        mtDistinct.setCategory("distinct");
        mtDistinct.setBase("INTEGER");

        MetaType mtRow = ms.createMetaType(_sTEST_UDTS_TYPE);
        mtRow.setCategory("udt");
        MetaAttribute mr1 = mtRow.createMetaAttribute(_sTEST_UDTS_ATTRIBUTE1_NAME);
        mr1.setType("INTEGER");
        MetaAttribute mr2 = mtRow.createMetaAttribute(_sTEST_UDTS_ATTRIBUTE2_NAME);
        mr2.setType("CLOB");
        MetaAttribute mr3 = mtRow.createMetaAttribute(_sTEST_UDTS_ATTRIBUTE3_NAME);
        mr3.setType("BLOB");

        MetaType mtUdt = ms.createMetaType(_sTEST_UDTC_TYPE);
        mtUdt.setCategory("udt");
        MetaAttribute mu1 = mtUdt.createMetaAttribute(_sTEST_UDTC_ATTRIBUTE1_NAME);
        mu1.setType("INTEGER");
        MetaAttribute mu2 = mtUdt.createMetaAttribute(_sTEST_UDTC_ATTRIBUTE2_NAME);
        mu2.setTypeName(_sTEST_UDTS_TYPE);
        MetaAttribute mu3 = mtUdt.createMetaAttribute(_sTEST_UDTC_ATTRIBUTE3_NAME);
        mu3.setType("DECIMAL");
        mu3.setCardinality(100);
    }

    private void createComplexColumns()
            throws IOException {
        MetaColumn mc1 = _mtNew.createMetaColumn(_sTEST_COLUMN1_NAME);
        assertEquals(_mtNew, mc1.getParentMetaTable(), "Wrong parent meta table of simple column!");
        mc1.setType("INTEGER");
        mc1.setNullable(false);

        MetaColumn mc2 = _mtNew.createMetaColumn(_sTEST_DISTINCT_COLUMN);
        assertEquals(_mtNew, mc2.getParentMetaTable(), "Wrong parent meta table of distinct column!");
        mc2.setTypeName(_sTEST_DISTINCT_TYPE);

        MetaColumn mc3 = _mtNew.createMetaColumn(_sTEST_UDTS_COLUMN);
        assertEquals(_mtNew, mc3.getParentMetaTable(), "Wrong parent meta table of row column!");
        mc3.setTypeName(_sTEST_UDTS_TYPE);

        MetaColumn mc4 = _mtNew.createMetaColumn(_sTEST_ARRAY_COLUMN);
        assertEquals(_mtNew, mc4.getParentMetaTable(), "Wrong parent meta table of array column!");
        mc4.setType("VARCHAR(256)");
        mc4.setCardinality(4);
        mc4.getMetaField(2); // 3 out of 4 array elements

        MetaColumn mc5 = _mtNew.createMetaColumn(_sTEST_UDTC_COLUMN);
        assertEquals(_mtNew, mc5.getParentMetaTable(), "Wrong parent meta table of udt column!");
        mc5.setTypeName(_sTEST_UDTC_TYPE);
    }

    @BeforeEach
    public void setUp() {
        try {
            Files.copy(_fileSIARD_10_SOURCE.toPath(), _fileSIARD_10.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
            deleteFolder(_fileLOBS_FOLDER);
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            createTypes(schema.getMetaSchema());
            Table table = schema.createTable(_sTEST_TABLE_NAME);
            _mtNew = table.getMetaTable();
            assertSame(table, _mtNew.getTable(), "MetaTable create failed!");
            archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_10);
            schema = archive.getSchema(0);
            table = schema.getTable(0);
            _mtOld = table.getMetaTable();
            assertSame(table, _mtOld.getTable(), "MetaTable open failed!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            setMandatoryMetaData(_mtNew.getTable()
                                       .getParentSchema());
            _mtNew.getTable()
                  .getParentSchema()
                  .getParentArchive()
                  .close();
            setMandatoryMetaData(_mtOld.getTable()
                                       .getParentSchema());
            _mtOld.getTable()
                  .getParentSchema()
                  .getParentArchive()
                  .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testName() {
        assertEquals(_sTEST_TABLE_NAME, _mtNew.getName(), "Invalid name of new table!");
        assertEquals("TABLETEST2", _mtOld.getName(), "Invalid name of old table!");
    }

    @Test
    public void testFolder() {
        assertEquals(TableImpl._sTABLE_FOLDER_PREFIX + "0", _mtNew.getFolder(), "Invalid folder name of new table!");
        assertEquals(TableImpl._sTABLE_FOLDER_PREFIX + "0", _mtOld.getFolder(), "Invalid folder name of old table!");
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _mtNew.setDescription(sDescription);
        assertEquals(sDescription, _mtNew.getDescription(), "Invalid Description!");
        _mtOld.setDescription(sDescription);
        assertEquals(sDescription, _mtOld.getDescription(), "Invalid Description!");
    }

    @Test
    public void testRows() {
        assertEquals(0, _mtNew.getRows(), "New table has zero rows!");
        assertEquals(1, _mtOld.getRows(), "Old table has one row!");
    }

    @Test
    public void testGetMetaColumns() {
        try {
            assertEquals(0, _mtNew.getMetaColumns(), "New table column meta data!");
            createComplexColumns();
            assertEquals(5, _mtNew.getMetaColumns(), "New table has wrong number of column meta data!");
            System.out.println(_mtOld.getMetaColumns());
            assertEquals(30, _mtOld.getMetaColumns(), "Old table has wrong number of column meta data!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaColumn_Int() {
        try {
            createComplexColumns(); // has already tested createMetaColumn() ...
            assertNull(_mtNew.getMetaColumn(2)
                                                                          .getType(), "Column of type row must not have PreType!");
            assertEquals(_sTEST_UDTS_COLUMN, _mtNew.getMetaColumn(2)
                                                                                                             .getName(), "Wrong column name of new column of type row retrieved!");
            assertEquals(_sTEST_UDTS_TYPE, _mtNew.getMetaColumn(2)
                                                                                                           .getTypeName(), "Wrong column type of new column of type row retrieved!");
            System.out.println(_mtOld.getMetaColumn(22)
                                     .getName());
            assertEquals("CREAL", _mtOld.getMetaColumn(22)
                                                                                     .getName(), "Wrong column name of old table retrieved!");
            System.out.println(_mtOld.getMetaColumn(22)
                                     .getType());
            assertEquals("REAL", _mtOld.getMetaColumn(22)
                                                                                   .getType(), "Wrong column type of old table retrievd!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaColumn_String() {
        try {
            createComplexColumns(); // has already tested createMetaColumn() ...
            assertEquals(_sTEST_UDTS_COLUMN, _mtNew.getMetaColumn(_sTEST_UDTS_COLUMN)
                                                                                                             .getName(), "Wrong column name of new column of type row retrieved!");
            assertEquals(_sTEST_UDTS_TYPE, _mtNew.getMetaColumn(_sTEST_UDTS_COLUMN)
                                                                                                           .getTypeName(), "Wrong column type of new column of type row retrieved!");
            assertEquals("CREAL", _mtOld.getMetaColumn("CREAL")
                                                                                     .getName(), "Wrong column name of old table retrieved!");
            assertEquals("REAL", _mtOld.getMetaColumn("CREAL")
                                                                                   .getType(), "Wrong column type of old table retrievd!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testPrimaryKey() {
        try {
            assertNull(_mtNew.getMetaPrimaryKey(), "New table has primary key!");
            createComplexColumns();
            MetaUniqueKey mpk = _mtNew.createMetaPrimaryKey(_sTEST_PRIMARY_KEY_NAME);
            assertSame(_mtNew, mpk.getParentMetaTable(), "Error in primary key!");
            assertEquals(_sTEST_PRIMARY_KEY_NAME, mpk.getName(), "Wrong primary key name!");
            assertEquals(0, mpk.getColumns(), "Wrong number of primary key columns!");
            assertFalse(mpk.isValid(), "Invalid primary key is valid!");
            String sDescription = "Description";
            mpk.setDescription(sDescription);
            assertEquals(sDescription, mpk.getDescription(), "Wrong primary key description!");
            mpk.addColumn(_sTEST_COLUMN1_NAME);
            assertTrue(mpk.isValid(), "Valid primary key is invalid!");
            assertEquals(1, mpk.getColumns(), "Wrong number of primary key columns!");
            assertEquals(_sTEST_COLUMN1_NAME, mpk.getColumn(0), "Erroneous primary key column!");

            assertNotNull(_mtOld.getMetaPrimaryKey(), "Old table has no primary key!");
            mpk = _mtOld.getMetaPrimaryKey();
            assertSame(_mtOld, mpk.getParentMetaTable(), "Error in primary key!");
            assertEquals("TABLETEST2PK", mpk.getName(), "Wrong primary key name!");
            assertTrue(mpk.isValid(), "Valid primary key is invalid!");
            assertEquals(2, mpk.getColumns(), "Wrong number of primary key columns!");
            assertEquals("CCHARACTER", mpk.getColumn(0), "Erroneous primary key column 0!");
            assertEquals("CINTEGER", mpk.getColumn(1), "Erroneous primary key column 1!");
            mpk.setDescription(sDescription);
            assertEquals(sDescription, mpk.getDescription(), "Wrong primary key description!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testForeignKeys() {
        try {
            assertEquals(0, _mtNew.getMetaForeignKeys(), "New table already has foreign keys!");
            createComplexColumns();
            MetaForeignKey mfk = _mtNew.createMetaForeignKey(_sTEST_FOREIGN_KEY_NAME);
            assertSame(_mtNew, mfk.getParentMetaTable(), "Error in foreign key!");
            assertEquals(_sTEST_FOREIGN_KEY_NAME, mfk.getName(), "Wrong foreign key name!");
            assertEquals(0, mfk.getReferences(), "Wrong number of foreign key references!");
            assertFalse(mfk.isValid(), "Invalid foreign key is valid!");

            String sReferencedTable = "TESTTABLEREFERENCED";
            mfk.setReferencedTable(sReferencedTable);
            assertEquals(sReferencedTable, mfk.getReferencedTable(), "Wrong referenced table!");
            assertEquals(_mtNew.getParentMetaSchema()
                                                                   .getName(), mfk.getReferencedSchema(), "Wrong referenced schema default!");

            String sReferencedSchema = "TESTSCHEMAREFERENCED";
            mfk.setReferencedSchema(sReferencedSchema);
            assertEquals(sReferencedSchema, mfk.getReferencedSchema(), "Wrong referenced schema!");

            String sMatchType = "GAGA";
            try {
                mfk.setMatchType(sMatchType);
                fail("Invalid match type accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
            sMatchType = "FULL";
            mfk.setMatchType(sMatchType);
            assertEquals(sMatchType, mfk.getMatchType(), "Wrong match type!");

            String sDeleteAction = "GAGA";
            try {
                mfk.setDeleteAction(sDeleteAction);
                fail("Invalid delete action accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
            sDeleteAction = "RESTRICT";
            mfk.setDeleteAction(sDeleteAction);
            assertEquals(sDeleteAction, mfk.getDeleteAction(), "Wrong delete action!");

            String sUpdateAction = "GAGA";
            try {
                mfk.setUpdateAction(sUpdateAction);
                fail("Invalid update action accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
            sUpdateAction = "CASCADE";
            mfk.setUpdateAction(sUpdateAction);
            assertEquals(sUpdateAction, mfk.getUpdateAction(), "Wrong update action!");

            String sDescription = "Description";
            mfk.setDescription(sDescription);
            assertEquals(sDescription, mfk.getDescription(), "Wrong primary key description!");

            String sReferenced = "TESTCOLUMNREFERENCED";
            mfk.addReference(_sTEST_COLUMN1_NAME, sReferenced);
            assertTrue(mfk.isValid(), "Valid foreign key is invalid!");
            assertEquals(1, mfk.getReferences(), "Wrong number of foreign key references!");
            assertEquals(_sTEST_COLUMN1_NAME, mfk.getColumn(0), "Erroneous foreign key column!");
            assertEquals(sReferenced, mfk.getReferenced(0), "Erroneous foreign key referenced column!");

            assertEquals(0, _mtOld.getMetaForeignKeys(), "Old table has foreign keys!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testUniqueKeys() {
        try {
            assertEquals(0, _mtNew.getMetaCandidateKeys(), "New table already has candidate keys!");
            createComplexColumns();
            MetaUniqueKey muk = _mtNew.createMetaCandidateKey(_sTEST_CANDIDATE_KEY_NAME);
            assertSame(_mtNew, muk.getParentMetaTable(), "Error in unique key!");
            assertEquals(_sTEST_CANDIDATE_KEY_NAME, muk.getName(), "Wrong unique key name!");
            assertEquals(0, muk.getColumns(), "Wrong number of unique key columns!");
            assertFalse(muk.isValid(), "Invalid unique key is valid!");
            String sDescription = "Description";
            muk.setDescription(sDescription);
            assertEquals(sDescription, muk.getDescription(), "Wrong unique key description!");
            muk.addColumn(_sTEST_COLUMN1_NAME);
            assertTrue(muk.isValid(), "Valid unique key is invalid!");
            assertEquals(1, muk.getColumns(), "Wrong number of unique key columns!");
            assertEquals(_sTEST_COLUMN1_NAME, muk.getColumn(0), "Erroneous unique key column!");

            assertEquals(0, _mtOld.getMetaCandidateKeys(), "Old table has candidate keys!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testCheckConstraints() {
        try {
            assertEquals(0, _mtNew.getMetaCheckConstraints(), "New table already has check contraints!");
            MetaCheckConstraint mcc = _mtNew.createMetaCheckConstraint(_sTEST_CHECK_CONSTRAINT_NAME);
            assertSame(_mtNew, mcc.getParentMetaTable(), "Error in check constraint!");
            assertEquals(_sTEST_CHECK_CONSTRAINT_NAME, mcc.getName(), "Wrong check constraint name!");

            String sCondition = "Condition";
            mcc.setCondition(sCondition);
            assertEquals(sCondition, mcc.getCondition(), "Wrong check constraint condition!");

            String sDescription = "Description";
            mcc.setDescription(sDescription);
            assertEquals(sDescription, mcc.getDescription(), "Wrong check constraint description!");

            assertEquals(0, _mtOld.getMetaCheckConstraints(), "Old table has check constraints!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTriggers() {
        try {
            assertEquals(0, _mtNew.getMetaTriggers(), "New table already has triggers!");
            MetaTrigger mt = _mtNew.createMetaTrigger(_sTEST_TRIGGER_NAME);
            assertSame(_mtNew, mt.getParentMetaTable(), "Error in trigger!");
            assertEquals(_sTEST_TRIGGER_NAME, mt.getName(), "Wrong trigger name!");

            String sActionTime = "GAGA";
            try {
                mt.setActionTime(sActionTime);
                fail("Invalid action time accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
            sActionTime = "BEFORE";
            mt.setActionTime(sActionTime);
            assertEquals(sActionTime, mt.getActionTime(), "Wrong action time!");

            String sTriggerEvent = "UPDATE OF " + _sTEST_COLUMN1_NAME;
            mt.setTriggerEvent(sTriggerEvent);
            assertEquals(sTriggerEvent, mt.getTriggerEvent(), "Wrong trigger event!");

            String sAliasList = "AliasList";
            mt.setAliasList(sAliasList);
            assertEquals(sAliasList, mt.getAliasList(), "Wrong alias list!");

            String sTriggeredAction = "TriggeredAction";
            mt.setTriggeredAction(sTriggeredAction);
            assertEquals(sTriggeredAction, mt.getTriggeredAction(), "Wrong triggered action!");

            String sDescription = "Description";
            mt.setDescription(sDescription);
            assertEquals(sDescription, mt.getDescription(), "Wrong check constraint description!");

            assertEquals(0, _mtOld.getMetaTriggers(), "Old table has triggers!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    private void checkColumnNames(boolean bSupportsArrays, boolean bSupportsUdts)
            throws IOException {
        System.out.println("Table: " + _mtNew.getName());
        System.out.println("  supports arrays: " + bSupportsArrays);
        System.out.println("  supports udts: " + bSupportsUdts);
        List<List<String>> llColumnNames = _mtNew.getColumnNames(bSupportsArrays, bSupportsUdts);
        for (int iColumn = 0; iColumn < llColumnNames.size(); iColumn++) {
            List<String> listFieldNames = llColumnNames.get(iColumn);
            StringBuilder sbColumnName = new StringBuilder();
            for (int iField = 0; iField < listFieldNames.size(); iField++) {
                if (iField > 0)
                    sbColumnName.append(".");
                sbColumnName.append(listFieldNames.get(iField));
            }
            System.out.println(sbColumnName);
        }
    }

    @Test
    public void testGetColumnNames() {
        try {
            assertEquals(0, _mtNew.getMetaColumns(), "New table column meta data!");
            createComplexColumns();
            checkColumnNames(true, true);
            System.out.println();
            checkColumnNames(true, false);
            System.out.println();
            checkColumnNames(false, true);
            System.out.println();
            checkColumnNames(false, false);
            System.out.println();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    private void checkGetType(boolean bSupportsArrays, boolean bSupportsUdts)
            throws IOException {
        System.out.println("Table: " + _mtNew.getName());
        System.out.println("  supports arrays: " + bSupportsArrays);
        System.out.println("  supports udts: " + bSupportsUdts);
        List<List<String>> llColumnNames = _mtNew.getColumnNames(bSupportsArrays, bSupportsUdts);
        for (int iColumn = 0; iColumn < llColumnNames.size(); iColumn++) {
            List<String> listFieldNames = llColumnNames.get(iColumn);
            StringBuilder sbColumnName = new StringBuilder();
            for (int iField = 0; iField < listFieldNames.size(); iField++) {
                if (iField > 0)
                    sbColumnName.append(".");
                sbColumnName.append(listFieldNames.get(iField));
            }
            String sType = _mtNew.getType(listFieldNames);
            System.out.println(sbColumnName + ": " + sType);
        }
    }

    @Test
    public void testGetType() {
        try {
            assertEquals(0, _mtNew.getMetaColumns(), "New table column meta data!");
            createComplexColumns();
            checkGetType(true, true);
            System.out.println();
            checkGetType(true, false);
            System.out.println();
            checkGetType(false, true);
            System.out.println();
            checkGetType(false, false);
            System.out.println();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
