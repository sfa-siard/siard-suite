package ch.admin.bar.siardsuite.localwebapp;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves that siard-api (JAXB RI internals, Xerces SAX factory, --add-opens) works inside
 * the Quarkus application (design D10). No endpoint is involved on purpose.
 */
@QuarkusTest
class SiardApiIntegrationTest {

    private static final Path SAMPLE_ARCHIVE =
            Path.of("../../siard-api/src/test/resources/testfiles/sample.siard").toAbsolutePath().normalize();

    @Test
    void opensSiardArchiveWithItsMetaData() throws Exception {
        Archive archive = ArchiveImpl.newInstance();
        archive.open(SAMPLE_ARCHIVE.toFile());
        try {
            assertTrue(archive.getMetaData().getMetaSchemas() > 0, "archive has at least one schema");
        } finally {
            archive.close();
        }
    }
}
