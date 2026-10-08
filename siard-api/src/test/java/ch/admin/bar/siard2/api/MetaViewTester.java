package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class MetaViewTester {
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
    private static final String _sTEST_VIEW_NAME = "TESTVIEW";
    private static final String _sTEST_COLUMN_NAME = "TESTCOLUMN";
    MetaView _mvNew = null;

    private void setMandatoryMetaData(MetaSchema ms) {
        try {
            MetaData md = ms.getParentMetaData();
            if (!SU.isNotEmpty(md.getDbName()))
                md.setDbName(_sDBNAME);
            if (!SU.isNotEmpty(md.getDataOwner()))
                md.setDataOwner(_sDATA_OWNER);
            if (!SU.isNotEmpty(md.getDataOriginTimespan()))
                md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
            if (md.getMetaSchemas() == 0)
                md.getArchive()
                  .createSchema("TEST_SCHEMA");
            if (_mvNew.getMetaColumn(_sTEST_COLUMN_NAME) == null) {
                MetaColumn mc = _mvNew.createMetaColumn(_sTEST_COLUMN_NAME);
                mc.setType("INTEGER");
            }
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @BeforeEach
    public void setUp() {
        try {
            Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
            MetaSchema ms = schema.getMetaSchema();
            _mvNew = ms.createMetaView(_sTEST_VIEW_NAME);
            assertSame(ms, _mvNew.getParentMetaSchema(), "Invalid MetaSchema!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            setMandatoryMetaData(_mvNew.getParentMetaSchema());
            _mvNew.getParentMetaSchema()
                  .getSchema()
                  .getParentArchive()
                  .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testName() {
        assertEquals(_sTEST_VIEW_NAME, _mvNew.getName(), "Invalid view name!");
    }

    @Test
    public void testQuery() {
        String sQuery = "Query";
        _mvNew.setQuery(sQuery);
        assertEquals(sQuery, _mvNew.getQuery(), "Invalid query!");
    }

    @Test
    public void testQueryOriginal() {
        try {
            String sQueryOriginal = "QueryOriginal";
            _mvNew.setQueryOriginal(sQueryOriginal);
            assertEquals(sQueryOriginal, _mvNew.getQueryOriginal(), "Invalid original query!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testDescription() {
        String sDescription = "Description";
        _mvNew.setDescription(sDescription);
        assertEquals(sDescription, _mvNew.getDescription(), "Invalid description!");
    }

    @Test
    public void testRows() {
        try {
            int iRows = 537587;
            System.out.println(_mvNew.getRows());
            assertEquals(0, _mvNew.getRows(), "Rows must initially be 0!");
            _mvNew.setRows(iRows);
            assertEquals(iRows, _mvNew.getRows(), "Invalid number of rows!");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testGetMetaColumns() {
        assertEquals(0, _mvNew.getMetaColumns(), "Columns must initially be 0!");
    }

    @Test
    public void testCreateMetaColumn() {
        try {
            _mvNew.createMetaColumn(_sTEST_COLUMN_NAME);
            assertEquals(1, _mvNew.getMetaColumns(), "Invalid number of columns!");
            MetaColumn mc = _mvNew.getMetaColumn(0);
            assertEquals(_sTEST_COLUMN_NAME, mc.getName(), "Invalid name!");
            mc = _mvNew.getMetaColumn(_sTEST_COLUMN_NAME);
            assertEquals(_sTEST_COLUMN_NAME, mc.getName(), "Invalid name!");
            mc.setType("VARCHAR(256)");
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
