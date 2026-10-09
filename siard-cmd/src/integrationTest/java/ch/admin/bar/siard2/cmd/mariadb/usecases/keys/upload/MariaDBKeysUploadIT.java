package ch.admin.bar.siard2.cmd.mariadb.usecases.keys.upload;

import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.mysql.usecases.keys.upload.MySqlKeysUpload;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

@Testcontainers
public class MariaDBKeysUploadIT {

    public final static String SIARD_ARCHIVE_MYSQL_5 = "mysql/usecases/keys/upload/simple-teams-example_mysql5.siard";

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public MariaDBContainer<?> db = new MariaDBContainer<>(DockerImageName.parse(SupportedDbVersions.MARIA_DB_10))
            .withUsername("admin")
            .withPassword("password")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource(MySqlKeysUpload.CREATE_IT_USER_SQL_SCRIPT),
                    "/docker-entrypoint-initdb.d/"
            );

    @Test
    public void executeTest() {
        MySqlKeysUpload.executeTest(siardArchivesHandler, db.getJdbcUrl()
                                                            .replace("jdbc:mariadb", "jdbc:mysql"), SIARD_ARCHIVE_MYSQL_5);
    }
}
