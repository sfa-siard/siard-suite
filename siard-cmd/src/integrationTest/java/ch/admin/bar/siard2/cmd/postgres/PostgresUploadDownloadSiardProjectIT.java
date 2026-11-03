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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
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
