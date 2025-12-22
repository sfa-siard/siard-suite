package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class MetaTypeTester {
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_TYPE_NAME = "TESTTYPE";
    private static final String _sTEST_UNDER_SCHEMA = "TESTUNDERSCHEMA";
    private static final String _sTEST_UNDER_TYPE = "TESTUNDERTYPE";
    private static final String _sTEST_ATTRIBUTE_NAME = "TESTATTRIBUTE";
    MetaType _mtNew = null;

    private void setMandatoryMetaData(MetaSchema ms) {
        MetaData md = ms.getParentMetaData();
        if (!SU.isNotEmpty(md.getDbName()))
            md.setDbName(_sDBNAME);
        if (!SU.isNotEmpty(md.getDataOwner()))
            md.setDataOwner(_sDATA_OWNER);
        if (!SU.isNotEmpty(md.getDataOriginTimespan()))
            md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
    }

    @Before
    public void setUp() {
        try {
            Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            MetaSchema ms = schema.getMetaSchema();
            _mtNew = ms.createMetaType(_sTEST_TYPE_NAME);
            assertSame("Invalid MetaSchema!", ms, _mtNew.getParentMetaSchema());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @After
    public void tearDown() {
        try {
            setMandatoryMetaData(_mtNew.getParentMetaSchema());
            _mtNew.getParentMetaSchema()
                  .getSchema()
                  .getParentArchive()
                  .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testName() {
        assertEquals("Invalid routine name!", _sTEST_TYPE_NAME, _mtNew.getName());
    }

    @Test
    public void testCategory() {
        String sCategory = "distinct";
        assertEquals("Invalid default category!", sCategory, _mtNew.getCategory());
        try {
            try {
                _mtNew.setCategory("gaga");
                fail("Invalid category accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
            assertEquals("Wrong row category!", sCategory, _mtNew.getCategory());
            sCategory = "udt";
            _mtNew.setCategory(sCategory);
            assertEquals("Wrong udt category!", sCategory, _mtNew.getCategory());
            sCategory = "distinct";
            _mtNew.setCategory(sCategory);
            assertEquals("Wrong distinct category!", sCategory, _mtNew.getCategory());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testUnderSchema() {
        try {
            _mtNew.setUnderSchema(_sTEST_UNDER_SCHEMA);
            assertEquals("Wrong under schema!", _sTEST_UNDER_SCHEMA, _mtNew.getUnderSchema());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testUnderType() {
        try {
            _mtNew.setUnderType(_sTEST_UNDER_TYPE);
            assertEquals("Wrong under type!", _sTEST_UNDER_TYPE, _mtNew.getUnderType());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testInstantiable() {
        assertTrue("Invalid default instantiability", _mtNew.isInstantiable());
        try {
            _mtNew.setInstantiable(false);
            assertFalse("Wrong instantiability!", _mtNew.isInstantiable());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testFinal() {
        assertTrue("Invalid default finality", _mtNew.isFinal());
        try {
            _mtNew.setFinal(false);
            assertFalse("Wrong finality!", _mtNew.isFinal());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testBase() {
        try {
            _mtNew.setCategory("distinct");
            try {
                _mtNew.setBase("GAGA");
                fail("Invalid predefined type accepted!");
            } catch (IllegalArgumentException iae) {
                System.out.println(EU.getExceptionMessage(iae));
            }
            try {
                _mtNew.setCategory("udt");
                _mtNew.setBase("INTEGER");
                fail("Base cannot be set for \"udt\" type!");
            } catch (IOException ie) {
                System.out.println(EU.getExceptionMessage(ie));
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _mtNew.setDescription(sDescription);
        assertEquals("Invalid description!", sDescription, _mtNew.getDescription());
    }

    @Test
    public void testGetMetaAttributes() {
        assertEquals("Attributes must initially be 0!", 0, _mtNew.getMetaAttributes());
    }

    @Test
    public void testCreateMetaAttribute() {
        try {
            _mtNew.createMetaAttribute(_sTEST_ATTRIBUTE_NAME);
            fail("Distinct types do not have attributes!");
        } catch (IOException ie) {
            System.out.println(EU.getExceptionMessage(ie));
        }
        try {
            _mtNew.setCategory("udt");
            _mtNew.createMetaAttribute(_sTEST_ATTRIBUTE_NAME);
            assertEquals("Invalid number of attributes!", 1, _mtNew.getMetaAttributes());
            MetaAttribute ma = _mtNew.getMetaAttribute(0);
            assertEquals("Invalid name!", _sTEST_ATTRIBUTE_NAME, ma.getName());
            ma = _mtNew.getMetaAttribute(_sTEST_ATTRIBUTE_NAME);
            assertEquals("Invalid name!", _sTEST_ATTRIBUTE_NAME, ma.getName());
            ma.setType("VARCHAR(256)");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }


}
