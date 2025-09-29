package ch.admin.bar.siard2.cmd.mysql.issues.siardsuite101;

import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class UploadToMySQLIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("test")
            .withDatabaseName("public")
            .withConfigurationOverride("mysql/config/with-blobs");

    @Test
    public void uploadOracle() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_ORACLE21_2_2);

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void uploadPostgres() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_POSTGRES13_2_2);

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void uploadMsSql() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_MSSQL2017CU12_2_2);

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void uploadMySql() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_MYSQL5_2_2);

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void uploadMsAccess() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.MS_ACCESS_NATIONS);

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void uploadDB2() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_DB2);

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
