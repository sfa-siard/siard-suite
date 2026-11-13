package ch.admin.bar.siard2.cmd.postgres;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

@Testcontainers
public class PostgresUploadDownloadSiardProjectIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"));

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_POSTGRES13_2_2);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + expectedArchive.getPathToArchiveFile());
        var siardFromDb = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + actualArchive.getPathToArchiveFile());

        // then
        Assertions.assertEquals(0, siardToDb.exitCode());
        Assertions.assertEquals(0, siardFromDb.exitCode());

        SiardArchiveAssertions.builder()
                              .actualArchive(actualArchive)
                              .expectedArchive(expectedArchive.preserveArchive())
                              .assertionModifier(SiardArchiveAssertions.IGNORE_DBNAME) // FIXME ?
                              .assertEqual();
    }

    @Test
    public void restoreSampleDataLinkCoversSiardTypeMatrix() throws IOException, SQLException {
        val archive = siardArchivesHandler.prepareResource(SiardProjectExamples.SAMPLE_DATALINK_2_2_SIARD);

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + archive.getPathToArchiveFile());

        Assertions.assertEquals(0, siardToDb.exitCode(), siardToDb.err());

        Properties props = new Properties();
        props.setProperty("user", db.getUsername());
        props.setProperty("password", db.getPassword());
        try (Connection conn = new org.postgresql.Driver().connect(db.getJdbcUrl(), props);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT (SELECT COUNT(*) FROM \"SampleSchema\".tsimple),"
                             + " (SELECT COUNT(*) FROM \"SampleSchema\".tcomplex)")) {
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals(4, rs.getLong(1));
            Assertions.assertEquals(2, rs.getLong(2));
        }
    }

    private static File corruptSecondComplexRow(File source, Path target) throws IOException {
        try (ZipFile zip = new ZipFile(source);
             ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(target))) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                out.putNextEntry(new ZipEntry(entry.getName()));
                if (!entry.isDirectory()) {
                    byte[] data = zip.getInputStream(entry).readAllBytes();
                    if (entry.getName().equals("content/schema0/table1/table1.xml")) {
                        // valid xs:integer but outside long range: XML validation passes,
                        // the first row is flushed by the byte budget, then binding fails
                        String xml = new String(data, StandardCharsets.UTF_8)
                                .replace("<c1>1987654321</c1>", "<c1>999999999999999999999</c1>");
                        data = xml.getBytes(StandardCharsets.UTF_8);
                    }
                    out.write(data);
                }
                out.closeEntry();
            }
        }
        return target.toFile();
    }

    @Test
    public void failedRestoreRollsBackCurrentTable(@TempDir Path tempDir) throws IOException {
        val original = siardArchivesHandler.prepareResource(SiardProjectExamples.SAMPLE_DATALINK_2_2_SIARD);
        File corrupted = corruptSecondComplexRow(original.getPathToArchiveFile(),
                                                 tempDir.resolve("corrupt.siard"));

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + corrupted.getAbsolutePath());

        Assertions.assertEquals(1, siardToDb.exitCode());

        Properties props = new Properties();
        props.setProperty("user", db.getUsername());
        props.setProperty("password", db.getPassword());
        try (Connection conn = new org.postgresql.Driver().connect(db.getJdbcUrl(), props);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT (SELECT COUNT(*) FROM \"SampleSchema\".tsimple),"
                             + " (SELECT COUNT(*) FROM \"SampleSchema\".tcomplex)")) {
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals(4, rs.getLong(1));
            Assertions.assertEquals(0, rs.getLong(2));
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Test
    public void exportXmlOnly_writesMetadataXmlAndExits0(@TempDir Path tempDir) throws IOException, SQLException {
        // given a populated database
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_POSTGRES13_2_2);
        var restore = SiardCliTest.execute("restore",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + expectedArchive.getPathToArchiveFile());
        Assertions.assertEquals(0, restore.exitCode());
        Path metaXml = tempDir.resolve("metadata.xml");

        // when
        var result = SiardCliTest.execute("archive",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--export-xml=" + metaXml);

        // then
        Assertions.assertEquals(0, result.exitCode());
        Assertions.assertTrue(Files.isRegularFile(metaXml), "metadata XML was not exported");
    }

    @Test
    public void restoreExistingObjectsWithoutOverwrite_failsWithConflictHint() throws IOException, SQLException {
        // given a target database already containing the archive's objects
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_POSTGRES13_2_2);
        var first = SiardCliTest.execute("restore",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + expectedArchive.getPathToArchiveFile());
        Assertions.assertEquals(0, first.exitCode());

        // when restoring the same archive again without --overwrite
        var second = SiardCliTest.execute("restore",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + expectedArchive.getPathToArchiveFile());

        // then
        Assertions.assertEquals(1, second.exitCode());
        Assertions.assertTrue(second.err().contains("overwrite") || second.err().contains("--overwrite"),
                "expected an overwrite hint, got: " + second.err());
    }
}
