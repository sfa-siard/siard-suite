package ch.admin.bar.siard2.cmd.postgres;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class PostgresUploadDownloadSiardProjectIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"));

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_POSTGRES13_2_2);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile()
        });
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        SiardArchiveAssertions.builder()
                .actualArchive(actualArchive)
                .expectedArchive(expectedArchive.preserveArchive())
                .assertionModifier(SiardArchiveAssertions.IGNORE_DBNAME) // FIXME ?
                .assertEqual();
    }
}
