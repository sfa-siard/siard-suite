package ch.admin.bar.siard2.cmd.oracle.issues.jdbcoracle6;

import ch.admin.bar.siard2.cmd.SiardFromDb;
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

// tests download of oracle db with a package/ an overloaded function
@Testcontainers
public class PackagesIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public final OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.PACKAGE)
                                                                   .toPath()),
                    "/container-entrypoint-initdb.d/00_create_package.sql");

    @Container
    public final OracleContainer emptyDb = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES)
                                                                   .toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql");

    @Test
    public void download() throws IOException, SQLException, ClassNotFoundException {
        // given
        val siardArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "testuser",
                "-p:" + "testpassword",
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        // then
        Assertions.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());
    }
}
