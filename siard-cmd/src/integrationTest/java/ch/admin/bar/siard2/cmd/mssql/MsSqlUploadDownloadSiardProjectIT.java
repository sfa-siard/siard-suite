package ch.admin.bar.siard2.cmd.mssql;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.mssql.usecases.keys.download.MsSqlDownloadSiardProjectIT;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class MsSqlUploadDownloadSiardProjectIT {

    /**
     * Output of {@link MsSqlDownloadSiardProjectIT}
     */
    public final static String SIMPLE_TEAMS_EXAMPLE = "mssql/simple-teams-example_mssql.siard";


    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MSSQLServerContainer<?> db = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense();

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SIMPLE_TEAMS_EXAMPLE);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile()
        });
        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

        SiardArchiveAssertions.builder()
                .expectedArchive(expectedArchive)
                .actualArchive(actualArchive)
                .assertionModifier(SiardArchiveAssertions.IGNORE_DBNAME) // FIXME ?
                .assertionModifier(SiardArchiveAssertions.IGNORE_PRIMARY_KEY_NAME) // DB restriction ?
                .assertionModifier(SiardArchiveAssertions.IGNORE_FOREIGN_KEY_DELETE_ACTION) // FIXME
                .assertionModifier(SiardArchiveAssertions.IGNORE_FOREIGN_KEY_UPDATE_ACTION) // FIXME
                .assertEqual();
    }
}
