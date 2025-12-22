package ch.admin.bar.siard2.cmd.mysql.usecases.keys.download;

import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySql8KeysDownloadIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.30"))
            .withUsername("root")
            .withPassword("test")
            .withCommand("--max-allowed-packet=1G --innodb_log_file_size=256M")
            .withInitScript(MySqlKeysDownload.INIT_SCRIPT);

    @Test
    public void download_expectNoExceptions() {
        MySqlKeysDownload.executeTest(siardArchivesHandler, db.getJdbcUrl());
    }
}
