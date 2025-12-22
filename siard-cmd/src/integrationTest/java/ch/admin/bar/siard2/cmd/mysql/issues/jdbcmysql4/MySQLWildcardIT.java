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

public class MySQLWildcardIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> downloadDbMySql8 = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_8_4))
            .withUsername("root")
            .withPassword("public")
            .withInitScript(SqlScripts.MySQL.JDBCMYSQL_4_WILDCARD)
            .withConfigurationOverride("mysql/config/mysql-version-support");

    @Rule
    public MySQLContainer<?> downloadDbMySql5 = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_7))
            .withUsername("root")
            .withPassword("public")
            .withInitScript(SqlScripts.MySQL.JDBCMYSQL_4_WILDCARD)
            .withConfigurationOverride("mysql/config/mysql-version-support");

    @Test
    public void downloadDbMySql8_expectNoException() throws SQLException, IOException, ClassNotFoundException {
        val createdArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbtoSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + downloadDbMySql8.getJdbcUrl(),
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + createdArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());
    }

    @Test
    public void downloadDbMySql5_expectNoException() throws SQLException, IOException, ClassNotFoundException {
        val createdArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbtoSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + downloadDbMySql5.getJdbcUrl() + "?zeroDateTimeBehavior=convertToNull",
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + createdArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());
    }
}
