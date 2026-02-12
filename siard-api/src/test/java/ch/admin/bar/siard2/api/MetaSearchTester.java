package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.EU;
import ch.enterag.utils.SU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.fail;

public class MetaSearchTester {
    private static final File _fileSIARD_10_SOURCE = new File("src/test/resources/testfiles/sql1999.siard");
    private static final File _fileSIARD_10 = new File("src/test/resources/tmp/sql1999.siard");
    private static final File _fileSIARD_21_NEW = new File("src/test/resources/tmp/sql2008new.siard");
    private static final File _fileSAMPLE = new File("src/test/resources/testfiles/sample.siard");
    private static final ConfigurationProperties _cp = new ConfigurationProperties();
    private static final File _fileLOBS_FOLDER = new File(_cp.getLobsFolder());
    private static final String _sDBNAME = "SIARD 2.1 Test Database";
    private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
    private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
    MetaData _mdNew = null;
    MetaData _mdOld = null;
    MetaData _mdSample = null;

    private void setMandatoryMetaData(MetaData md) {
        try {
            if (!SU.isNotEmpty(md.getDbName()))
                md.setDbName(_sDBNAME);
            if (!SU.isNotEmpty(md.getDataOwner()))
                md.setDataOwner(_sDATA_OWNER);
            if (!SU.isNotEmpty(md.getDataOriginTimespan()))
                md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
            if (md.getMetaSchemas() == 0)
                md.getArchive()
                  .createSchema("TEST_SCHEMA");
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
                throw new IOException("deleteFolder onlye deletes directories!");
        }
    }

    @Before
    public void setUp() {
        try {
            Files.copy(_fileSIARD_10_SOURCE.toPath(), _fileSIARD_10.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(_fileSIARD_21_NEW.toPath());
            deleteFolder(_fileLOBS_FOLDER);
            Archive archive = ArchiveImpl.newInstance();
            archive.create(_fileSIARD_21_NEW);
            _mdNew = archive.getMetaData();
            archive = ArchiveImpl.newInstance();
            archive.open(_fileSIARD_10);
            _mdOld = archive.getMetaData();
            setMandatoryMetaData(_mdNew);
            archive = ArchiveImpl.newInstance();
            archive.open(_fileSAMPLE);
            _mdSample = archive.getMetaData();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @After
    public void tearDown() {
        try {
            _mdNew.getArchive()
                  .close();
            setMandatoryMetaData(_mdOld);
            _mdOld.getArchive()
                  .close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testFindOld() {
        DU du = DU.getInstance("de", "dd.MM.yyyy");
        try {
            _mdOld.find("int", false);
            for (MetaSearch ms = _mdOld.findNext(du); ms != null; ms = _mdOld.findNext(du))
                System.out.println(ms.getFoundString(du) + " with offset " + ms.getFoundOffset() + " of " + ms.getClass()
                                                                                                              .getName());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testFindNew() {
        DU du = DU.getInstance("de", "dd.MM.yyyy");
        try {
            _mdNew.find("RÜTI", false);
            for (MetaSearch ms = _mdNew.findNext(du); ms != null; ms = _mdNew.findNext(du))
                System.out.println("\"" + ms.getFoundString(du) + "\" with offset " + ms.getFoundOffset() + " of " + ms.getClass()
                                                                                                                       .getName());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Test
    public void testFindSample() {
        DU du = DU.getInstance("de", "dd.MM.yyyy");
        try {
            _mdSample.find("data", false);
            for (MetaSearch ms = _mdSample.findNext(du); ms != null; ms = _mdSample.findNext(du))
                System.out.println("\"" + ms.getFoundString(du) + "\" with offset " + ms.getFoundOffset() + " of " + ms.getClass()
                                                                                                                       .getName());
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

}
