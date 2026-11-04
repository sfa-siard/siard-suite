package ch.admin.bar.siard2.cmd.postgres.issues.siardsuite101;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

@Testcontainers
public class UploadToPostgresIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.3"));

    @Test
    public void uploadOracle() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_ORACLE21_2_2);

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + expectedArchive.getPathToArchiveFile());

        Assertions.assertEquals(0, siardToDb.exitCode());
    }

    @Test
    public void uploadPostgres() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_POSTGRES13_2_2);

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + expectedArchive.getPathToArchiveFile());

        Assertions.assertEquals(0, siardToDb.exitCode());
    }

    @Test
    public void uploadMsSql() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_MSSQL2017CU12_2_2);

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + expectedArchive.getPathToArchiveFile());

        Assertions.assertEquals(0, siardToDb.exitCode());
    }

    @Test
    public void uploadMySql() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_MYSQL5_2_2);

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + expectedArchive.getPathToArchiveFile());

        Assertions.assertEquals(0, siardToDb.exitCode());
    }

    @Disabled
    @Test
    public void uploadMsAccess() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.MS_ACCESS_NATIONS);

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + expectedArchive.getPathToArchiveFile());

        Assertions.assertEquals(0, siardToDb.exitCode());
    }

    @Test
    public void uploadDB2() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_DB2);

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + db.getJdbcUrl(),
                "--user=" + db.getUsername(),
                "--password=" + db.getPassword(),
                "--siard-file=" + expectedArchive.getPathToArchiveFile());

        Assertions.assertEquals(0, siardToDb.exitCode());
    }
}
