package ch.admin.bar.siard2.cmd.mariadb.usecases.keys.download;

import ch.admin.bar.siard2.cmd.mysql.usecases.keys.download.MySqlKeysDownload;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class MariaDBKeysDownloadIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MariaDBContainer<?> db = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"))
            .withUsername("admin")
            .withPassword("password")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource(MySqlKeysDownload.INIT_SCRIPT),
                    "/docker-entrypoint-initdb.d/"
            );

    @Test
    public void download_expectNoExceptions() {
        MySqlKeysDownload.executeTest(siardArchivesHandler, db.getJdbcUrl().replace("jdbc:mariadb", "jdbc:mysql"));
    }
}
