package ch.admin.bar.siard2.cmd.mysql.issues.jdbcmysql4;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class MySQLDownloadVersionSupport9xIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> downloadDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_9_3))
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

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());
    }
}
