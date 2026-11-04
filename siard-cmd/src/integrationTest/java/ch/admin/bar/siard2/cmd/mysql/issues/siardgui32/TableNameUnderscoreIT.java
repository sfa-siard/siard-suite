package ch.admin.bar.siard2.cmd.mysql.issues.siardgui32;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.extension.RegisterExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class TableNameUnderscoreIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Container
    public MySQLContainer<?> emptyDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withConfigurationOverride("mysql/config/with-blobs");

    @Container
    public MySQLContainer<?> dbMySql5 = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_32_TABLE_NAME)
            .withConfigurationOverride("mysql/config/with-blobs");

    @Container
    public MySQLContainer<?> dbMySql8 = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_8_4))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_32_TABLE_NAME)
            .withConfigurationOverride("mysql/config/mysql-version-support");

    //This test fails because the provided archive may have not been created by siard software
    @Disabled
    @Test
    public void uploadSubmittedArchive_expectNoExceptions() throws SQLException, IOException {
        val submittedArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/submitted-table-names-underscore.siard");

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + emptyDb.getJdbcUrl(),
                "--user=" + emptyDb.getUsername(),
                "--password=" + emptyDb.getPassword(),
                "--siard-file=" + submittedArchive.getPathToArchiveFile());

        assertEquals(0, siardToDb.exitCode());
    }

    @Test
    public void downloadArchiveMySql5() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        var siardFromDb = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + dbMySql5.getJdbcUrl(),
                "--user=" + dbMySql5.getUsername(),
                "--password=" + dbMySql5.getPassword(),
                "--siard-file=" + siardArchive.getPathToArchiveFile());

        assertEquals(0, siardFromDb.exitCode());

        val metadataExplorer = siardArchive.exploreMetadata();

        val testTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                                                                       .schemaId(Id.of("public"))
                                                                       .tableId(Id.of("test_table"))
                                                                       .build());
        Assertions.assertThat(testTable.getName())
                  .isEqualTo(Id.of("test_table"));

        val jobHistoryTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                                                                             .schemaId(Id.of("public"))
                                                                             .tableId(Id.of("job_history"))
                                                                             .build());
        Assertions.assertThat(jobHistoryTable.getName())
                  .isEqualTo(Id.of("job_history"));

        val employeesTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                                                                            .schemaId(Id.of("public"))
                                                                            .tableId(Id.of("employees"))
                                                                            .build());
        Assertions.assertThat(employeesTable.getName())
                  .isEqualTo(Id.of("employees"));
    }

    //The issue had reported an error when uploading back to db
    @Test
    public void uploadSiardArchiveMySql5_expectNoExceptions() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/created-table-names-underscore-mysql5.siard");

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + dbMySql5.getJdbcUrl(),
                "--user=" + dbMySql5.getUsername(),
                "--password=" + dbMySql5.getPassword(),
                "--siard-file=" + siardArchive.getPathToArchiveFile());

        assertEquals(0, siardToDb.exitCode());
    }

    @Test
    public void downloadArchiveMySql8() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        var siardFromDb = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + dbMySql8.getJdbcUrl(),
                "--user=" + dbMySql8.getUsername(),
                "--password=" + dbMySql8.getPassword(),
                "--siard-file=" + siardArchive.getPathToArchiveFile());


        assertEquals(0, siardFromDb.exitCode());

        val metadataExplorer = siardArchive.exploreMetadata();

        val testTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                                                                       .schemaId(Id.of("public"))
                                                                       .tableId(Id.of("test_table"))
                                                                       .build());
        Assertions.assertThat(testTable.getName())
                  .isEqualTo(Id.of("test_table"));

        val jobHistoryTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                                                                             .schemaId(Id.of("public"))
                                                                             .tableId(Id.of("job_history"))
                                                                             .build());
        Assertions.assertThat(jobHistoryTable.getName())
                  .isEqualTo(Id.of("job_history"));

        val employeesTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                                                                            .schemaId(Id.of("public"))
                                                                            .tableId(Id.of("employees"))
                                                                            .build());
        Assertions.assertThat(employeesTable.getName())
                  .isEqualTo(Id.of("employees"));
    }

    @Test
    public void uploadSiardArchiveMySql8_expectNoExceptions() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/created-table-names-underscore-mysql8.siard");

        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + dbMySql8.getJdbcUrl(),
                "--user=" + dbMySql8.getUsername(),
                "--password=" + dbMySql8.getPassword(),
                "--siard-file=" + siardArchive.getPathToArchiveFile());

        assertEquals(0, siardToDb.exitCode());
    }
}
