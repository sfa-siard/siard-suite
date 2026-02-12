package ch.admin.bar.siard2.cmd.mysql.issues.siardsuite113;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class MySQLZeroDateValueIT {

    public final static String SQL = "mysql/issues/siardsuite113/empty-table.sql";

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer db = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("public")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SQL)
            .withConfigurationOverride("mysql/config/no-zero-date");

    @Test
    public void shouldCreateSiardArchiveFromDbWithNoZeroDate() throws SQLException, IOException, ClassNotFoundException {
        val actualArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl() + "?zeroDateTimeBehavior=convertToNull",
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());
    }
}
