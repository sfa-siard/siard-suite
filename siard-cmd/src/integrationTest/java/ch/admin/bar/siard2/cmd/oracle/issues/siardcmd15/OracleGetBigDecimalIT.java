package ch.admin.bar.siard2.cmd.oracle.issues.siardcmd15;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.utils.OracleTestDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
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
public class OracleGetBigDecimalIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public final OracleContainer db = OracleTestDb.newContainer()
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDCMD_15)
                                                                   .toPath()),
                    "/container-entrypoint-initdb.d/siardcmd15.sql");

    @Container
    public final OracleContainer uploadDb = OracleTestDb.newContainer()
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDCMD_15)
                                                                   .toPath()),
                    "/container-entrypoint-initdb.d/siardcmd15.sql");

    @Test
    public void download_and_upload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        val actualArchive = siardArchivesHandler.prepareEmpty();

        var siardFromDb = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + "test",
                "--password=" + "test",
                "--siard-file=" + actualArchive.getPathToArchiveFile());
        Assertions.assertEquals(0, siardFromDb.exitCode());

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + uploadDb.getJdbcUrl(),
                "--user=" + "test",
                "--password=" + "test",
                "--siard-file=" + actualArchive.getPathToArchiveFile());
        Assertions.assertEquals(0, siardToDb.exitCode());
    }
}
