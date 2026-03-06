package ch.admin.bar.siard2.cmd.postgres.issues.siardsuite101;

import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class UploadToPostgresIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.3"));

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

    @Ignore
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
