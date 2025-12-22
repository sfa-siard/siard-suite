package ch.admin.bar.siard2.cmd.mariadb.issues.jdbcmysql4;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;

public class MariaDBDownloadNationsIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MariaDBContainer<?> downloadDb = new MariaDBContainer<>(DockerImageName.parse("mariadb:11.8.2"))
            .withCopyFileToContainer(MountableFile.forClasspathResource(SqlScripts.MySQL.JDBCMYSQL_4),
                    "/docker-entrypoint-initdb.d/");


    @Test
    public void download_expectNoExceptions() throws SQLException, IOException, ClassNotFoundException {
        // given
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + downloadDb.getJdbcUrl().replace("jdbc:mariadb", "jdbc:mysql"),
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());
    }
}
