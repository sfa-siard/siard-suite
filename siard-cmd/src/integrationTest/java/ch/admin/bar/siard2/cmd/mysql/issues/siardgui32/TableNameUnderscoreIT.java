package ch.admin.bar.siard2.cmd.mysql.issues.siardgui32;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class TableNameUnderscoreIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> emptyDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withConfigurationOverride("mysql/config/with-blobs");

    @Rule
    public MySQLContainer<?> dbMySql5 = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_32_TABLE_NAME)
            .withConfigurationOverride("mysql/config/with-blobs");

    @Rule
    public MySQLContainer<?> dbMySql8 = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_8_4))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_32_TABLE_NAME)
            .withConfigurationOverride("mysql/config/mysql-version-support");

    //This test fails because the provided archive may have not been created by siard software
    @Ignore
    @Test
    public void uploadSubmittedArchive_expectNoExceptions() throws SQLException, IOException {
        val submittedArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/submitted-table-names-underscore.siard");

        SiardToDb siardToDb = new SiardToDb(new String[] {
                "-o",
                "-j:" + emptyDb.getJdbcUrl(),
                "-u:" + emptyDb.getUsername(),
                "-p:" + emptyDb.getPassword(),
                "-s:" + submittedArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void downloadArchiveMySql5() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + dbMySql5.getJdbcUrl(),
                "-u:" + dbMySql5.getUsername(),
                "-p:" + dbMySql5.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val testTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("test_table"))
                .build());
        Assertions.assertThat(testTable.getName()).isEqualTo(Id.of("test_table"));

        val jobHistoryTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("job_history"))
                .build());
        Assertions.assertThat(jobHistoryTable.getName()).isEqualTo(Id.of("job_history"));

        val employeesTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("employees"))
                .build());
        Assertions.assertThat(employeesTable.getName()).isEqualTo(Id.of("employees"));
    }

    //The issue had reported an error when uploading back to db
    @Test
    public void uploadSiardArchiveMySql5_expectNoExceptions() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/created-table-names-underscore-mysql5.siard");

        SiardToDb siardToDb = new SiardToDb(new String[] {
                "-o",
                "-j:" + dbMySql5.getJdbcUrl(),
                "-u:" + dbMySql5.getUsername(),
                "-p:" + dbMySql5.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void downloadArchiveMySql8() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + dbMySql8.getJdbcUrl(),
                "-u:" + dbMySql8.getUsername(),
                "-p:" + dbMySql8.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });


        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val testTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("test_table"))
                .build());
        Assertions.assertThat(testTable.getName()).isEqualTo(Id.of("test_table"));

        val jobHistoryTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("job_history"))
                .build());
        Assertions.assertThat(jobHistoryTable.getName()).isEqualTo(Id.of("job_history"));

        val employeesTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("employees"))
                .build());
        Assertions.assertThat(employeesTable.getName()).isEqualTo(Id.of("employees"));
    }

    @Test
    public void uploadSiardArchiveMySql8_expectNoExceptions() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/created-table-names-underscore-mysql8.siard");

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + dbMySql8.getJdbcUrl(),
                "-u:" + dbMySql8.getUsername(),
                "-p:" + dbMySql8.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
