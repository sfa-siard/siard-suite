package ch.admin.bar.siard2.cmd.db2;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

@Testcontainers
public class Db2UploadDownloadSiardProjectIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public Db2Container db = new Db2Container(DockerImageName.parse("ibmcom/db2:11.5.8.0"))
            .acceptLicense();

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_DB2);
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
                              .expectedArchive(expectedArchive)
                              .actualArchive(actualArchive)
                              .assertionModifier(SiardArchiveAssertions.IGNORE_DBNAME) // FIXME ?
                              .assertionModifier(SiardArchiveAssertions.IGNORE_PRIMARY_KEY_NAME) // Probably a DB-restriction (primary key names are generated)
                              .assertionModifier(SiardArchiveAssertions.IGNORE_FOREIGN_KEY_UPDATE_ACTION) // FIXME ?
                              .assertionModifier(SiardArchiveAssertions.IGNORE_FOREIGN_KEY_DELETE_ACTION) // FIXME ?
                              .assertEqual();
    }
}
