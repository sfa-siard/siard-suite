package ch.admin.bar.siard2.cmd.oracle.issues.siardcmd15;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.ConsoleLogConsumer;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;

public class OracleGetBigDecimalIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public final OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDCMD_15).toPath()),
                    "/container-entrypoint-initdb.d/siardcmd15.sql");

    @Rule
    public final OracleContainer uploadDb = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDCMD_15).toPath()),
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
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + uploadDb.getJdbcUrl(),
                "-u:" + "test",
                "-p:" + "test",
                "-s:" + actualArchive.getPathToArchiveFile()
        });
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardToDb.getReturn());
    }
}
