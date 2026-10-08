package ch.admin.bar.siard2.cmd.mysql.issues.siardcmd31;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;

@Testcontainers
public class CaseSensitiveColumnNamesInMySqlT {
    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse("mysql:5.7"))
            .withUsername("public")
            .withPassword("public")
            .withDatabaseName("public")
            .waitingFor(new LogMessageWaitStrategy()
                                .withRegEx(".*Ready for connections.*\\s")
                                .withTimes(2)
                                .withStartupTimeout(Duration.of(60, SECONDS)))
            .withInitScript(SqlScripts.MySQL.SIARDCMD_31);

    @Test
    public void download_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assertions.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());
        actualArchive.preserveArchive();
    }
}
