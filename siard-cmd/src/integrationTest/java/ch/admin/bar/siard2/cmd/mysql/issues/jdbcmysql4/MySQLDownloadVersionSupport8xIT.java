package ch.admin.bar.siard2.cmd.mysql.issues.jdbcmysql4;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

@Testcontainers
public class MySQLDownloadVersionSupport8xIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public MySQLContainer<?> downloadDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_8_4))
            .withUsername("root")
            .withPassword("public")
            .withInitScript(SqlScripts.MySQL.JDBCMYSQL_4)
            .withConfigurationOverride("mysql/config/mysql-version-support");

    @Test
    public void downloadDb_expectNoException() throws SQLException, IOException, ClassNotFoundException {
        val createdArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbtoSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + downloadDb.getJdbcUrl() + "?zeroDateTimeBehavior=convertToNull",
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + createdArchive.getPathToArchiveFile()
        });

        Assertions.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());
    }
}
