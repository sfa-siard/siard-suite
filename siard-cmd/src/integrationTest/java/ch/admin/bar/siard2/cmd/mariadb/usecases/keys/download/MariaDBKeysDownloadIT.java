package ch.admin.bar.siard2.cmd.mariadb.usecases.keys.download;

import ch.admin.bar.siard2.cmd.mysql.usecases.keys.download.MySqlKeysDownload;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

@Testcontainers
public class MariaDBKeysDownloadIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public MariaDBContainer<?> db = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"))
            .withUsername("admin")
            .withPassword("password")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource(MySqlKeysDownload.INIT_SCRIPT),
                    "/docker-entrypoint-initdb.d/"
            );

    @Test
    public void download_expectNoExceptions() {
        MySqlKeysDownload.executeTest(siardArchivesHandler, db.getJdbcUrl()
                                                              .replace("jdbc:mariadb", "jdbc:mysql"));
    }
}
