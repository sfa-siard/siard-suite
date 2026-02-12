package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.generated.SiardArchive;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class MetaDataXmlTest {
    private static final String TESTFILES_METADATA_DIR = "src/test/resources/testfiles/metadata/";
    private static final String METADATA_1_0_XML = "1.0/metadata-1.0.xml";
    private static final String METADATA_2_1_XML = "2.1/metadata-2.1.xml";
    private static final String METADATA_2_2_XML = "2.2/metadata-2.2.xml";

    @Test
    public void shoudReadSiard10XmlMetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = getFileInputStream(METADATA_1_0_XML);

        // when
        SiardArchive sa = MetaDataXml.readAndConvertSiard10Xml(fis);

        // then
        assertEquals(sa.getDbname(), "SIARD 1.0 MetaData");
        assertIsSiard22Archive(sa);
    }


    @Test
    public void shouldReadSiard22XmlMetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = getFileInputStream(METADATA_2_2_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard22Xml(fis);

        // then
        assertEquals(sa.getDbname(), "SIARD 2.2 MetaData");
        assertIsSiard22Archive(sa);
    }


    @Test
    public void shouldReadSiard21XmlMetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = getFileInputStream(METADATA_2_1_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard21Xml(fis);

        // then
        assertEquals(sa.getDbname(), "SIARD 2.1 MetaData");
        assertIsSiard22Archive(sa);
    }

    @Test
    public void shouldNotReadSiard22XmlMetaData_ForSiard10_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = getFileInputStream(METADATA_1_0_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard22Xml(fis);

        // then
        assertNull("should not have loaded a siard archive instance for metadata v 1.0", sa);
    }

    @Test
    public void shouldNotReadSiard21XmlMetaData_ForSiard10_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = getFileInputStream(METADATA_1_0_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard21Xml(fis);

        // then
        assertNull("should not have loaded a siard archive instance for metadata v 1.0", sa);
    }

    @Test
    public void shouldNotReadSiard21XmlMetaData_ForSiard22_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = getFileInputStream(METADATA_2_2_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard21Xml(fis);

        // then
        assertNull("should not have loaded a siard archive instance for metadata v 2.2", sa);
    }


    @Test
    public void shouldNotReadSiard22XmlMetatData_ForSiard21_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = getFileInputStream(METADATA_2_1_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard22Xml(fis);

        // then
        assertNull("should not have loaded a siard archive instance for metadata v 2.1", sa);
    }

    @Test
    public void shouldNotReadSiard10XmlMetaData_ForSiard21_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = getFileInputStream(METADATA_2_1_XML);

        // when
        SiardArchive sa = MetaDataXml.readAndConvertSiard10Xml(fis);


        // then
        assertNull("should not have loaded a siard archive instance for metadata v 2.1", sa);
    }

    @Test
    public void shouldNotReadSiard10XmlMetaData_ForSiard22_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = getFileInputStream(METADATA_2_2_XML);

        // when
        SiardArchive sa = MetaDataXml.readAndConvertSiard10Xml(fis);


        // then
        assertNull("should not have loaded a siard archive instance for metadata v 2.2", sa);
    }

    private void assertIsSiard22Archive(SiardArchive sa) {
        // assertEquals(sa.getClass().getName(), "ch.admin.bar.siard2.api.generated.SiardArchive");
        assertTrue(sa instanceof SiardArchive);
    }

    private FileInputStream getFileInputStream(String metadata10Xml) throws FileNotFoundException {
        return new FileInputStream(TESTFILES_METADATA_DIR + metadata10Xml);
    }

}
