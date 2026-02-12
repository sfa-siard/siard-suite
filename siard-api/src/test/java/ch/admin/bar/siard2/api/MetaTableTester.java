package ch.admin.bar.siard2.api;


import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.api.primary.TableImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.Assert.*;

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
        assertEquals("Wrong parent meta table of simple column!", _mtNew, mc1.getParentMetaTable());
        mc1.setType("INTEGER");
        mc1.setNullable(false);

        MetaColumn mc2 = _mtNew.createMetaColumn(_sTEST_DISTINCT_COLUMN);
        assertEquals("Wrong parent meta table of distinct column!", _mtNew, mc2.getParentMetaTable());
        mc2.setTypeName(_sTEST_DISTINCT_TYPE);

        MetaColumn mc3 = _mtNew.createMetaColumn(_sTEST_UDTS_COLUMN);
        assertEquals("Wrong parent meta table of row column!", _mtNew, mc3.getParentMetaTable());
        mc3.setTypeName(_sTEST_UDTS_TYPE);

        MetaColumn mc4 = _mtNew.createMetaColumn(_sTEST_ARRAY_COLUMN);
        assertEquals("Wrong parent meta table of array column!", _mtNew, mc4.getParentMetaTable());
        mc4.setType("VARCHAR(256)");
        mc4.setCardinality(4);
        mc4.getMetaField(2); // 3 out of 4 array elements

        MetaColumn mc5 = _mtNew.createMetaColumn(_sTEST_UDTC_COLUMN);
        assertEquals("Wrong parent meta table of udt column!", _mtNew, mc5.getParentMetaTable());
        mc5.setTypeName(_sTEST_UDTC_TYPE);
    }

    @Before
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
            assertSame("MetaTable create failed!", table, _mtNew.getTable());
            archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_10);
            schema = archive.getSchema(0);
            table = schema.getTable(0);
            _mtOld = table.getMetaTable();
            assertSame("MetaTable open failed!", table, _mtOld.getTable());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @After
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
        assertEquals("Invalid name of new table!", _sTEST_TABLE_NAME, _mtNew.getName());
        assertEquals("Invalid name of old table!", "TABLETEST2", _mtOld.getName());
    }

    @Test
    public void testFolder() {
        assertEquals("Invalid folder name of new table!", TableImpl._sTABLE_FOLDER_PREFIX + "0", _mtNew.getFolder());
        assertEquals("Invalid folder name of old table!", TableImpl._sTABLE_FOLDER_PREFIX + "0", _mtOld.getFolder());
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _mtNew.setDescription(sDescription);
        assertEquals("Invalid Description!", sDescription, _mtNew.getDescription());
        _mtOld.setDescription(sDescription);
        assertEquals("Invalid Description!", sDescription, _mtOld.getDescription());
    }

    @Test
    public void testRows() {
        assertEquals("New table has zero rows!", 0, _mtNew.getRows());
        assertEquals("Old table has one row!", 1, _mtOld.getRows());
    }

    @Test
    public void testGetMetaColumns() {
        try {
            assertEquals("New table column meta data!", 0, _mtNew.getMetaColumns());
            createComplexColumns();
            assertEquals("New table has wrong number of column meta data!", 5, _mtNew.getMetaColumns());
            System.out.println(_mtOld.getMetaColumns());
            assertEquals("Old table has wrong number of column meta data!", 30, _mtOld.getMetaColumns());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaColumn_Int() {
        try {
            createComplexColumns(); // has already tested createMetaColumn() ...
            assertNull("Column of type row must not have PreType!", _mtNew.getMetaColumn(2)
                                                                          .getType());
            assertEquals("Wrong column name of new column of type row retrieved!", _sTEST_UDTS_COLUMN, _mtNew.getMetaColumn(2)
                                                                                                             .getName());
            assertEquals("Wrong column type of new column of type row retrieved!", _sTEST_UDTS_TYPE, _mtNew.getMetaColumn(2)
                                                                                                           .getTypeName());
            System.out.println(_mtOld.getMetaColumn(22)
                                     .getName());
            assertEquals("Wrong column name of old table retrieved!", "CREAL", _mtOld.getMetaColumn(22)
                                                                                     .getName());
            System.out.println(_mtOld.getMetaColumn(22)
                                     .getType());
            assertEquals("Wrong column type of old table retrievd!", "REAL", _mtOld.getMetaColumn(22)
                                                                                   .getType());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaColumn_String() {
        try {
            createComplexColumns(); // has already tested createMetaColumn() ...
            assertEquals("Wrong column name of new column of type row retrieved!", _sTEST_UDTS_COLUMN, _mtNew.getMetaColumn(_sTEST_UDTS_COLUMN)
                                                                                                             .getName());
            assertEquals("Wrong column type of new column of type row retrieved!", _sTEST_UDTS_TYPE, _mtNew.getMetaColumn(_sTEST_UDTS_COLUMN)
                                                                                                           .getTypeName());
            assertEquals("Wrong column name of old table retrieved!", "CREAL", _mtOld.getMetaColumn("CREAL")
                                                                                     .getName());
            assertEquals("Wrong column type of old table retrievd!", "REAL", _mtOld.getMetaColumn("CREAL")
                                                                                   .getType());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testPrimaryKey() {
        try {
            assertNull("New table has primary key!", _mtNew.getMetaPrimaryKey());
            createComplexColumns();
            MetaUniqueKey mpk = _mtNew.createMetaPrimaryKey(_sTEST_PRIMARY_KEY_NAME);
            assertSame("Error in primary key!", _mtNew, mpk.getParentMetaTable());
            assertEquals("Wrong primary key name!", _sTEST_PRIMARY_KEY_NAME, mpk.getName());
            assertEquals("Wrong number of primary key columns!", 0, mpk.getColumns());
            assertFalse("Invalid primary key is valid!", mpk.isValid());
            String sDescription = "Description";
            mpk.setDescription(sDescription);
            assertEquals("Wrong primary key description!", sDescription, mpk.getDescription());
            mpk.addColumn(_sTEST_COLUMN1_NAME);
            assertTrue("Valid primary key is invalid!", mpk.isValid());
            assertEquals("Wrong number of primary key columns!", 1, mpk.getColumns());
            assertEquals("Erroneous primary key column!", _sTEST_COLUMN1_NAME, mpk.getColumn(0));

            assertNotNull("Old table has no primary key!", _mtOld.getMetaPrimaryKey());
            mpk = _mtOld.getMetaPrimaryKey();
            assertSame("Error in primary key!", _mtOld, mpk.getParentMetaTable());
            assertEquals("Wrong primary key name!", "TABLETEST2PK", mpk.getName());
            assertTrue("Valid primary key is invalid!", mpk.isValid());
            assertEquals("Wrong number of primary key columns!", 2, mpk.getColumns());
            assertEquals("Erroneous primary key column 0!", "CCHARACTER", mpk.getColumn(0));
            assertEquals("Erroneous primary key column 1!", "CINTEGER", mpk.getColumn(1));
            mpk.setDescription(sDescription);
            assertEquals("Wrong primary key description!", sDescription, mpk.getDescription());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testForeignKeys() {
        try {
            assertEquals("New table already has foreign keys!", 0, _mtNew.getMetaForeignKeys());
            createComplexColumns();
            MetaForeignKey mfk = _mtNew.createMetaForeignKey(_sTEST_FOREIGN_KEY_NAME);
            assertSame("Error in foreign key!", _mtNew, mfk.getParentMetaTable());
            assertEquals("Wrong foreign key name!", _sTEST_FOREIGN_KEY_NAME, mfk.getName());
            assertEquals("Wrong number of foreign key references!", 0, mfk.getReferences());
            assertFalse("Invalid foreign key is valid!", mfk.isValid());

            String sReferencedTable = "TESTTABLEREFERENCED";
            mfk.setReferencedTable(sReferencedTable);
            assertEquals("Wrong referenced table!", sReferencedTable, mfk.getReferencedTable());
            assertEquals("Wrong referenced schema default!", _mtNew.getParentMetaSchema()
                                                                   .getName(), mfk.getReferencedSchema());

            String sReferencedSchema = "TESTSCHEMAREFERENCED";
            mfk.setReferencedSchema(sReferencedSchema);
            assertEquals("Wrong referenced schema!", sReferencedSchema, mfk.getReferencedSchema());

            String sMatchType = "GAGA";
            try {
                mfk.setMatchType(sMatchType);
                fail("Invalid match type accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
            sMatchType = "FULL";
            mfk.setMatchType(sMatchType);
            assertEquals("Wrong match type!", sMatchType, mfk.getMatchType());

            String sDeleteAction = "GAGA";
            try {
                mfk.setDeleteAction(sDeleteAction);
                fail("Invalid delete action accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
            sDeleteAction = "RESTRICT";
            mfk.setDeleteAction(sDeleteAction);
            assertEquals("Wrong delete action!", sDeleteAction, mfk.getDeleteAction());

            String sUpdateAction = "GAGA";
            try {
                mfk.setUpdateAction(sUpdateAction);
                fail("Invalid update action accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
            sUpdateAction = "CASCADE";
            mfk.setUpdateAction(sUpdateAction);
            assertEquals("Wrong update action!", sUpdateAction, mfk.getUpdateAction());

            String sDescription = "Description";
            mfk.setDescription(sDescription);
            assertEquals("Wrong primary key description!", sDescription, mfk.getDescription());

            String sReferenced = "TESTCOLUMNREFERENCED";
            mfk.addReference(_sTEST_COLUMN1_NAME, sReferenced);
            assertTrue("Valid foreign key is invalid!", mfk.isValid());
            assertEquals("Wrong number of foreign key references!", 1, mfk.getReferences());
            assertEquals("Erroneous foreign key column!", _sTEST_COLUMN1_NAME, mfk.getColumn(0));
            assertEquals("Erroneous foreign key referenced column!", sReferenced, mfk.getReferenced(0));

            assertEquals("Old table has foreign keys!", 0, _mtOld.getMetaForeignKeys());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testUniqueKeys() {
        try {
            assertEquals("New table already has candidate keys!", 0, _mtNew.getMetaCandidateKeys());
            createComplexColumns();
            MetaUniqueKey muk = _mtNew.createMetaCandidateKey(_sTEST_CANDIDATE_KEY_NAME);
            assertSame("Error in unique key!", _mtNew, muk.getParentMetaTable());
            assertEquals("Wrong unique key name!", _sTEST_CANDIDATE_KEY_NAME, muk.getName());
            assertEquals("Wrong number of unique key columns!", 0, muk.getColumns());
            assertFalse("Invalid unique key is valid!", muk.isValid());
            String sDescription = "Description";
            muk.setDescription(sDescription);
            assertEquals("Wrong unique key description!", sDescription, muk.getDescription());
            muk.addColumn(_sTEST_COLUMN1_NAME);
            assertTrue("Valid unique key is invalid!", muk.isValid());
            assertEquals("Wrong number of unique key columns!", 1, muk.getColumns());
            assertEquals("Erroneous unique key column!", _sTEST_COLUMN1_NAME, muk.getColumn(0));

            assertEquals("Old table has candidate keys!", 0, _mtOld.getMetaCandidateKeys());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testCheckConstraints() {
        try {
            assertEquals("New table already has check contraints!", 0, _mtNew.getMetaCheckConstraints());
            MetaCheckConstraint mcc = _mtNew.createMetaCheckConstraint(_sTEST_CHECK_CONSTRAINT_NAME);
            assertSame("Error in check constraint!", _mtNew, mcc.getParentMetaTable());
            assertEquals("Wrong check constraint name!", _sTEST_CHECK_CONSTRAINT_NAME, mcc.getName());

            String sCondition = "Condition";
            mcc.setCondition(sCondition);
            assertEquals("Wrong check constraint condition!", sCondition, mcc.getCondition());

            String sDescription = "Description";
            mcc.setDescription(sDescription);
            assertEquals("Wrong check constraint description!", sDescription, mcc.getDescription());

            assertEquals("Old table has check constraints!", 0, _mtOld.getMetaCheckConstraints());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testTriggers() {
        try {
            assertEquals("New table already has triggers!", 0, _mtNew.getMetaTriggers());
            MetaTrigger mt = _mtNew.createMetaTrigger(_sTEST_TRIGGER_NAME);
            assertSame("Error in trigger!", _mtNew, mt.getParentMetaTable());
            assertEquals("Wrong trigger name!", _sTEST_TRIGGER_NAME, mt.getName());

            String sActionTime = "GAGA";
            try {
                mt.setActionTime(sActionTime);
                fail("Invalid action time accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
            sActionTime = "BEFORE";
            mt.setActionTime(sActionTime);
            assertEquals("Wrong action time!", sActionTime, mt.getActionTime());

            String sTriggerEvent = "UPDATE OF " + _sTEST_COLUMN1_NAME;
            mt.setTriggerEvent(sTriggerEvent);
            assertEquals("Wrong trigger event!", sTriggerEvent, mt.getTriggerEvent());

            String sAliasList = "AliasList";
            mt.setAliasList(sAliasList);
            assertEquals("Wrong alias list!", sAliasList, mt.getAliasList());

            String sTriggeredAction = "TriggeredAction";
            mt.setTriggeredAction(sTriggeredAction);
            assertEquals("Wrong triggered action!", sTriggeredAction, mt.getTriggeredAction());

            String sDescription = "Description";
            mt.setDescription(sDescription);
            assertEquals("Wrong check constraint description!", sDescription, mt.getDescription());

            assertEquals("Old table has triggers!", 0, _mtOld.getMetaTriggers());
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
            assertEquals("New table column meta data!", 0, _mtNew.getMetaColumns());
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
            assertEquals("New table column meta data!", 0, _mtNew.getMetaColumns());
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
