package ch.admin.bar.siard2.cmd.mssql;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.mssql.usecases.keys.download.MsSqlDownloadSiardProjectIT;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import ch.admin.bar.siard2.cmd.utils.ssl.SelfSignedCert;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;

public class MsSqlUploadDownloadSiardProjectUsingSSLIT {

    /**
     * Output of {@link MsSqlDownloadSiardProjectIT}
     */
    public final static String SIMPLE_TEAMS_EXAMPLE = "mssql/simple-teams-example_mssql.siard";


    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    private final SelfSignedCert cert = SelfSignedCert.generate("localhost", Duration.ofDays(1));

    @Rule
    public final MSSQLServerContainer<?> db = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withCopyToContainer(Transferable.of(cert.getCertificatePem(), 0644), "/var/opt/mssql/mssql.pem")
            .withCopyToContainer(Transferable.of(cert.getPrivateKeyPem(), 0644), "/var/opt/mssql/mssql.key")
            .withCopyToContainer(MountableFile.forClasspathResource("mssql/ssl/mssql.conf", 0644), "/var/opt/mssql/mssql.conf")
            .withUrlParam("trustServerCertificate", "true")
            .withUrlParam("encrypt", "true")
            .waitingFor(Wait.forLogMessage(".*SQL Server is now ready for client connections.*\\n", 1));

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
