package ch.admin.bar.siard2.cmd.mariadb.usecases.types.download.defaulttypes;

import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.mysql.usecases.types.download.defaulttypes.MySqlDefaultDataTypes;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class MariaDBDefaultDataTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MariaDBContainer<?> db = new MariaDBContainer<>(DockerImageName.parse(SupportedDbVersions.MARIA_DB_10))
            .withUsername("admin")
            .withPassword("password")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource(MySqlDefaultDataTypes.INIT_SCRIPT),
                    "/docker-entrypoint-initdb.d/"
            );

    @Test
    public void executeTest() {
        MySqlDefaultDataTypes.executeTest(siardArchivesHandler, db.getJdbcUrl().replace("jdbc:mariadb", "jdbc:mysql"));
    }
}
