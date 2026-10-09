package ch.admin.bar.siard2.cmd.mysql.usecases.keys.upload;

import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

@Testcontainers
public class MySql5KeysUploadIT {

    public final static String SIARD_ARCHIVE_MYSQL_5 = "mysql/usecases/keys/upload/simple-teams-example_mysql5.siard";

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("test")
            .withCommand("--max-allowed-packet=1G --innodb_log_file_size=256M")
            .withInitScript(MySqlKeysUpload.CREATE_IT_USER_SQL_SCRIPT);

    @Test
    public void executeTest() {
        MySqlKeysUpload.executeTest(siardArchivesHandler, db.getJdbcUrl(), SIARD_ARCHIVE_MYSQL_5);
    }
}
