package ch.admin.bar.siard2.cmd.mysql.issues.jdbcmysql4;

import ch.admin.bar.siard2.cmd.SiardCliTest;
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
public class MySQLWildcardIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public MySQLContainer<?> downloadDbMySql8 = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_8_4))
            .withUsername("root")
            .withPassword("public")
            .withInitScript(SqlScripts.MySQL.JDBCMYSQL_4_WILDCARD)
            .withConfigurationOverride("mysql/config/mysql-version-support");

    @Container
    public MySQLContainer<?> downloadDbMySql5 = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_7))
            .withUsername("root")
            .withPassword("public")
            .withInitScript(SqlScripts.MySQL.JDBCMYSQL_4_WILDCARD)
            .withConfigurationOverride("mysql/config/mysql-version-support");

    @Test
    public void downloadDbMySql8_expectNoException() throws SQLException, IOException, ClassNotFoundException {
        val createdArchive = siardArchivesHandler.prepareEmpty();

        var dbtoSiard = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + downloadDbMySql8.getJdbcUrl(),
                "--user=" + "it_user",
                "--password=" + "it_password",
                "--siard-file=" + createdArchive.getPathToArchiveFile());

        Assertions.assertEquals(0, dbtoSiard.exitCode());
    }

    @Test
    public void downloadDbMySql5_expectNoException() throws SQLException, IOException, ClassNotFoundException {
        val createdArchive = siardArchivesHandler.prepareEmpty();

        var dbtoSiard = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + downloadDbMySql5.getJdbcUrl() + "?zeroDateTimeBehavior=convertToNull",
                "--user=" + "it_user",
                "--password=" + "it_password",
                "--siard-file=" + createdArchive.getPathToArchiveFile());

        Assertions.assertEquals(0, dbtoSiard.exitCode());
    }
}
