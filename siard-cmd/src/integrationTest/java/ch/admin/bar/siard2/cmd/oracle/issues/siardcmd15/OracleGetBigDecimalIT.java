package ch.admin.bar.siard2.cmd.oracle.issues.siardcmd15;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
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
    public final OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDCMD_15)
                                                                   .toPath()),
                    "/container-entrypoint-initdb.d/siardcmd15.sql");

    @Container
    public final OracleContainer uploadDb = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDCMD_15)
                                                                   .toPath()),
                    "/container-entrypoint-initdb.d/siardcmd15.sql");

    @Test
    public void download_and_upload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        val actualArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "test",
                "-p:" + "test",
                "-s:" + actualArchive.getPathToArchiveFile()
        });
        Assertions.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + uploadDb.getJdbcUrl(),
                "-u:" + "test",
                "-p:" + "test",
                "-s:" + actualArchive.getPathToArchiveFile()
        });
        Assertions.assertEquals(SiardFromDb.iRETURN_OK, siardToDb.getReturn());
    }
}
