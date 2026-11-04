package ch.admin.bar.siard2.cmd.oracle;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.utils.OracleTestDb;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

@Testcontainers
public class OracleUploadDownloadSiardProjectIT {


    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public final OracleContainer db = OracleTestDb.newContainer()
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES)
                                                                   .toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql");

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_ORACLE21_2_2);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + "IT_USER",
                "--password=" + "password",
                "--siard-file=" + expectedArchive.getPathToArchiveFile());
        var dbToSiard = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + "IT_USER",
                "--password=" + "password",
                "--siard-file=" + actualArchive.getPathToArchiveFile());

        // then
        Assertions.assertEquals(0, siardToDb.exitCode());
        Assertions.assertEquals(0, dbToSiard.exitCode());

        SiardArchiveAssertions.builder()
                              .expectedArchive(expectedArchive)
                              .actualArchive(actualArchive)
                              .assertionModifier(SiardArchiveAssertions.IGNORE_DBNAME) // FIXME ?
                              .assertionModifier(SiardArchiveAssertions.IGNORE_PRIMARY_KEY_NAME) // Probably an oracle-restriction (primary key names are generated)
                              .assertionModifier(SiardArchiveAssertions.IGNORE_METADATA) // FIXME: Ignore metadata until https://github.com/sfa-siard/JdbcOracle/issues/12 and https://github.com/sfa-siard/JdbcOracle/issues/13 are fixed
                              .assertEqual();
    }
}
