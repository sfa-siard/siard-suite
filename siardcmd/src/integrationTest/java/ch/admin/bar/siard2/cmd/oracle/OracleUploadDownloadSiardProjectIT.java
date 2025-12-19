package ch.admin.bar.siard2.cmd.oracle;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.ConsoleLogConsumer;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;

public class OracleUploadDownloadSiardProjectIT {


    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public final OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES).toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql");

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_ORACLE21_2_2);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + expectedArchive.getPathToArchiveFile()
        });
        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

        SiardArchiveAssertions.builder()
                .expectedArchive(expectedArchive)
                .actualArchive(actualArchive)
                .assertionModifier(SiardArchiveAssertions.IGNORE_DBNAME) // FIXME ?
                .assertionModifier(SiardArchiveAssertions.IGNORE_PRIMARY_KEY_NAME) // Probably an oracle-restriction (primary key names are generated)
                .assertionModifier(SiardArchiveAssertions.IGNORE_METADATA) // FIXME: Ignore metadata until https://github.com/sfa-siard/JdbcOracle/issues/12 and https://github.com/sfa-siard/JdbcOracle/issues/13 are fixed
                .assertEqual();
    }
}
